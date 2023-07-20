package com.bujisoft.mybuji.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.bujisoft.mybuji.domain.EstimateDesign;
import com.bujisoft.mybuji.repository.EstimateDesignRepository;
import com.bujisoft.mybuji.service.EstimateDesignService;
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
 * REST controller for managing {@link com.bujisoft.mybuji.domain.EstimateDesign}.
 */
@RestController
@RequestMapping("/api")
public class EstimateDesignResource {

    private final Logger log = LoggerFactory.getLogger(EstimateDesignResource.class);

    private static final String ENTITY_NAME = "estimateDesign";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EstimateDesignService estimateDesignService;

    private final EstimateDesignRepository estimateDesignRepository;

    public EstimateDesignResource(EstimateDesignService estimateDesignService, EstimateDesignRepository estimateDesignRepository) {
        this.estimateDesignService = estimateDesignService;
        this.estimateDesignRepository = estimateDesignRepository;
    }

    /**
     * {@code POST  /estimate-designs} : Create a new estimateDesign.
     *
     * @param estimateDesign the estimateDesign to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new estimateDesign, or with status {@code 400 (Bad Request)} if the estimateDesign has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/estimate-designs")
    public ResponseEntity<EstimateDesign> createEstimateDesign(@Valid @RequestBody EstimateDesign estimateDesign)
        throws URISyntaxException {
        log.debug("REST request to save EstimateDesign : {}", estimateDesign);
        if (estimateDesign.getId() != null) {
            throw new BadRequestAlertException("A new estimateDesign cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EstimateDesign result = estimateDesignService.save(estimateDesign);
        return ResponseEntity
            .created(new URI("/api/estimate-designs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /estimate-designs/:id} : Updates an existing estimateDesign.
     *
     * @param id the id of the estimateDesign to save.
     * @param estimateDesign the estimateDesign to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated estimateDesign,
     * or with status {@code 400 (Bad Request)} if the estimateDesign is not valid,
     * or with status {@code 500 (Internal Server Error)} if the estimateDesign couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/estimate-designs/{id}")
    public ResponseEntity<EstimateDesign> updateEstimateDesign(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EstimateDesign estimateDesign
    ) throws URISyntaxException {
        log.debug("REST request to update EstimateDesign : {}, {}", id, estimateDesign);
        if (estimateDesign.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, estimateDesign.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!estimateDesignRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        EstimateDesign result = estimateDesignService.update(estimateDesign);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, estimateDesign.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /estimate-designs/:id} : Partial updates given fields of an existing estimateDesign, field will ignore if it is null
     *
     * @param id the id of the estimateDesign to save.
     * @param estimateDesign the estimateDesign to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated estimateDesign,
     * or with status {@code 400 (Bad Request)} if the estimateDesign is not valid,
     * or with status {@code 404 (Not Found)} if the estimateDesign is not found,
     * or with status {@code 500 (Internal Server Error)} if the estimateDesign couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/estimate-designs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EstimateDesign> partialUpdateEstimateDesign(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EstimateDesign estimateDesign
    ) throws URISyntaxException {
        log.debug("REST request to partial update EstimateDesign partially : {}, {}", id, estimateDesign);
        if (estimateDesign.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, estimateDesign.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!estimateDesignRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EstimateDesign> result = estimateDesignService.partialUpdate(estimateDesign);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, estimateDesign.getId().toString())
        );
    }

    /**
     * {@code GET  /estimate-designs} : get all the estimateDesigns.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of estimateDesigns in body.
     */
    @GetMapping("/estimate-designs")
    public ResponseEntity<List<EstimateDesign>> getAllEstimateDesigns(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of EstimateDesigns");
        Page<EstimateDesign> page;
        if (eagerload) {
            page = estimateDesignService.findAllWithEagerRelationships(pageable);
        } else {
            page = estimateDesignService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /estimate-designs/:id} : get the "id" estimateDesign.
     *
     * @param id the id of the estimateDesign to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the estimateDesign, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/estimate-designs/{id}")
    public ResponseEntity<EstimateDesign> getEstimateDesign(@PathVariable Long id) {
        log.debug("REST request to get EstimateDesign : {}", id);
        Optional<EstimateDesign> estimateDesign = estimateDesignService.findOne(id);
        return ResponseUtil.wrapOrNotFound(estimateDesign);
    }

    /**
     * {@code DELETE  /estimate-designs/:id} : delete the "id" estimateDesign.
     *
     * @param id the id of the estimateDesign to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/estimate-designs/{id}")
    public ResponseEntity<Void> deleteEstimateDesign(@PathVariable Long id) {
        log.debug("REST request to delete EstimateDesign : {}", id);
        estimateDesignService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/estimate-designs?query=:query} : search for the estimateDesign corresponding
     * to the query.
     *
     * @param query the query of the estimateDesign search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/estimate-designs")
    public ResponseEntity<List<EstimateDesign>> searchEstimateDesigns(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of EstimateDesigns for query {}", query);
        Page<EstimateDesign> page = estimateDesignService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
