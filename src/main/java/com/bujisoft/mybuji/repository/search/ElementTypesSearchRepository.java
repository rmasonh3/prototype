package com.bujisoft.mybuji.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.bujisoft.mybuji.domain.ElementTypes;
import com.bujisoft.mybuji.repository.ElementTypesRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link ElementTypes} entity.
 */
public interface ElementTypesSearchRepository extends ElasticsearchRepository<ElementTypes, Long>, ElementTypesSearchRepositoryInternal {}

interface ElementTypesSearchRepositoryInternal {
    Stream<ElementTypes> search(String query);

    Stream<ElementTypes> search(Query query);

    void index(ElementTypes entity);
}

class ElementTypesSearchRepositoryInternalImpl implements ElementTypesSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final ElementTypesRepository repository;

    ElementTypesSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, ElementTypesRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<ElementTypes> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<ElementTypes> search(Query query) {
        return elasticsearchTemplate.search(query, ElementTypes.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(ElementTypes entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
