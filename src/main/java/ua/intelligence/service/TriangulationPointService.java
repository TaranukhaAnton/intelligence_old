package ua.intelligence.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.intelligence.domain.TriangulationPoint;

/**
 * Service Interface for managing {@link TriangulationPoint}.
 */
public interface TriangulationPointService {
    /**
     * Save a triangulationPoint.
     *
     * @param triangulationPoint the entity to save.
     * @return the persisted entity.
     */
    TriangulationPoint save(TriangulationPoint triangulationPoint);

    /**
     * Updates a triangulationPoint.
     *
     * @param triangulationPoint the entity to update.
     * @return the persisted entity.
     */
    TriangulationPoint update(TriangulationPoint triangulationPoint);

    /**
     * Partially updates a triangulationPoint.
     *
     * @param triangulationPoint the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TriangulationPoint> partialUpdate(TriangulationPoint triangulationPoint);

    /**
     * Get all the triangulationPoints.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TriangulationPoint> findAll(Pageable pageable);

    /**
     * Get the "id" triangulationPoint.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TriangulationPoint> findOne(Long id);

    /**
     * Delete the "id" triangulationPoint.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
