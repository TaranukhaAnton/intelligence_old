package ua.intelligence.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.intelligence.domain.TriangulationReport;

/**
 * Service Interface for managing {@link TriangulationReport}.
 */
public interface TriangulationReportService {
    /**
     * Save a triangulationReport.
     *
     * @param triangulationReport the entity to save.
     * @return the persisted entity.
     */
    TriangulationReport save(TriangulationReport triangulationReport);

    /**
     * Updates a triangulationReport.
     *
     * @param triangulationReport the entity to update.
     * @return the persisted entity.
     */
    TriangulationReport update(TriangulationReport triangulationReport);

    /**
     * Partially updates a triangulationReport.
     *
     * @param triangulationReport the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TriangulationReport> partialUpdate(TriangulationReport triangulationReport);

    /**
     * Get all the triangulationReports.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TriangulationReport> findAll(Pageable pageable);

    /**
     * Get the "id" triangulationReport.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TriangulationReport> findOne(Long id);

    /**
     * Delete the "id" triangulationReport.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
