package ua.intelligence.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
import ua.intelligence.domain.TriangulationReport;
import ua.intelligence.repository.TriangulationReportRepository;
import ua.intelligence.service.TriangulationReportService;
import ua.intelligence.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link ua.intelligence.domain.TriangulationReport}.
 */
@RestController
@RequestMapping("/api")
public class TriangulationReportResource {

    private final Logger log = LoggerFactory.getLogger(TriangulationReportResource.class);

    private static final String ENTITY_NAME = "triangulationReport";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TriangulationReportService triangulationReportService;

    private final TriangulationReportRepository triangulationReportRepository;

    public TriangulationReportResource(
        TriangulationReportService triangulationReportService,
        TriangulationReportRepository triangulationReportRepository
    ) {
        this.triangulationReportService = triangulationReportService;
        this.triangulationReportRepository = triangulationReportRepository;
    }

    /**
     * {@code POST  /triangulation-reports} : Create a new triangulationReport.
     *
     * @param triangulationReport the triangulationReport to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new triangulationReport, or with status {@code 400 (Bad Request)} if the triangulationReport has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/triangulation-reports")
    public ResponseEntity<TriangulationReport> createTriangulationReport(@RequestBody TriangulationReport triangulationReport)
        throws URISyntaxException {
        log.debug("REST request to save TriangulationReport : {}", triangulationReport);
        if (triangulationReport.getId() != null) {
            throw new BadRequestAlertException("A new triangulationReport cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TriangulationReport result = triangulationReportService.save(triangulationReport);
        return ResponseEntity
            .created(new URI("/api/triangulation-reports/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /triangulation-reports/:id} : Updates an existing triangulationReport.
     *
     * @param id the id of the triangulationReport to save.
     * @param triangulationReport the triangulationReport to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated triangulationReport,
     * or with status {@code 400 (Bad Request)} if the triangulationReport is not valid,
     * or with status {@code 500 (Internal Server Error)} if the triangulationReport couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/triangulation-reports/{id}")
    public ResponseEntity<TriangulationReport> updateTriangulationReport(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TriangulationReport triangulationReport
    ) throws URISyntaxException {
        log.debug("REST request to update TriangulationReport : {}, {}", id, triangulationReport);
        if (triangulationReport.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, triangulationReport.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!triangulationReportRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TriangulationReport result = triangulationReportService.update(triangulationReport);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, triangulationReport.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /triangulation-reports/:id} : Partial updates given fields of an existing triangulationReport, field will ignore if it is null
     *
     * @param id the id of the triangulationReport to save.
     * @param triangulationReport the triangulationReport to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated triangulationReport,
     * or with status {@code 400 (Bad Request)} if the triangulationReport is not valid,
     * or with status {@code 404 (Not Found)} if the triangulationReport is not found,
     * or with status {@code 500 (Internal Server Error)} if the triangulationReport couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/triangulation-reports/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TriangulationReport> partialUpdateTriangulationReport(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TriangulationReport triangulationReport
    ) throws URISyntaxException {
        log.debug("REST request to partial update TriangulationReport partially : {}, {}", id, triangulationReport);
        if (triangulationReport.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, triangulationReport.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!triangulationReportRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TriangulationReport> result = triangulationReportService.partialUpdate(triangulationReport);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, triangulationReport.getId().toString())
        );
    }

    /**
     * {@code GET  /triangulation-reports} : get all the triangulationReports.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of triangulationReports in body.
     */
    @GetMapping("/triangulation-reports")
    public ResponseEntity<List<TriangulationReport>> getAllTriangulationReports(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of TriangulationReports");
        Page<TriangulationReport> page = triangulationReportService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /triangulation-reports/:id} : get the "id" triangulationReport.
     *
     * @param id the id of the triangulationReport to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the triangulationReport, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/triangulation-reports/{id}")
    public ResponseEntity<TriangulationReport> getTriangulationReport(@PathVariable Long id) {
        log.debug("REST request to get TriangulationReport : {}", id);
        Optional<TriangulationReport> triangulationReport = triangulationReportService.findOne(id);
        return ResponseUtil.wrapOrNotFound(triangulationReport);
    }

    /**
     * {@code DELETE  /triangulation-reports/:id} : delete the "id" triangulationReport.
     *
     * @param id the id of the triangulationReport to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/triangulation-reports/{id}")
    public ResponseEntity<Void> deleteTriangulationReport(@PathVariable Long id) {
        log.debug("REST request to delete TriangulationReport : {}", id);
        triangulationReportService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
