package net.engining.elasticsearch.autoconfigure.autotest.support;

import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : Eric Lu
 * @version :
 * @since : 2023-12-05 13:48
 **/
@Service
public class ExceptionDocRepoService {

    @Autowired
    private ElasticsearchOperations elasticsearchTemplate;

    public List<ExceptionDoc> findWithNative(String exception) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(
                        QueryBuilders.boolQuery()
                                .must(QueryBuilders.matchQuery("Exception_name", exception))
                                .must(QueryBuilders.rangeQuery("@timestamp").gte("now-30m/m").lte("now/m"))
                );
        SearchHits<ExceptionDoc> exceptionDocs = elasticsearchTemplate.search(queryBuilder.build(), ExceptionDoc.class);
        return exceptionDocs.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }
}
