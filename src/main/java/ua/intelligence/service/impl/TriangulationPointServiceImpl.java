package ua.intelligence.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.intelligence.domain.TriangulationPoint;
import ua.intelligence.repository.TriangulationPointRepository;
import ua.intelligence.service.TriangulationPointService;

/**
 * Service Implementation for managing {@link TriangulationPoint}.
 */
@Service
@Transactional
public class TriangulationPointServiceImpl implements TriangulationPointService {

    private final Logger log = LoggerFactory.getLogger(TriangulationPointServiceImpl.class);

    private final TriangulationPointRepository triangulationPointRepository;

    public TriangulationPointServiceImpl(TriangulationPointRepository triangulationPointRepository) {
        this.triangulationPointRepository = triangulationPointRepository;
    }

    @Override
    public TriangulationPoint save(TriangulationPoint triangulationPoint) {
        log.debug("Request to save TriangulationPoint : {}", triangulationPoint);
        return triangulationPointRepository.save(triangulationPoint);
    }

    @Override
    public TriangulationPoint update(TriangulationPoint triangulationPoint) {
        log.debug("Request to update TriangulationPoint : {}", triangulationPoint);
        return triangulationPointRepository.save(triangulationPoint);
    }

    @Override
    public Optional<TriangulationPoint> partialUpdate(TriangulationPoint triangulationPoint) {
        log.debug("Request to partially update TriangulationPoint : {}", triangulationPoint);

        return triangulationPointRepository
            .findById(triangulationPoint.getId())
            .map(existingTriangulationPoint -> {
                if (triangulationPoint.getFrequency() != null) {
                    existingTriangulationPoint.setFrequency(triangulationPoint.getFrequency());
                }
                if (triangulationPoint.getDate() != null) {
                    existingTriangulationPoint.setDate(triangulationPoint.getDate());
                }
                if (triangulationPoint.getDescription() != null) {
                    existingTriangulationPoint.setDescription(triangulationPoint.getDescription());
                }
                if (triangulationPoint.getLatitude() != null) {
                    existingTriangulationPoint.setLatitude(triangulationPoint.getLatitude());
                }
                if (triangulationPoint.getLongitude() != null) {
                    existingTriangulationPoint.setLongitude(triangulationPoint.getLongitude());
                }

                return existingTriangulationPoint;
            })
            .map(triangulationPointRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TriangulationPoint> findAll(Pageable pageable) {
        log.debug("Request to get all TriangulationPoints");
        return triangulationPointRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TriangulationPoint> findOne(Long id) {
        log.debug("Request to get TriangulationPoint : {}", id);
        return triangulationPointRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TriangulationPoint : {}", id);
        triangulationPointRepository.deleteById(id);
    }
}
