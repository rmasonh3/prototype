package com.bujisoft.mybuji.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.bujisoft.mybuji.domain.ElementTypes;
import com.bujisoft.mybuji.repository.ElementTypesRepository;
import com.bujisoft.mybuji.repository.search.ElementTypesSearchRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ElementTypes}.
 */
@Service
@Transactional
public class ElementTypesService {

    private final Logger log = LoggerFactory.getLogger(ElementTypesService.class);

    private final ElementTypesRepository elementTypesRepository;

    private final ElementTypesSearchRepository elementTypesSearchRepository;

    public ElementTypesService(ElementTypesRepository elementTypesRepository, ElementTypesSearchRepository elementTypesSearchRepository) {
        this.elementTypesRepository = elementTypesRepository;
        this.elementTypesSearchRepository = elementTypesSearchRepository;
    }

    /**
     * Save a elementTypes.
     *
     * @param elementTypes the entity to save.
     * @return the persisted entity.
     */
    public ElementTypes save(ElementTypes elementTypes) {
        log.debug("Request to save ElementTypes : {}", elementTypes);
        ElementTypes result = elementTypesRepository.save(elementTypes);
        elementTypesSearchRepository.index(result);
        return result;
    }

    /**
     * Update a elementTypes.
     *
     * @param elementTypes the entity to save.
     * @return the persisted entity.
     */
    public ElementTypes update(ElementTypes elementTypes) {
        log.debug("Request to update ElementTypes : {}", elementTypes);
        ElementTypes result = elementTypesRepository.save(elementTypes);
        elementTypesSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a elementTypes.
     *
     * @param elementTypes the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ElementTypes> partialUpdate(ElementTypes elementTypes) {
        log.debug("Request to partially update ElementTypes : {}", elementTypes);

        return elementTypesRepository
            .findById(elementTypes.getId())
            .map(existingElementTypes -> {
                if (elementTypes.getElement() != null) {
                    existingElementTypes.setElement(elementTypes.getElement());
                }
                if (elementTypes.getType() != null) {
                    existingElementTypes.setType(elementTypes.getType());
                }

                return existingElementTypes;
            })
            .map(elementTypesRepository::save)
            .map(savedElementTypes -> {
                elementTypesSearchRepository.save(savedElementTypes);

                return savedElementTypes;
            });
    }

    /**
     * Get all the elementTypes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ElementTypes> findAll() {
        log.debug("Request to get all ElementTypes");
        return elementTypesRepository.findAll();
    }

    /**
     * Get one elementTypes by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ElementTypes> findOne(Long id) {
        log.debug("Request to get ElementTypes : {}", id);
        return elementTypesRepository.findById(id);
    }

    /**
     * Delete the elementTypes by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ElementTypes : {}", id);
        elementTypesRepository.deleteById(id);
        elementTypesSearchRepository.deleteById(id);
    }

    /**
     * Search for the elementTypes corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ElementTypes> search(String query) {
        log.debug("Request to search ElementTypes for query {}", query);
        return StreamSupport.stream(elementTypesSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
