package ua.intelligence.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.intelligence.domain.Message;
import ua.intelligence.domain.Report;
import ua.intelligence.repository.MessageRepository;
import ua.intelligence.service.MessageService;

/**
 * Service Implementation for managing {@link Message}.
 */
@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private final Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);

    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message save(Message message) {
        log.debug("Request to save Message : {}", message);
        return messageRepository.save(message);
    }

    @Override
    public Message update(Message message) {
        log.debug("Request to update Message : {}", message);
        return messageRepository.save(message);
    }

    @Override
    public Optional<Message> partialUpdate(Message message) {
        log.debug("Request to partially update Message : {}", message);

        return messageRepository
            .findById(message.getId())
            .map(existingMessage -> {
                if (message.getDate() != null) {
                    existingMessage.setDate(message.getDate());
                }
                if (message.getFrequency() != null) {
                    existingMessage.setFrequency(message.getFrequency());
                }
                if (message.getSenderCallSign() != null) {
                    existingMessage.setSenderCallSign(message.getSenderCallSign());
                }
                if (message.getReceiverCallSign() != null) {
                    existingMessage.setReceiverCallSign(message.getReceiverCallSign());
                }
                if (message.getText() != null) {
                    existingMessage.setText(message.getText());
                }
                if (message.getSourceUuid() != null) {
                    existingMessage.setSourceUuid(message.getSourceUuid());
                }

                return existingMessage;
            })
            .map(messageRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> findAll(Pageable pageable) {
        log.debug("Request to get all Messages");
        return messageRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Message> findOne(Long id) {
        log.debug("Request to get Message : {}", id);
        return messageRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Message : {}", id);
        messageRepository.deleteById(id);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<Message> findByReportId(Pageable pageable, Long repId) {
        final Report report = new Report();
        report.setId(repId);
        return messageRepository.findByReport(pageable, report);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> findUnprocessed() {
        return messageRepository.findUnprocessed();
    }
}
