package com.bujisoft.mybuji.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.bujisoft.mybuji.domain.EstimateBasis;
import com.bujisoft.mybuji.repository.EstimateBasisRepository;
import com.bujisoft.mybuji.service.EstimateBasisService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.bujisoft.mybuji.domain.EstimateBasis}.
 */
@RestController
@RequestMapping("/api")
public class EstimateBasisResource {

    private final Logger log = LoggerFactory.getLogger(EstimateBasisResource.class);

    private static final String ENTITY_NAME = "estimateBasis";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EstimateBasisService estimateBasisService;

    private final EstimateBasisRepository estimateBasisRepository;

    public EstimateBasisResource(EstimateBasisService estimateBasisService, EstimateBasisRepository estimateBasisRepository) {
        this.estimateBasisService = estimateBasisService;
        this.estimateBasisRepository = estimateBasisRepository;
    }

    /**
     * {@code POST  /estimate-bases} : Create a new estimateBasis.
     *
     * @param estimateBasis the estimateBasis to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new estimateBasis, or with status {@code 400 (Bad Request)} if the estimateBasis has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/estimate-bases")
    public ResponseEntity<EstimateBasis> createEstimateBasis(@Valid @RequestBody EstimateBasis estimateBasis) throws URISyntaxException {
        log.debug("REST request to save EstimateBasis : {}", estimateBasis);
        if (estimateBasis.getId() != null) {
            throw new BadRequestAlertException("A new estimateBasis cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EstimateBasis result = estimateBasisService.save(estimateBasis);
        return ResponseEntity
            .created(new URI("/api/estimate-bases/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /estimate-bases/:id} : Updates an existing estimateBasis.
     *
     * @param id the id of the estimateBasis to save.
     * @param estimateBasis the estimateBasis to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated estimateBasis,
     * or with status {@code 400 (Bad Request)} if the estimateBasis is not valid,
     * or with status {@code 500 (Internal Server Error)} if the estimateBasis couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/estimate-bases/{id}")
    public ResponseEntity<EstimateBasis> updateEstimateBasis(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EstimateBasis estimateBasis
    ) throws URISyntaxException {
        log.debug("REST request to update EstimateBasis : {}, {}", id, estimateBasis);
        if (estimateBasis.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, estimateBasis.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!estimateBasisRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        EstimateBasis result = estimateBasisService.update(estimateBasis);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, estimateBasis.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /estimate-bases/:id} : Partial updates given fields of an existing estimateBasis, field will ignore if it is null
     *
     * @param id the id of the estimateBasis to save.
     * @param estimateBasis the estimateBasis to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated estimateBasis,
     * or with status {@code 400 (Bad Request)} if the estimateBasis is not valid,
     * or with status {@code 404 (Not Found)} if the estimateBasis is not found,
     * or with status {@code 500 (Internal Server Error)} if the estimateBasis couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/estimate-bases/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EstimateBasis> partialUpdateEstimateBasis(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EstimateBasis estimateBasis
    ) throws URISyntaxException {
        log.debug("REST request to partial update EstimateBasis partially : {}, {}", id, estimateBasis);
        if (estimateBasis.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, estimateBasis.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!estimateBasisRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EstimateBasis> result = estimateBasisService.partialUpdate(estimateBasis);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, estimateBasis.getId().toString())
        );
    }

    /**
     * {@code GET  /estimate-bases} : get all the estimateBases.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of estimateBases in body.
     */
    @GetMapping("/estimate-bases")
    public ResponseEntity<List<EstimateBasis>> getAllEstimateBases(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of EstimateBases");
        Page<EstimateBasis> page;
        if (eagerload) {
            page = estimateBasisService.findAllWithEagerRelationships(pageable);
        } else {
            page = estimateBasisService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /estimate-bases/:id} : get the "id" estimateBasis.
     *
     * @param id the id of the estimateBasis to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the estimateBasis, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/estimate-bases/{id}")
    public ResponseEntity<EstimateBasis> getEstimateBasis(@PathVariable Long id) {
        log.debug("REST request to get EstimateBasis : {}", id);
        Optional<EstimateBasis> estimateBasis = estimateBasisService.findOne(id);
        return ResponseUtil.wrapOrNotFound(estimateBasis);
    }

    /**
     * {@code DELETE  /estimate-bases/:id} : delete the "id" estimateBasis.
     *
     * @param id the id of the estimateBasis to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/estimate-bases/{id}")
    public ResponseEntity<Void> deleteEstimateBasis(@PathVariable Long id) {
        log.debug("REST request to delete EstimateBasis : {}", id);
        estimateBasisService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/estimate-bases?query=:query} : search for the estimateBasis corresponding
     * to the query.
     *
     * @param query the query of the estimateBasis search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/estimate-bases")
    public ResponseEntity<List<EstimateBasis>> searchEstimateBases(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of EstimateBases for query {}", query);
        Page<EstimateBasis> page = estimateBasisService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
