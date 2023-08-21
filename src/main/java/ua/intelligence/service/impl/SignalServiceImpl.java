package ua.intelligence.service.impl;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import ua.intelligence.domain.signal.DataMessage;
import ua.intelligence.domain.signal.SignalMessage;
import ua.intelligence.service.SignalService;
import ua.intelligence.service.dto.AttachmentDto;
import ua.intelligence.service.dto.SignalMessageDto;

/**
 * @author Taranukha Anton
 */
@Service
public class SignalServiceImpl implements SignalService {

    private static final String SIGNAL_PREFIX = "signal-";
    public static final String CONTENT_TYPE = "contentType";
    public static final String SIZE = "size";
    public static final String FILENAME = "filename";
    public static final String ATTACHMENTS = "attachments";
    private final Logger log = LoggerFactory.getLogger(SignalServiceImpl.class);

    @Value("${signal.phone}")
    private String signalPhone;

    @Value("${signal.url}")
    private String signalApiUrl;

    private final RestTemplate restTemplate;

    public SignalServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendMessage(String phone, String message, AttachmentDto... attachment) {
        JSONObject request = new JSONObject();
        request.put("message", message);
        request.put("number", signalPhone);
        request.put("recipients", List.of(phone));
        List<String> base64Attachments = new ArrayList<>();
        for (AttachmentDto a : attachment) {
            base64Attachments.add(
                String.format(
                    "data:%s;filename=%s,base64,%s",
                    a.getContentType(),
                    a.getFileName(),
                    Base64.getEncoder().encodeToString(a.getContent())
                )
            );
        }
        if (!base64Attachments.isEmpty()) {
            request.put("base64_attachments", base64Attachments);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(signalApiUrl + "/v2/send", HttpMethod.POST, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to send message.");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public Set<SignalMessageDto> receiveMessage() {
        try {
            final List<SignalMessage> messages = getMessages();

            log.info("{} messages received.", messages.size());
            //send greeting message to new users. And we believe next time they will come with defined phone number.
            for (SignalMessage message : messages) {
                if (message.getEnvelope().getSourceNumber() == null) {
                    log.info("Message from unknown user {}.", message.getEnvelope().getSourceUuid());
                    sendMessage(message.getEnvelope().getSourceUuid(), "Signal bot вітає вас.");
                }
            }

            return messages
                .stream()
                .filter(filterSignalMessagePredicate())
                .map(m ->
                    new SignalMessageDto(
                        m.getEnvelope().getSourceUuid(),
                        m.getEnvelope().getSourceNumber(),
                        m.getEnvelope().getSourceName(),
                        m.getEnvelope().getDataMessage().getMessage(),
                        m.getEnvelope().getDataMessage().getTimestamp(),
                        processAttachments(m.getEnvelope().getDataMessage()),
                        processGroups(m.getEnvelope().getDataMessage())
                    )
                )
                .collect(Collectors.toSet());
        } catch (Exception hcee) {
            log.error(hcee.getMessage());
            return Collections.EMPTY_SET;
        }
    }

    private String processGroups(DataMessage dataMessage) {
        final Map<String, Object> additionalProperties = dataMessage.getAdditionalProperties();
        final Map<String, String> groupInfo = (Map<String, String>) additionalProperties.get("groupInfo");
        if (groupInfo != null) {
            return groupInfo.get("groupId");
        } else {
            return null;
        }
    }

    private Predicate<SignalMessage> filterSignalMessagePredicate() {
        return m ->
            m.getEnvelope() != null &&
            m.getEnvelope().getDataMessage() != null &&
            m.getEnvelope().getSourceNumber() != null &&
            (StringUtils.isNoneEmpty(m.getEnvelope().getDataMessage().getMessage()) || hasAttachments(m.getEnvelope().getDataMessage()));
    }

    private List<AttachmentDto> processAttachments(DataMessage dataMessage) {
        final Map<String, Object> additionalProperties = dataMessage.getAdditionalProperties();
        if (hasAttachments(dataMessage)) {
            final List<Map<String, Object>> attachments = (List<Map<String, Object>>) additionalProperties.get("attachments");
            return attachments
                .stream()
                .map(a ->
                    new AttachmentDto(getContent((String) a.get("id")), (String) a.get(CONTENT_TYPE), getFilename(a), (Integer) a.get(SIZE))
                )
                .collect(Collectors.toList());
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    private String getFilename(Map<String, Object> properties) {
        if (properties.get(FILENAME) == null || ((String) properties.get("filename")).isBlank()) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String fileExtension = getFileExtension((String) properties.get("id"));
            return SIGNAL_PREFIX + currentDateTime.format(formatter) + fileExtension;
        } else {
            return (String) properties.get(FILENAME);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return ""; // No extension or dot is at the end
        } else {
            return filename.substring(lastDotIndex);
        }
    }

    private List<SignalMessage> getMessages() {
        final String url = signalApiUrl + "/v1/receive/" + (signalPhone.startsWith("+") ? "" : "+") + signalPhone;
        ResponseEntity<SignalMessage[]> response = restTemplate.getForEntity(url, SignalMessage[].class);
        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }

    private byte[] getContent(String id) {
        return restTemplate.execute(
            signalApiUrl + "/v1/attachments/" + id,
            HttpMethod.GET,
            null,
            clientHttpResponse -> {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                StreamUtils.copy(clientHttpResponse.getBody(), out);
                return out.toByteArray();
            }
        );
    }

    private boolean hasAttachments(DataMessage dataMessage) {
        final Map<String, Object> additionalProperties = dataMessage.getAdditionalProperties();
        return additionalProperties.containsKey(ATTACHMENTS);
    }
}
