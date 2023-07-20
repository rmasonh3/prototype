package com.bujisoft.mybuji.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bujisoft.mybuji.IntegrationTest;
import com.bujisoft.mybuji.domain.ScopeDesign;
import com.bujisoft.mybuji.domain.WorkRequest;
import com.bujisoft.mybuji.repository.ScopeDesignRepository;
import com.bujisoft.mybuji.repository.search.ScopeDesignSearchRepository;
import com.bujisoft.mybuji.service.ScopeDesignService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ScopeDesignResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ScopeDesignResourceIT {

    private static final Float DEFAULT_DESIGN_ESTIMATE = 1F;
    private static final Float UPDATED_DESIGN_ESTIMATE = 2F;

    private static final Float DEFAULT_CODE_ESTIMATE = 1F;
    private static final Float UPDATED_CODE_ESTIMATE = 2F;

    private static final Float DEFAULT_SYST_1_ESTIMATE = 1F;
    private static final Float UPDATED_SYST_1_ESTIMATE = 2F;

    private static final Float DEFAULT_SYST_2_ESTIMATE = 1F;
    private static final Float UPDATED_SYST_2_ESTIMATE = 2F;

    private static final Float DEFAULT_QUAL_ESTIMATE = 1F;
    private static final Float UPDATED_QUAL_ESTIMATE = 2F;

    private static final Float DEFAULT_IMP_ESTIMATE = 1F;
    private static final Float UPDATED_IMP_ESTIMATE = 2F;

    private static final Float DEFAULT_POST_IMP_ESTIMATE = 1F;
    private static final Float UPDATED_POST_IMP_ESTIMATE = 2F;

    private static final Float DEFAULT_TOTAL_HOURS = 1F;
    private static final Float UPDATED_TOTAL_HOURS = 2F;

    private static final String ENTITY_API_URL = "/api/scope-designs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/scope-designs";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ScopeDesignRepository scopeDesignRepository;

    @Mock
    private ScopeDesignRepository scopeDesignRepositoryMock;

    @Mock
    private ScopeDesignService scopeDesignServiceMock;

    @Autowired
    private ScopeDesignSearchRepository scopeDesignSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restScopeDesignMockMvc;

    private ScopeDesign scopeDesign;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScopeDesign createEntity(EntityManager em) {
        ScopeDesign scopeDesign = new ScopeDesign()
            .designEstimate(DEFAULT_DESIGN_ESTIMATE)
            .codeEstimate(DEFAULT_CODE_ESTIMATE)
            .syst1Estimate(DEFAULT_SYST_1_ESTIMATE)
            .syst2Estimate(DEFAULT_SYST_2_ESTIMATE)
            .qualEstimate(DEFAULT_QUAL_ESTIMATE)
            .impEstimate(DEFAULT_IMP_ESTIMATE)
            .postImpEstimate(DEFAULT_POST_IMP_ESTIMATE)
            .totalHours(DEFAULT_TOTAL_HOURS);
        // Add required entity
        WorkRequest workRequest;
        if (TestUtil.findAll(em, WorkRequest.class).isEmpty()) {
            workRequest = WorkRequestResourceIT.createEntity(em);
            em.persist(workRequest);
            em.flush();
        } else {
            workRequest = TestUtil.findAll(em, WorkRequest.class).get(0);
        }
        scopeDesign.setWorkrequest(workRequest);
        return scopeDesign;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScopeDesign createUpdatedEntity(EntityManager em) {
        ScopeDesign scopeDesign = new ScopeDesign()
            .designEstimate(UPDATED_DESIGN_ESTIMATE)
            .codeEstimate(UPDATED_CODE_ESTIMATE)
            .syst1Estimate(UPDATED_SYST_1_ESTIMATE)
            .syst2Estimate(UPDATED_SYST_2_ESTIMATE)
            .qualEstimate(UPDATED_QUAL_ESTIMATE)
            .impEstimate(UPDATED_IMP_ESTIMATE)
            .postImpEstimate(UPDATED_POST_IMP_ESTIMATE)
            .totalHours(UPDATED_TOTAL_HOURS);
        // Add required entity
        WorkRequest workRequest;
        if (TestUtil.findAll(em, WorkRequest.class).isEmpty()) {
            workRequest = WorkRequestResourceIT.createUpdatedEntity(em);
            em.persist(workRequest);
            em.flush();
        } else {
            workRequest = TestUtil.findAll(em, WorkRequest.class).get(0);
        }
        scopeDesign.setWorkrequest(workRequest);
        return scopeDesign;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        scopeDesignSearchRepository.deleteAll();
        assertThat(scopeDesignSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        scopeDesign = createEntity(em);
    }

    @Test
    @Transactional
    void createScopeDesign() throws Exception {
        int databaseSizeBeforeCreate = scopeDesignRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scopeDesignSearchRepository.findAll());
        // Create the ScopeDesign
        restScopeDesignMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(scopeDesign)))
            .andExpect(status().isCreated());

        // Validate the ScopeDesign in the database
        List<ScopeDesign> scopeDesignList = scopeDesignRepository.findAll();
        assertThat(scopeDesignList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(scopeDesignSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ScopeDesign testScopeDesign = scopeDesignList.get(scopeDesignList.size() - 1);
        assertThat(testScopeDesign.getDesignEstimate()).isEqualTo(DEFAULT_DESIGN_ESTIMATE);
        assertThat(testScopeDesign.getCodeEstimate()).isEqualTo(DEFAULT_CODE_ESTIMATE);
        assertThat(testScopeDesign.getSyst1Estimate()).isEqualTo(DEFAULT_SYST_1_ESTIMATE);
        assertThat(testScopeDesign.getSyst2Estimate()).isEqualTo(DEFAULT_SYST_2_ESTIMATE);
        assertThat(testScopeDesign.getQualEstimate()).isEqualTo(DEFAULT_QUAL_ESTIMATE);
        assertThat(testScopeDesign.getImpEstimate()).isEqualTo(DEFAULT_IMP_ESTIMATE);
        assertThat(testScopeDesign.getPostImpEstimate()).isEqualTo(DEFAULT_POST_IMP_ESTIMATE);
        assertThat(testScopeDesign.getTotalHours()).isEqualTo(DEFAULT_TOTAL_HOURS);
    }

    @Test
    @Transactional
    void createScopeDesignWithExistingId() throws Exception {
        // Create the ScopeDesign with an existing ID
        scopeDesign.setId(1L);

        int databaseSizeBeforeCreate = scopeDesignRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scopeDesignSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restScopeDesignMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(scopeDesign)))
            .andExpect(status().isBadRequest());

        // Validate the ScopeDesign in the database
        List<ScopeDesign> scopeDesignList = scopeDesignRepository.findAll();
        assertThat(scopeDesignList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(scopeDesignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllScopeDesigns() throws Exception {
        // Initialize the database
        scopeDesignRepository.saveAndFlush(scopeDesign);

        // Get all the scopeDesignList
        restScopeDesignMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scopeDesign.getId().intValue())))
            .andExpect(jsonPath("$.[*].designEstimate").value(hasItem(DEFAULT_DESIGN_ESTIMATE.doubleValue())))
            .andExpect(jsonPath("$.[*].codeEstimate").value(hasItem(DEFAULT_CODE_ESTIMATE.doubleValue())))
            .andExpect(jsonPath("$.[*].syst1Estimate").value(hasItem(DEFAULT_SYST_1_ESTIMATE.doubleValue())))
            .andExpect(jsonPath("$.[*].syst2Estimate").value(hasItem(DEFAULT_SYST_2_ESTIMATE.doubleValue())))
            .andExpect(jsonPath("$.[*].qualEstimate").value(hasItem(DEFAULT_QUAL_ESTIMATE.doubleValue())))
            .andExpect(jsonPath("$.[*].impEstimate").value(hasItem(DEFAULT_IMP_ESTIMATE.doubleValue())))
            .andExpect(jsonPath("$.[*].postImpEstimate").value(hasItem(DEFAULT_POST_IMP_ESTIMATE.doubleValue())))
            .andExpect(jsonPath("$.[*].totalHours").value(hasItem(DEFAULT_TOTAL_HOURS.doubleValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllScopeDesignsWithEagerRelationshipsIsEnabled() throws Exception {
        when(scopeDesignServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restScopeDesignMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(scopeDesignServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllScopeDesignsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(scopeDesignServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restScopeDesignMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(scopeDesignRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getScopeDesign() throws Exception {
        // Initialize the database
        scopeDesignRepository.saveAndFlush(scopeDesign);

        // Get the scopeDesign
        restScopeDesignMockMvc
            .perform(get(ENTITY_API_URL_ID, scopeDesign.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(scopeDesign.getId().intValue()))
            .andExpect(jsonPath("$.designEstimate").value(DEFAULT_DESIGN_ESTIMATE.doubleValue()))
            .andExpect(jsonPath("$.codeEstimate").value(DEFAULT_CODE_ESTIMATE.doubleValue()))
            .andExpect(jsonPath("$.syst1Estimate").value(DEFAULT_SYST_1_ESTIMATE.doubleValue()))
            .andExpect(jsonPath("$.syst2Estimate").value(DEFAULT_SYST_2_ESTIMATE.doubleValue()))
            .andExpect(jsonPath("$.qualEstimate").value(DEFAULT_QUAL_ESTIMATE.doubleValue()))
            .andExpect(jsonPath("$.impEstimate").value(DEFAULT_IMP_ESTIMATE.doubleValue()))
            .andExpect(jsonPath("$.postImpEstimate").value(DEFAULT_POST_IMP_ESTIMATE.doubleValue()))
            .andExpect(jsonPath("$.totalHours").value(DEFAULT_TOTAL_HOURS.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingScopeDesign() throws Exception {
        // Get the scopeDesign
        restScopeDesignMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingScopeDesign() throws Exception {
        // Initialize the database
        scopeDesignRepository.saveAndFlush(scopeDesign);

        int databaseSizeBeforeUpdate = scopeDesignRepository.findAll().size();
        scopeDesignSearchRepository.save(scopeDesign);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scopeDesignSearchRepository.findAll());

        // Update the scopeDesign
        ScopeDesign updatedScopeDesign = scopeDesignRepository.findById(scopeDesign.getId()).get();
        // Disconnect from session so that the updates on updatedScopeDesign are not directly saved in db
        em.detach(updatedScopeDesign);
        updatedScopeDesign
            .designEstimate(UPDATED_DESIGN_ESTIMATE)
            .codeEstimate(UPDATED_CODE_ESTIMATE)
            .syst1Estimate(UPDATED_SYST_1_ESTIMATE)
            .syst2Estimate(UPDATED_SYST_2_ESTIMATE)
            .qualEstimate(UPDATED_QUAL_ESTIMATE)
            .impEstimate(UPDATED_IMP_ESTIMATE)
            .postImpEstimate(UPDATED_POST_IMP_ESTIMATE)
            .totalHours(UPDATED_TOTAL_HOURS);

        restScopeDesignMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedScopeDesign.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedScopeDesign))
            )
            .andExpect(status().isOk());

        // Validate the ScopeDesign in the database
        List<ScopeDesign> scopeDesignList = scopeDesignRepository.findAll();
        assertThat(scopeDesignList).hasSize(databaseSizeBeforeUpdate);
        ScopeDesign testScopeDesign = scopeDesignList.get(scopeDesignList.size() - 1);
        assertThat(testScopeDesign.getDesignEstimate()).isEqualTo(UPDATED_DESIGN_ESTIMATE);
        assertThat(testScopeDesign.getCodeEstimate()).isEqualTo(UPDATED_CODE_ESTIMATE);
        assertThat(testScopeDesign.getSyst1Estimate()).isEqualTo(UPDATED_SYST_1_ESTIMATE);
        assertThat(testScopeDesign.getSyst2Estimate()).isEqualTo(UPDATED_SYST_2_ESTIMATE);
        assertThat(testScopeDesign.getQualEstimate()).isEqualTo(UPDATED_QUAL_ESTIMATE);
        assertThat(testScopeDesign.getImpEstimate()).isEqualTo(UPDATED_IMP_ESTIMATE);
        assertThat(testScopeDesign.getPostImpEstimate()).isEqualTo(UPDATED_POST_IMP_ESTIMATE);
        assertThat(testScopeDesign.getTotalHours()).isEqualTo(UPDATED_TOTAL_HOURS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(scopeDesignSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ScopeDesign> scopeDesignSearchList = IterableUtils.toList(scopeDesignSearchRepository.findAll());
                ScopeDesign testScopeDesignSearch = scopeDesignSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testScopeDesignSearch.getDesignEstimate()).isEqualTo(UPDATED_DESIGN_ESTIMATE);
                assertThat(testScopeDesignSearch.getCodeEstimate()).isEqualTo(UPDATED_CODE_ESTIMATE);
                assertThat(testScopeDesignSearch.getSyst1Estimate()).isEqualTo(UPDATED_SYST_1_ESTIMATE);
                assertThat(testScopeDesignSearch.getSyst2Estimate()).isEqualTo(UPDATED_SYST_2_ESTIMATE);
                assertThat(testScopeDesignSearch.getQualEstimate()).isEqualTo(UPDATED_QUAL_ESTIMATE);
                assertThat(testScopeDesignSearch.getImpEstimate()).isEqualTo(UPDATED_IMP_ESTIMATE);
                assertThat(testScopeDesignSearch.getPostImpEstimate()).isEqualTo(UPDATED_POST_IMP_ESTIMATE);
                assertThat(testScopeDesignSearch.getTotalHours()).isEqualTo(UPDATED_TOTAL_HOURS);
            });
    }

    @Test
    @Transactional
    void putNonExistingScopeDesign() throws Exception {
        int databaseSizeBeforeUpdate = scopeDesignRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scopeDesignSearchRepository.findAll());
        scopeDesign.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScopeDesignMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scopeDesign.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(scopeDesign))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScopeDesign in the database
        List<ScopeDesign> scopeDesignList = scopeDesignRepository.findAll();
        assertThat(scopeDesignList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(scopeDesignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchScopeDesign() throws Exception {
        int databaseSizeBeforeUpdate = scopeDesignRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scopeDesignSearchRepository.findAll());
        scopeDesign.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScopeDesignMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(scopeDesign))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScopeDesign in the database
        List<ScopeDesign> scopeDesignList = scopeDesignRepository.findAll();
        assertThat(scopeDesignList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(scopeDesignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamScopeDesign() throws Exception {
        int databaseSizeBeforeUpdate = scopeDesignRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scopeDesignSearchRepository.findAll());
        scopeDesign.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScopeDesignMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(scopeDesign)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ScopeDesign in the database
        List<ScopeDesign> scopeDesignList = scopeDesignRepository.findAll();
        assertThat(scopeDesignList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(scopeDesignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateScopeDesignWithPatch() throws Exception {
        // Initialize the database
        scopeDesignRepository.saveAndFlush(scopeDesign);

        int databaseSizeBeforeUpdate = scopeDesignRepository.findAll().size();

        // Update the scopeDesign using partial update
        ScopeDesign partialUpdatedScopeDesign = new ScopeDesign();
        partialUpdatedScopeDesign.setId(scopeDesign.getId());

        partialUpdatedScopeDesign.syst1Estimate(UPDATED_SYST_1_ESTIMATE);

        restScopeDesignMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScopeDesign.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedScopeDesign))
            )
            .andExpect(status().isOk());

        // Validate the ScopeDesign in the database
        List<ScopeDesign> scopeDesignList = scopeDesignRepository.findAll();
        assertThat(scopeDesignList).hasSize(databaseSizeBeforeUpdate);
        ScopeDesign testScopeDesign = scopeDesignList.get(scopeDesignList.size() - 1);
        assertThat(testScopeDesign.getDesignEstimate()).isEqualTo(DEFAULT_DESIGN_ESTIMATE);
        assertThat(testScopeDesign.getCodeEstimate()).isEqualTo(DEFAULT_CODE_ESTIMATE);
        assertThat(testScopeDesign.getSyst1Estimate()).isEqualTo(UPDATED_SYST_1_ESTIMATE);
        assertThat(testScopeDesign.getSyst2Estimate()).isEqualTo(DEFAULT_SYST_2_ESTIMATE);
        assertThat(testScopeDesign.getQualEstimate()).isEqualTo(DEFAULT_QUAL_ESTIMATE);
        assertThat(testScopeDesign.getImpEstimate()).isEqualTo(DEFAULT_IMP_ESTIMATE);
        assertThat(testScopeDesign.getPostImpEstimate()).isEqualTo(DEFAULT_POST_IMP_ESTIMATE);
        assertThat(testScopeDesign.getTotalHours()).isEqualTo(DEFAULT_TOTAL_HOURS);
    }

    @Test
    @Transactional
    void fullUpdateScopeDesignWithPatch() throws Exception {
        // Initialize the database
        scopeDesignRepository.saveAndFlush(scopeDesign);

        int databaseSizeBeforeUpdate = scopeDesignRepository.findAll().size();

        // Update the scopeDesign using partial update
        ScopeDesign partialUpdatedScopeDesign = new ScopeDesign();
        partialUpdatedScopeDesign.setId(scopeDesign.getId());

        partialUpdatedScopeDesign
            .designEstimate(UPDATED_DESIGN_ESTIMATE)
            .codeEstimate(UPDATED_CODE_ESTIMATE)
            .syst1Estimate(UPDATED_SYST_1_ESTIMATE)
            .syst2Estimate(UPDATED_SYST_2_ESTIMATE)
            .qualEstimate(UPDATED_QUAL_ESTIMATE)
            .impEstimate(UPDATED_IMP_ESTIMATE)
            .postImpEstimate(UPDATED_POST_IMP_ESTIMATE)
            .totalHours(UPDATED_TOTAL_HOURS);

        restScopeDesignMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScopeDesign.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedScopeDesign))
            )
            .andExpect(status().isOk());

        // Validate the ScopeDesign in the database
        List<ScopeDesign> scopeDesignList = scopeDesignRepository.findAll();
        assertThat(scopeDesignList).hasSize(databaseSizeBeforeUpdate);
        ScopeDesign testScopeDesign = scopeDesignList.get(scopeDesignList.size() - 1);
        assertThat(testScopeDesign.getDesignEstimate()).isEqualTo(UPDATED_DESIGN_ESTIMATE);
        assertThat(testScopeDesign.getCodeEstimate()).isEqualTo(UPDATED_CODE_ESTIMATE);
        assertThat(testScopeDesign.getSyst1Estimate()).isEqualTo(UPDATED_SYST_1_ESTIMATE);
        assertThat(testScopeDesign.getSyst2Estimate()).isEqualTo(UPDATED_SYST_2_ESTIMATE);
        assertThat(testScopeDesign.getQualEstimate()).isEqualTo(UPDATED_QUAL_ESTIMATE);
        assertThat(testScopeDesign.getImpEstimate()).isEqualTo(UPDATED_IMP_ESTIMATE);
        assertThat(testScopeDesign.getPostImpEstimate()).isEqualTo(UPDATED_POST_IMP_ESTIMATE);
        assertThat(testScopeDesign.getTotalHours()).isEqualTo(UPDATED_TOTAL_HOURS);
    }

    @Test
    @Transactional
    void patchNonExistingScopeDesign() throws Exception {
        int databaseSizeBeforeUpdate = scopeDesignRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scopeDesignSearchRepository.findAll());
        scopeDesign.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScopeDesignMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, scopeDesign.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(scopeDesign))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScopeDesign in the database
        List<ScopeDesign> scopeDesignList = scopeDesignRepository.findAll();
        assertThat(scopeDesignList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(scopeDesignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchScopeDesign() throws Exception {
        int databaseSizeBeforeUpdate = scopeDesignRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scopeDesignSearchRepository.findAll());
        scopeDesign.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScopeDesignMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(scopeDesign))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScopeDesign in the database
        List<ScopeDesign> scopeDesignList = scopeDesignRepository.findAll();
        assertThat(scopeDesignList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(scopeDesignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamScopeDesign() throws Exception {
        int databaseSizeBeforeUpdate = scopeDesignRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scopeDesignSearchRepository.findAll());
        scopeDesign.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScopeDesignMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(scopeDesign))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ScopeDesign in the database
        List<ScopeDesign> scopeDesignList = scopeDesignRepository.findAll();
        assertThat(scopeDesignList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(scopeDesignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteScopeDesign() throws Exception {
        // Initialize the database
        scopeDesignRepository.saveAndFlush(scopeDesign);
        scopeDesignRepository.save(scopeDesign);
        scopeDesignSearchRepository.save(scopeDesign);

        int databaseSizeBeforeDelete = scopeDesignRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(scopeDesignSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the scopeDesign
        restScopeDesignMockMvc
            .perform(delete(ENTITY_API_URL_ID, scopeDesign.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ScopeDesign> scopeDesignList = scopeDesignRepository.findAll();
        assertThat(scopeDesignList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(scopeDesignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchScopeDesign() throws Exception {
        // Initialize the database
        scopeDesign = scopeDesignRepository.saveAndFlush(scopeDesign);
        scopeDesignSearchRepository.save(scopeDesign);

        // Search the scopeDesign
        restScopeDesignMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + scopeDesign.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scopeDesign.getId().intValue())))
            .andExpect(jsonPath("$.[*].designEstimate").value(hasItem(DEFAULT_DESIGN_ESTIMATE.doubleValue())))
            .andExpect(jsonPath("$.[*].codeEstimate").value(hasItem(DEFAULT_CODE_ESTIMATE.doubleValue())))
            .andExpect(jsonPath("$.[*].syst1Estimate").value(hasItem(DEFAULT_SYST_1_ESTIMATE.doubleValue())))
            .andExpect(jsonPath("$.[*].syst2Estimate").value(hasItem(DEFAULT_SYST_2_ESTIMATE.doubleValue())))
            .andExpect(jsonPath("$.[*].qualEstimate").value(hasItem(DEFAULT_QUAL_ESTIMATE.doubleValue())))
            .andExpect(jsonPath("$.[*].impEstimate").value(hasItem(DEFAULT_IMP_ESTIMATE.doubleValue())))
            .andExpect(jsonPath("$.[*].postImpEstimate").value(hasItem(DEFAULT_POST_IMP_ESTIMATE.doubleValue())))
            .andExpect(jsonPath("$.[*].totalHours").value(hasItem(DEFAULT_TOTAL_HOURS.doubleValue())));
    }
}
