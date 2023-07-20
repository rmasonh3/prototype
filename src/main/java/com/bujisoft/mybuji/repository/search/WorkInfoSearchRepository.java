package com.bujisoft.mybuji.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.bujisoft.mybuji.domain.WorkInfo;
import com.bujisoft.mybuji.repository.WorkInfoRepository;
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
 * Spring Data Elasticsearch repository for the {@link WorkInfo} entity.
 */
public interface WorkInfoSearchRepository extends ElasticsearchRepository<WorkInfo, Long>, WorkInfoSearchRepositoryInternal {}

interface WorkInfoSearchRepositoryInternal {
    Page<WorkInfo> search(String query, Pageable pageable);

    Page<WorkInfo> search(Query query);

    void index(WorkInfo entity);
}

class WorkInfoSearchRepositoryInternalImpl implements WorkInfoSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final WorkInfoRepository repository;

    WorkInfoSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, WorkInfoRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<WorkInfo> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<WorkInfo> search(Query query) {
        SearchHits<WorkInfo> searchHits = elasticsearchTemplate.search(query, WorkInfo.class);
        List<WorkInfo> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(WorkInfo entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
