package com.bujisoft.mybuji.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.bujisoft.mybuji.domain.WorkRequest;
import com.bujisoft.mybuji.repository.WorkRequestRepository;
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
 * Spring Data Elasticsearch repository for the {@link WorkRequest} entity.
 */
public interface WorkRequestSearchRepository extends ElasticsearchRepository<WorkRequest, Long>, WorkRequestSearchRepositoryInternal {}

interface WorkRequestSearchRepositoryInternal {
    Page<WorkRequest> search(String query, Pageable pageable);

    Page<WorkRequest> search(Query query);

    void index(WorkRequest entity);
}

class WorkRequestSearchRepositoryInternalImpl implements WorkRequestSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final WorkRequestRepository repository;

    WorkRequestSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, WorkRequestRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<WorkRequest> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<WorkRequest> search(Query query) {
        SearchHits<WorkRequest> searchHits = elasticsearchTemplate.search(query, WorkRequest.class);
        List<WorkRequest> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(WorkRequest entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
