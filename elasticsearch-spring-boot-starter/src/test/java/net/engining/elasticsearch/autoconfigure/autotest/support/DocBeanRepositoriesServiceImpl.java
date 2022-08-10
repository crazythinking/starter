package net.engining.elasticsearch.autoconfigure.autotest.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2020-07-03 17:12
 * @since :
 **/
@Service
public class DocBeanRepositoriesServiceImpl implements DocBeanRepositoriesService {

    @Autowired
    private ElasticsearchOperations elasticsearchTemplate;

    @Autowired
    private DocBeanElasticsearchRepository elasticsearchRepository;

    private IndexOperations indexOperations;

    private Pageable pageable = PageRequest.of(0, 10);

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }

    @Override
    public void createIndex() {
        if (!indexOperations.exists()){
            indexOperations.create();
        }
    }

    @Override
    public void deleteIndex(String index) {
        if (indexOperations.exists()){
            indexOperations.delete();
        }
    }

    @Override
    public void saveAll(List<DocBean> list) {
        elasticsearchRepository.saveAll(list);
    }

    @Override
    public Iterator<DocBean> findAll() {
        return elasticsearchRepository.findAll(pageable).iterator();
    }

    @Override
    public Page<DocBean> findByContent(String content) {
        return elasticsearchRepository.findByContent(content, pageable);
    }

    @Override
    public Page<DocBean> findByFirstCode(String firstCode) {
        return elasticsearchRepository.findByFirstCode(firstCode, pageable);
    }

    @Override
    public Page<DocBean> findBySecondCode(String secondCode) {
        return elasticsearchRepository.findBySecondCode(secondCode, pageable);
    }

    @Override
    public List<DocBean> findByFirstCodeAndSecondCode(String firstCode, String secondCode) {
        //NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
        //        .withQuery(
        //                QueryBuilders.boolQuery()
        //                        .must(QueryBuilders.matchQuery("firstCode", firstCode))
        //                        .must(QueryBuilders.matchQuery("secondCode", secondCode))
        //        );
        //
        //SearchHits<DocBean> docBeans = elasticsearchTemplate.search(queryBuilder.build(), DocBean.class);
        //List<DocBean> beans = docBeans.stream().collect(Collectors.toList());
        return elasticsearchRepository.findByFirstCodeAndSecondCode(firstCode, secondCode);
    }

    @Override
    public void afterPropertiesSet() {
        indexOperations = elasticsearchTemplate.indexOps(DocBean.class);
    }

    @Override
    public void batchSave(List<?> entities) {

    }
}
