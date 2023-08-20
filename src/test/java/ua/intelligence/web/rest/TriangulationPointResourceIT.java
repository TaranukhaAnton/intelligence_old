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
import ua.intelligence.domain.TriangulationPoint;
import ua.intelligence.repository.TriangulationPointRepository;

/**
 * Integration tests for the {@link TriangulationPointResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TriangulationPointResourceIT {

    private static final String DEFAULT_FREQUENCY = "AAAAAAAAAA";
    private static final String UPDATED_FREQUENCY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Float DEFAULT_LATITUDE = 1F;
    private static final Float UPDATED_LATITUDE = 2F;

    private static final Float DEFAULT_LONGITUDE = 1F;
    private static final Float UPDATED_LONGITUDE = 2F;

    private static final String ENTITY_API_URL = "/api/triangulation-points";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TriangulationPointRepository triangulationPointRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTriangulationPointMockMvc;

    private TriangulationPoint triangulationPoint;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TriangulationPoint createEntity(EntityManager em) {
        TriangulationPoint triangulationPoint = new TriangulationPoint()
            .frequency(DEFAULT_FREQUENCY)
            .date(DEFAULT_DATE)
            .description(DEFAULT_DESCRIPTION)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE);
        return triangulationPoint;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TriangulationPoint createUpdatedEntity(EntityManager em) {
        TriangulationPoint triangulationPoint = new TriangulationPoint()
            .frequency(UPDATED_FREQUENCY)
            .date(UPDATED_DATE)
            .description(UPDATED_DESCRIPTION)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);
        return triangulationPoint;
    }

    @BeforeEach
    public void initTest() {
        triangulationPoint = createEntity(em);
    }

    @Test
    @Transactional
    void createTriangulationPoint() throws Exception {
        int databaseSizeBeforeCreate = triangulationPointRepository.findAll().size();
        // Create the TriangulationPoint
        restTriangulationPointMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isCreated());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeCreate + 1);
        TriangulationPoint testTriangulationPoint = triangulationPointList.get(triangulationPointList.size() - 1);
        assertThat(testTriangulationPoint.getFrequency()).isEqualTo(DEFAULT_FREQUENCY);
        assertThat(testTriangulationPoint.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testTriangulationPoint.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTriangulationPoint.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testTriangulationPoint.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void createTriangulationPointWithExistingId() throws Exception {
        // Create the TriangulationPoint with an existing ID
        triangulationPoint.setId(1L);

        int databaseSizeBeforeCreate = triangulationPointRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTriangulationPointMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isBadRequest());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFrequencyIsRequired() throws Exception {
        int databaseSizeBeforeTest = triangulationPointRepository.findAll().size();
        // set the field null
        triangulationPoint.setFrequency(null);

        // Create the TriangulationPoint, which fails.

        restTriangulationPointMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isBadRequest());

        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLatitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = triangulationPointRepository.findAll().size();
        // set the field null
        triangulationPoint.setLatitude(null);

        // Create the TriangulationPoint, which fails.

        restTriangulationPointMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isBadRequest());

        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLongitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = triangulationPointRepository.findAll().size();
        // set the field null
        triangulationPoint.setLongitude(null);

        // Create the TriangulationPoint, which fails.

        restTriangulationPointMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isBadRequest());

        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTriangulationPoints() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList
        restTriangulationPointMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(triangulationPoint.getId().intValue())))
            .andExpect(jsonPath("$.[*].frequency").value(hasItem(DEFAULT_FREQUENCY)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())));
    }

    @Test
    @Transactional
    void getTriangulationPoint() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get the triangulationPoint
        restTriangulationPointMockMvc
            .perform(get(ENTITY_API_URL_ID, triangulationPoint.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(triangulationPoint.getId().intValue()))
            .andExpect(jsonPath("$.frequency").value(DEFAULT_FREQUENCY))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingTriangulationPoint() throws Exception {
        // Get the triangulationPoint
        restTriangulationPointMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTriangulationPoint() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        int databaseSizeBeforeUpdate = triangulationPointRepository.findAll().size();

        // Update the triangulationPoint
        TriangulationPoint updatedTriangulationPoint = triangulationPointRepository.findById(triangulationPoint.getId()).get();
        // Disconnect from session so that the updates on updatedTriangulationPoint are not directly saved in db
        em.detach(updatedTriangulationPoint);
        updatedTriangulationPoint
            .frequency(UPDATED_FREQUENCY)
            .date(UPDATED_DATE)
            .description(UPDATED_DESCRIPTION)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);

        restTriangulationPointMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTriangulationPoint.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTriangulationPoint))
            )
            .andExpect(status().isOk());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeUpdate);
        TriangulationPoint testTriangulationPoint = triangulationPointList.get(triangulationPointList.size() - 1);
        assertThat(testTriangulationPoint.getFrequency()).isEqualTo(UPDATED_FREQUENCY);
        assertThat(testTriangulationPoint.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testTriangulationPoint.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTriangulationPoint.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testTriangulationPoint.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void putNonExistingTriangulationPoint() throws Exception {
        int databaseSizeBeforeUpdate = triangulationPointRepository.findAll().size();
        triangulationPoint.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTriangulationPointMockMvc
            .perform(
                put(ENTITY_API_URL_ID, triangulationPoint.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isBadRequest());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTriangulationPoint() throws Exception {
        int databaseSizeBeforeUpdate = triangulationPointRepository.findAll().size();
        triangulationPoint.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTriangulationPointMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isBadRequest());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTriangulationPoint() throws Exception {
        int databaseSizeBeforeUpdate = triangulationPointRepository.findAll().size();
        triangulationPoint.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTriangulationPointMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTriangulationPointWithPatch() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        int databaseSizeBeforeUpdate = triangulationPointRepository.findAll().size();

        // Update the triangulationPoint using partial update
        TriangulationPoint partialUpdatedTriangulationPoint = new TriangulationPoint();
        partialUpdatedTriangulationPoint.setId(triangulationPoint.getId());

        partialUpdatedTriangulationPoint.description(UPDATED_DESCRIPTION).latitude(UPDATED_LATITUDE).longitude(UPDATED_LONGITUDE);

        restTriangulationPointMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTriangulationPoint.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTriangulationPoint))
            )
            .andExpect(status().isOk());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeUpdate);
        TriangulationPoint testTriangulationPoint = triangulationPointList.get(triangulationPointList.size() - 1);
        assertThat(testTriangulationPoint.getFrequency()).isEqualTo(DEFAULT_FREQUENCY);
        assertThat(testTriangulationPoint.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testTriangulationPoint.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTriangulationPoint.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testTriangulationPoint.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void fullUpdateTriangulationPointWithPatch() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        int databaseSizeBeforeUpdate = triangulationPointRepository.findAll().size();

        // Update the triangulationPoint using partial update
        TriangulationPoint partialUpdatedTriangulationPoint = new TriangulationPoint();
        partialUpdatedTriangulationPoint.setId(triangulationPoint.getId());

        partialUpdatedTriangulationPoint
            .frequency(UPDATED_FREQUENCY)
            .date(UPDATED_DATE)
            .description(UPDATED_DESCRIPTION)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE);

        restTriangulationPointMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTriangulationPoint.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTriangulationPoint))
            )
            .andExpect(status().isOk());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeUpdate);
        TriangulationPoint testTriangulationPoint = triangulationPointList.get(triangulationPointList.size() - 1);
        assertThat(testTriangulationPoint.getFrequency()).isEqualTo(UPDATED_FREQUENCY);
        assertThat(testTriangulationPoint.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testTriangulationPoint.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTriangulationPoint.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testTriangulationPoint.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void patchNonExistingTriangulationPoint() throws Exception {
        int databaseSizeBeforeUpdate = triangulationPointRepository.findAll().size();
        triangulationPoint.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTriangulationPointMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, triangulationPoint.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isBadRequest());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTriangulationPoint() throws Exception {
        int databaseSizeBeforeUpdate = triangulationPointRepository.findAll().size();
        triangulationPoint.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTriangulationPointMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isBadRequest());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTriangulationPoint() throws Exception {
        int databaseSizeBeforeUpdate = triangulationPointRepository.findAll().size();
        triangulationPoint.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTriangulationPointMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTriangulationPoint() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        int databaseSizeBeforeDelete = triangulationPointRepository.findAll().size();

        // Delete the triangulationPoint
        restTriangulationPointMockMvc
            .perform(delete(ENTITY_API_URL_ID, triangulationPoint.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
