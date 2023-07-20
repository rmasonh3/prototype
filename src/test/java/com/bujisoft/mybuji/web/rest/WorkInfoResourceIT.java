package com.bujisoft.mybuji.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bujisoft.mybuji.IntegrationTest;
import com.bujisoft.mybuji.domain.WorkInfo;
import com.bujisoft.mybuji.domain.WorkRequest;
import com.bujisoft.mybuji.repository.WorkInfoRepository;
import com.bujisoft.mybuji.repository.search.WorkInfoSearchRepository;
import com.bujisoft.mybuji.service.WorkInfoService;
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
 * Integration tests for the {@link WorkInfoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class WorkInfoResourceIT {

    private static final Float DEFAULT_SCOPE_ACT = 1F;
    private static final Float UPDATED_SCOPE_ACT = 2F;

    private static final Float DEFAULT_DESIGN_ACT = 1F;
    private static final Float UPDATED_DESIGN_ACT = 2F;

    private static final Float DEFAULT_CODE_ACT = 1F;
    private static final Float UPDATED_CODE_ACT = 2F;

    private static final Float DEFAULT_SYST_1_ACT = 1F;
    private static final Float UPDATED_SYST_1_ACT = 2F;

    private static final Float DEFAULT_SYST_2_ACT = 1F;
    private static final Float UPDATED_SYST_2_ACT = 2F;

    private static final Float DEFAULT_QUAL_ACT = 1F;
    private static final Float UPDATED_QUAL_ACT = 2F;

    private static final Float DEFAULT_IMP_ACT = 1F;
    private static final Float UPDATED_IMP_ACT = 2F;

    private static final Float DEFAULT_POST_IMP_ACT = 1F;
    private static final Float UPDATED_POST_IMP_ACT = 2F;

    private static final Float DEFAULT_TOTAL_ACT = 1F;
    private static final Float UPDATED_TOTAL_ACT = 2F;

    private static final String ENTITY_API_URL = "/api/work-infos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/work-infos";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WorkInfoRepository workInfoRepository;

    @Mock
    private WorkInfoRepository workInfoRepositoryMock;

    @Mock
    private WorkInfoService workInfoServiceMock;

    @Autowired
    private WorkInfoSearchRepository workInfoSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWorkInfoMockMvc;

    private WorkInfo workInfo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkInfo createEntity(EntityManager em) {
        WorkInfo workInfo = new WorkInfo()
            .scopeAct(DEFAULT_SCOPE_ACT)
            .designAct(DEFAULT_DESIGN_ACT)
            .codeAct(DEFAULT_CODE_ACT)
            .syst1Act(DEFAULT_SYST_1_ACT)
            .syst2Act(DEFAULT_SYST_2_ACT)
            .qualAct(DEFAULT_QUAL_ACT)
            .impAct(DEFAULT_IMP_ACT)
            .postImpAct(DEFAULT_POST_IMP_ACT)
            .totalAct(DEFAULT_TOTAL_ACT);
        // Add required entity
        WorkRequest workRequest;
        if (TestUtil.findAll(em, WorkRequest.class).isEmpty()) {
            workRequest = WorkRequestResourceIT.createEntity(em);
            em.persist(workRequest);
            em.flush();
        } else {
            workRequest = TestUtil.findAll(em, WorkRequest.class).get(0);
        }
        workInfo.setWorkrequest(workRequest);
        return workInfo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkInfo createUpdatedEntity(EntityManager em) {
        WorkInfo workInfo = new WorkInfo()
            .scopeAct(UPDATED_SCOPE_ACT)
            .designAct(UPDATED_DESIGN_ACT)
            .codeAct(UPDATED_CODE_ACT)
            .syst1Act(UPDATED_SYST_1_ACT)
            .syst2Act(UPDATED_SYST_2_ACT)
            .qualAct(UPDATED_QUAL_ACT)
            .impAct(UPDATED_IMP_ACT)
            .postImpAct(UPDATED_POST_IMP_ACT)
            .totalAct(UPDATED_TOTAL_ACT);
        // Add required entity
        WorkRequest workRequest;
        if (TestUtil.findAll(em, WorkRequest.class).isEmpty()) {
            workRequest = WorkRequestResourceIT.createUpdatedEntity(em);
            em.persist(workRequest);
            em.flush();
        } else {
            workRequest = TestUtil.findAll(em, WorkRequest.class).get(0);
        }
        workInfo.setWorkrequest(workRequest);
        return workInfo;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        workInfoSearchRepository.deleteAll();
        assertThat(workInfoSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        workInfo = createEntity(em);
    }

    @Test
    @Transactional
    void createWorkInfo() throws Exception {
        int databaseSizeBeforeCreate = workInfoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        // Create the WorkInfo
        restWorkInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workInfo)))
            .andExpect(status().isCreated());

        // Validate the WorkInfo in the database
        List<WorkInfo> workInfoList = workInfoRepository.findAll();
        assertThat(workInfoList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        WorkInfo testWorkInfo = workInfoList.get(workInfoList.size() - 1);
        assertThat(testWorkInfo.getScopeAct()).isEqualTo(DEFAULT_SCOPE_ACT);
        assertThat(testWorkInfo.getDesignAct()).isEqualTo(DEFAULT_DESIGN_ACT);
        assertThat(testWorkInfo.getCodeAct()).isEqualTo(DEFAULT_CODE_ACT);
        assertThat(testWorkInfo.getSyst1Act()).isEqualTo(DEFAULT_SYST_1_ACT);
        assertThat(testWorkInfo.getSyst2Act()).isEqualTo(DEFAULT_SYST_2_ACT);
        assertThat(testWorkInfo.getQualAct()).isEqualTo(DEFAULT_QUAL_ACT);
        assertThat(testWorkInfo.getImpAct()).isEqualTo(DEFAULT_IMP_ACT);
        assertThat(testWorkInfo.getPostImpAct()).isEqualTo(DEFAULT_POST_IMP_ACT);
        assertThat(testWorkInfo.getTotalAct()).isEqualTo(DEFAULT_TOTAL_ACT);
    }

    @Test
    @Transactional
    void createWorkInfoWithExistingId() throws Exception {
        // Create the WorkInfo with an existing ID
        workInfo.setId(1L);

        int databaseSizeBeforeCreate = workInfoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workInfoSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restWorkInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workInfo)))
            .andExpect(status().isBadRequest());

        // Validate the WorkInfo in the database
        List<WorkInfo> workInfoList = workInfoRepository.findAll();
        assertThat(workInfoList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkScopeActIsRequired() throws Exception {
        int databaseSizeBeforeTest = workInfoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        // set the field null
        workInfo.setScopeAct(null);

        // Create the WorkInfo, which fails.

        restWorkInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workInfo)))
            .andExpect(status().isBadRequest());

        List<WorkInfo> workInfoList = workInfoRepository.findAll();
        assertThat(workInfoList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDesignActIsRequired() throws Exception {
        int databaseSizeBeforeTest = workInfoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        // set the field null
        workInfo.setDesignAct(null);

        // Create the WorkInfo, which fails.

        restWorkInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workInfo)))
            .andExpect(status().isBadRequest());

        List<WorkInfo> workInfoList = workInfoRepository.findAll();
        assertThat(workInfoList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCodeActIsRequired() throws Exception {
        int databaseSizeBeforeTest = workInfoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        // set the field null
        workInfo.setCodeAct(null);

        // Create the WorkInfo, which fails.

        restWorkInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workInfo)))
            .andExpect(status().isBadRequest());

        List<WorkInfo> workInfoList = workInfoRepository.findAll();
        assertThat(workInfoList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSyst1ActIsRequired() throws Exception {
        int databaseSizeBeforeTest = workInfoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        // set the field null
        workInfo.setSyst1Act(null);

        // Create the WorkInfo, which fails.

        restWorkInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workInfo)))
            .andExpect(status().isBadRequest());

        List<WorkInfo> workInfoList = workInfoRepository.findAll();
        assertThat(workInfoList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSyst2ActIsRequired() throws Exception {
        int databaseSizeBeforeTest = workInfoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        // set the field null
        workInfo.setSyst2Act(null);

        // Create the WorkInfo, which fails.

        restWorkInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workInfo)))
            .andExpect(status().isBadRequest());

        List<WorkInfo> workInfoList = workInfoRepository.findAll();
        assertThat(workInfoList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkQualActIsRequired() throws Exception {
        int databaseSizeBeforeTest = workInfoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        // set the field null
        workInfo.setQualAct(null);

        // Create the WorkInfo, which fails.

        restWorkInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workInfo)))
            .andExpect(status().isBadRequest());

        List<WorkInfo> workInfoList = workInfoRepository.findAll();
        assertThat(workInfoList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkImpActIsRequired() throws Exception {
        int databaseSizeBeforeTest = workInfoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        // set the field null
        workInfo.setImpAct(null);

        // Create the WorkInfo, which fails.

        restWorkInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workInfo)))
            .andExpect(status().isBadRequest());

        List<WorkInfo> workInfoList = workInfoRepository.findAll();
        assertThat(workInfoList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPostImpActIsRequired() throws Exception {
        int databaseSizeBeforeTest = workInfoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        // set the field null
        workInfo.setPostImpAct(null);

        // Create the WorkInfo, which fails.

        restWorkInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workInfo)))
            .andExpect(status().isBadRequest());

        List<WorkInfo> workInfoList = workInfoRepository.findAll();
        assertThat(workInfoList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTotalActIsRequired() throws Exception {
        int databaseSizeBeforeTest = workInfoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        // set the field null
        workInfo.setTotalAct(null);

        // Create the WorkInfo, which fails.

        restWorkInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workInfo)))
            .andExpect(status().isBadRequest());

        List<WorkInfo> workInfoList = workInfoRepository.findAll();
        assertThat(workInfoList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllWorkInfos() throws Exception {
        // Initialize the database
        workInfoRepository.saveAndFlush(workInfo);

        // Get all the workInfoList
        restWorkInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].scopeAct").value(hasItem(DEFAULT_SCOPE_ACT.doubleValue())))
            .andExpect(jsonPath("$.[*].designAct").value(hasItem(DEFAULT_DESIGN_ACT.doubleValue())))
            .andExpect(jsonPath("$.[*].codeAct").value(hasItem(DEFAULT_CODE_ACT.doubleValue())))
            .andExpect(jsonPath("$.[*].syst1Act").value(hasItem(DEFAULT_SYST_1_ACT.doubleValue())))
            .andExpect(jsonPath("$.[*].syst2Act").value(hasItem(DEFAULT_SYST_2_ACT.doubleValue())))
            .andExpect(jsonPath("$.[*].qualAct").value(hasItem(DEFAULT_QUAL_ACT.doubleValue())))
            .andExpect(jsonPath("$.[*].impAct").value(hasItem(DEFAULT_IMP_ACT.doubleValue())))
            .andExpect(jsonPath("$.[*].postImpAct").value(hasItem(DEFAULT_POST_IMP_ACT.doubleValue())))
            .andExpect(jsonPath("$.[*].totalAct").value(hasItem(DEFAULT_TOTAL_ACT.doubleValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWorkInfosWithEagerRelationshipsIsEnabled() throws Exception {
        when(workInfoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWorkInfoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(workInfoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWorkInfosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(workInfoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWorkInfoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(workInfoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getWorkInfo() throws Exception {
        // Initialize the database
        workInfoRepository.saveAndFlush(workInfo);

        // Get the workInfo
        restWorkInfoMockMvc
            .perform(get(ENTITY_API_URL_ID, workInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(workInfo.getId().intValue()))
            .andExpect(jsonPath("$.scopeAct").value(DEFAULT_SCOPE_ACT.doubleValue()))
            .andExpect(jsonPath("$.designAct").value(DEFAULT_DESIGN_ACT.doubleValue()))
            .andExpect(jsonPath("$.codeAct").value(DEFAULT_CODE_ACT.doubleValue()))
            .andExpect(jsonPath("$.syst1Act").value(DEFAULT_SYST_1_ACT.doubleValue()))
            .andExpect(jsonPath("$.syst2Act").value(DEFAULT_SYST_2_ACT.doubleValue()))
            .andExpect(jsonPath("$.qualAct").value(DEFAULT_QUAL_ACT.doubleValue()))
            .andExpect(jsonPath("$.impAct").value(DEFAULT_IMP_ACT.doubleValue()))
            .andExpect(jsonPath("$.postImpAct").value(DEFAULT_POST_IMP_ACT.doubleValue()))
            .andExpect(jsonPath("$.totalAct").value(DEFAULT_TOTAL_ACT.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingWorkInfo() throws Exception {
        // Get the workInfo
        restWorkInfoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWorkInfo() throws Exception {
        // Initialize the database
        workInfoRepository.saveAndFlush(workInfo);

        int databaseSizeBeforeUpdate = workInfoRepository.findAll().size();
        workInfoSearchRepository.save(workInfo);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workInfoSearchRepository.findAll());

        // Update the workInfo
        WorkInfo updatedWorkInfo = workInfoRepository.findById(workInfo.getId()).get();
        // Disconnect from session so that the updates on updatedWorkInfo are not directly saved in db
        em.detach(updatedWorkInfo);
        updatedWorkInfo
            .scopeAct(UPDATED_SCOPE_ACT)
            .designAct(UPDATED_DESIGN_ACT)
            .codeAct(UPDATED_CODE_ACT)
            .syst1Act(UPDATED_SYST_1_ACT)
            .syst2Act(UPDATED_SYST_2_ACT)
            .qualAct(UPDATED_QUAL_ACT)
            .impAct(UPDATED_IMP_ACT)
            .postImpAct(UPDATED_POST_IMP_ACT)
            .totalAct(UPDATED_TOTAL_ACT);

        restWorkInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedWorkInfo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedWorkInfo))
            )
            .andExpect(status().isOk());

        // Validate the WorkInfo in the database
        List<WorkInfo> workInfoList = workInfoRepository.findAll();
        assertThat(workInfoList).hasSize(databaseSizeBeforeUpdate);
        WorkInfo testWorkInfo = workInfoList.get(workInfoList.size() - 1);
        assertThat(testWorkInfo.getScopeAct()).isEqualTo(UPDATED_SCOPE_ACT);
        assertThat(testWorkInfo.getDesignAct()).isEqualTo(UPDATED_DESIGN_ACT);
        assertThat(testWorkInfo.getCodeAct()).isEqualTo(UPDATED_CODE_ACT);
        assertThat(testWorkInfo.getSyst1Act()).isEqualTo(UPDATED_SYST_1_ACT);
        assertThat(testWorkInfo.getSyst2Act()).isEqualTo(UPDATED_SYST_2_ACT);
        assertThat(testWorkInfo.getQualAct()).isEqualTo(UPDATED_QUAL_ACT);
        assertThat(testWorkInfo.getImpAct()).isEqualTo(UPDATED_IMP_ACT);
        assertThat(testWorkInfo.getPostImpAct()).isEqualTo(UPDATED_POST_IMP_ACT);
        assertThat(testWorkInfo.getTotalAct()).isEqualTo(UPDATED_TOTAL_ACT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<WorkInfo> workInfoSearchList = IterableUtils.toList(workInfoSearchRepository.findAll());
                WorkInfo testWorkInfoSearch = workInfoSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testWorkInfoSearch.getScopeAct()).isEqualTo(UPDATED_SCOPE_ACT);
                assertThat(testWorkInfoSearch.getDesignAct()).isEqualTo(UPDATED_DESIGN_ACT);
                assertThat(testWorkInfoSearch.getCodeAct()).isEqualTo(UPDATED_CODE_ACT);
                assertThat(testWorkInfoSearch.getSyst1Act()).isEqualTo(UPDATED_SYST_1_ACT);
                assertThat(testWorkInfoSearch.getSyst2Act()).isEqualTo(UPDATED_SYST_2_ACT);
                assertThat(testWorkInfoSearch.getQualAct()).isEqualTo(UPDATED_QUAL_ACT);
                assertThat(testWorkInfoSearch.getImpAct()).isEqualTo(UPDATED_IMP_ACT);
                assertThat(testWorkInfoSearch.getPostImpAct()).isEqualTo(UPDATED_POST_IMP_ACT);
                assertThat(testWorkInfoSearch.getTotalAct()).isEqualTo(UPDATED_TOTAL_ACT);
            });
    }

    @Test
    @Transactional
    void putNonExistingWorkInfo() throws Exception {
        int databaseSizeBeforeUpdate = workInfoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        workInfo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workInfo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkInfo in the database
        List<WorkInfo> workInfoList = workInfoRepository.findAll();
        assertThat(workInfoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchWorkInfo() throws Exception {
        int databaseSizeBeforeUpdate = workInfoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        workInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkInfo in the database
        List<WorkInfo> workInfoList = workInfoRepository.findAll();
        assertThat(workInfoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWorkInfo() throws Exception {
        int databaseSizeBeforeUpdate = workInfoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        workInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkInfoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workInfo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WorkInfo in the database
        List<WorkInfo> workInfoList = workInfoRepository.findAll();
        assertThat(workInfoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateWorkInfoWithPatch() throws Exception {
        // Initialize the database
        workInfoRepository.saveAndFlush(workInfo);

        int databaseSizeBeforeUpdate = workInfoRepository.findAll().size();

        // Update the workInfo using partial update
        WorkInfo partialUpdatedWorkInfo = new WorkInfo();
        partialUpdatedWorkInfo.setId(workInfo.getId());

        partialUpdatedWorkInfo.codeAct(UPDATED_CODE_ACT).syst1Act(UPDATED_SYST_1_ACT).qualAct(UPDATED_QUAL_ACT).impAct(UPDATED_IMP_ACT);

        restWorkInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkInfo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWorkInfo))
            )
            .andExpect(status().isOk());

        // Validate the WorkInfo in the database
        List<WorkInfo> workInfoList = workInfoRepository.findAll();
        assertThat(workInfoList).hasSize(databaseSizeBeforeUpdate);
        WorkInfo testWorkInfo = workInfoList.get(workInfoList.size() - 1);
        assertThat(testWorkInfo.getScopeAct()).isEqualTo(DEFAULT_SCOPE_ACT);
        assertThat(testWorkInfo.getDesignAct()).isEqualTo(DEFAULT_DESIGN_ACT);
        assertThat(testWorkInfo.getCodeAct()).isEqualTo(UPDATED_CODE_ACT);
        assertThat(testWorkInfo.getSyst1Act()).isEqualTo(UPDATED_SYST_1_ACT);
        assertThat(testWorkInfo.getSyst2Act()).isEqualTo(DEFAULT_SYST_2_ACT);
        assertThat(testWorkInfo.getQualAct()).isEqualTo(UPDATED_QUAL_ACT);
        assertThat(testWorkInfo.getImpAct()).isEqualTo(UPDATED_IMP_ACT);
        assertThat(testWorkInfo.getPostImpAct()).isEqualTo(DEFAULT_POST_IMP_ACT);
        assertThat(testWorkInfo.getTotalAct()).isEqualTo(DEFAULT_TOTAL_ACT);
    }

    @Test
    @Transactional
    void fullUpdateWorkInfoWithPatch() throws Exception {
        // Initialize the database
        workInfoRepository.saveAndFlush(workInfo);

        int databaseSizeBeforeUpdate = workInfoRepository.findAll().size();

        // Update the workInfo using partial update
        WorkInfo partialUpdatedWorkInfo = new WorkInfo();
        partialUpdatedWorkInfo.setId(workInfo.getId());

        partialUpdatedWorkInfo
            .scopeAct(UPDATED_SCOPE_ACT)
            .designAct(UPDATED_DESIGN_ACT)
            .codeAct(UPDATED_CODE_ACT)
            .syst1Act(UPDATED_SYST_1_ACT)
            .syst2Act(UPDATED_SYST_2_ACT)
            .qualAct(UPDATED_QUAL_ACT)
            .impAct(UPDATED_IMP_ACT)
            .postImpAct(UPDATED_POST_IMP_ACT)
            .totalAct(UPDATED_TOTAL_ACT);

        restWorkInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkInfo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWorkInfo))
            )
            .andExpect(status().isOk());

        // Validate the WorkInfo in the database
        List<WorkInfo> workInfoList = workInfoRepository.findAll();
        assertThat(workInfoList).hasSize(databaseSizeBeforeUpdate);
        WorkInfo testWorkInfo = workInfoList.get(workInfoList.size() - 1);
        assertThat(testWorkInfo.getScopeAct()).isEqualTo(UPDATED_SCOPE_ACT);
        assertThat(testWorkInfo.getDesignAct()).isEqualTo(UPDATED_DESIGN_ACT);
        assertThat(testWorkInfo.getCodeAct()).isEqualTo(UPDATED_CODE_ACT);
        assertThat(testWorkInfo.getSyst1Act()).isEqualTo(UPDATED_SYST_1_ACT);
        assertThat(testWorkInfo.getSyst2Act()).isEqualTo(UPDATED_SYST_2_ACT);
        assertThat(testWorkInfo.getQualAct()).isEqualTo(UPDATED_QUAL_ACT);
        assertThat(testWorkInfo.getImpAct()).isEqualTo(UPDATED_IMP_ACT);
        assertThat(testWorkInfo.getPostImpAct()).isEqualTo(UPDATED_POST_IMP_ACT);
        assertThat(testWorkInfo.getTotalAct()).isEqualTo(UPDATED_TOTAL_ACT);
    }

    @Test
    @Transactional
    void patchNonExistingWorkInfo() throws Exception {
        int databaseSizeBeforeUpdate = workInfoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        workInfo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, workInfo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(workInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkInfo in the database
        List<WorkInfo> workInfoList = workInfoRepository.findAll();
        assertThat(workInfoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWorkInfo() throws Exception {
        int databaseSizeBeforeUpdate = workInfoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        workInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(workInfo))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkInfo in the database
        List<WorkInfo> workInfoList = workInfoRepository.findAll();
        assertThat(workInfoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWorkInfo() throws Exception {
        int databaseSizeBeforeUpdate = workInfoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        workInfo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkInfoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(workInfo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WorkInfo in the database
        List<WorkInfo> workInfoList = workInfoRepository.findAll();
        assertThat(workInfoList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteWorkInfo() throws Exception {
        // Initialize the database
        workInfoRepository.saveAndFlush(workInfo);
        workInfoRepository.save(workInfo);
        workInfoSearchRepository.save(workInfo);

        int databaseSizeBeforeDelete = workInfoRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the workInfo
        restWorkInfoMockMvc
            .perform(delete(ENTITY_API_URL_ID, workInfo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<WorkInfo> workInfoList = workInfoRepository.findAll();
        assertThat(workInfoList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workInfoSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchWorkInfo() throws Exception {
        // Initialize the database
        workInfo = workInfoRepository.saveAndFlush(workInfo);
        workInfoSearchRepository.save(workInfo);

        // Search the workInfo
        restWorkInfoMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + workInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].scopeAct").value(hasItem(DEFAULT_SCOPE_ACT.doubleValue())))
            .andExpect(jsonPath("$.[*].designAct").value(hasItem(DEFAULT_DESIGN_ACT.doubleValue())))
            .andExpect(jsonPath("$.[*].codeAct").value(hasItem(DEFAULT_CODE_ACT.doubleValue())))
            .andExpect(jsonPath("$.[*].syst1Act").value(hasItem(DEFAULT_SYST_1_ACT.doubleValue())))
            .andExpect(jsonPath("$.[*].syst2Act").value(hasItem(DEFAULT_SYST_2_ACT.doubleValue())))
            .andExpect(jsonPath("$.[*].qualAct").value(hasItem(DEFAULT_QUAL_ACT.doubleValue())))
            .andExpect(jsonPath("$.[*].impAct").value(hasItem(DEFAULT_IMP_ACT.doubleValue())))
            .andExpect(jsonPath("$.[*].postImpAct").value(hasItem(DEFAULT_POST_IMP_ACT.doubleValue())))
            .andExpect(jsonPath("$.[*].totalAct").value(hasItem(DEFAULT_TOTAL_ACT.doubleValue())));
    }
}
