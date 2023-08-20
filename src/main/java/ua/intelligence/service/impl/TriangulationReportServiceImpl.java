package ua.intelligence.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.intelligence.domain.TriangulationReport;
import ua.intelligence.repository.TriangulationReportRepository;
import ua.intelligence.service.TriangulationReportService;

/**
 * Service Implementation for managing {@link TriangulationReport}.
 */
@Service
@Transactional
public class TriangulationReportServiceImpl implements TriangulationReportService {

    private final Logger log = LoggerFactory.getLogger(TriangulationReportServiceImpl.class);

    private final TriangulationReportRepository triangulationReportRepository;

    public TriangulationReportServiceImpl(TriangulationReportRepository triangulationReportRepository) {
        this.triangulationReportRepository = triangulationReportRepository;
    }

    @Override
    public TriangulationReport save(TriangulationReport triangulationReport) {
        log.debug("Request to save TriangulationReport : {}", triangulationReport);
        return triangulationReportRepository.save(triangulationReport);
    }

    @Override
    public TriangulationReport update(TriangulationReport triangulationReport) {
        log.debug("Request to update TriangulationReport : {}", triangulationReport);
        return triangulationReportRepository.save(triangulationReport);
    }

    @Override
    public Optional<TriangulationReport> partialUpdate(TriangulationReport triangulationReport) {
        log.debug("Request to partially update TriangulationReport : {}", triangulationReport);

        return triangulationReportRepository
            .findById(triangulationReport.getId())
            .map(existingTriangulationReport -> {
                if (triangulationReport.getDate() != null) {
                    existingTriangulationReport.setDate(triangulationReport.getDate());
                }
                if (triangulationReport.getName() != null) {
                    existingTriangulationReport.setName(triangulationReport.getName());
                }
                if (triangulationReport.getConclusion() != null) {
                    existingTriangulationReport.setConclusion(triangulationReport.getConclusion());
                }

                return existingTriangulationReport;
            })
            .map(triangulationReportRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TriangulationReport> findAll(Pageable pageable) {
        log.debug("Request to get all TriangulationReports");
        return triangulationReportRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TriangulationReport> findOne(Long id) {
        log.debug("Request to get TriangulationReport : {}", id);
        return triangulationReportRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TriangulationReport : {}", id);
        triangulationReportRepository.deleteById(id);
    }
}
