package com.bujisoft.mybuji.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.bujisoft.mybuji.domain.EstimateDesign;
import com.bujisoft.mybuji.repository.EstimateDesignRepository;
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
 * Spring Data Elasticsearch repository for the {@link EstimateDesign} entity.
 */
public interface EstimateDesignSearchRepository
    extends ElasticsearchRepository<EstimateDesign, Long>, EstimateDesignSearchRepositoryInternal {}

interface EstimateDesignSearchRepositoryInternal {
    Page<EstimateDesign> search(String query, Pageable pageable);

    Page<EstimateDesign> search(Query query);

    void index(EstimateDesign entity);
}

class EstimateDesignSearchRepositoryInternalImpl implements EstimateDesignSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final EstimateDesignRepository repository;

    EstimateDesignSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, EstimateDesignRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<EstimateDesign> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<EstimateDesign> search(Query query) {
        SearchHits<EstimateDesign> searchHits = elasticsearchTemplate.search(query, EstimateDesign.class);
        List<EstimateDesign> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(EstimateDesign entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
