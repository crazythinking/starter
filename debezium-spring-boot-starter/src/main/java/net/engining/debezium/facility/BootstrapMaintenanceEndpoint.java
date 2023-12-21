package net.engining.debezium.facility;

import com.alipay.sofa.jraft.rhea.client.DefaultRheaKVStore;
import com.alipay.sofa.jraft.util.Endpoint;
import net.engining.pg.storage.rheakv.KvServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import java.util.Map;

import static net.engining.debezium.autoconfigure.DebeziumAutoConfiguration.DEBEZIUM_SERVER_BOOTSTRAP_MAP;

/**
 * 提供管理 Debezium Bootstrap 的接口
 *
 * @author : Eric Lu
 * @version :
 * @since : 2023-12-20 18:13
 **/
@RestControllerEndpoint(id = "bootstrapMaintenanceEndpoint")
public class BootstrapMaintenanceEndpoint {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(BootstrapMaintenanceEndpoint.class);

    @Resource(name = DEBEZIUM_SERVER_BOOTSTRAP_MAP)
    Map<String, DebeziumServerBootstrap> debeziumServerBootstrapMap;

    @Resource
    KvServer kvServer;

    @GetMapping("/findLeader/{regionId}")
    public Endpoint findLeader(@PathVariable Long regionId) {
        DefaultRheaKVStore defaultRheaKVStore = (DefaultRheaKVStore) kvServer.getRheaKVStore();
        return defaultRheaKVStore.getStoreEngine().getRegionEngine(regionId).getLeaderId().getEndpoint();
    }

    @PostMapping("/delOffset")
    public void deleteOffset(String key) {
        kvServer.getRheaKVStore().bDelete(key);
    }

    @PostMapping("/delHistory")
    public void deleteHistory(String key) {
        kvServer.getRheaKVStore().bDelete(key);
    }

    @PostMapping("/restart")
    public void restart(String name) {
        DebeziumServerBootstrap debeziumServerBootstrap = debeziumServerBootstrapMap.get(name);
        if (debeziumServerBootstrap != null) {
            if (debeziumServerBootstrap.isRunning()) {
                debeziumServerBootstrap.stop();
                debeziumServerBootstrap.start();
            }
            else {
                debeziumServerBootstrap.start();
            }
        }
    }
    
}
