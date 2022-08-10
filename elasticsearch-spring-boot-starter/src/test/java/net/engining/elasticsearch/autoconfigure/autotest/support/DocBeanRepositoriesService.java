package net.engining.elasticsearch.autoconfigure.autotest.support;

import net.engining.pg.storage.core.RepositoriesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Iterator;
import java.util.List;

/**
 * DocBean存储层的聚合根
 * @author Eric Lu
 * @date 2021-09-08 10:39
 **/
public interface DocBeanRepositoriesService extends RepositoriesService {

    void setPageable(Pageable pageable);

    void createIndex();

    void deleteIndex(String index);

    void saveAll(List<DocBean> list);

    Iterator<DocBean> findAll();

    Page<DocBean> findByContent(String content);

    Page<DocBean> findByFirstCode(String firstCode);

    Page<DocBean> findBySecondCode(String secondCode);

    List<DocBean> findByFirstCodeAndSecondCode(String firstCode, String secondCode);

}
