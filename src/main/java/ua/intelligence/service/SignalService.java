package ua.intelligence.service;

import java.util.List;
import ua.intelligence.service.dto.AttachmentDto;
import ua.intelligence.service.dto.SignalMessageDto;

/**
 * @author Taranukha Anton
 */
public interface SignalService {
    void sendMessage(String phone, String message, AttachmentDto... attachment);

    List<SignalMessageDto> receiveMessage();
}
