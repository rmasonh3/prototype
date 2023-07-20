package com.bujisoft.mybuji.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.bujisoft.mybuji.domain.ScopeDesign;
import com.bujisoft.mybuji.repository.ScopeDesignRepository;
import com.bujisoft.mybuji.repository.search.ScopeDesignSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ScopeDesign}.
 */
@Service
@Transactional
public class ScopeDesignService {

    private final Logger log = LoggerFactory.getLogger(ScopeDesignService.class);

    private final ScopeDesignRepository scopeDesignRepository;

    private final ScopeDesignSearchRepository scopeDesignSearchRepository;

    public ScopeDesignService(ScopeDesignRepository scopeDesignRepository, ScopeDesignSearchRepository scopeDesignSearchRepository) {
        this.scopeDesignRepository = scopeDesignRepository;
        this.scopeDesignSearchRepository = scopeDesignSearchRepository;
    }

    /**
     * Save a scopeDesign.
     *
     * @param scopeDesign the entity to save.
     * @return the persisted entity.
     */
    public ScopeDesign save(ScopeDesign scopeDesign) {
        log.debug("Request to save ScopeDesign : {}", scopeDesign);
        ScopeDesign result = scopeDesignRepository.save(scopeDesign);
        scopeDesignSearchRepository.index(result);
        return result;
    }

    /**
     * Update a scopeDesign.
     *
     * @param scopeDesign the entity to save.
     * @return the persisted entity.
     */
    public ScopeDesign update(ScopeDesign scopeDesign) {
        log.debug("Request to update ScopeDesign : {}", scopeDesign);
        ScopeDesign result = scopeDesignRepository.save(scopeDesign);
        scopeDesignSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a scopeDesign.
     *
     * @param scopeDesign the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ScopeDesign> partialUpdate(ScopeDesign scopeDesign) {
        log.debug("Request to partially update ScopeDesign : {}", scopeDesign);

        return scopeDesignRepository
            .findById(scopeDesign.getId())
            .map(existingScopeDesign -> {
                if (scopeDesign.getDesignEstimate() != null) {
                    existingScopeDesign.setDesignEstimate(scopeDesign.getDesignEstimate());
                }
                if (scopeDesign.getCodeEstimate() != null) {
                    existingScopeDesign.setCodeEstimate(scopeDesign.getCodeEstimate());
                }
                if (scopeDesign.getSyst1Estimate() != null) {
                    existingScopeDesign.setSyst1Estimate(scopeDesign.getSyst1Estimate());
                }
                if (scopeDesign.getSyst2Estimate() != null) {
                    existingScopeDesign.setSyst2Estimate(scopeDesign.getSyst2Estimate());
                }
                if (scopeDesign.getQualEstimate() != null) {
                    existingScopeDesign.setQualEstimate(scopeDesign.getQualEstimate());
                }
                if (scopeDesign.getImpEstimate() != null) {
                    existingScopeDesign.setImpEstimate(scopeDesign.getImpEstimate());
                }
                if (scopeDesign.getPostImpEstimate() != null) {
                    existingScopeDesign.setPostImpEstimate(scopeDesign.getPostImpEstimate());
                }
                if (scopeDesign.getTotalHours() != null) {
                    existingScopeDesign.setTotalHours(scopeDesign.getTotalHours());
                }

                return existingScopeDesign;
            })
            .map(scopeDesignRepository::save)
            .map(savedScopeDesign -> {
                scopeDesignSearchRepository.save(savedScopeDesign);

                return savedScopeDesign;
            });
    }

    /**
     * Get all the scopeDesigns.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ScopeDesign> findAll(Pageable pageable) {
        log.debug("Request to get all ScopeDesigns");
        return scopeDesignRepository.findAll(pageable);
    }

    /**
     * Get all the scopeDesigns with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ScopeDesign> findAllWithEagerRelationships(Pageable pageable) {
        return scopeDesignRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one scopeDesign by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ScopeDesign> findOne(Long id) {
        log.debug("Request to get ScopeDesign : {}", id);
        return scopeDesignRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the scopeDesign by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ScopeDesign : {}", id);
        scopeDesignRepository.deleteById(id);
        scopeDesignSearchRepository.deleteById(id);
    }

    /**
     * Search for the scopeDesign corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ScopeDesign> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ScopeDesigns for query {}", query);
        return scopeDesignSearchRepository.search(query, pageable);
    }
}
