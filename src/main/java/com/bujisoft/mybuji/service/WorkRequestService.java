package com.bujisoft.mybuji.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.bujisoft.mybuji.domain.WorkRequest;
import com.bujisoft.mybuji.repository.WorkRequestRepository;
import com.bujisoft.mybuji.repository.search.WorkRequestSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link WorkRequest}.
 */
@Service
@Transactional
public class WorkRequestService {

    private final Logger log = LoggerFactory.getLogger(WorkRequestService.class);

    private final WorkRequestRepository workRequestRepository;

    private final WorkRequestSearchRepository workRequestSearchRepository;

    public WorkRequestService(WorkRequestRepository workRequestRepository, WorkRequestSearchRepository workRequestSearchRepository) {
        this.workRequestRepository = workRequestRepository;
        this.workRequestSearchRepository = workRequestSearchRepository;
    }

    /**
     * Save a workRequest.
     *
     * @param workRequest the entity to save.
     * @return the persisted entity.
     */
    public WorkRequest save(WorkRequest workRequest) {
        log.debug("Request to save WorkRequest : {}", workRequest);
        WorkRequest result = workRequestRepository.save(workRequest);
        workRequestSearchRepository.index(result);
        return result;
    }

    /**
     * Update a workRequest.
     *
     * @param workRequest the entity to save.
     * @return the persisted entity.
     */
    public WorkRequest update(WorkRequest workRequest) {
        log.debug("Request to update WorkRequest : {}", workRequest);
        WorkRequest result = workRequestRepository.save(workRequest);
        workRequestSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a workRequest.
     *
     * @param workRequest the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<WorkRequest> partialUpdate(WorkRequest workRequest) {
        log.debug("Request to partially update WorkRequest : {}", workRequest);

        return workRequestRepository
            .findById(workRequest.getId())
            .map(existingWorkRequest -> {
                if (workRequest.getProjectId() != null) {
                    existingWorkRequest.setProjectId(workRequest.getProjectId());
                }
                if (workRequest.getWorkRequest() != null) {
                    existingWorkRequest.setWorkRequest(workRequest.getWorkRequest());
                }
                if (workRequest.getWorkRequestDescription() != null) {
                    existingWorkRequest.setWorkRequestDescription(workRequest.getWorkRequestDescription());
                }
                if (workRequest.getWorkRwquestPhase() != null) {
                    existingWorkRequest.setWorkRwquestPhase(workRequest.getWorkRwquestPhase());
                }
                if (workRequest.getStartDate() != null) {
                    existingWorkRequest.setStartDate(workRequest.getStartDate());
                }
                if (workRequest.getEndDate() != null) {
                    existingWorkRequest.setEndDate(workRequest.getEndDate());
                }
                if (workRequest.getStatus() != null) {
                    existingWorkRequest.setStatus(workRequest.getStatus());
                }
                if (workRequest.getDesign() != null) {
                    existingWorkRequest.setDesign(workRequest.getDesign());
                }

                return existingWorkRequest;
            })
            .map(workRequestRepository::save)
            .map(savedWorkRequest -> {
                workRequestSearchRepository.save(savedWorkRequest);

                return savedWorkRequest;
            });
    }

    /**
     * Get all the workRequests.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WorkRequest> findAll(Pageable pageable) {
        log.debug("Request to get all WorkRequests");
        return workRequestRepository.findAll(pageable);
    }

    /**
     * Get one workRequest by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WorkRequest> findOne(Long id) {
        log.debug("Request to get WorkRequest : {}", id);
        return workRequestRepository.findById(id);
    }

    /**
     * Delete the workRequest by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete WorkRequest : {}", id);
        workRequestRepository.deleteById(id);
        workRequestSearchRepository.deleteById(id);
    }

    /**
     * Search for the workRequest corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WorkRequest> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of WorkRequests for query {}", query);
        return workRequestSearchRepository.search(query, pageable);
    }
}
