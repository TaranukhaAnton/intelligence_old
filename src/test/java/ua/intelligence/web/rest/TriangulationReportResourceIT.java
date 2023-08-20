package ua.intelligence.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.intelligence.web.rest.TestUtil.sameInstant;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ua.intelligence.IntegrationTest;
import ua.intelligence.domain.TriangulationReport;
import ua.intelligence.repository.TriangulationReportRepository;

/**
 * Integration tests for the {@link TriangulationReportResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TriangulationReportResourceIT {

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CONCLUSION = "AAAAAAAAAA";
    private static final String UPDATED_CONCLUSION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/triangulation-reports";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TriangulationReportRepository triangulationReportRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTriangulationReportMockMvc;

    private TriangulationReport triangulationReport;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TriangulationReport createEntity(EntityManager em) {
        TriangulationReport triangulationReport = new TriangulationReport()
            .date(DEFAULT_DATE)
            .name(DEFAULT_NAME)
            .conclusion(DEFAULT_CONCLUSION);
        return triangulationReport;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TriangulationReport createUpdatedEntity(EntityManager em) {
        TriangulationReport triangulationReport = new TriangulationReport()
            .date(UPDATED_DATE)
            .name(UPDATED_NAME)
            .conclusion(UPDATED_CONCLUSION);
        return triangulationReport;
    }

    @BeforeEach
    public void initTest() {
        triangulationReport = createEntity(em);
    }

    @Test
    @Transactional
    void createTriangulationReport() throws Exception {
        int databaseSizeBeforeCreate = triangulationReportRepository.findAll().size();
        // Create the TriangulationReport
        restTriangulationReportMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(triangulationReport))
            )
            .andExpect(status().isCreated());

        // Validate the TriangulationReport in the database
        List<TriangulationReport> triangulationReportList = triangulationReportRepository.findAll();
        assertThat(triangulationReportList).hasSize(databaseSizeBeforeCreate + 1);
        TriangulationReport testTriangulationReport = triangulationReportList.get(triangulationReportList.size() - 1);
        assertThat(testTriangulationReport.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testTriangulationReport.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTriangulationReport.getConclusion()).isEqualTo(DEFAULT_CONCLUSION);
    }

    @Test
    @Transactional
    void createTriangulationReportWithExistingId() throws Exception {
        // Create the TriangulationReport with an existing ID
        triangulationReport.setId(1L);

        int databaseSizeBeforeCreate = triangulationReportRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTriangulationReportMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(triangulationReport))
            )
            .andExpect(status().isBadRequest());

        // Validate the TriangulationReport in the database
        List<TriangulationReport> triangulationReportList = triangulationReportRepository.findAll();
        assertThat(triangulationReportList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTriangulationReports() throws Exception {
        // Initialize the database
        triangulationReportRepository.saveAndFlush(triangulationReport);

        // Get all the triangulationReportList
        restTriangulationReportMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(triangulationReport.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].conclusion").value(hasItem(DEFAULT_CONCLUSION)));
    }

    @Test
    @Transactional
    void getTriangulationReport() throws Exception {
        // Initialize the database
        triangulationReportRepository.saveAndFlush(triangulationReport);

        // Get the triangulationReport
        restTriangulationReportMockMvc
            .perform(get(ENTITY_API_URL_ID, triangulationReport.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(triangulationReport.getId().intValue()))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.conclusion").value(DEFAULT_CONCLUSION));
    }

    @Test
    @Transactional
    void getNonExistingTriangulationReport() throws Exception {
        // Get the triangulationReport
        restTriangulationReportMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTriangulationReport() throws Exception {
        // Initialize the database
        triangulationReportRepository.saveAndFlush(triangulationReport);

        int databaseSizeBeforeUpdate = triangulationReportRepository.findAll().size();

        // Update the triangulationReport
        TriangulationReport updatedTriangulationReport = triangulationReportRepository.findById(triangulationReport.getId()).get();
        // Disconnect from session so that the updates on updatedTriangulationReport are not directly saved in db
        em.detach(updatedTriangulationReport);
        updatedTriangulationReport.date(UPDATED_DATE).name(UPDATED_NAME).conclusion(UPDATED_CONCLUSION);

        restTriangulationReportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTriangulationReport.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTriangulationReport))
            )
            .andExpect(status().isOk());

        // Validate the TriangulationReport in the database
        List<TriangulationReport> triangulationReportList = triangulationReportRepository.findAll();
        assertThat(triangulationReportList).hasSize(databaseSizeBeforeUpdate);
        TriangulationReport testTriangulationReport = triangulationReportList.get(triangulationReportList.size() - 1);
        assertThat(testTriangulationReport.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testTriangulationReport.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTriangulationReport.getConclusion()).isEqualTo(UPDATED_CONCLUSION);
    }

    @Test
    @Transactional
    void putNonExistingTriangulationReport() throws Exception {
        int databaseSizeBeforeUpdate = triangulationReportRepository.findAll().size();
        triangulationReport.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTriangulationReportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, triangulationReport.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(triangulationReport))
            )
            .andExpect(status().isBadRequest());

        // Validate the TriangulationReport in the database
        List<TriangulationReport> triangulationReportList = triangulationReportRepository.findAll();
        assertThat(triangulationReportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTriangulationReport() throws Exception {
        int databaseSizeBeforeUpdate = triangulationReportRepository.findAll().size();
        triangulationReport.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTriangulationReportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(triangulationReport))
            )
            .andExpect(status().isBadRequest());

        // Validate the TriangulationReport in the database
        List<TriangulationReport> triangulationReportList = triangulationReportRepository.findAll();
        assertThat(triangulationReportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTriangulationReport() throws Exception {
        int databaseSizeBeforeUpdate = triangulationReportRepository.findAll().size();
        triangulationReport.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTriangulationReportMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(triangulationReport))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TriangulationReport in the database
        List<TriangulationReport> triangulationReportList = triangulationReportRepository.findAll();
        assertThat(triangulationReportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTriangulationReportWithPatch() throws Exception {
        // Initialize the database
        triangulationReportRepository.saveAndFlush(triangulationReport);

        int databaseSizeBeforeUpdate = triangulationReportRepository.findAll().size();

        // Update the triangulationReport using partial update
        TriangulationReport partialUpdatedTriangulationReport = new TriangulationReport();
        partialUpdatedTriangulationReport.setId(triangulationReport.getId());

        partialUpdatedTriangulationReport.date(UPDATED_DATE).name(UPDATED_NAME);

        restTriangulationReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTriangulationReport.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTriangulationReport))
            )
            .andExpect(status().isOk());

        // Validate the TriangulationReport in the database
        List<TriangulationReport> triangulationReportList = triangulationReportRepository.findAll();
        assertThat(triangulationReportList).hasSize(databaseSizeBeforeUpdate);
        TriangulationReport testTriangulationReport = triangulationReportList.get(triangulationReportList.size() - 1);
        assertThat(testTriangulationReport.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testTriangulationReport.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTriangulationReport.getConclusion()).isEqualTo(DEFAULT_CONCLUSION);
    }

    @Test
    @Transactional
    void fullUpdateTriangulationReportWithPatch() throws Exception {
        // Initialize the database
        triangulationReportRepository.saveAndFlush(triangulationReport);

        int databaseSizeBeforeUpdate = triangulationReportRepository.findAll().size();

        // Update the triangulationReport using partial update
        TriangulationReport partialUpdatedTriangulationReport = new TriangulationReport();
        partialUpdatedTriangulationReport.setId(triangulationReport.getId());

        partialUpdatedTriangulationReport.date(UPDATED_DATE).name(UPDATED_NAME).conclusion(UPDATED_CONCLUSION);

        restTriangulationReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTriangulationReport.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTriangulationReport))
            )
            .andExpect(status().isOk());

        // Validate the TriangulationReport in the database
        List<TriangulationReport> triangulationReportList = triangulationReportRepository.findAll();
        assertThat(triangulationReportList).hasSize(databaseSizeBeforeUpdate);
        TriangulationReport testTriangulationReport = triangulationReportList.get(triangulationReportList.size() - 1);
        assertThat(testTriangulationReport.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testTriangulationReport.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTriangulationReport.getConclusion()).isEqualTo(UPDATED_CONCLUSION);
    }

    @Test
    @Transactional
    void patchNonExistingTriangulationReport() throws Exception {
        int databaseSizeBeforeUpdate = triangulationReportRepository.findAll().size();
        triangulationReport.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTriangulationReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, triangulationReport.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(triangulationReport))
            )
            .andExpect(status().isBadRequest());

        // Validate the TriangulationReport in the database
        List<TriangulationReport> triangulationReportList = triangulationReportRepository.findAll();
        assertThat(triangulationReportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTriangulationReport() throws Exception {
        int databaseSizeBeforeUpdate = triangulationReportRepository.findAll().size();
        triangulationReport.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTriangulationReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(triangulationReport))
            )
            .andExpect(status().isBadRequest());

        // Validate the TriangulationReport in the database
        List<TriangulationReport> triangulationReportList = triangulationReportRepository.findAll();
        assertThat(triangulationReportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTriangulationReport() throws Exception {
        int databaseSizeBeforeUpdate = triangulationReportRepository.findAll().size();
        triangulationReport.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTriangulationReportMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(triangulationReport))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TriangulationReport in the database
        List<TriangulationReport> triangulationReportList = triangulationReportRepository.findAll();
        assertThat(triangulationReportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTriangulationReport() throws Exception {
        // Initialize the database
        triangulationReportRepository.saveAndFlush(triangulationReport);

        int databaseSizeBeforeDelete = triangulationReportRepository.findAll().size();

        // Delete the triangulationReport
        restTriangulationReportMockMvc
            .perform(delete(ENTITY_API_URL_ID, triangulationReport.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TriangulationReport> triangulationReportList = triangulationReportRepository.findAll();
        assertThat(triangulationReportList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
