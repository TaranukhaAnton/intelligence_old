package ua.intelligence.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
import ua.intelligence.domain.TriangulationPoint;
import ua.intelligence.repository.TriangulationPointRepository;
import ua.intelligence.service.TriangulationPointService;
import ua.intelligence.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link ua.intelligence.domain.TriangulationPoint}.
 */
@RestController
@RequestMapping("/api")
public class TriangulationPointResource {

    private final Logger log = LoggerFactory.getLogger(TriangulationPointResource.class);

    private static final String ENTITY_NAME = "triangulationPoint";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TriangulationPointService triangulationPointService;

    private final TriangulationPointRepository triangulationPointRepository;

    public TriangulationPointResource(
        TriangulationPointService triangulationPointService,
        TriangulationPointRepository triangulationPointRepository
    ) {
        this.triangulationPointService = triangulationPointService;
        this.triangulationPointRepository = triangulationPointRepository;
    }

    /**
     * {@code POST  /triangulation-points} : Create a new triangulationPoint.
     *
     * @param triangulationPoint the triangulationPoint to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new triangulationPoint, or with status {@code 400 (Bad Request)} if the triangulationPoint has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/triangulation-points")
    public ResponseEntity<TriangulationPoint> createTriangulationPoint(@Valid @RequestBody TriangulationPoint triangulationPoint)
        throws URISyntaxException {
        log.debug("REST request to save TriangulationPoint : {}", triangulationPoint);
        if (triangulationPoint.getId() != null) {
            throw new BadRequestAlertException("A new triangulationPoint cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TriangulationPoint result = triangulationPointService.save(triangulationPoint);
        return ResponseEntity
            .created(new URI("/api/triangulation-points/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /triangulation-points/:id} : Updates an existing triangulationPoint.
     *
     * @param id the id of the triangulationPoint to save.
     * @param triangulationPoint the triangulationPoint to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated triangulationPoint,
     * or with status {@code 400 (Bad Request)} if the triangulationPoint is not valid,
     * or with status {@code 500 (Internal Server Error)} if the triangulationPoint couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/triangulation-points/{id}")
    public ResponseEntity<TriangulationPoint> updateTriangulationPoint(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TriangulationPoint triangulationPoint
    ) throws URISyntaxException {
        log.debug("REST request to update TriangulationPoint : {}, {}", id, triangulationPoint);
        if (triangulationPoint.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, triangulationPoint.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!triangulationPointRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TriangulationPoint result = triangulationPointService.update(triangulationPoint);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, triangulationPoint.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /triangulation-points/:id} : Partial updates given fields of an existing triangulationPoint, field will ignore if it is null
     *
     * @param id the id of the triangulationPoint to save.
     * @param triangulationPoint the triangulationPoint to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated triangulationPoint,
     * or with status {@code 400 (Bad Request)} if the triangulationPoint is not valid,
     * or with status {@code 404 (Not Found)} if the triangulationPoint is not found,
     * or with status {@code 500 (Internal Server Error)} if the triangulationPoint couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/triangulation-points/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TriangulationPoint> partialUpdateTriangulationPoint(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TriangulationPoint triangulationPoint
    ) throws URISyntaxException {
        log.debug("REST request to partial update TriangulationPoint partially : {}, {}", id, triangulationPoint);
        if (triangulationPoint.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, triangulationPoint.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!triangulationPointRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TriangulationPoint> result = triangulationPointService.partialUpdate(triangulationPoint);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, triangulationPoint.getId().toString())
        );
    }

    /**
     * {@code GET  /triangulation-points} : get all the triangulationPoints.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of triangulationPoints in body.
     */
    @GetMapping("/triangulation-points")
    public ResponseEntity<List<TriangulationPoint>> getAllTriangulationPoints(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of TriangulationPoints");
        Page<TriangulationPoint> page = triangulationPointService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /triangulation-points/:id} : get the "id" triangulationPoint.
     *
     * @param id the id of the triangulationPoint to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the triangulationPoint, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/triangulation-points/{id}")
    public ResponseEntity<TriangulationPoint> getTriangulationPoint(@PathVariable Long id) {
        log.debug("REST request to get TriangulationPoint : {}", id);
        Optional<TriangulationPoint> triangulationPoint = triangulationPointService.findOne(id);
        return ResponseUtil.wrapOrNotFound(triangulationPoint);
    }

    /**
     * {@code DELETE  /triangulation-points/:id} : delete the "id" triangulationPoint.
     *
     * @param id the id of the triangulationPoint to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/triangulation-points/{id}")
    public ResponseEntity<Void> deleteTriangulationPoint(@PathVariable Long id) {
        log.debug("REST request to delete TriangulationPoint : {}", id);
        triangulationPointService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
