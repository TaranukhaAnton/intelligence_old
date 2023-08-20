package ua.intelligence.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import ua.intelligence.domain.Message;
import ua.intelligence.domain.Report;
import ua.intelligence.domain.TriangulationPoint;
import ua.intelligence.domain.signal.DataMessage;
import ua.intelligence.domain.signal.Envelope;
import ua.intelligence.domain.signal.SignalMessage;
import ua.intelligence.service.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RemoteApiServiceImpl implements RemoteApiService {


    public static final String REP_GROUP = "yXE2sBQHEVcVc79eXqePWstXd7XRMejpg/HnK/N+4bw=";

    public static final String FAITA = "H2UMFkV7ZH+6jMxkpUr/3zw7g6byjFqRS2TUsl26yAI=";
    public static final String STEPNO = "Btb71pNufy93e/tEZ4A9RDILd7CcdLprMB6U4Y7+KdM=";

    public static final String REPORT_STORE_FLDR = "D:\\WAR_WORK\\tmp\\";

    public static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final RestTemplate restTemplate;
    private final MessageService messageService;
    private final ReportService reportService;
    private final TriangulationPointService triangulationPointService;
    private final DocxService docxService;

    public RemoteApiServiceImpl(RestTemplate restTemplate, MessageService messageRepository, ReportService reportRepository, TriangulationPointService triangulationPointService, DocxService docxService) {
        this.restTemplate = restTemplate;
        this.messageService = messageRepository;
        this.reportService = reportRepository;
        this.triangulationPointService = triangulationPointService;
        this.docxService = docxService;
    }


    //            @Scheduled(fixedDelay = 3000000)
    @Scheduled(fixedDelay = 10000)
    public void scheduleFixedDelayTask() {
        final List<SignalMessage> messages = getMessages();


        System.out.println("messages.size() = " + messages.size());
        for (SignalMessage message : messages) {

            final Envelope envelope = message.getEnvelope();
            if (envelope == null) {
                continue;
            }
            final DataMessage dataMessage = envelope.getDataMessage();
            if (dataMessage == null) {
                continue;
            }


            String groupId = getGroupId(message);


            if (REP_GROUP.equals(groupId)) {
                System.out.println("REPORTS GROUP");

                List<String> attPaths = downloadAttachments(message);
                System.out.println("attPaths = " + attPaths.size());
                final String reportPath = attPaths.get(0);
//                if (isTextReport(reportPath)) {
                processTextReport(message, reportPath);
//                } else {
//                    processDirectionFindingReport(message);
//                }
            } else if (STEPNO.equals(groupId)) {
                processTriangulationPoint(dataMessage);
            } else if (FAITA.equals(groupId)) {
                processTextMessage(dataMessage, groupId);
            }


        }
    }

    private void processTriangulationPoint(DataMessage message) {
        final String text = message.getMessage();
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
                e.printStackTrace();

                System.out.println("text = " + text);

            }
        }
    }


    @Override
    public List<SignalMessage> getMessages() {

//        String url = "http://localhost:8090/v1/receive/+380957319209";
//        ResponseEntity<SignalMessage[]> response =
//            restTemplate.getForEntity(
//                url,
//                SignalMessage[].class);
//        return Arrays.asList(response.getBody());

        return new LinkedList<>();
    }

    private void processTextReport(SignalMessage message, String reportPath) {
//        final Long timestamp = message.getEnvelope().getDataMessage().getTimestamp();
//        LocalDateTime triggerTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId());

        final Report r = new Report();
        r.setPath(reportPath);
        r.setDate(ZonedDateTime.now(ZoneId.of("GMT+03:00")));
        r.name(repName());

        try {
            final File file = new File(reportPath);
            byte[] fileContent = Files.readAllBytes(file.toPath());
            r.setContent(fileContent);
            r.setContentContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");


            reportService.save(r);

            for (Message m : docxService.extractMessagesFromDocx(fileContent)) {
                m.setReport(r);
                messageService.save(m);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        final List<Message> unprocessed = messageService.findUnprocessed();
        if (unprocessed != null) {
            for (Message m : unprocessed) {
                m.setReport(r);
                messageService.save(m);
            }
        }

    }

    private void processDirectionFindingReport(SignalMessage message) {

    }

    private boolean isTextReport(String reportPath) {
        return true;
    }

    private List<String> downloadAttachments(SignalMessage message) {
        List<String> attPaths = new LinkedList<>();
        final Map<String, Object> additionalProperties = message.getEnvelope().getDataMessage().getAdditionalProperties();
        final boolean hasAttachments = additionalProperties.containsKey("attachments");
        if (hasAttachments) {
            final List<Map<String, Object>> attachments = (List<Map<String, Object>>) additionalProperties.get("attachments");
            System.out.println("attachments.size() = " + attachments.size());

            for (Map<String, Object> att : attachments) {
                final String contentType = (String) att.get("contentType");
                final String id = (String) att.get("id");
                final Integer size = (Integer) att.get("size");
                attPaths.add(REPORT_STORE_FLDR + id);
                String FILE_URL = "http://localhost:8090/v1/attachments/" + id;
                restTemplate.execute(FILE_URL, HttpMethod.GET, null, clientHttpResponse -> {
                    File dir = new File("D:\\WAR_WORK\\tmp\\");
                    File actualFile = new File(dir, id);
                    StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(actualFile));
                    return actualFile;
                });
            }


        }
        return attPaths;
    }

    private void processTextMessage(DataMessage dataMessage, String groupId) {
        final Message m = new Message();
        final String text = dataMessage.getMessage();

        if (StringUtils.isNoneEmpty(text)) {
            m.setSourceUuid(groupId);
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
