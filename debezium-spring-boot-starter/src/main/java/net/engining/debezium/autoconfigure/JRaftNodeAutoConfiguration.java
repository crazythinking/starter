package net.engining.debezium.autoconfigure;

import com.alipay.sofa.jraft.rhea.StateListener;
import com.alipay.sofa.jraft.rhea.options.RegionEngineOptions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.engining.debezium.facility.BootstrapLeaderStateListener;
import net.engining.debezium.facility.DebeziumServerBootstrap;
import net.engining.pg.rheakv.props.KvServerProperties;
import net.engining.pg.storage.rheakv.KvServer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

import static net.engining.debezium.autoconfigure.DebeziumAutoConfiguration.DEBEZIUM_SERVER_BOOTSTRAP_MAP;

@Configuration
@ConditionalOnProperty(prefix = "pg.debezium", name = "enabled-j-raft", havingValue = "true")
@AutoConfigureAfter(DebeziumAutoConfiguration.class)
public class JRaftNodeAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "pg.rheakv.server", name = "enabled", havingValue = "true")
    public KvServer server(KvServerProperties kvServerProperties, Environment environment,
                           @Qualifier(DEBEZIUM_SERVER_BOOTSTRAP_MAP) Map<String, DebeziumServerBootstrap> bootstrapMap
    ) {
        Map<Long, List<StateListener>> map = Maps.newHashMap();
        List<RegionEngineOptions> optionsList =
                kvServerProperties.getStoreOptions().getStoreEngineOptions().getRegionEngineOptionsList();
        bootstrapMap.forEach((k, v) -> optionsList.forEach(
                regionEngineOptions -> {
                    if (k.equals(regionEngineOptions.getStartKey())) {
                        map.put(
                                regionEngineOptions.getRegionId(),
                                Lists.newArrayList(new BootstrapLeaderStateListener(v))
                        );
                    }
                }

        ));
        KvServer kvServer = new KvServer(kvServerProperties, environment, map);
        kvServer.start();
        return kvServer;
    }

}
