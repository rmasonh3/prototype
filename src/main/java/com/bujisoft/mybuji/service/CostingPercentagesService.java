package com.bujisoft.mybuji.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.bujisoft.mybuji.domain.CostingPercentages;
import com.bujisoft.mybuji.repository.CostingPercentagesRepository;
import com.bujisoft.mybuji.repository.search.CostingPercentagesSearchRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link CostingPercentages}.
 */
@Service
@Transactional
public class CostingPercentagesService {

    private final Logger log = LoggerFactory.getLogger(CostingPercentagesService.class);

    private final CostingPercentagesRepository costingPercentagesRepository;

    private final CostingPercentagesSearchRepository costingPercentagesSearchRepository;

    public CostingPercentagesService(
        CostingPercentagesRepository costingPercentagesRepository,
        CostingPercentagesSearchRepository costingPercentagesSearchRepository
    ) {
        this.costingPercentagesRepository = costingPercentagesRepository;
        this.costingPercentagesSearchRepository = costingPercentagesSearchRepository;
    }

    /**
     * Save a costingPercentages.
     *
     * @param costingPercentages the entity to save.
     * @return the persisted entity.
     */
    public CostingPercentages save(CostingPercentages costingPercentages) {
        log.debug("Request to save CostingPercentages : {}", costingPercentages);
        CostingPercentages result = costingPercentagesRepository.save(costingPercentages);
        costingPercentagesSearchRepository.index(result);
        return result;
    }

    /**
     * Update a costingPercentages.
     *
     * @param costingPercentages the entity to save.
     * @return the persisted entity.
     */
    public CostingPercentages update(CostingPercentages costingPercentages) {
        log.debug("Request to update CostingPercentages : {}", costingPercentages);
        CostingPercentages result = costingPercentagesRepository.save(costingPercentages);
        costingPercentagesSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a costingPercentages.
     *
     * @param costingPercentages the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CostingPercentages> partialUpdate(CostingPercentages costingPercentages) {
        log.debug("Request to partially update CostingPercentages : {}", costingPercentages);

        return costingPercentagesRepository
            .findById(costingPercentages.getId())
            .map(existingCostingPercentages -> {
                if (costingPercentages.getCostingSystem() != null) {
                    existingCostingPercentages.setCostingSystem(costingPercentages.getCostingSystem());
                }
                if (costingPercentages.getCostingQual() != null) {
                    existingCostingPercentages.setCostingQual(costingPercentages.getCostingQual());
                }
                if (costingPercentages.getCostingImp() != null) {
                    existingCostingPercentages.setCostingImp(costingPercentages.getCostingImp());
                }
                if (costingPercentages.getCostingPostImp() != null) {
                    existingCostingPercentages.setCostingPostImp(costingPercentages.getCostingPostImp());
                }
                if (costingPercentages.getActive() != null) {
                    existingCostingPercentages.setActive(costingPercentages.getActive());
                }
                if (costingPercentages.getDateAdded() != null) {
                    existingCostingPercentages.setDateAdded(costingPercentages.getDateAdded());
                }

                return existingCostingPercentages;
            })
            .map(costingPercentagesRepository::save)
            .map(savedCostingPercentages -> {
                costingPercentagesSearchRepository.save(savedCostingPercentages);

                return savedCostingPercentages;
            });
    }

    /**
     * Get all the costingPercentages.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CostingPercentages> findAll() {
        log.debug("Request to get all CostingPercentages");
        return costingPercentagesRepository.findAll();
    }

    /**
     * Get one costingPercentages by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CostingPercentages> findOne(Long id) {
        log.debug("Request to get CostingPercentages : {}", id);
        return costingPercentagesRepository.findById(id);
    }

    /**
     * Delete the costingPercentages by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete CostingPercentages : {}", id);
        costingPercentagesRepository.deleteById(id);
        costingPercentagesSearchRepository.deleteById(id);
    }

    /**
     * Search for the costingPercentages corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CostingPercentages> search(String query) {
        log.debug("Request to search CostingPercentages for query {}", query);
        return StreamSupport.stream(costingPercentagesSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
