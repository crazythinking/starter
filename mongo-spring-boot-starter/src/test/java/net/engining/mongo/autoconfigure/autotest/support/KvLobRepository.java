package net.engining.mongo.autoconfigure.autotest.support;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Eric Lu
 * @date 2023-05-19 15:55
 **/
@Repository
public interface KvLobRepository extends MongoRepository<KvLob, String> {
}
