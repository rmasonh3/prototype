package com.bujisoft.mybuji.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bujisoft.mybuji.IntegrationTest;
import com.bujisoft.mybuji.domain.ElementTypes;
import com.bujisoft.mybuji.domain.EstimateDesign;
import com.bujisoft.mybuji.domain.WorkRequest;
import com.bujisoft.mybuji.domain.enumeration.Complexity;
import com.bujisoft.mybuji.repository.EstimateDesignRepository;
import com.bujisoft.mybuji.repository.search.EstimateDesignSearchRepository;
import com.bujisoft.mybuji.service.EstimateDesignService;
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
 * Integration tests for the {@link EstimateDesignResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EstimateDesignResourceIT {

    private static final Float DEFAULT_QPPROACH_NUMBER = 1F;
    private static final Float UPDATED_QPPROACH_NUMBER = 2F;

    private static final Complexity DEFAULT_COMPLEXITY = Complexity.Easy;
    private static final Complexity UPDATED_COMPLEXITY = Complexity.Average;

    private static final String ENTITY_API_URL = "/api/estimate-designs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/estimate-designs";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EstimateDesignRepository estimateDesignRepository;

    @Mock
    private EstimateDesignRepository estimateDesignRepositoryMock;

    @Mock
    private EstimateDesignService estimateDesignServiceMock;

    @Autowired
    private EstimateDesignSearchRepository estimateDesignSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEstimateDesignMockMvc;

    private EstimateDesign estimateDesign;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EstimateDesign createEntity(EntityManager em) {
        EstimateDesign estimateDesign = new EstimateDesign().qpproachNumber(DEFAULT_QPPROACH_NUMBER).complexity(DEFAULT_COMPLEXITY);
        // Add required entity
        WorkRequest workRequest;
        if (TestUtil.findAll(em, WorkRequest.class).isEmpty()) {
            workRequest = WorkRequestResourceIT.createEntity(em);
            em.persist(workRequest);
            em.flush();
        } else {
            workRequest = TestUtil.findAll(em, WorkRequest.class).get(0);
        }
        estimateDesign.setWorkrequest(workRequest);
        // Add required entity
        ElementTypes elementTypes;
        if (TestUtil.findAll(em, ElementTypes.class).isEmpty()) {
            elementTypes = ElementTypesResourceIT.createEntity(em);
            em.persist(elementTypes);
            em.flush();
        } else {
            elementTypes = TestUtil.findAll(em, ElementTypes.class).get(0);
        }
        estimateDesign.setElementtypes(elementTypes);
        return estimateDesign;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EstimateDesign createUpdatedEntity(EntityManager em) {
        EstimateDesign estimateDesign = new EstimateDesign().qpproachNumber(UPDATED_QPPROACH_NUMBER).complexity(UPDATED_COMPLEXITY);
        // Add required entity
        WorkRequest workRequest;
        if (TestUtil.findAll(em, WorkRequest.class).isEmpty()) {
            workRequest = WorkRequestResourceIT.createUpdatedEntity(em);
            em.persist(workRequest);
            em.flush();
        } else {
            workRequest = TestUtil.findAll(em, WorkRequest.class).get(0);
        }
        estimateDesign.setWorkrequest(workRequest);
        // Add required entity
        ElementTypes elementTypes;
        if (TestUtil.findAll(em, ElementTypes.class).isEmpty()) {
            elementTypes = ElementTypesResourceIT.createUpdatedEntity(em);
            em.persist(elementTypes);
            em.flush();
        } else {
            elementTypes = TestUtil.findAll(em, ElementTypes.class).get(0);
        }
        estimateDesign.setElementtypes(elementTypes);
        return estimateDesign;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        estimateDesignSearchRepository.deleteAll();
        assertThat(estimateDesignSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        estimateDesign = createEntity(em);
    }

    @Test
    @Transactional
    void createEstimateDesign() throws Exception {
        int databaseSizeBeforeCreate = estimateDesignRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());
        // Create the EstimateDesign
        restEstimateDesignMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(estimateDesign))
            )
            .andExpect(status().isCreated());

        // Validate the EstimateDesign in the database
        List<EstimateDesign> estimateDesignList = estimateDesignRepository.findAll();
        assertThat(estimateDesignList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        EstimateDesign testEstimateDesign = estimateDesignList.get(estimateDesignList.size() - 1);
        assertThat(testEstimateDesign.getQpproachNumber()).isEqualTo(DEFAULT_QPPROACH_NUMBER);
        assertThat(testEstimateDesign.getComplexity()).isEqualTo(DEFAULT_COMPLEXITY);
    }

    @Test
    @Transactional
    void createEstimateDesignWithExistingId() throws Exception {
        // Create the EstimateDesign with an existing ID
        estimateDesign.setId(1L);

        int databaseSizeBeforeCreate = estimateDesignRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restEstimateDesignMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(estimateDesign))
            )
            .andExpect(status().isBadRequest());

        // Validate the EstimateDesign in the database
        List<EstimateDesign> estimateDesignList = estimateDesignRepository.findAll();
        assertThat(estimateDesignList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkQpproachNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = estimateDesignRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());
        // set the field null
        estimateDesign.setQpproachNumber(null);

        // Create the EstimateDesign, which fails.

        restEstimateDesignMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(estimateDesign))
            )
            .andExpect(status().isBadRequest());

        List<EstimateDesign> estimateDesignList = estimateDesignRepository.findAll();
        assertThat(estimateDesignList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllEstimateDesigns() throws Exception {
        // Initialize the database
        estimateDesignRepository.saveAndFlush(estimateDesign);

        // Get all the estimateDesignList
        restEstimateDesignMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(estimateDesign.getId().intValue())))
            .andExpect(jsonPath("$.[*].qpproachNumber").value(hasItem(DEFAULT_QPPROACH_NUMBER.doubleValue())))
            .andExpect(jsonPath("$.[*].complexity").value(hasItem(DEFAULT_COMPLEXITY.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEstimateDesignsWithEagerRelationshipsIsEnabled() throws Exception {
        when(estimateDesignServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEstimateDesignMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(estimateDesignServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEstimateDesignsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(estimateDesignServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEstimateDesignMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(estimateDesignRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEstimateDesign() throws Exception {
        // Initialize the database
        estimateDesignRepository.saveAndFlush(estimateDesign);

        // Get the estimateDesign
        restEstimateDesignMockMvc
            .perform(get(ENTITY_API_URL_ID, estimateDesign.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(estimateDesign.getId().intValue()))
            .andExpect(jsonPath("$.qpproachNumber").value(DEFAULT_QPPROACH_NUMBER.doubleValue()))
            .andExpect(jsonPath("$.complexity").value(DEFAULT_COMPLEXITY.toString()));
    }

    @Test
    @Transactional
    void getNonExistingEstimateDesign() throws Exception {
        // Get the estimateDesign
        restEstimateDesignMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEstimateDesign() throws Exception {
        // Initialize the database
        estimateDesignRepository.saveAndFlush(estimateDesign);

        int databaseSizeBeforeUpdate = estimateDesignRepository.findAll().size();
        estimateDesignSearchRepository.save(estimateDesign);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());

        // Update the estimateDesign
        EstimateDesign updatedEstimateDesign = estimateDesignRepository.findById(estimateDesign.getId()).get();
        // Disconnect from session so that the updates on updatedEstimateDesign are not directly saved in db
        em.detach(updatedEstimateDesign);
        updatedEstimateDesign.qpproachNumber(UPDATED_QPPROACH_NUMBER).complexity(UPDATED_COMPLEXITY);

        restEstimateDesignMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEstimateDesign.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEstimateDesign))
            )
            .andExpect(status().isOk());

        // Validate the EstimateDesign in the database
        List<EstimateDesign> estimateDesignList = estimateDesignRepository.findAll();
        assertThat(estimateDesignList).hasSize(databaseSizeBeforeUpdate);
        EstimateDesign testEstimateDesign = estimateDesignList.get(estimateDesignList.size() - 1);
        assertThat(testEstimateDesign.getQpproachNumber()).isEqualTo(UPDATED_QPPROACH_NUMBER);
        assertThat(testEstimateDesign.getComplexity()).isEqualTo(UPDATED_COMPLEXITY);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<EstimateDesign> estimateDesignSearchList = IterableUtils.toList(estimateDesignSearchRepository.findAll());
                EstimateDesign testEstimateDesignSearch = estimateDesignSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testEstimateDesignSearch.getQpproachNumber()).isEqualTo(UPDATED_QPPROACH_NUMBER);
                assertThat(testEstimateDesignSearch.getComplexity()).isEqualTo(UPDATED_COMPLEXITY);
            });
    }

    @Test
    @Transactional
    void putNonExistingEstimateDesign() throws Exception {
        int databaseSizeBeforeUpdate = estimateDesignRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());
        estimateDesign.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEstimateDesignMockMvc
            .perform(
                put(ENTITY_API_URL_ID, estimateDesign.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(estimateDesign))
            )
            .andExpect(status().isBadRequest());

        // Validate the EstimateDesign in the database
        List<EstimateDesign> estimateDesignList = estimateDesignRepository.findAll();
        assertThat(estimateDesignList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchEstimateDesign() throws Exception {
        int databaseSizeBeforeUpdate = estimateDesignRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());
        estimateDesign.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEstimateDesignMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(estimateDesign))
            )
            .andExpect(status().isBadRequest());

        // Validate the EstimateDesign in the database
        List<EstimateDesign> estimateDesignList = estimateDesignRepository.findAll();
        assertThat(estimateDesignList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEstimateDesign() throws Exception {
        int databaseSizeBeforeUpdate = estimateDesignRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());
        estimateDesign.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEstimateDesignMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(estimateDesign)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EstimateDesign in the database
        List<EstimateDesign> estimateDesignList = estimateDesignRepository.findAll();
        assertThat(estimateDesignList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateEstimateDesignWithPatch() throws Exception {
        // Initialize the database
        estimateDesignRepository.saveAndFlush(estimateDesign);

        int databaseSizeBeforeUpdate = estimateDesignRepository.findAll().size();

        // Update the estimateDesign using partial update
        EstimateDesign partialUpdatedEstimateDesign = new EstimateDesign();
        partialUpdatedEstimateDesign.setId(estimateDesign.getId());

        partialUpdatedEstimateDesign.qpproachNumber(UPDATED_QPPROACH_NUMBER).complexity(UPDATED_COMPLEXITY);

        restEstimateDesignMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEstimateDesign.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEstimateDesign))
            )
            .andExpect(status().isOk());

        // Validate the EstimateDesign in the database
        List<EstimateDesign> estimateDesignList = estimateDesignRepository.findAll();
        assertThat(estimateDesignList).hasSize(databaseSizeBeforeUpdate);
        EstimateDesign testEstimateDesign = estimateDesignList.get(estimateDesignList.size() - 1);
        assertThat(testEstimateDesign.getQpproachNumber()).isEqualTo(UPDATED_QPPROACH_NUMBER);
        assertThat(testEstimateDesign.getComplexity()).isEqualTo(UPDATED_COMPLEXITY);
    }

    @Test
    @Transactional
    void fullUpdateEstimateDesignWithPatch() throws Exception {
        // Initialize the database
        estimateDesignRepository.saveAndFlush(estimateDesign);

        int databaseSizeBeforeUpdate = estimateDesignRepository.findAll().size();

        // Update the estimateDesign using partial update
        EstimateDesign partialUpdatedEstimateDesign = new EstimateDesign();
        partialUpdatedEstimateDesign.setId(estimateDesign.getId());

        partialUpdatedEstimateDesign.qpproachNumber(UPDATED_QPPROACH_NUMBER).complexity(UPDATED_COMPLEXITY);

        restEstimateDesignMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEstimateDesign.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEstimateDesign))
            )
            .andExpect(status().isOk());

        // Validate the EstimateDesign in the database
        List<EstimateDesign> estimateDesignList = estimateDesignRepository.findAll();
        assertThat(estimateDesignList).hasSize(databaseSizeBeforeUpdate);
        EstimateDesign testEstimateDesign = estimateDesignList.get(estimateDesignList.size() - 1);
        assertThat(testEstimateDesign.getQpproachNumber()).isEqualTo(UPDATED_QPPROACH_NUMBER);
        assertThat(testEstimateDesign.getComplexity()).isEqualTo(UPDATED_COMPLEXITY);
    }

    @Test
    @Transactional
    void patchNonExistingEstimateDesign() throws Exception {
        int databaseSizeBeforeUpdate = estimateDesignRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());
        estimateDesign.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEstimateDesignMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, estimateDesign.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(estimateDesign))
            )
            .andExpect(status().isBadRequest());

        // Validate the EstimateDesign in the database
        List<EstimateDesign> estimateDesignList = estimateDesignRepository.findAll();
        assertThat(estimateDesignList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEstimateDesign() throws Exception {
        int databaseSizeBeforeUpdate = estimateDesignRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());
        estimateDesign.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEstimateDesignMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(estimateDesign))
            )
            .andExpect(status().isBadRequest());

        // Validate the EstimateDesign in the database
        List<EstimateDesign> estimateDesignList = estimateDesignRepository.findAll();
        assertThat(estimateDesignList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEstimateDesign() throws Exception {
        int databaseSizeBeforeUpdate = estimateDesignRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());
        estimateDesign.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEstimateDesignMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(estimateDesign))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the EstimateDesign in the database
        List<EstimateDesign> estimateDesignList = estimateDesignRepository.findAll();
        assertThat(estimateDesignList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteEstimateDesign() throws Exception {
        // Initialize the database
        estimateDesignRepository.saveAndFlush(estimateDesign);
        estimateDesignRepository.save(estimateDesign);
        estimateDesignSearchRepository.save(estimateDesign);

        int databaseSizeBeforeDelete = estimateDesignRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the estimateDesign
        restEstimateDesignMockMvc
            .perform(delete(ENTITY_API_URL_ID, estimateDesign.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<EstimateDesign> estimateDesignList = estimateDesignRepository.findAll();
        assertThat(estimateDesignList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(estimateDesignSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchEstimateDesign() throws Exception {
        // Initialize the database
        estimateDesign = estimateDesignRepository.saveAndFlush(estimateDesign);
        estimateDesignSearchRepository.save(estimateDesign);

        // Search the estimateDesign
        restEstimateDesignMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + estimateDesign.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(estimateDesign.getId().intValue())))
            .andExpect(jsonPath("$.[*].qpproachNumber").value(hasItem(DEFAULT_QPPROACH_NUMBER.doubleValue())))
            .andExpect(jsonPath("$.[*].complexity").value(hasItem(DEFAULT_COMPLEXITY.toString())));
    }
}
