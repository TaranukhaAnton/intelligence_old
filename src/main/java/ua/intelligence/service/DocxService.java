package ua.intelligence.service;

import ua.intelligence.domain.Message;

import java.util.List;
import java.util.Set;

public interface DocxService {

    List<Message> extractMessagesFromDocx(byte[] content);

	byte[] processReport(byte[] content, Set<Message> messages, String conclusion);
}
