package net.engining.rocksdb.autoconfigure.autotest.support;

import net.engining.pg.storage.rocksdb.RocksdbKeyValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.keyvalue.core.KeyValueOperations;
import org.springframework.stereotype.Repository;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-10-27 15:38
 * @since :
 **/
@Repository
public class DocBeanRepository implements RocksdbKeyValueRepository<DocBean, Integer> {

    KeyValueOperations keyValueTemplate;

    @Autowired
    public DocBeanRepository(KeyValueOperations keyValueTemplate) {
        this.keyValueTemplate = keyValueTemplate;
    }

    @Override
    public Class<DocBean> entityClass() {
        return DocBean.class;
    }

    @Override
    public KeyValueOperations operations() {
        return keyValueTemplate;
    }
}
