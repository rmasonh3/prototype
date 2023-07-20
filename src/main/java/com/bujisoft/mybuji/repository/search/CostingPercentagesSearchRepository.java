package com.bujisoft.mybuji.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.bujisoft.mybuji.domain.CostingPercentages;
import com.bujisoft.mybuji.repository.CostingPercentagesRepository;
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
 * Spring Data Elasticsearch repository for the {@link CostingPercentages} entity.
 */
public interface CostingPercentagesSearchRepository
    extends ElasticsearchRepository<CostingPercentages, Long>, CostingPercentagesSearchRepositoryInternal {}

interface CostingPercentagesSearchRepositoryInternal {
    Stream<CostingPercentages> search(String query);

    Stream<CostingPercentages> search(Query query);

    void index(CostingPercentages entity);
}

class CostingPercentagesSearchRepositoryInternalImpl implements CostingPercentagesSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final CostingPercentagesRepository repository;

    CostingPercentagesSearchRepositoryInternalImpl(
        ElasticsearchRestTemplate elasticsearchTemplate,
        CostingPercentagesRepository repository
    ) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<CostingPercentages> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<CostingPercentages> search(Query query) {
        return elasticsearchTemplate.search(query, CostingPercentages.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(CostingPercentages entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
