package net.engining.elasticsearch.autoconfigure.autotest.support;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DocBean Elasticsearch 的存储层
 * @author Eric Lu
 * @date 2020-07-03 17:00
 **/
@Repository
public interface DocBeanElasticsearchRepository extends ElasticsearchRepository<DocBean, Long> {

    @Query("{\"match\": {\"content\": {\"query\": \"?0\"}}}")
    Page<DocBean> findByContent(String content, Pageable pageable);

    Page<DocBean> findByFirstCode(String firstCode, Pageable pageable);

    Page<DocBean> findBySecondCode(String secordCode, Pageable pageable);

    List<DocBean> findByFirstCodeAndSecondCode(String firstCode, String secondCode);

}

