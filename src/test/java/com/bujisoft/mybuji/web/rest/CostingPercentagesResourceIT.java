package com.bujisoft.mybuji.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bujisoft.mybuji.IntegrationTest;
import com.bujisoft.mybuji.domain.CostingPercentages;
import com.bujisoft.mybuji.repository.CostingPercentagesRepository;
import com.bujisoft.mybuji.repository.search.CostingPercentagesSearchRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CostingPercentagesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CostingPercentagesResourceIT {

    private static final Integer DEFAULT_COSTING_SYSTEM = 1;
    private static final Integer UPDATED_COSTING_SYSTEM = 2;

    private static final Integer DEFAULT_COSTING_QUAL = 1;
    private static final Integer UPDATED_COSTING_QUAL = 2;

    private static final Integer DEFAULT_COSTING_IMP = 1;
    private static final Integer UPDATED_COSTING_IMP = 2;

    private static final Integer DEFAULT_COSTING_POST_IMP = 1;
    private static final Integer UPDATED_COSTING_POST_IMP = 2;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Instant DEFAULT_DATE_ADDED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_ADDED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/costing-percentages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/costing-percentages";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CostingPercentagesRepository costingPercentagesRepository;

    @Autowired
    private CostingPercentagesSearchRepository costingPercentagesSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCostingPercentagesMockMvc;

    private CostingPercentages costingPercentages;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CostingPercentages createEntity(EntityManager em) {
        CostingPercentages costingPercentages = new CostingPercentages()
            .costingSystem(DEFAULT_COSTING_SYSTEM)
            .costingQual(DEFAULT_COSTING_QUAL)
            .costingImp(DEFAULT_COSTING_IMP)
            .costingPostImp(DEFAULT_COSTING_POST_IMP)
            .active(DEFAULT_ACTIVE)
            .dateAdded(DEFAULT_DATE_ADDED);
        return costingPercentages;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CostingPercentages createUpdatedEntity(EntityManager em) {
        CostingPercentages costingPercentages = new CostingPercentages()
            .costingSystem(UPDATED_COSTING_SYSTEM)
            .costingQual(UPDATED_COSTING_QUAL)
            .costingImp(UPDATED_COSTING_IMP)
            .costingPostImp(UPDATED_COSTING_POST_IMP)
            .active(UPDATED_ACTIVE)
            .dateAdded(UPDATED_DATE_ADDED);
        return costingPercentages;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        costingPercentagesSearchRepository.deleteAll();
        assertThat(costingPercentagesSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        costingPercentages = createEntity(em);
    }

    @Test
    @Transactional
    void createCostingPercentages() throws Exception {
        int databaseSizeBeforeCreate = costingPercentagesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        // Create the CostingPercentages
        restCostingPercentagesMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(costingPercentages))
            )
            .andExpect(status().isCreated());

        // Validate the CostingPercentages in the database
        List<CostingPercentages> costingPercentagesList = costingPercentagesRepository.findAll();
        assertThat(costingPercentagesList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        CostingPercentages testCostingPercentages = costingPercentagesList.get(costingPercentagesList.size() - 1);
        assertThat(testCostingPercentages.getCostingSystem()).isEqualTo(DEFAULT_COSTING_SYSTEM);
        assertThat(testCostingPercentages.getCostingQual()).isEqualTo(DEFAULT_COSTING_QUAL);
        assertThat(testCostingPercentages.getCostingImp()).isEqualTo(DEFAULT_COSTING_IMP);
        assertThat(testCostingPercentages.getCostingPostImp()).isEqualTo(DEFAULT_COSTING_POST_IMP);
        assertThat(testCostingPercentages.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testCostingPercentages.getDateAdded()).isEqualTo(DEFAULT_DATE_ADDED);
    }

    @Test
    @Transactional
    void createCostingPercentagesWithExistingId() throws Exception {
        // Create the CostingPercentages with an existing ID
        costingPercentages.setId(1L);

        int databaseSizeBeforeCreate = costingPercentagesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCostingPercentagesMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(costingPercentages))
            )
            .andExpect(status().isBadRequest());

        // Validate the CostingPercentages in the database
        List<CostingPercentages> costingPercentagesList = costingPercentagesRepository.findAll();
        assertThat(costingPercentagesList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCostingSystemIsRequired() throws Exception {
        int databaseSizeBeforeTest = costingPercentagesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        // set the field null
        costingPercentages.setCostingSystem(null);

        // Create the CostingPercentages, which fails.

        restCostingPercentagesMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(costingPercentages))
            )
            .andExpect(status().isBadRequest());

        List<CostingPercentages> costingPercentagesList = costingPercentagesRepository.findAll();
        assertThat(costingPercentagesList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCostingQualIsRequired() throws Exception {
        int databaseSizeBeforeTest = costingPercentagesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        // set the field null
        costingPercentages.setCostingQual(null);

        // Create the CostingPercentages, which fails.

        restCostingPercentagesMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(costingPercentages))
            )
            .andExpect(status().isBadRequest());

        List<CostingPercentages> costingPercentagesList = costingPercentagesRepository.findAll();
        assertThat(costingPercentagesList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCostingImpIsRequired() throws Exception {
        int databaseSizeBeforeTest = costingPercentagesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        // set the field null
        costingPercentages.setCostingImp(null);

        // Create the CostingPercentages, which fails.

        restCostingPercentagesMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(costingPercentages))
            )
            .andExpect(status().isBadRequest());

        List<CostingPercentages> costingPercentagesList = costingPercentagesRepository.findAll();
        assertThat(costingPercentagesList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCostingPostImpIsRequired() throws Exception {
        int databaseSizeBeforeTest = costingPercentagesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        // set the field null
        costingPercentages.setCostingPostImp(null);

        // Create the CostingPercentages, which fails.

        restCostingPercentagesMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(costingPercentages))
            )
            .andExpect(status().isBadRequest());

        List<CostingPercentages> costingPercentagesList = costingPercentagesRepository.findAll();
        assertThat(costingPercentagesList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDateAddedIsRequired() throws Exception {
        int databaseSizeBeforeTest = costingPercentagesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        // set the field null
        costingPercentages.setDateAdded(null);

        // Create the CostingPercentages, which fails.

        restCostingPercentagesMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(costingPercentages))
            )
            .andExpect(status().isBadRequest());

        List<CostingPercentages> costingPercentagesList = costingPercentagesRepository.findAll();
        assertThat(costingPercentagesList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCostingPercentages() throws Exception {
        // Initialize the database
        costingPercentagesRepository.saveAndFlush(costingPercentages);

        // Get all the costingPercentagesList
        restCostingPercentagesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(costingPercentages.getId().intValue())))
            .andExpect(jsonPath("$.[*].costingSystem").value(hasItem(DEFAULT_COSTING_SYSTEM)))
            .andExpect(jsonPath("$.[*].costingQual").value(hasItem(DEFAULT_COSTING_QUAL)))
            .andExpect(jsonPath("$.[*].costingImp").value(hasItem(DEFAULT_COSTING_IMP)))
            .andExpect(jsonPath("$.[*].costingPostImp").value(hasItem(DEFAULT_COSTING_POST_IMP)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].dateAdded").value(hasItem(DEFAULT_DATE_ADDED.toString())));
    }

    @Test
    @Transactional
    void getCostingPercentages() throws Exception {
        // Initialize the database
        costingPercentagesRepository.saveAndFlush(costingPercentages);

        // Get the costingPercentages
        restCostingPercentagesMockMvc
            .perform(get(ENTITY_API_URL_ID, costingPercentages.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(costingPercentages.getId().intValue()))
            .andExpect(jsonPath("$.costingSystem").value(DEFAULT_COSTING_SYSTEM))
            .andExpect(jsonPath("$.costingQual").value(DEFAULT_COSTING_QUAL))
            .andExpect(jsonPath("$.costingImp").value(DEFAULT_COSTING_IMP))
            .andExpect(jsonPath("$.costingPostImp").value(DEFAULT_COSTING_POST_IMP))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.dateAdded").value(DEFAULT_DATE_ADDED.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCostingPercentages() throws Exception {
        // Get the costingPercentages
        restCostingPercentagesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCostingPercentages() throws Exception {
        // Initialize the database
        costingPercentagesRepository.saveAndFlush(costingPercentages);

        int databaseSizeBeforeUpdate = costingPercentagesRepository.findAll().size();
        costingPercentagesSearchRepository.save(costingPercentages);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());

        // Update the costingPercentages
        CostingPercentages updatedCostingPercentages = costingPercentagesRepository.findById(costingPercentages.getId()).get();
        // Disconnect from session so that the updates on updatedCostingPercentages are not directly saved in db
        em.detach(updatedCostingPercentages);
        updatedCostingPercentages
            .costingSystem(UPDATED_COSTING_SYSTEM)
            .costingQual(UPDATED_COSTING_QUAL)
            .costingImp(UPDATED_COSTING_IMP)
            .costingPostImp(UPDATED_COSTING_POST_IMP)
            .active(UPDATED_ACTIVE)
            .dateAdded(UPDATED_DATE_ADDED);

        restCostingPercentagesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCostingPercentages.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCostingPercentages))
            )
            .andExpect(status().isOk());

        // Validate the CostingPercentages in the database
        List<CostingPercentages> costingPercentagesList = costingPercentagesRepository.findAll();
        assertThat(costingPercentagesList).hasSize(databaseSizeBeforeUpdate);
        CostingPercentages testCostingPercentages = costingPercentagesList.get(costingPercentagesList.size() - 1);
        assertThat(testCostingPercentages.getCostingSystem()).isEqualTo(UPDATED_COSTING_SYSTEM);
        assertThat(testCostingPercentages.getCostingQual()).isEqualTo(UPDATED_COSTING_QUAL);
        assertThat(testCostingPercentages.getCostingImp()).isEqualTo(UPDATED_COSTING_IMP);
        assertThat(testCostingPercentages.getCostingPostImp()).isEqualTo(UPDATED_COSTING_POST_IMP);
        assertThat(testCostingPercentages.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testCostingPercentages.getDateAdded()).isEqualTo(UPDATED_DATE_ADDED);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<CostingPercentages> costingPercentagesSearchList = IterableUtils.toList(costingPercentagesSearchRepository.findAll());
                CostingPercentages testCostingPercentagesSearch = costingPercentagesSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testCostingPercentagesSearch.getCostingSystem()).isEqualTo(UPDATED_COSTING_SYSTEM);
                assertThat(testCostingPercentagesSearch.getCostingQual()).isEqualTo(UPDATED_COSTING_QUAL);
                assertThat(testCostingPercentagesSearch.getCostingImp()).isEqualTo(UPDATED_COSTING_IMP);
                assertThat(testCostingPercentagesSearch.getCostingPostImp()).isEqualTo(UPDATED_COSTING_POST_IMP);
                assertThat(testCostingPercentagesSearch.getActive()).isEqualTo(UPDATED_ACTIVE);
                assertThat(testCostingPercentagesSearch.getDateAdded()).isEqualTo(UPDATED_DATE_ADDED);
            });
    }

    @Test
    @Transactional
    void putNonExistingCostingPercentages() throws Exception {
        int databaseSizeBeforeUpdate = costingPercentagesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        costingPercentages.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCostingPercentagesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, costingPercentages.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(costingPercentages))
            )
            .andExpect(status().isBadRequest());

        // Validate the CostingPercentages in the database
        List<CostingPercentages> costingPercentagesList = costingPercentagesRepository.findAll();
        assertThat(costingPercentagesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCostingPercentages() throws Exception {
        int databaseSizeBeforeUpdate = costingPercentagesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        costingPercentages.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCostingPercentagesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(costingPercentages))
            )
            .andExpect(status().isBadRequest());

        // Validate the CostingPercentages in the database
        List<CostingPercentages> costingPercentagesList = costingPercentagesRepository.findAll();
        assertThat(costingPercentagesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCostingPercentages() throws Exception {
        int databaseSizeBeforeUpdate = costingPercentagesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        costingPercentages.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCostingPercentagesMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(costingPercentages))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CostingPercentages in the database
        List<CostingPercentages> costingPercentagesList = costingPercentagesRepository.findAll();
        assertThat(costingPercentagesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCostingPercentagesWithPatch() throws Exception {
        // Initialize the database
        costingPercentagesRepository.saveAndFlush(costingPercentages);

        int databaseSizeBeforeUpdate = costingPercentagesRepository.findAll().size();

        // Update the costingPercentages using partial update
        CostingPercentages partialUpdatedCostingPercentages = new CostingPercentages();
        partialUpdatedCostingPercentages.setId(costingPercentages.getId());

        partialUpdatedCostingPercentages.costingSystem(UPDATED_COSTING_SYSTEM);

        restCostingPercentagesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCostingPercentages.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCostingPercentages))
            )
            .andExpect(status().isOk());

        // Validate the CostingPercentages in the database
        List<CostingPercentages> costingPercentagesList = costingPercentagesRepository.findAll();
        assertThat(costingPercentagesList).hasSize(databaseSizeBeforeUpdate);
        CostingPercentages testCostingPercentages = costingPercentagesList.get(costingPercentagesList.size() - 1);
        assertThat(testCostingPercentages.getCostingSystem()).isEqualTo(UPDATED_COSTING_SYSTEM);
        assertThat(testCostingPercentages.getCostingQual()).isEqualTo(DEFAULT_COSTING_QUAL);
        assertThat(testCostingPercentages.getCostingImp()).isEqualTo(DEFAULT_COSTING_IMP);
        assertThat(testCostingPercentages.getCostingPostImp()).isEqualTo(DEFAULT_COSTING_POST_IMP);
        assertThat(testCostingPercentages.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testCostingPercentages.getDateAdded()).isEqualTo(DEFAULT_DATE_ADDED);
    }

    @Test
    @Transactional
    void fullUpdateCostingPercentagesWithPatch() throws Exception {
        // Initialize the database
        costingPercentagesRepository.saveAndFlush(costingPercentages);

        int databaseSizeBeforeUpdate = costingPercentagesRepository.findAll().size();

        // Update the costingPercentages using partial update
        CostingPercentages partialUpdatedCostingPercentages = new CostingPercentages();
        partialUpdatedCostingPercentages.setId(costingPercentages.getId());

        partialUpdatedCostingPercentages
            .costingSystem(UPDATED_COSTING_SYSTEM)
            .costingQual(UPDATED_COSTING_QUAL)
            .costingImp(UPDATED_COSTING_IMP)
            .costingPostImp(UPDATED_COSTING_POST_IMP)
            .active(UPDATED_ACTIVE)
            .dateAdded(UPDATED_DATE_ADDED);

        restCostingPercentagesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCostingPercentages.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCostingPercentages))
            )
            .andExpect(status().isOk());

        // Validate the CostingPercentages in the database
        List<CostingPercentages> costingPercentagesList = costingPercentagesRepository.findAll();
        assertThat(costingPercentagesList).hasSize(databaseSizeBeforeUpdate);
        CostingPercentages testCostingPercentages = costingPercentagesList.get(costingPercentagesList.size() - 1);
        assertThat(testCostingPercentages.getCostingSystem()).isEqualTo(UPDATED_COSTING_SYSTEM);
        assertThat(testCostingPercentages.getCostingQual()).isEqualTo(UPDATED_COSTING_QUAL);
        assertThat(testCostingPercentages.getCostingImp()).isEqualTo(UPDATED_COSTING_IMP);
        assertThat(testCostingPercentages.getCostingPostImp()).isEqualTo(UPDATED_COSTING_POST_IMP);
        assertThat(testCostingPercentages.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testCostingPercentages.getDateAdded()).isEqualTo(UPDATED_DATE_ADDED);
    }

    @Test
    @Transactional
    void patchNonExistingCostingPercentages() throws Exception {
        int databaseSizeBeforeUpdate = costingPercentagesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        costingPercentages.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCostingPercentagesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, costingPercentages.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(costingPercentages))
            )
            .andExpect(status().isBadRequest());

        // Validate the CostingPercentages in the database
        List<CostingPercentages> costingPercentagesList = costingPercentagesRepository.findAll();
        assertThat(costingPercentagesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCostingPercentages() throws Exception {
        int databaseSizeBeforeUpdate = costingPercentagesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        costingPercentages.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCostingPercentagesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(costingPercentages))
            )
            .andExpect(status().isBadRequest());

        // Validate the CostingPercentages in the database
        List<CostingPercentages> costingPercentagesList = costingPercentagesRepository.findAll();
        assertThat(costingPercentagesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCostingPercentages() throws Exception {
        int databaseSizeBeforeUpdate = costingPercentagesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        costingPercentages.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCostingPercentagesMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(costingPercentages))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CostingPercentages in the database
        List<CostingPercentages> costingPercentagesList = costingPercentagesRepository.findAll();
        assertThat(costingPercentagesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCostingPercentages() throws Exception {
        // Initialize the database
        costingPercentagesRepository.saveAndFlush(costingPercentages);
        costingPercentagesRepository.save(costingPercentages);
        costingPercentagesSearchRepository.save(costingPercentages);

        int databaseSizeBeforeDelete = costingPercentagesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the costingPercentages
        restCostingPercentagesMockMvc
            .perform(delete(ENTITY_API_URL_ID, costingPercentages.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CostingPercentages> costingPercentagesList = costingPercentagesRepository.findAll();
        assertThat(costingPercentagesList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(costingPercentagesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCostingPercentages() throws Exception {
        // Initialize the database
        costingPercentages = costingPercentagesRepository.saveAndFlush(costingPercentages);
        costingPercentagesSearchRepository.save(costingPercentages);

        // Search the costingPercentages
        restCostingPercentagesMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + costingPercentages.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(costingPercentages.getId().intValue())))
            .andExpect(jsonPath("$.[*].costingSystem").value(hasItem(DEFAULT_COSTING_SYSTEM)))
            .andExpect(jsonPath("$.[*].costingQual").value(hasItem(DEFAULT_COSTING_QUAL)))
            .andExpect(jsonPath("$.[*].costingImp").value(hasItem(DEFAULT_COSTING_IMP)))
            .andExpect(jsonPath("$.[*].costingPostImp").value(hasItem(DEFAULT_COSTING_POST_IMP)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].dateAdded").value(hasItem(DEFAULT_DATE_ADDED.toString())));
    }
}
