package ua.intelligence.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.intelligence.domain.Message;

/**
 * Service Interface for managing {@link Message}.
 */
public interface MessageService {
    /**
     * Save a message.
     *
     * @param message the entity to save.
     * @return the persisted entity.
     */
    Message save(Message message);

    /**
     * Updates a message.
     *
     * @param message the entity to update.
     * @return the persisted entity.
     */
    Message update(Message message);

    /**
     * Partially updates a message.
     *
     * @param message the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Message> partialUpdate(Message message);

    /**
     * Get all the messages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Message> findAll(Pageable pageable);

    /**
     * Get the "id" message.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Message> findOne(Long id);

    /**
     * Delete the "id" message.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    Page<Message> findByReportId(Pageable pageable, Long repId);

    List<Message> findUnprocessed();
}
