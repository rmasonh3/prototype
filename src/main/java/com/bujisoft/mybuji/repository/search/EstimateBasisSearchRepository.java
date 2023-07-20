package com.bujisoft.mybuji.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.bujisoft.mybuji.domain.EstimateBasis;
import com.bujisoft.mybuji.repository.EstimateBasisRepository;
import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link EstimateBasis} entity.
 */
public interface EstimateBasisSearchRepository
    extends ElasticsearchRepository<EstimateBasis, Long>, EstimateBasisSearchRepositoryInternal {}

interface EstimateBasisSearchRepositoryInternal {
    Page<EstimateBasis> search(String query, Pageable pageable);

    Page<EstimateBasis> search(Query query);

    void index(EstimateBasis entity);
}

class EstimateBasisSearchRepositoryInternalImpl implements EstimateBasisSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final EstimateBasisRepository repository;

    EstimateBasisSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, EstimateBasisRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<EstimateBasis> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<EstimateBasis> search(Query query) {
        SearchHits<EstimateBasis> searchHits = elasticsearchTemplate.search(query, EstimateBasis.class);
        List<EstimateBasis> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(EstimateBasis entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
