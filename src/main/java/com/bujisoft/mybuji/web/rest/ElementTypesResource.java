package com.bujisoft.mybuji.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.bujisoft.mybuji.domain.ElementTypes;
import com.bujisoft.mybuji.repository.ElementTypesRepository;
import com.bujisoft.mybuji.service.ElementTypesService;
import com.bujisoft.mybuji.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.bujisoft.mybuji.domain.ElementTypes}.
 */
@RestController
@RequestMapping("/api")
public class ElementTypesResource {

    private final Logger log = LoggerFactory.getLogger(ElementTypesResource.class);

    private static final String ENTITY_NAME = "elementTypes";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ElementTypesService elementTypesService;

    private final ElementTypesRepository elementTypesRepository;

    public ElementTypesResource(ElementTypesService elementTypesService, ElementTypesRepository elementTypesRepository) {
        this.elementTypesService = elementTypesService;
        this.elementTypesRepository = elementTypesRepository;
    }

    /**
     * {@code POST  /element-types} : Create a new elementTypes.
     *
     * @param elementTypes the elementTypes to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new elementTypes, or with status {@code 400 (Bad Request)} if the elementTypes has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/element-types")
    public ResponseEntity<ElementTypes> createElementTypes(@Valid @RequestBody ElementTypes elementTypes) throws URISyntaxException {
        log.debug("REST request to save ElementTypes : {}", elementTypes);
        if (elementTypes.getId() != null) {
            throw new BadRequestAlertException("A new elementTypes cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ElementTypes result = elementTypesService.save(elementTypes);
        return ResponseEntity
            .created(new URI("/api/element-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /element-types/:id} : Updates an existing elementTypes.
     *
     * @param id the id of the elementTypes to save.
     * @param elementTypes the elementTypes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated elementTypes,
     * or with status {@code 400 (Bad Request)} if the elementTypes is not valid,
     * or with status {@code 500 (Internal Server Error)} if the elementTypes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/element-types/{id}")
    public ResponseEntity<ElementTypes> updateElementTypes(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ElementTypes elementTypes
    ) throws URISyntaxException {
        log.debug("REST request to update ElementTypes : {}, {}", id, elementTypes);
        if (elementTypes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, elementTypes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!elementTypesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ElementTypes result = elementTypesService.update(elementTypes);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, elementTypes.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /element-types/:id} : Partial updates given fields of an existing elementTypes, field will ignore if it is null
     *
     * @param id the id of the elementTypes to save.
     * @param elementTypes the elementTypes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated elementTypes,
     * or with status {@code 400 (Bad Request)} if the elementTypes is not valid,
     * or with status {@code 404 (Not Found)} if the elementTypes is not found,
     * or with status {@code 500 (Internal Server Error)} if the elementTypes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/element-types/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ElementTypes> partialUpdateElementTypes(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ElementTypes elementTypes
    ) throws URISyntaxException {
        log.debug("REST request to partial update ElementTypes partially : {}, {}", id, elementTypes);
        if (elementTypes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, elementTypes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!elementTypesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ElementTypes> result = elementTypesService.partialUpdate(elementTypes);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, elementTypes.getId().toString())
        );
    }

    /**
     * {@code GET  /element-types} : get all the elementTypes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of elementTypes in body.
     */
    @GetMapping("/element-types")
    public List<ElementTypes> getAllElementTypes() {
        log.debug("REST request to get all ElementTypes");
        return elementTypesService.findAll();
    }

    /**
     * {@code GET  /element-types/:id} : get the "id" elementTypes.
     *
     * @param id the id of the elementTypes to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the elementTypes, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/element-types/{id}")
    public ResponseEntity<ElementTypes> getElementTypes(@PathVariable Long id) {
        log.debug("REST request to get ElementTypes : {}", id);
        Optional<ElementTypes> elementTypes = elementTypesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(elementTypes);
    }

    /**
     * {@code DELETE  /element-types/:id} : delete the "id" elementTypes.
     *
     * @param id the id of the elementTypes to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/element-types/{id}")
    public ResponseEntity<Void> deleteElementTypes(@PathVariable Long id) {
        log.debug("REST request to delete ElementTypes : {}", id);
        elementTypesService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/element-types?query=:query} : search for the elementTypes corresponding
     * to the query.
     *
     * @param query the query of the elementTypes search.
     * @return the result of the search.
     */
    @GetMapping("/_search/element-types")
    public List<ElementTypes> searchElementTypes(@RequestParam String query) {
        log.debug("REST request to search ElementTypes for query {}", query);
        return elementTypesService.search(query);
    }
}
