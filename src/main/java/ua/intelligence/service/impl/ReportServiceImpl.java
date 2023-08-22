package ua.intelligence.service.impl;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.intelligence.domain.Message;
import ua.intelligence.domain.Report;
import ua.intelligence.repository.ReportRepository;
import ua.intelligence.service.DocxService;
import ua.intelligence.service.ReportService;

/**
 * Service Implementation for managing {@link Report}.
 */
@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    private final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final ReportRepository reportRepository;
    private final DocxService docxService;

    public ReportServiceImpl(ReportRepository reportRepository, DocxService docxService) {
        this.reportRepository = reportRepository;
        this.docxService = docxService;
    }

    @Override
    public Report save(Report report) {
        log.debug("Request to save Report : {}", report);
        return reportRepository.save(report);
    }

    @Override
    public Report update(Report report) {
        log.debug("Request to update Report : {}", report);
        return reportRepository.save(report);
    }

    @Override
    public Optional<Report> partialUpdate(Report report) {
        log.debug("Request to partially update Report : {}", report);

        return reportRepository
            .findById(report.getId())
            .map(existingReport -> {
                if (report.getName() != null) {
                    existingReport.setName(report.getName());
                }
                if (report.getDate() != null) {
                    existingReport.setDate(report.getDate());
                }
                if (report.getPath() != null) {
                    existingReport.setPath(report.getPath());
                }
                if (report.getResourceId() != null) {
                    existingReport.setResourceId(report.getResourceId());
                }
                if (report.getConclusion() != null) {
                    existingReport.setConclusion(report.getConclusion());
                }
                if (report.getContent() != null) {
                    existingReport.setContent(report.getContent());
                }
                if (report.getContentContentType() != null) {
                    existingReport.setContentContentType(report.getContentContentType());
                }

                return existingReport;
            })
            .map(reportRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Report> findAll(Pageable pageable) {
        log.debug("Request to get all Reports");
        final Page<Report> all = reportRepository.findAll(pageable);
        return all;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Report> findOne(Long id) {
        log.debug("Request to get Report : {}", id);
        return reportRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Report : {}", id);
        reportRepository.deleteById(id);
    }

    @Override
    public byte[] generateReport(Long id) {
        final Optional<Report> r = reportRepository.findById(id);
        if (r.isPresent()) {
            final Report report = r.get();

            final byte[] content = report.getContent();
            final String conclusion = report.getConclusion();
            final Set<Message> messages = report.getMessages().stream().filter(m -> m.getSourceUuid() != null).collect(Collectors.toSet());

            return docxService.processReport(content, messages, conclusion);
        } else {
            return new byte[0];
        }
    }
}
