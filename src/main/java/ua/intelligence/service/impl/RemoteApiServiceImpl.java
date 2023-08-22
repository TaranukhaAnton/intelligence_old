package ua.intelligence.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.intelligence.domain.Message;
import ua.intelligence.domain.Report;
import ua.intelligence.domain.TriangulationPoint;
import ua.intelligence.domain.signal.SignalMessage;
import ua.intelligence.service.*;
import ua.intelligence.service.dto.SignalMessageDto;

@Service
public class RemoteApiServiceImpl implements RemoteApiService {

    private final Logger log = LoggerFactory.getLogger(RemoteApiServiceImpl.class);

    public static final String REP_GROUP = "yXE2sBQHEVcVc79eXqePWstXd7XRMejpg/HnK/N+4bw=";

    public static final String FAITA = "H2UMFkV7ZH+6jMxkpUr/3zw7g6byjFqRS2TUsl26yAI=";
    public static final String STEPNO = "Btb71pNufy93e/tEZ4A9RDILd7CcdLprMB6U4Y7+KdM=";

    public static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final MessageService messageService;
    private final ReportService reportService;
    private final TriangulationPointService triangulationPointService;
    private final DocxService docxService;
    private final SignalService signalService;

    public RemoteApiServiceImpl(
        MessageService messageRepository,
        ReportService reportRepository,
        TriangulationPointService triangulationPointService,
        DocxService docxService,
        SignalService signalService
    ) {
        this.messageService = messageRepository;
        this.reportService = reportRepository;
        this.triangulationPointService = triangulationPointService;
        this.docxService = docxService;
        this.signalService = signalService;
    }

    @Scheduled(fixedDelay = 10000)
    public void scheduleFixedDelayTask() {
        final List<SignalMessageDto> signalMessages = signalService.receiveMessage();
        log.info("Received {} messages", signalMessages.size());
        for (SignalMessageDto message : signalMessages) {
            if (REP_GROUP.equals(message.getGroupId())) {
                log.info("message from REPORTS GROUP");
                processTextReport(message);
            } else if (STEPNO.equals(message.getGroupId())) {
                log.info("message from STEPNO");
                processTriangulationPoint(message);
            } else if (FAITA.equals(message.getGroupId())) {
                log.info("message from FAITA");
                processTextMessage(message);
            }
        }
    }

    private void processTriangulationPoint(SignalMessageDto message) {
        final String text = message.getText();
        if (text == null || text.isEmpty()) {
            return;
        }
        final String replace = text.replace("\n\n", "\n");
        final String[] split = replace.split("\\n");

        if (split.length == 3) {
            try {
                final Float lat = Float.parseFloat(split[0].replace(" ПнШ", "").replace(",", ".").trim());
                final Float lon = Float.parseFloat(split[1].replace(" СхД", "").replace(",", ".").trim());
                final String frequency = split[2];
                TriangulationPoint p = new TriangulationPoint();
                p.setDate(ZonedDateTime.now(ZoneId.of("GMT+03:00")));
                p.setFrequency(frequency.replace(",", "."));
                p.setLatitude(lat);
                p.setLongitude(lon);
                triangulationPointService.save(p);
            } catch (Exception e) {
                log.error("Error during triangulation point processing ", e);
            }
        }
    }

    @Transactional
    public void processTextReport(SignalMessageDto message) {
        if (message.getAttachments().isEmpty()) {
            log.info("Message has no attachments. Ignore.");
            return;
        }

        if (message.getAttachments().size() > 1) {
            log.info("Message has more than one attachment.");
            log.info("We will process only first one.");
        }
        final byte[] fileContent = message.getAttachments().get(0).getContent();

        final Report r = new Report();
        r.setDate(ZonedDateTime.now(ZoneId.of("GMT+03:00")));
        r.name(repName());
        r.setContent(fileContent);
        r.setContentContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        reportService.save(r);

        for (Message m : docxService.extractMessagesFromDocx(fileContent)) {
            m.setReport(r);
            messageService.save(m);
        }

        final List<Message> unprocessed = messageService.findUnprocessed();
        if (unprocessed != null) {
            for (Message m : unprocessed) {
                m.setReport(r);
                messageService.save(m);
            }
        }
    }

    private void processTextMessage(SignalMessageDto message) {
        final Message m = new Message();
        final String text = message.getText();

        if (StringUtils.isNoneEmpty(text)) {
            m.setSourceUuid(message.getGroupId());
            m.setText(text);
            m.setFrequency(getFrequency(text));
            m.setDate(getDate(text));
            messageService.save(m);
        }
    }

    private String getGroupId(SignalMessage message) {
        final Map<String, Object> additionalProperties = message.getEnvelope().getDataMessage().getAdditionalProperties();
        final Map<String, String> groupInfo = (Map<String, String>) additionalProperties.get("groupInfo");
        if (groupInfo != null) {
            return groupInfo.get("groupId");
        } else {
            return null;
        }
    }

    private static String getFrequency(String messageText) {
        if (messageText == null) return null;

        final String[] split = messageText.split("F=");
        if (split.length > 1) {
            String[] splitByDot = split[1].split("\\(МГц\\)");
            if (splitByDot.length > 0) {
                return splitByDot[0];
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private static ZonedDateTime getDate(String messageText) {
        if (messageText == null) {
            return null;
        }
        String regex = "\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d.\\d\\d:\\d\\d";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(messageText);
        if (matcher.find()) {
            final String dateTimeString = messageText.substring(matcher.start(), matcher.end());
            final String[] s = dateTimeString.split("_");
            return getZonedDateTime(s[0], s[1]);
        } else {
            return null;
        }
    }

    public static String repName() {
        LocalDate dt = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalTime localTime = LocalTime.now();
        LocalTime middle = LocalTime.of(12, 0);
        final String s = localTime.isAfter(middle) ? "23" : "11";
        return "розвідувальне зведення " + formatter.format(dt) + "_" + s + ".docx";
    }

    private static ZonedDateTime getZonedDateTime(String d, String t) {
        LocalDate localDate2 = LocalDate.parse(d, DATE_FORMATTER);
        LocalTime localTime2 = LocalTime.parse(t, DateTimeFormatter.ISO_LOCAL_TIME);
        ZoneId zoneId2 = ZoneId.of("GMT+03:00");
        return ZonedDateTime.of(localDate2, localTime2, zoneId2);
    }
}
