package com.bujisoft.mybuji.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.bujisoft.mybuji.domain.WorkRequest;
import com.bujisoft.mybuji.repository.WorkRequestRepository;
import com.bujisoft.mybuji.service.WorkRequestService;
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
 * REST controller for managing {@link com.bujisoft.mybuji.domain.WorkRequest}.
 */
@RestController
@RequestMapping("/api")
public class WorkRequestResource {

    private final Logger log = LoggerFactory.getLogger(WorkRequestResource.class);

    private static final String ENTITY_NAME = "workRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WorkRequestService workRequestService;

    private final WorkRequestRepository workRequestRepository;

    public WorkRequestResource(WorkRequestService workRequestService, WorkRequestRepository workRequestRepository) {
        this.workRequestService = workRequestService;
        this.workRequestRepository = workRequestRepository;
    }

    /**
     * {@code POST  /work-requests} : Create a new workRequest.
     *
     * @param workRequest the workRequest to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new workRequest, or with status {@code 400 (Bad Request)} if the workRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/work-requests")
    public ResponseEntity<WorkRequest> createWorkRequest(@Valid @RequestBody WorkRequest workRequest) throws URISyntaxException {
        log.debug("REST request to save WorkRequest : {}", workRequest);
        if (workRequest.getId() != null) {
            throw new BadRequestAlertException("A new workRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WorkRequest result = workRequestService.save(workRequest);
        return ResponseEntity
            .created(new URI("/api/work-requests/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /work-requests/:id} : Updates an existing workRequest.
     *
     * @param id the id of the workRequest to save.
     * @param workRequest the workRequest to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workRequest,
     * or with status {@code 400 (Bad Request)} if the workRequest is not valid,
     * or with status {@code 500 (Internal Server Error)} if the workRequest couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/work-requests/{id}")
    public ResponseEntity<WorkRequest> updateWorkRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WorkRequest workRequest
    ) throws URISyntaxException {
        log.debug("REST request to update WorkRequest : {}, {}", id, workRequest);
        if (workRequest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workRequest.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        WorkRequest result = workRequestService.update(workRequest);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, workRequest.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /work-requests/:id} : Partial updates given fields of an existing workRequest, field will ignore if it is null
     *
     * @param id the id of the workRequest to save.
     * @param workRequest the workRequest to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workRequest,
     * or with status {@code 400 (Bad Request)} if the workRequest is not valid,
     * or with status {@code 404 (Not Found)} if the workRequest is not found,
     * or with status {@code 500 (Internal Server Error)} if the workRequest couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/work-requests/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WorkRequest> partialUpdateWorkRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WorkRequest workRequest
    ) throws URISyntaxException {
        log.debug("REST request to partial update WorkRequest partially : {}, {}", id, workRequest);
        if (workRequest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workRequest.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WorkRequest> result = workRequestService.partialUpdate(workRequest);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, workRequest.getId().toString())
        );
    }

    /**
     * {@code GET  /work-requests} : get all the workRequests.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of workRequests in body.
     */
    @GetMapping("/work-requests")
    public ResponseEntity<List<WorkRequest>> getAllWorkRequests(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of WorkRequests");
        Page<WorkRequest> page = workRequestService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /work-requests/:id} : get the "id" workRequest.
     *
     * @param id the id of the workRequest to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the workRequest, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/work-requests/{id}")
    public ResponseEntity<WorkRequest> getWorkRequest(@PathVariable Long id) {
        log.debug("REST request to get WorkRequest : {}", id);
        Optional<WorkRequest> workRequest = workRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(workRequest);
    }

    /**
     * {@code DELETE  /work-requests/:id} : delete the "id" workRequest.
     *
     * @param id the id of the workRequest to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/work-requests/{id}")
    public ResponseEntity<Void> deleteWorkRequest(@PathVariable Long id) {
        log.debug("REST request to delete WorkRequest : {}", id);
        workRequestService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/work-requests?query=:query} : search for the workRequest corresponding
     * to the query.
     *
     * @param query the query of the workRequest search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/work-requests")
    public ResponseEntity<List<WorkRequest>> searchWorkRequests(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of WorkRequests for query {}", query);
        Page<WorkRequest> page = workRequestService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
