package com.bujisoft.mybuji.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.bujisoft.mybuji.domain.ScopeDesign;
import com.bujisoft.mybuji.repository.ScopeDesignRepository;
import com.bujisoft.mybuji.service.ScopeDesignService;
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
 * REST controller for managing {@link com.bujisoft.mybuji.domain.ScopeDesign}.
 */
@RestController
@RequestMapping("/api")
public class ScopeDesignResource {

    private final Logger log = LoggerFactory.getLogger(ScopeDesignResource.class);

    private static final String ENTITY_NAME = "scopeDesign";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ScopeDesignService scopeDesignService;

    private final ScopeDesignRepository scopeDesignRepository;

    public ScopeDesignResource(ScopeDesignService scopeDesignService, ScopeDesignRepository scopeDesignRepository) {
        this.scopeDesignService = scopeDesignService;
        this.scopeDesignRepository = scopeDesignRepository;
    }

    /**
     * {@code POST  /scope-designs} : Create a new scopeDesign.
     *
     * @param scopeDesign the scopeDesign to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new scopeDesign, or with status {@code 400 (Bad Request)} if the scopeDesign has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/scope-designs")
    public ResponseEntity<ScopeDesign> createScopeDesign(@Valid @RequestBody ScopeDesign scopeDesign) throws URISyntaxException {
        log.debug("REST request to save ScopeDesign : {}", scopeDesign);
        if (scopeDesign.getId() != null) {
            throw new BadRequestAlertException("A new scopeDesign cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ScopeDesign result = scopeDesignService.save(scopeDesign);
        return ResponseEntity
            .created(new URI("/api/scope-designs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /scope-designs/:id} : Updates an existing scopeDesign.
     *
     * @param id the id of the scopeDesign to save.
     * @param scopeDesign the scopeDesign to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scopeDesign,
     * or with status {@code 400 (Bad Request)} if the scopeDesign is not valid,
     * or with status {@code 500 (Internal Server Error)} if the scopeDesign couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/scope-designs/{id}")
    public ResponseEntity<ScopeDesign> updateScopeDesign(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ScopeDesign scopeDesign
    ) throws URISyntaxException {
        log.debug("REST request to update ScopeDesign : {}, {}", id, scopeDesign);
        if (scopeDesign.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scopeDesign.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scopeDesignRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ScopeDesign result = scopeDesignService.update(scopeDesign);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, scopeDesign.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /scope-designs/:id} : Partial updates given fields of an existing scopeDesign, field will ignore if it is null
     *
     * @param id the id of the scopeDesign to save.
     * @param scopeDesign the scopeDesign to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scopeDesign,
     * or with status {@code 400 (Bad Request)} if the scopeDesign is not valid,
     * or with status {@code 404 (Not Found)} if the scopeDesign is not found,
     * or with status {@code 500 (Internal Server Error)} if the scopeDesign couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/scope-designs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ScopeDesign> partialUpdateScopeDesign(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ScopeDesign scopeDesign
    ) throws URISyntaxException {
        log.debug("REST request to partial update ScopeDesign partially : {}, {}", id, scopeDesign);
        if (scopeDesign.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scopeDesign.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scopeDesignRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ScopeDesign> result = scopeDesignService.partialUpdate(scopeDesign);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, scopeDesign.getId().toString())
        );
    }

    /**
     * {@code GET  /scope-designs} : get all the scopeDesigns.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of scopeDesigns in body.
     */
    @GetMapping("/scope-designs")
    public ResponseEntity<List<ScopeDesign>> getAllScopeDesigns(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of ScopeDesigns");
        Page<ScopeDesign> page;
        if (eagerload) {
            page = scopeDesignService.findAllWithEagerRelationships(pageable);
        } else {
            page = scopeDesignService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /scope-designs/:id} : get the "id" scopeDesign.
     *
     * @param id the id of the scopeDesign to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scopeDesign, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/scope-designs/{id}")
    public ResponseEntity<ScopeDesign> getScopeDesign(@PathVariable Long id) {
        log.debug("REST request to get ScopeDesign : {}", id);
        Optional<ScopeDesign> scopeDesign = scopeDesignService.findOne(id);
        return ResponseUtil.wrapOrNotFound(scopeDesign);
    }

    /**
     * {@code DELETE  /scope-designs/:id} : delete the "id" scopeDesign.
     *
     * @param id the id of the scopeDesign to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/scope-designs/{id}")
    public ResponseEntity<Void> deleteScopeDesign(@PathVariable Long id) {
        log.debug("REST request to delete ScopeDesign : {}", id);
        scopeDesignService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/scope-designs?query=:query} : search for the scopeDesign corresponding
     * to the query.
     *
     * @param query the query of the scopeDesign search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/scope-designs")
    public ResponseEntity<List<ScopeDesign>> searchScopeDesigns(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of ScopeDesigns for query {}", query);
        Page<ScopeDesign> page = scopeDesignService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
