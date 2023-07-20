package com.bujisoft.mybuji.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.bujisoft.mybuji.domain.WorkInfo;
import com.bujisoft.mybuji.repository.WorkInfoRepository;
import com.bujisoft.mybuji.repository.search.WorkInfoSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link WorkInfo}.
 */
@Service
@Transactional
public class WorkInfoService {

    private final Logger log = LoggerFactory.getLogger(WorkInfoService.class);

    private final WorkInfoRepository workInfoRepository;

    private final WorkInfoSearchRepository workInfoSearchRepository;

    public WorkInfoService(WorkInfoRepository workInfoRepository, WorkInfoSearchRepository workInfoSearchRepository) {
        this.workInfoRepository = workInfoRepository;
        this.workInfoSearchRepository = workInfoSearchRepository;
    }

    /**
     * Save a workInfo.
     *
     * @param workInfo the entity to save.
     * @return the persisted entity.
     */
    public WorkInfo save(WorkInfo workInfo) {
        log.debug("Request to save WorkInfo : {}", workInfo);
        WorkInfo result = workInfoRepository.save(workInfo);
        workInfoSearchRepository.index(result);
        return result;
    }

    /**
     * Update a workInfo.
     *
     * @param workInfo the entity to save.
     * @return the persisted entity.
     */
    public WorkInfo update(WorkInfo workInfo) {
        log.debug("Request to update WorkInfo : {}", workInfo);
        WorkInfo result = workInfoRepository.save(workInfo);
        workInfoSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a workInfo.
     *
     * @param workInfo the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<WorkInfo> partialUpdate(WorkInfo workInfo) {
        log.debug("Request to partially update WorkInfo : {}", workInfo);

        return workInfoRepository
            .findById(workInfo.getId())
            .map(existingWorkInfo -> {
                if (workInfo.getScopeAct() != null) {
                    existingWorkInfo.setScopeAct(workInfo.getScopeAct());
                }
                if (workInfo.getDesignAct() != null) {
                    existingWorkInfo.setDesignAct(workInfo.getDesignAct());
                }
                if (workInfo.getCodeAct() != null) {
                    existingWorkInfo.setCodeAct(workInfo.getCodeAct());
                }
                if (workInfo.getSyst1Act() != null) {
                    existingWorkInfo.setSyst1Act(workInfo.getSyst1Act());
                }
                if (workInfo.getSyst2Act() != null) {
                    existingWorkInfo.setSyst2Act(workInfo.getSyst2Act());
                }
                if (workInfo.getQualAct() != null) {
                    existingWorkInfo.setQualAct(workInfo.getQualAct());
                }
                if (workInfo.getImpAct() != null) {
                    existingWorkInfo.setImpAct(workInfo.getImpAct());
                }
                if (workInfo.getPostImpAct() != null) {
                    existingWorkInfo.setPostImpAct(workInfo.getPostImpAct());
                }
                if (workInfo.getTotalAct() != null) {
                    existingWorkInfo.setTotalAct(workInfo.getTotalAct());
                }

                return existingWorkInfo;
            })
            .map(workInfoRepository::save)
            .map(savedWorkInfo -> {
                workInfoSearchRepository.save(savedWorkInfo);

                return savedWorkInfo;
            });
    }

    /**
     * Get all the workInfos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WorkInfo> findAll(Pageable pageable) {
        log.debug("Request to get all WorkInfos");
        return workInfoRepository.findAll(pageable);
    }

    /**
     * Get all the workInfos with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<WorkInfo> findAllWithEagerRelationships(Pageable pageable) {
        return workInfoRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one workInfo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WorkInfo> findOne(Long id) {
        log.debug("Request to get WorkInfo : {}", id);
        return workInfoRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the workInfo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete WorkInfo : {}", id);
        workInfoRepository.deleteById(id);
        workInfoSearchRepository.deleteById(id);
    }

    /**
     * Search for the workInfo corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WorkInfo> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of WorkInfos for query {}", query);
        return workInfoSearchRepository.search(query, pageable);
    }
}
