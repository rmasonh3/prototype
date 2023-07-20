package com.bujisoft.mybuji.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bujisoft.mybuji.IntegrationTest;
import com.bujisoft.mybuji.domain.EstimateBasis;
import com.bujisoft.mybuji.domain.WorkRequest;
import com.bujisoft.mybuji.repository.EstimateBasisRepository;
import com.bujisoft.mybuji.repository.search.EstimateBasisSearchRepository;
import com.bujisoft.mybuji.service.EstimateBasisService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link EstimateBasisResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EstimateBasisResourceIT {

    private static final Integer DEFAULT_SUBSYSTEM_ID = 1;
    private static final Integer UPDATED_SUBSYSTEM_ID = 2;

    private static final String DEFAULT_BASIS_OF_ESTIMATE = "AAAAAAAAAA";
    private static final String UPDATED_BASIS_OF_ESTIMATE = "BBBBBBBBBB";

    private static final String DEFAULT_ASSUMPTIONS = "AAAAAAAAAA";
    private static final String UPDATED_ASSUMPTIONS = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_UPDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_UPDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/estimate-bases";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/estimate-bases";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EstimateBasisRepository estimateBasisRepository;

    @Mock
    private EstimateBasisRepository estimateBasisRepositoryMock;

    @Mock
    private EstimateBasisService estimateBasisServiceMock;

    @Autowired
    private EstimateBasisSearchRepository estimateBasisSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEstimateBasisMockMvc;

    private EstimateBasis estimateBasis;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EstimateBasis createEntity(EntityManager em) {
        EstimateBasis estimateBasis = new EstimateBasis()
            .subsystemId(DEFAULT_SUBSYSTEM_ID)
            .basisOfEstimate(DEFAULT_BASIS_OF_ESTIMATE)
            .assumptions(DEFAULT_ASSUMPTIONS)
            .lastUpdate(DEFAULT_LAST_UPDATE);
        // Add required entity
        WorkRequest workRequest;
        if (TestUtil.findAll(em, WorkRequest.class).isEmpty()) {
            workRequest = WorkRequestResourceIT.createEntity(em);
            em.persist(workRequest);
            em.flush();
        } else {
            workRequest = TestUtil.findAll(em, WorkRequest.class).get(0);
        }
        estimateBasis.setWorkrequest(workRequest);
        return estimateBasis;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EstimateBasis createUpdatedEntity(EntityManager em) {
        EstimateBasis estimateBasis = new EstimateBasis()
            .subsystemId(UPDATED_SUBSYSTEM_ID)
            .basisOfEstimate(UPDATED_BASIS_OF_ESTIMATE)
            .assumptions(UPDATED_ASSUMPTIONS)
            .lastUpdate(UPDATED_LAST_UPDATE);
        // Add required entity
        WorkRequest workRequest;
        if (TestUtil.findAll(em, WorkRequest.class).isEmpty()) {
            workRequest = WorkRequestResourceIT.createUpdatedEntity(em);
            em.persist(workRequest);
            em.flush();
        } else {
            workRequest = TestUtil.findAll(em, WorkRequest.class).get(0);
        }
        estimateBasis.setWorkrequest(workRequest);
        return estimateBasis;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        estimateBasisSearchRepository.deleteAll();
        assertThat(estimateBasisSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        estimateBasis = createEntity(em);
    }

    @Test
    @Transactional
    void createEstimateBasis() throws Exception {
        int databaseSizeBeforeCreate = estimateBasisRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
        // Create the EstimateBasis
        restEstimateBasisMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(estimateBasis)))
            .andExpect(status().isCreated());

        // Validate the EstimateBasis in the database
        List<EstimateBasis> estimateBasisList = estimateBasisRepository.findAll();
        assertThat(estimateBasisList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        EstimateBasis testEstimateBasis = estimateBasisList.get(estimateBasisList.size() - 1);
        assertThat(testEstimateBasis.getSubsystemId()).isEqualTo(DEFAULT_SUBSYSTEM_ID);
        assertThat(testEstimateBasis.getBasisOfEstimate()).isEqualTo(DEFAULT_BASIS_OF_ESTIMATE);
        assertThat(testEstimateBasis.getAssumptions()).isEqualTo(DEFAULT_ASSUMPTIONS);
        assertThat(testEstimateBasis.getLastUpdate()).isEqualTo(DEFAULT_LAST_UPDATE);
    }

    @Test
    @Transactional
    void createEstimateBasisWithExistingId() throws Exception {
        // Create the EstimateBasis with an existing ID
        estimateBasis.setId(1L);

        int databaseSizeBeforeCreate = estimateBasisRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restEstimateBasisMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(estimateBasis)))
            .andExpect(status().isBadRequest());

        // Validate the EstimateBasis in the database
        List<EstimateBasis> estimateBasisList = estimateBasisRepository.findAll();
        assertThat(estimateBasisList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSubsystemIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = estimateBasisRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
        // set the field null
        estimateBasis.setSubsystemId(null);

        // Create the EstimateBasis, which fails.

        restEstimateBasisMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(estimateBasis)))
            .andExpect(status().isBadRequest());

        List<EstimateBasis> estimateBasisList = estimateBasisRepository.findAll();
        assertThat(estimateBasisList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLastUpdateIsRequired() throws Exception {
        int databaseSizeBeforeTest = estimateBasisRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
        // set the field null
        estimateBasis.setLastUpdate(null);

        // Create the EstimateBasis, which fails.

        restEstimateBasisMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(estimateBasis)))
            .andExpect(status().isBadRequest());

        List<EstimateBasis> estimateBasisList = estimateBasisRepository.findAll();
        assertThat(estimateBasisList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllEstimateBases() throws Exception {
        // Initialize the database
        estimateBasisRepository.saveAndFlush(estimateBasis);

        // Get all the estimateBasisList
        restEstimateBasisMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(estimateBasis.getId().intValue())))
            .andExpect(jsonPath("$.[*].subsystemId").value(hasItem(DEFAULT_SUBSYSTEM_ID)))
            .andExpect(jsonPath("$.[*].basisOfEstimate").value(hasItem(DEFAULT_BASIS_OF_ESTIMATE.toString())))
            .andExpect(jsonPath("$.[*].assumptions").value(hasItem(DEFAULT_ASSUMPTIONS.toString())))
            .andExpect(jsonPath("$.[*].lastUpdate").value(hasItem(DEFAULT_LAST_UPDATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEstimateBasesWithEagerRelationshipsIsEnabled() throws Exception {
        when(estimateBasisServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEstimateBasisMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(estimateBasisServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEstimateBasesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(estimateBasisServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEstimateBasisMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(estimateBasisRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEstimateBasis() throws Exception {
        // Initialize the database
        estimateBasisRepository.saveAndFlush(estimateBasis);

        // Get the estimateBasis
        restEstimateBasisMockMvc
            .perform(get(ENTITY_API_URL_ID, estimateBasis.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(estimateBasis.getId().intValue()))
            .andExpect(jsonPath("$.subsystemId").value(DEFAULT_SUBSYSTEM_ID))
            .andExpect(jsonPath("$.basisOfEstimate").value(DEFAULT_BASIS_OF_ESTIMATE.toString()))
            .andExpect(jsonPath("$.assumptions").value(DEFAULT_ASSUMPTIONS.toString()))
            .andExpect(jsonPath("$.lastUpdate").value(DEFAULT_LAST_UPDATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingEstimateBasis() throws Exception {
        // Get the estimateBasis
        restEstimateBasisMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEstimateBasis() throws Exception {
        // Initialize the database
        estimateBasisRepository.saveAndFlush(estimateBasis);

        int databaseSizeBeforeUpdate = estimateBasisRepository.findAll().size();
        estimateBasisSearchRepository.save(estimateBasis);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());

        // Update the estimateBasis
        EstimateBasis updatedEstimateBasis = estimateBasisRepository.findById(estimateBasis.getId()).get();
        // Disconnect from session so that the updates on updatedEstimateBasis are not directly saved in db
        em.detach(updatedEstimateBasis);
        updatedEstimateBasis
            .subsystemId(UPDATED_SUBSYSTEM_ID)
            .basisOfEstimate(UPDATED_BASIS_OF_ESTIMATE)
            .assumptions(UPDATED_ASSUMPTIONS)
            .lastUpdate(UPDATED_LAST_UPDATE);

        restEstimateBasisMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEstimateBasis.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEstimateBasis))
            )
            .andExpect(status().isOk());

        // Validate the EstimateBasis in the database
        List<EstimateBasis> estimateBasisList = estimateBasisRepository.findAll();
        assertThat(estimateBasisList).hasSize(databaseSizeBeforeUpdate);
        EstimateBasis testEstimateBasis = estimateBasisList.get(estimateBasisList.size() - 1);
        assertThat(testEstimateBasis.getSubsystemId()).isEqualTo(UPDATED_SUBSYSTEM_ID);
        assertThat(testEstimateBasis.getBasisOfEstimate()).isEqualTo(UPDATED_BASIS_OF_ESTIMATE);
        assertThat(testEstimateBasis.getAssumptions()).isEqualTo(UPDATED_ASSUMPTIONS);
        assertThat(testEstimateBasis.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<EstimateBasis> estimateBasisSearchList = IterableUtils.toList(estimateBasisSearchRepository.findAll());
                EstimateBasis testEstimateBasisSearch = estimateBasisSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testEstimateBasisSearch.getSubsystemId()).isEqualTo(UPDATED_SUBSYSTEM_ID);
                assertThat(testEstimateBasisSearch.getBasisOfEstimate()).isEqualTo(UPDATED_BASIS_OF_ESTIMATE);
                assertThat(testEstimateBasisSearch.getAssumptions()).isEqualTo(UPDATED_ASSUMPTIONS);
                assertThat(testEstimateBasisSearch.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
            });
    }

    @Test
    @Transactional
    void putNonExistingEstimateBasis() throws Exception {
        int databaseSizeBeforeUpdate = estimateBasisRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
        estimateBasis.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEstimateBasisMockMvc
            .perform(
                put(ENTITY_API_URL_ID, estimateBasis.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(estimateBasis))
            )
            .andExpect(status().isBadRequest());

        // Validate the EstimateBasis in the database
        List<EstimateBasis> estimateBasisList = estimateBasisRepository.findAll();
        assertThat(estimateBasisList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchEstimateBasis() throws Exception {
        int databaseSizeBeforeUpdate = estimateBasisRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
        estimateBasis.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEstimateBasisMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(estimateBasis))
            )
            .andExpect(status().isBadRequest());

        // Validate the EstimateBasis in the database
        List<EstimateBasis> estimateBasisList = estimateBasisRepository.findAll();
        assertThat(estimateBasisList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEstimateBasis() throws Exception {
        int databaseSizeBeforeUpdate = estimateBasisRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
        estimateBasis.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEstimateBasisMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(estimateBasis)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EstimateBasis in the database
        List<EstimateBasis> estimateBasisList = estimateBasisRepository.findAll();
        assertThat(estimateBasisList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateEstimateBasisWithPatch() throws Exception {
        // Initialize the database
        estimateBasisRepository.saveAndFlush(estimateBasis);

        int databaseSizeBeforeUpdate = estimateBasisRepository.findAll().size();

        // Update the estimateBasis using partial update
        EstimateBasis partialUpdatedEstimateBasis = new EstimateBasis();
        partialUpdatedEstimateBasis.setId(estimateBasis.getId());

        partialUpdatedEstimateBasis.basisOfEstimate(UPDATED_BASIS_OF_ESTIMATE);

        restEstimateBasisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEstimateBasis.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEstimateBasis))
            )
            .andExpect(status().isOk());

        // Validate the EstimateBasis in the database
        List<EstimateBasis> estimateBasisList = estimateBasisRepository.findAll();
        assertThat(estimateBasisList).hasSize(databaseSizeBeforeUpdate);
        EstimateBasis testEstimateBasis = estimateBasisList.get(estimateBasisList.size() - 1);
        assertThat(testEstimateBasis.getSubsystemId()).isEqualTo(DEFAULT_SUBSYSTEM_ID);
        assertThat(testEstimateBasis.getBasisOfEstimate()).isEqualTo(UPDATED_BASIS_OF_ESTIMATE);
        assertThat(testEstimateBasis.getAssumptions()).isEqualTo(DEFAULT_ASSUMPTIONS);
        assertThat(testEstimateBasis.getLastUpdate()).isEqualTo(DEFAULT_LAST_UPDATE);
    }

    @Test
    @Transactional
    void fullUpdateEstimateBasisWithPatch() throws Exception {
        // Initialize the database
        estimateBasisRepository.saveAndFlush(estimateBasis);

        int databaseSizeBeforeUpdate = estimateBasisRepository.findAll().size();

        // Update the estimateBasis using partial update
        EstimateBasis partialUpdatedEstimateBasis = new EstimateBasis();
        partialUpdatedEstimateBasis.setId(estimateBasis.getId());

        partialUpdatedEstimateBasis
            .subsystemId(UPDATED_SUBSYSTEM_ID)
            .basisOfEstimate(UPDATED_BASIS_OF_ESTIMATE)
            .assumptions(UPDATED_ASSUMPTIONS)
            .lastUpdate(UPDATED_LAST_UPDATE);

        restEstimateBasisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEstimateBasis.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEstimateBasis))
            )
            .andExpect(status().isOk());

        // Validate the EstimateBasis in the database
        List<EstimateBasis> estimateBasisList = estimateBasisRepository.findAll();
        assertThat(estimateBasisList).hasSize(databaseSizeBeforeUpdate);
        EstimateBasis testEstimateBasis = estimateBasisList.get(estimateBasisList.size() - 1);
        assertThat(testEstimateBasis.getSubsystemId()).isEqualTo(UPDATED_SUBSYSTEM_ID);
        assertThat(testEstimateBasis.getBasisOfEstimate()).isEqualTo(UPDATED_BASIS_OF_ESTIMATE);
        assertThat(testEstimateBasis.getAssumptions()).isEqualTo(UPDATED_ASSUMPTIONS);
        assertThat(testEstimateBasis.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
    }

    @Test
    @Transactional
    void patchNonExistingEstimateBasis() throws Exception {
        int databaseSizeBeforeUpdate = estimateBasisRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
        estimateBasis.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEstimateBasisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, estimateBasis.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(estimateBasis))
            )
            .andExpect(status().isBadRequest());

        // Validate the EstimateBasis in the database
        List<EstimateBasis> estimateBasisList = estimateBasisRepository.findAll();
        assertThat(estimateBasisList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEstimateBasis() throws Exception {
        int databaseSizeBeforeUpdate = estimateBasisRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
        estimateBasis.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEstimateBasisMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(estimateBasis))
            )
            .andExpect(status().isBadRequest());

        // Validate the EstimateBasis in the database
        List<EstimateBasis> estimateBasisList = estimateBasisRepository.findAll();
        assertThat(estimateBasisList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEstimateBasis() throws Exception {
        int databaseSizeBeforeUpdate = estimateBasisRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
        estimateBasis.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEstimateBasisMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(estimateBasis))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the EstimateBasis in the database
        List<EstimateBasis> estimateBasisList = estimateBasisRepository.findAll();
        assertThat(estimateBasisList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteEstimateBasis() throws Exception {
        // Initialize the database
        estimateBasisRepository.saveAndFlush(estimateBasis);
        estimateBasisRepository.save(estimateBasis);
        estimateBasisSearchRepository.save(estimateBasis);

        int databaseSizeBeforeDelete = estimateBasisRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the estimateBasis
        restEstimateBasisMockMvc
            .perform(delete(ENTITY_API_URL_ID, estimateBasis.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<EstimateBasis> estimateBasisList = estimateBasisRepository.findAll();
        assertThat(estimateBasisList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateBasisSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchEstimateBasis() throws Exception {
        // Initialize the database
        estimateBasis = estimateBasisRepository.saveAndFlush(estimateBasis);
        estimateBasisSearchRepository.save(estimateBasis);

        // Search the estimateBasis
        restEstimateBasisMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + estimateBasis.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(estimateBasis.getId().intValue())))
            .andExpect(jsonPath("$.[*].subsystemId").value(hasItem(DEFAULT_SUBSYSTEM_ID)))
            .andExpect(jsonPath("$.[*].basisOfEstimate").value(hasItem(DEFAULT_BASIS_OF_ESTIMATE.toString())))
            .andExpect(jsonPath("$.[*].assumptions").value(hasItem(DEFAULT_ASSUMPTIONS.toString())))
            .andExpect(jsonPath("$.[*].lastUpdate").value(hasItem(DEFAULT_LAST_UPDATE.toString())));
    }
}
