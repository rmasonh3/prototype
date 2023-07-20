package com.bujisoft.mybuji.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.bujisoft.mybuji.domain.CostingPercentages;
import com.bujisoft.mybuji.repository.CostingPercentagesRepository;
import com.bujisoft.mybuji.service.CostingPercentagesService;
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
 * REST controller for managing {@link com.bujisoft.mybuji.domain.CostingPercentages}.
 */
@RestController
@RequestMapping("/api")
public class CostingPercentagesResource {

    private final Logger log = LoggerFactory.getLogger(CostingPercentagesResource.class);

    private static final String ENTITY_NAME = "costingPercentages";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CostingPercentagesService costingPercentagesService;

    private final CostingPercentagesRepository costingPercentagesRepository;

    public CostingPercentagesResource(
        CostingPercentagesService costingPercentagesService,
        CostingPercentagesRepository costingPercentagesRepository
    ) {
        this.costingPercentagesService = costingPercentagesService;
        this.costingPercentagesRepository = costingPercentagesRepository;
    }

    /**
     * {@code POST  /costing-percentages} : Create a new costingPercentages.
     *
     * @param costingPercentages the costingPercentages to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new costingPercentages, or with status {@code 400 (Bad Request)} if the costingPercentages has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/costing-percentages")
    public ResponseEntity<CostingPercentages> createCostingPercentages(@Valid @RequestBody CostingPercentages costingPercentages)
        throws URISyntaxException {
        log.debug("REST request to save CostingPercentages : {}", costingPercentages);
        if (costingPercentages.getId() != null) {
            throw new BadRequestAlertException("A new costingPercentages cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CostingPercentages result = costingPercentagesService.save(costingPercentages);
        return ResponseEntity
            .created(new URI("/api/costing-percentages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /costing-percentages/:id} : Updates an existing costingPercentages.
     *
     * @param id the id of the costingPercentages to save.
     * @param costingPercentages the costingPercentages to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated costingPercentages,
     * or with status {@code 400 (Bad Request)} if the costingPercentages is not valid,
     * or with status {@code 500 (Internal Server Error)} if the costingPercentages couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/costing-percentages/{id}")
    public ResponseEntity<CostingPercentages> updateCostingPercentages(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CostingPercentages costingPercentages
    ) throws URISyntaxException {
        log.debug("REST request to update CostingPercentages : {}, {}", id, costingPercentages);
        if (costingPercentages.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, costingPercentages.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!costingPercentagesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CostingPercentages result = costingPercentagesService.update(costingPercentages);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, costingPercentages.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /costing-percentages/:id} : Partial updates given fields of an existing costingPercentages, field will ignore if it is null
     *
     * @param id the id of the costingPercentages to save.
     * @param costingPercentages the costingPercentages to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated costingPercentages,
     * or with status {@code 400 (Bad Request)} if the costingPercentages is not valid,
     * or with status {@code 404 (Not Found)} if the costingPercentages is not found,
     * or with status {@code 500 (Internal Server Error)} if the costingPercentages couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/costing-percentages/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CostingPercentages> partialUpdateCostingPercentages(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CostingPercentages costingPercentages
    ) throws URISyntaxException {
        log.debug("REST request to partial update CostingPercentages partially : {}, {}", id, costingPercentages);
        if (costingPercentages.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, costingPercentages.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!costingPercentagesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CostingPercentages> result = costingPercentagesService.partialUpdate(costingPercentages);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, costingPercentages.getId().toString())
        );
    }

    /**
     * {@code GET  /costing-percentages} : get all the costingPercentages.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of costingPercentages in body.
     */
    @GetMapping("/costing-percentages")
    public List<CostingPercentages> getAllCostingPercentages() {
        log.debug("REST request to get all CostingPercentages");
        return costingPercentagesService.findAll();
    }

    /**
     * {@code GET  /costing-percentages/:id} : get the "id" costingPercentages.
     *
     * @param id the id of the costingPercentages to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the costingPercentages, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/costing-percentages/{id}")
    public ResponseEntity<CostingPercentages> getCostingPercentages(@PathVariable Long id) {
        log.debug("REST request to get CostingPercentages : {}", id);
        Optional<CostingPercentages> costingPercentages = costingPercentagesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(costingPercentages);
    }

    /**
     * {@code DELETE  /costing-percentages/:id} : delete the "id" costingPercentages.
     *
     * @param id the id of the costingPercentages to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/costing-percentages/{id}")
    public ResponseEntity<Void> deleteCostingPercentages(@PathVariable Long id) {
        log.debug("REST request to delete CostingPercentages : {}", id);
        costingPercentagesService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/costing-percentages?query=:query} : search for the costingPercentages corresponding
     * to the query.
     *
     * @param query the query of the costingPercentages search.
     * @return the result of the search.
     */
    @GetMapping("/_search/costing-percentages")
    public List<CostingPercentages> searchCostingPercentages(@RequestParam String query) {
        log.debug("REST request to search CostingPercentages for query {}", query);
        return costingPercentagesService.search(query);
    }
}
