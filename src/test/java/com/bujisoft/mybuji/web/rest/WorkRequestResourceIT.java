package com.bujisoft.mybuji.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bujisoft.mybuji.IntegrationTest;
import com.bujisoft.mybuji.domain.Employee;
import com.bujisoft.mybuji.domain.WorkRequest;
import com.bujisoft.mybuji.domain.enumeration.DesignStatus;
import com.bujisoft.mybuji.domain.enumeration.ProjectStatus;
import com.bujisoft.mybuji.repository.WorkRequestRepository;
import com.bujisoft.mybuji.repository.search.WorkRequestSearchRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link WorkRequestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WorkRequestResourceIT {

    private static final String DEFAULT_PROJECT_ID = "AAAAAAAAAA";
    private static final String UPDATED_PROJECT_ID = "BBBBBBBBBB";

    private static final String DEFAULT_WORK_REQUEST = "AAAAAAAAAA";
    private static final String UPDATED_WORK_REQUEST = "BBBBBBBBBB";

    private static final String DEFAULT_WORK_REQUEST_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_WORK_REQUEST_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_WORK_RWQUEST_PHASE = "AAAAAAAAAA";
    private static final String UPDATED_WORK_RWQUEST_PHASE = "BBBBBBBBBB";

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final ProjectStatus DEFAULT_STATUS = ProjectStatus.Pending;
    private static final ProjectStatus UPDATED_STATUS = ProjectStatus.InProgress;

    private static final DesignStatus DEFAULT_DESIGN = DesignStatus.Pending;
    private static final DesignStatus UPDATED_DESIGN = DesignStatus.InProgress;

    private static final String ENTITY_API_URL = "/api/work-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/work-requests";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WorkRequestRepository workRequestRepository;

    @Autowired
    private WorkRequestSearchRepository workRequestSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWorkRequestMockMvc;

    private WorkRequest workRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkRequest createEntity(EntityManager em) {
        WorkRequest workRequest = new WorkRequest()
            .projectId(DEFAULT_PROJECT_ID)
            .workRequest(DEFAULT_WORK_REQUEST)
            .workRequestDescription(DEFAULT_WORK_REQUEST_DESCRIPTION)
            .workRwquestPhase(DEFAULT_WORK_RWQUEST_PHASE)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .status(DEFAULT_STATUS)
            .design(DEFAULT_DESIGN);
        // Add required entity
        Employee employee;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            employee = EmployeeResourceIT.createEntity(em);
            em.persist(employee);
            em.flush();
        } else {
            employee = TestUtil.findAll(em, Employee.class).get(0);
        }
        workRequest.getEmployees().add(employee);
        return workRequest;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkRequest createUpdatedEntity(EntityManager em) {
        WorkRequest workRequest = new WorkRequest()
            .projectId(UPDATED_PROJECT_ID)
            .workRequest(UPDATED_WORK_REQUEST)
            .workRequestDescription(UPDATED_WORK_REQUEST_DESCRIPTION)
            .workRwquestPhase(UPDATED_WORK_RWQUEST_PHASE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS)
            .design(UPDATED_DESIGN);
        // Add required entity
        Employee employee;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            employee = EmployeeResourceIT.createUpdatedEntity(em);
            em.persist(employee);
            em.flush();
        } else {
            employee = TestUtil.findAll(em, Employee.class).get(0);
        }
        workRequest.getEmployees().add(employee);
        return workRequest;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        workRequestSearchRepository.deleteAll();
        assertThat(workRequestSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        workRequest = createEntity(em);
    }

    @Test
    @Transactional
    void createWorkRequest() throws Exception {
        int databaseSizeBeforeCreate = workRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        // Create the WorkRequest
        restWorkRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workRequest)))
            .andExpect(status().isCreated());

        // Validate the WorkRequest in the database
        List<WorkRequest> workRequestList = workRequestRepository.findAll();
        assertThat(workRequestList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        WorkRequest testWorkRequest = workRequestList.get(workRequestList.size() - 1);
        assertThat(testWorkRequest.getProjectId()).isEqualTo(DEFAULT_PROJECT_ID);
        assertThat(testWorkRequest.getWorkRequest()).isEqualTo(DEFAULT_WORK_REQUEST);
        assertThat(testWorkRequest.getWorkRequestDescription()).isEqualTo(DEFAULT_WORK_REQUEST_DESCRIPTION);
        assertThat(testWorkRequest.getWorkRwquestPhase()).isEqualTo(DEFAULT_WORK_RWQUEST_PHASE);
        assertThat(testWorkRequest.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testWorkRequest.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testWorkRequest.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testWorkRequest.getDesign()).isEqualTo(DEFAULT_DESIGN);
    }

    @Test
    @Transactional
    void createWorkRequestWithExistingId() throws Exception {
        // Create the WorkRequest with an existing ID
        workRequest.setId(1L);

        int databaseSizeBeforeCreate = workRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workRequestSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restWorkRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workRequest)))
            .andExpect(status().isBadRequest());

        // Validate the WorkRequest in the database
        List<WorkRequest> workRequestList = workRequestRepository.findAll();
        assertThat(workRequestList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkProjectIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = workRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        // set the field null
        workRequest.setProjectId(null);

        // Create the WorkRequest, which fails.

        restWorkRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workRequest)))
            .andExpect(status().isBadRequest());

        List<WorkRequest> workRequestList = workRequestRepository.findAll();
        assertThat(workRequestList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkWorkRequestIsRequired() throws Exception {
        int databaseSizeBeforeTest = workRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        // set the field null
        workRequest.setWorkRequest(null);

        // Create the WorkRequest, which fails.

        restWorkRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workRequest)))
            .andExpect(status().isBadRequest());

        List<WorkRequest> workRequestList = workRequestRepository.findAll();
        assertThat(workRequestList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkWorkRequestDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = workRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        // set the field null
        workRequest.setWorkRequestDescription(null);

        // Create the WorkRequest, which fails.

        restWorkRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workRequest)))
            .andExpect(status().isBadRequest());

        List<WorkRequest> workRequestList = workRequestRepository.findAll();
        assertThat(workRequestList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkWorkRwquestPhaseIsRequired() throws Exception {
        int databaseSizeBeforeTest = workRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        // set the field null
        workRequest.setWorkRwquestPhase(null);

        // Create the WorkRequest, which fails.

        restWorkRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workRequest)))
            .andExpect(status().isBadRequest());

        List<WorkRequest> workRequestList = workRequestRepository.findAll();
        assertThat(workRequestList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = workRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        // set the field null
        workRequest.setStartDate(null);

        // Create the WorkRequest, which fails.

        restWorkRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workRequest)))
            .andExpect(status().isBadRequest());

        List<WorkRequest> workRequestList = workRequestRepository.findAll();
        assertThat(workRequestList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEndDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = workRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        // set the field null
        workRequest.setEndDate(null);

        // Create the WorkRequest, which fails.

        restWorkRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workRequest)))
            .andExpect(status().isBadRequest());

        List<WorkRequest> workRequestList = workRequestRepository.findAll();
        assertThat(workRequestList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllWorkRequests() throws Exception {
        // Initialize the database
        workRequestRepository.saveAndFlush(workRequest);

        // Get all the workRequestList
        restWorkRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].projectId").value(hasItem(DEFAULT_PROJECT_ID)))
            .andExpect(jsonPath("$.[*].workRequest").value(hasItem(DEFAULT_WORK_REQUEST)))
            .andExpect(jsonPath("$.[*].workRequestDescription").value(hasItem(DEFAULT_WORK_REQUEST_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].workRwquestPhase").value(hasItem(DEFAULT_WORK_RWQUEST_PHASE)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].design").value(hasItem(DEFAULT_DESIGN.toString())));
    }

    @Test
    @Transactional
    void getWorkRequest() throws Exception {
        // Initialize the database
        workRequestRepository.saveAndFlush(workRequest);

        // Get the workRequest
        restWorkRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, workRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(workRequest.getId().intValue()))
            .andExpect(jsonPath("$.projectId").value(DEFAULT_PROJECT_ID))
            .andExpect(jsonPath("$.workRequest").value(DEFAULT_WORK_REQUEST))
            .andExpect(jsonPath("$.workRequestDescription").value(DEFAULT_WORK_REQUEST_DESCRIPTION))
            .andExpect(jsonPath("$.workRwquestPhase").value(DEFAULT_WORK_RWQUEST_PHASE))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.design").value(DEFAULT_DESIGN.toString()));
    }

    @Test
    @Transactional
    void getNonExistingWorkRequest() throws Exception {
        // Get the workRequest
        restWorkRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWorkRequest() throws Exception {
        // Initialize the database
        workRequestRepository.saveAndFlush(workRequest);

        int databaseSizeBeforeUpdate = workRequestRepository.findAll().size();
        workRequestSearchRepository.save(workRequest);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workRequestSearchRepository.findAll());

        // Update the workRequest
        WorkRequest updatedWorkRequest = workRequestRepository.findById(workRequest.getId()).get();
        // Disconnect from session so that the updates on updatedWorkRequest are not directly saved in db
        em.detach(updatedWorkRequest);
        updatedWorkRequest
            .projectId(UPDATED_PROJECT_ID)
            .workRequest(UPDATED_WORK_REQUEST)
            .workRequestDescription(UPDATED_WORK_REQUEST_DESCRIPTION)
            .workRwquestPhase(UPDATED_WORK_RWQUEST_PHASE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS)
            .design(UPDATED_DESIGN);

        restWorkRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedWorkRequest.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedWorkRequest))
            )
            .andExpect(status().isOk());

        // Validate the WorkRequest in the database
        List<WorkRequest> workRequestList = workRequestRepository.findAll();
        assertThat(workRequestList).hasSize(databaseSizeBeforeUpdate);
        WorkRequest testWorkRequest = workRequestList.get(workRequestList.size() - 1);
        assertThat(testWorkRequest.getProjectId()).isEqualTo(UPDATED_PROJECT_ID);
        assertThat(testWorkRequest.getWorkRequest()).isEqualTo(UPDATED_WORK_REQUEST);
        assertThat(testWorkRequest.getWorkRequestDescription()).isEqualTo(UPDATED_WORK_REQUEST_DESCRIPTION);
        assertThat(testWorkRequest.getWorkRwquestPhase()).isEqualTo(UPDATED_WORK_RWQUEST_PHASE);
        assertThat(testWorkRequest.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testWorkRequest.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testWorkRequest.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testWorkRequest.getDesign()).isEqualTo(UPDATED_DESIGN);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<WorkRequest> workRequestSearchList = IterableUtils.toList(workRequestSearchRepository.findAll());
                WorkRequest testWorkRequestSearch = workRequestSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testWorkRequestSearch.getProjectId()).isEqualTo(UPDATED_PROJECT_ID);
                assertThat(testWorkRequestSearch.getWorkRequest()).isEqualTo(UPDATED_WORK_REQUEST);
                assertThat(testWorkRequestSearch.getWorkRequestDescription()).isEqualTo(UPDATED_WORK_REQUEST_DESCRIPTION);
                assertThat(testWorkRequestSearch.getWorkRwquestPhase()).isEqualTo(UPDATED_WORK_RWQUEST_PHASE);
                assertThat(testWorkRequestSearch.getStartDate()).isEqualTo(UPDATED_START_DATE);
                assertThat(testWorkRequestSearch.getEndDate()).isEqualTo(UPDATED_END_DATE);
                assertThat(testWorkRequestSearch.getStatus()).isEqualTo(UPDATED_STATUS);
                assertThat(testWorkRequestSearch.getDesign()).isEqualTo(UPDATED_DESIGN);
            });
    }

    @Test
    @Transactional
    void putNonExistingWorkRequest() throws Exception {
        int databaseSizeBeforeUpdate = workRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        workRequest.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workRequest.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkRequest in the database
        List<WorkRequest> workRequestList = workRequestRepository.findAll();
        assertThat(workRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchWorkRequest() throws Exception {
        int databaseSizeBeforeUpdate = workRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        workRequest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkRequest in the database
        List<WorkRequest> workRequestList = workRequestRepository.findAll();
        assertThat(workRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWorkRequest() throws Exception {
        int databaseSizeBeforeUpdate = workRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        workRequest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkRequestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workRequest)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WorkRequest in the database
        List<WorkRequest> workRequestList = workRequestRepository.findAll();
        assertThat(workRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateWorkRequestWithPatch() throws Exception {
        // Initialize the database
        workRequestRepository.saveAndFlush(workRequest);

        int databaseSizeBeforeUpdate = workRequestRepository.findAll().size();

        // Update the workRequest using partial update
        WorkRequest partialUpdatedWorkRequest = new WorkRequest();
        partialUpdatedWorkRequest.setId(workRequest.getId());

        partialUpdatedWorkRequest
            .projectId(UPDATED_PROJECT_ID)
            .workRwquestPhase(UPDATED_WORK_RWQUEST_PHASE)
            .startDate(UPDATED_START_DATE)
            .design(UPDATED_DESIGN);

        restWorkRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWorkRequest))
            )
            .andExpect(status().isOk());

        // Validate the WorkRequest in the database
        List<WorkRequest> workRequestList = workRequestRepository.findAll();
        assertThat(workRequestList).hasSize(databaseSizeBeforeUpdate);
        WorkRequest testWorkRequest = workRequestList.get(workRequestList.size() - 1);
        assertThat(testWorkRequest.getProjectId()).isEqualTo(UPDATED_PROJECT_ID);
        assertThat(testWorkRequest.getWorkRequest()).isEqualTo(DEFAULT_WORK_REQUEST);
        assertThat(testWorkRequest.getWorkRequestDescription()).isEqualTo(DEFAULT_WORK_REQUEST_DESCRIPTION);
        assertThat(testWorkRequest.getWorkRwquestPhase()).isEqualTo(UPDATED_WORK_RWQUEST_PHASE);
        assertThat(testWorkRequest.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testWorkRequest.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testWorkRequest.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testWorkRequest.getDesign()).isEqualTo(UPDATED_DESIGN);
    }

    @Test
    @Transactional
    void fullUpdateWorkRequestWithPatch() throws Exception {
        // Initialize the database
        workRequestRepository.saveAndFlush(workRequest);

        int databaseSizeBeforeUpdate = workRequestRepository.findAll().size();

        // Update the workRequest using partial update
        WorkRequest partialUpdatedWorkRequest = new WorkRequest();
        partialUpdatedWorkRequest.setId(workRequest.getId());

        partialUpdatedWorkRequest
            .projectId(UPDATED_PROJECT_ID)
            .workRequest(UPDATED_WORK_REQUEST)
            .workRequestDescription(UPDATED_WORK_REQUEST_DESCRIPTION)
            .workRwquestPhase(UPDATED_WORK_RWQUEST_PHASE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS)
            .design(UPDATED_DESIGN);

        restWorkRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWorkRequest))
            )
            .andExpect(status().isOk());

        // Validate the WorkRequest in the database
        List<WorkRequest> workRequestList = workRequestRepository.findAll();
        assertThat(workRequestList).hasSize(databaseSizeBeforeUpdate);
        WorkRequest testWorkRequest = workRequestList.get(workRequestList.size() - 1);
        assertThat(testWorkRequest.getProjectId()).isEqualTo(UPDATED_PROJECT_ID);
        assertThat(testWorkRequest.getWorkRequest()).isEqualTo(UPDATED_WORK_REQUEST);
        assertThat(testWorkRequest.getWorkRequestDescription()).isEqualTo(UPDATED_WORK_REQUEST_DESCRIPTION);
        assertThat(testWorkRequest.getWorkRwquestPhase()).isEqualTo(UPDATED_WORK_RWQUEST_PHASE);
        assertThat(testWorkRequest.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testWorkRequest.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testWorkRequest.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testWorkRequest.getDesign()).isEqualTo(UPDATED_DESIGN);
    }

    @Test
    @Transactional
    void patchNonExistingWorkRequest() throws Exception {
        int databaseSizeBeforeUpdate = workRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        workRequest.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, workRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(workRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkRequest in the database
        List<WorkRequest> workRequestList = workRequestRepository.findAll();
        assertThat(workRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWorkRequest() throws Exception {
        int databaseSizeBeforeUpdate = workRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        workRequest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(workRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkRequest in the database
        List<WorkRequest> workRequestList = workRequestRepository.findAll();
        assertThat(workRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWorkRequest() throws Exception {
        int databaseSizeBeforeUpdate = workRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        workRequest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkRequestMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(workRequest))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WorkRequest in the database
        List<WorkRequest> workRequestList = workRequestRepository.findAll();
        assertThat(workRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteWorkRequest() throws Exception {
        // Initialize the database
        workRequestRepository.saveAndFlush(workRequest);
        workRequestRepository.save(workRequest);
        workRequestSearchRepository.save(workRequest);

        int databaseSizeBeforeDelete = workRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the workRequest
        restWorkRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, workRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<WorkRequest> workRequestList = workRequestRepository.findAll();
        assertThat(workRequestList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchWorkRequest() throws Exception {
        // Initialize the database
        workRequest = workRequestRepository.saveAndFlush(workRequest);
        workRequestSearchRepository.save(workRequest);

        // Search the workRequest
        restWorkRequestMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + workRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].projectId").value(hasItem(DEFAULT_PROJECT_ID)))
            .andExpect(jsonPath("$.[*].workRequest").value(hasItem(DEFAULT_WORK_REQUEST)))
            .andExpect(jsonPath("$.[*].workRequestDescription").value(hasItem(DEFAULT_WORK_REQUEST_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].workRwquestPhase").value(hasItem(DEFAULT_WORK_RWQUEST_PHASE)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].design").value(hasItem(DEFAULT_DESIGN.toString())));
    }
}
