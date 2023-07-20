package com.bujisoft.mybuji.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.bujisoft.mybuji.domain.EstimateBasis;
import com.bujisoft.mybuji.repository.EstimateBasisRepository;
import com.bujisoft.mybuji.repository.search.EstimateBasisSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link EstimateBasis}.
 */
@Service
@Transactional
public class EstimateBasisService {

    private final Logger log = LoggerFactory.getLogger(EstimateBasisService.class);

    private final EstimateBasisRepository estimateBasisRepository;

    private final EstimateBasisSearchRepository estimateBasisSearchRepository;

    public EstimateBasisService(
        EstimateBasisRepository estimateBasisRepository,
        EstimateBasisSearchRepository estimateBasisSearchRepository
    ) {
        this.estimateBasisRepository = estimateBasisRepository;
        this.estimateBasisSearchRepository = estimateBasisSearchRepository;
    }

    /**
     * Save a estimateBasis.
     *
     * @param estimateBasis the entity to save.
     * @return the persisted entity.
     */
    public EstimateBasis save(EstimateBasis estimateBasis) {
        log.debug("Request to save EstimateBasis : {}", estimateBasis);
        EstimateBasis result = estimateBasisRepository.save(estimateBasis);
        estimateBasisSearchRepository.index(result);
        return result;
    }

    /**
     * Update a estimateBasis.
     *
     * @param estimateBasis the entity to save.
     * @return the persisted entity.
     */
    public EstimateBasis update(EstimateBasis estimateBasis) {
        log.debug("Request to update EstimateBasis : {}", estimateBasis);
        EstimateBasis result = estimateBasisRepository.save(estimateBasis);
        estimateBasisSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a estimateBasis.
     *
     * @param estimateBasis the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EstimateBasis> partialUpdate(EstimateBasis estimateBasis) {
        log.debug("Request to partially update EstimateBasis : {}", estimateBasis);

        return estimateBasisRepository
            .findById(estimateBasis.getId())
            .map(existingEstimateBasis -> {
                if (estimateBasis.getSubsystemId() != null) {
                    existingEstimateBasis.setSubsystemId(estimateBasis.getSubsystemId());
                }
                if (estimateBasis.getBasisOfEstimate() != null) {
                    existingEstimateBasis.setBasisOfEstimate(estimateBasis.getBasisOfEstimate());
                }
                if (estimateBasis.getAssumptions() != null) {
                    existingEstimateBasis.setAssumptions(estimateBasis.getAssumptions());
                }
                if (estimateBasis.getLastUpdate() != null) {
                    existingEstimateBasis.setLastUpdate(estimateBasis.getLastUpdate());
                }

                return existingEstimateBasis;
            })
            .map(estimateBasisRepository::save)
            .map(savedEstimateBasis -> {
                estimateBasisSearchRepository.save(savedEstimateBasis);

                return savedEstimateBasis;
            });
    }

    /**
     * Get all the estimateBases.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EstimateBasis> findAll(Pageable pageable) {
        log.debug("Request to get all EstimateBases");
        return estimateBasisRepository.findAll(pageable);
    }

    /**
     * Get all the estimateBases with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<EstimateBasis> findAllWithEagerRelationships(Pageable pageable) {
        return estimateBasisRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one estimateBasis by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EstimateBasis> findOne(Long id) {
        log.debug("Request to get EstimateBasis : {}", id);
        return estimateBasisRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the estimateBasis by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete EstimateBasis : {}", id);
        estimateBasisRepository.deleteById(id);
        estimateBasisSearchRepository.deleteById(id);
    }

    /**
     * Search for the estimateBasis corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EstimateBasis> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of EstimateBases for query {}", query);
        return estimateBasisSearchRepository.search(query, pageable);
    }
}
