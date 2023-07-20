package com.bujisoft.mybuji.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bujisoft.mybuji.IntegrationTest;
import com.bujisoft.mybuji.domain.ElementTypes;
import com.bujisoft.mybuji.domain.enumeration.Type;
import com.bujisoft.mybuji.repository.ElementTypesRepository;
import com.bujisoft.mybuji.repository.search.ElementTypesSearchRepository;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ElementTypesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ElementTypesResourceIT {

    private static final UUID DEFAULT_ELEMENT = UUID.randomUUID();
    private static final UUID UPDATED_ELEMENT = UUID.randomUUID();

    private static final Type DEFAULT_TYPE = Type.New;
    private static final Type UPDATED_TYPE = Type.Modify;

    private static final String ENTITY_API_URL = "/api/element-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/element-types";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ElementTypesRepository elementTypesRepository;

    @Autowired
    private ElementTypesSearchRepository elementTypesSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restElementTypesMockMvc;

    private ElementTypes elementTypes;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ElementTypes createEntity(EntityManager em) {
        ElementTypes elementTypes = new ElementTypes().element(DEFAULT_ELEMENT).type(DEFAULT_TYPE);
        return elementTypes;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ElementTypes createUpdatedEntity(EntityManager em) {
        ElementTypes elementTypes = new ElementTypes().element(UPDATED_ELEMENT).type(UPDATED_TYPE);
        return elementTypes;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        elementTypesSearchRepository.deleteAll();
        assertThat(elementTypesSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        elementTypes = createEntity(em);
    }

    @Test
    @Transactional
    void createElementTypes() throws Exception {
        int databaseSizeBeforeCreate = elementTypesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());
        // Create the ElementTypes
        restElementTypesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(elementTypes)))
            .andExpect(status().isCreated());

        // Validate the ElementTypes in the database
        List<ElementTypes> elementTypesList = elementTypesRepository.findAll();
        assertThat(elementTypesList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        ElementTypes testElementTypes = elementTypesList.get(elementTypesList.size() - 1);
        assertThat(testElementTypes.getElement()).isEqualTo(DEFAULT_ELEMENT);
        assertThat(testElementTypes.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void createElementTypesWithExistingId() throws Exception {
        // Create the ElementTypes with an existing ID
        elementTypes.setId(1L);

        int databaseSizeBeforeCreate = elementTypesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restElementTypesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(elementTypes)))
            .andExpect(status().isBadRequest());

        // Validate the ElementTypes in the database
        List<ElementTypes> elementTypesList = elementTypesRepository.findAll();
        assertThat(elementTypesList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkElementIsRequired() throws Exception {
        int databaseSizeBeforeTest = elementTypesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());
        // set the field null
        elementTypes.setElement(null);

        // Create the ElementTypes, which fails.

        restElementTypesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(elementTypes)))
            .andExpect(status().isBadRequest());

        List<ElementTypes> elementTypesList = elementTypesRepository.findAll();
        assertThat(elementTypesList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllElementTypes() throws Exception {
        // Initialize the database
        elementTypesRepository.saveAndFlush(elementTypes);

        // Get all the elementTypesList
        restElementTypesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(elementTypes.getId().intValue())))
            .andExpect(jsonPath("$.[*].element").value(hasItem(DEFAULT_ELEMENT.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    void getElementTypes() throws Exception {
        // Initialize the database
        elementTypesRepository.saveAndFlush(elementTypes);

        // Get the elementTypes
        restElementTypesMockMvc
            .perform(get(ENTITY_API_URL_ID, elementTypes.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(elementTypes.getId().intValue()))
            .andExpect(jsonPath("$.element").value(DEFAULT_ELEMENT.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingElementTypes() throws Exception {
        // Get the elementTypes
        restElementTypesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingElementTypes() throws Exception {
        // Initialize the database
        elementTypesRepository.saveAndFlush(elementTypes);

        int databaseSizeBeforeUpdate = elementTypesRepository.findAll().size();
        elementTypesSearchRepository.save(elementTypes);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());

        // Update the elementTypes
        ElementTypes updatedElementTypes = elementTypesRepository.findById(elementTypes.getId()).get();
        // Disconnect from session so that the updates on updatedElementTypes are not directly saved in db
        em.detach(updatedElementTypes);
        updatedElementTypes.element(UPDATED_ELEMENT).type(UPDATED_TYPE);

        restElementTypesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedElementTypes.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedElementTypes))
            )
            .andExpect(status().isOk());

        // Validate the ElementTypes in the database
        List<ElementTypes> elementTypesList = elementTypesRepository.findAll();
        assertThat(elementTypesList).hasSize(databaseSizeBeforeUpdate);
        ElementTypes testElementTypes = elementTypesList.get(elementTypesList.size() - 1);
        assertThat(testElementTypes.getElement()).isEqualTo(UPDATED_ELEMENT);
        assertThat(testElementTypes.getType()).isEqualTo(UPDATED_TYPE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ElementTypes> elementTypesSearchList = IterableUtils.toList(elementTypesSearchRepository.findAll());
                ElementTypes testElementTypesSearch = elementTypesSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testElementTypesSearch.getElement()).isEqualTo(UPDATED_ELEMENT);
                assertThat(testElementTypesSearch.getType()).isEqualTo(UPDATED_TYPE);
            });
    }

    @Test
    @Transactional
    void putNonExistingElementTypes() throws Exception {
        int databaseSizeBeforeUpdate = elementTypesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());
        elementTypes.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restElementTypesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, elementTypes.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(elementTypes))
            )
            .andExpect(status().isBadRequest());

        // Validate the ElementTypes in the database
        List<ElementTypes> elementTypesList = elementTypesRepository.findAll();
        assertThat(elementTypesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchElementTypes() throws Exception {
        int databaseSizeBeforeUpdate = elementTypesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());
        elementTypes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restElementTypesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(elementTypes))
            )
            .andExpect(status().isBadRequest());

        // Validate the ElementTypes in the database
        List<ElementTypes> elementTypesList = elementTypesRepository.findAll();
        assertThat(elementTypesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamElementTypes() throws Exception {
        int databaseSizeBeforeUpdate = elementTypesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());
        elementTypes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restElementTypesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(elementTypes)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ElementTypes in the database
        List<ElementTypes> elementTypesList = elementTypesRepository.findAll();
        assertThat(elementTypesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateElementTypesWithPatch() throws Exception {
        // Initialize the database
        elementTypesRepository.saveAndFlush(elementTypes);

        int databaseSizeBeforeUpdate = elementTypesRepository.findAll().size();

        // Update the elementTypes using partial update
        ElementTypes partialUpdatedElementTypes = new ElementTypes();
        partialUpdatedElementTypes.setId(elementTypes.getId());

        partialUpdatedElementTypes.type(UPDATED_TYPE);

        restElementTypesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedElementTypes.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedElementTypes))
            )
            .andExpect(status().isOk());

        // Validate the ElementTypes in the database
        List<ElementTypes> elementTypesList = elementTypesRepository.findAll();
        assertThat(elementTypesList).hasSize(databaseSizeBeforeUpdate);
        ElementTypes testElementTypes = elementTypesList.get(elementTypesList.size() - 1);
        assertThat(testElementTypes.getElement()).isEqualTo(DEFAULT_ELEMENT);
        assertThat(testElementTypes.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateElementTypesWithPatch() throws Exception {
        // Initialize the database
        elementTypesRepository.saveAndFlush(elementTypes);

        int databaseSizeBeforeUpdate = elementTypesRepository.findAll().size();

        // Update the elementTypes using partial update
        ElementTypes partialUpdatedElementTypes = new ElementTypes();
        partialUpdatedElementTypes.setId(elementTypes.getId());

        partialUpdatedElementTypes.element(UPDATED_ELEMENT).type(UPDATED_TYPE);

        restElementTypesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedElementTypes.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedElementTypes))
            )
            .andExpect(status().isOk());

        // Validate the ElementTypes in the database
        List<ElementTypes> elementTypesList = elementTypesRepository.findAll();
        assertThat(elementTypesList).hasSize(databaseSizeBeforeUpdate);
        ElementTypes testElementTypes = elementTypesList.get(elementTypesList.size() - 1);
        assertThat(testElementTypes.getElement()).isEqualTo(UPDATED_ELEMENT);
        assertThat(testElementTypes.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingElementTypes() throws Exception {
        int databaseSizeBeforeUpdate = elementTypesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());
        elementTypes.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restElementTypesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, elementTypes.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(elementTypes))
            )
            .andExpect(status().isBadRequest());

        // Validate the ElementTypes in the database
        List<ElementTypes> elementTypesList = elementTypesRepository.findAll();
        assertThat(elementTypesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchElementTypes() throws Exception {
        int databaseSizeBeforeUpdate = elementTypesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());
        elementTypes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restElementTypesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(elementTypes))
            )
            .andExpect(status().isBadRequest());

        // Validate the ElementTypes in the database
        List<ElementTypes> elementTypesList = elementTypesRepository.findAll();
        assertThat(elementTypesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamElementTypes() throws Exception {
        int databaseSizeBeforeUpdate = elementTypesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());
        elementTypes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restElementTypesMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(elementTypes))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ElementTypes in the database
        List<ElementTypes> elementTypesList = elementTypesRepository.findAll();
        assertThat(elementTypesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteElementTypes() throws Exception {
        // Initialize the database
        elementTypesRepository.saveAndFlush(elementTypes);
        elementTypesRepository.save(elementTypes);
        elementTypesSearchRepository.save(elementTypes);

        int databaseSizeBeforeDelete = elementTypesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the elementTypes
        restElementTypesMockMvc
            .perform(delete(ENTITY_API_URL_ID, elementTypes.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ElementTypes> elementTypesList = elementTypesRepository.findAll();
        assertThat(elementTypesList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(elementTypesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchElementTypes() throws Exception {
        // Initialize the database
        elementTypes = elementTypesRepository.saveAndFlush(elementTypes);
        elementTypesSearchRepository.save(elementTypes);

        // Search the elementTypes
        restElementTypesMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + elementTypes.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(elementTypes.getId().intValue())))
            .andExpect(jsonPath("$.[*].element").value(hasItem(DEFAULT_ELEMENT.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }
}
