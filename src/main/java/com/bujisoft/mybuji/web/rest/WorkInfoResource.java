package com.bujisoft.mybuji.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.bujisoft.mybuji.domain.WorkInfo;
import com.bujisoft.mybuji.repository.WorkInfoRepository;
import com.bujisoft.mybuji.service.WorkInfoService;
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
 * REST controller for managing {@link com.bujisoft.mybuji.domain.WorkInfo}.
 */
@RestController
@RequestMapping("/api")
public class WorkInfoResource {

    private final Logger log = LoggerFactory.getLogger(WorkInfoResource.class);

    private static final String ENTITY_NAME = "workInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WorkInfoService workInfoService;

    private final WorkInfoRepository workInfoRepository;

    public WorkInfoResource(WorkInfoService workInfoService, WorkInfoRepository workInfoRepository) {
        this.workInfoService = workInfoService;
        this.workInfoRepository = workInfoRepository;
    }

    /**
     * {@code POST  /work-infos} : Create a new workInfo.
     *
     * @param workInfo the workInfo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new workInfo, or with status {@code 400 (Bad Request)} if the workInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/work-infos")
    public ResponseEntity<WorkInfo> createWorkInfo(@Valid @RequestBody WorkInfo workInfo) throws URISyntaxException {
        log.debug("REST request to save WorkInfo : {}", workInfo);
        if (workInfo.getId() != null) {
            throw new BadRequestAlertException("A new workInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WorkInfo result = workInfoService.save(workInfo);
        return ResponseEntity
            .created(new URI("/api/work-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /work-infos/:id} : Updates an existing workInfo.
     *
     * @param id the id of the workInfo to save.
     * @param workInfo the workInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workInfo,
     * or with status {@code 400 (Bad Request)} if the workInfo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the workInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/work-infos/{id}")
    public ResponseEntity<WorkInfo> updateWorkInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WorkInfo workInfo
    ) throws URISyntaxException {
        log.debug("REST request to update WorkInfo : {}, {}", id, workInfo);
        if (workInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workInfo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        WorkInfo result = workInfoService.update(workInfo);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, workInfo.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /work-infos/:id} : Partial updates given fields of an existing workInfo, field will ignore if it is null
     *
     * @param id the id of the workInfo to save.
     * @param workInfo the workInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workInfo,
     * or with status {@code 400 (Bad Request)} if the workInfo is not valid,
     * or with status {@code 404 (Not Found)} if the workInfo is not found,
     * or with status {@code 500 (Internal Server Error)} if the workInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/work-infos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WorkInfo> partialUpdateWorkInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WorkInfo workInfo
    ) throws URISyntaxException {
        log.debug("REST request to partial update WorkInfo partially : {}, {}", id, workInfo);
        if (workInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workInfo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WorkInfo> result = workInfoService.partialUpdate(workInfo);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, workInfo.getId().toString())
        );
    }

    /**
     * {@code GET  /work-infos} : get all the workInfos.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of workInfos in body.
     */
    @GetMapping("/work-infos")
    public ResponseEntity<List<WorkInfo>> getAllWorkInfos(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of WorkInfos");
        Page<WorkInfo> page;
        if (eagerload) {
            page = workInfoService.findAllWithEagerRelationships(pageable);
        } else {
            page = workInfoService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /work-infos/:id} : get the "id" workInfo.
     *
     * @param id the id of the workInfo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the workInfo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/work-infos/{id}")
    public ResponseEntity<WorkInfo> getWorkInfo(@PathVariable Long id) {
        log.debug("REST request to get WorkInfo : {}", id);
        Optional<WorkInfo> workInfo = workInfoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(workInfo);
    }

    /**
     * {@code DELETE  /work-infos/:id} : delete the "id" workInfo.
     *
     * @param id the id of the workInfo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/work-infos/{id}")
    public ResponseEntity<Void> deleteWorkInfo(@PathVariable Long id) {
        log.debug("REST request to delete WorkInfo : {}", id);
        workInfoService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/work-infos?query=:query} : search for the workInfo corresponding
     * to the query.
     *
     * @param query the query of the workInfo search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/work-infos")
    public ResponseEntity<List<WorkInfo>> searchWorkInfos(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of WorkInfos for query {}", query);
        Page<WorkInfo> page = workInfoService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
