package com.bujisoft.mybuji.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.bujisoft.mybuji.domain.EstimateDesign;
import com.bujisoft.mybuji.repository.EstimateDesignRepository;
import com.bujisoft.mybuji.repository.search.EstimateDesignSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link EstimateDesign}.
 */
@Service
@Transactional
public class EstimateDesignService {

    private final Logger log = LoggerFactory.getLogger(EstimateDesignService.class);

    private final EstimateDesignRepository estimateDesignRepository;

    private final EstimateDesignSearchRepository estimateDesignSearchRepository;

    public EstimateDesignService(
        EstimateDesignRepository estimateDesignRepository,
        EstimateDesignSearchRepository estimateDesignSearchRepository
    ) {
        this.estimateDesignRepository = estimateDesignRepository;
        this.estimateDesignSearchRepository = estimateDesignSearchRepository;
    }

    /**
     * Save a estimateDesign.
     *
     * @param estimateDesign the entity to save.
     * @return the persisted entity.
     */
    public EstimateDesign save(EstimateDesign estimateDesign) {
        log.debug("Request to save EstimateDesign : {}", estimateDesign);
        EstimateDesign result = estimateDesignRepository.save(estimateDesign);
        estimateDesignSearchRepository.index(result);
        return result;
    }

    /**
     * Update a estimateDesign.
     *
     * @param estimateDesign the entity to save.
     * @return the persisted entity.
     */
    public EstimateDesign update(EstimateDesign estimateDesign) {
        log.debug("Request to update EstimateDesign : {}", estimateDesign);
        EstimateDesign result = estimateDesignRepository.save(estimateDesign);
        estimateDesignSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a estimateDesign.
     *
     * @param estimateDesign the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EstimateDesign> partialUpdate(EstimateDesign estimateDesign) {
        log.debug("Request to partially update EstimateDesign : {}", estimateDesign);

        return estimateDesignRepository
            .findById(estimateDesign.getId())
            .map(existingEstimateDesign -> {
                if (estimateDesign.getQpproachNumber() != null) {
                    existingEstimateDesign.setQpproachNumber(estimateDesign.getQpproachNumber());
                }
                if (estimateDesign.getComplexity() != null) {
                    existingEstimateDesign.setComplexity(estimateDesign.getComplexity());
                }

                return existingEstimateDesign;
            })
            .map(estimateDesignRepository::save)
            .map(savedEstimateDesign -> {
                estimateDesignSearchRepository.save(savedEstimateDesign);

                return savedEstimateDesign;
            });
    }

    /**
     * Get all the estimateDesigns.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EstimateDesign> findAll(Pageable pageable) {
        log.debug("Request to get all EstimateDesigns");
        return estimateDesignRepository.findAll(pageable);
    }

    /**
     * Get all the estimateDesigns with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<EstimateDesign> findAllWithEagerRelationships(Pageable pageable) {
        return estimateDesignRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one estimateDesign by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EstimateDesign> findOne(Long id) {
        log.debug("Request to get EstimateDesign : {}", id);
        return estimateDesignRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the estimateDesign by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete EstimateDesign : {}", id);
        estimateDesignRepository.deleteById(id);
        estimateDesignSearchRepository.deleteById(id);
    }

    /**
     * Search for the estimateDesign corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EstimateDesign> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of EstimateDesigns for query {}", query);
        return estimateDesignSearchRepository.search(query, pageable);
    }
}
