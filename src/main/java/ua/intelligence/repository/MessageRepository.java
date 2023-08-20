package ua.intelligence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ua.intelligence.domain.Message;
import ua.intelligence.domain.Report;

import java.util.List;

/**
 * Spring Data JPA repository for the Message entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.report.id is null")
    List<Message> findUnprocessed();

    Page<Message> findByReport(Pageable pageable, Report report);
}
