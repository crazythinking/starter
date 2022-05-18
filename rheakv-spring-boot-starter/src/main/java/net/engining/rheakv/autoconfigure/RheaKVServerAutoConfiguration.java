package net.engining.rheakv.autoconfigure;

import com.alipay.sofa.jraft.rhea.StateListener;
import com.alipay.sofa.jraft.rhea.options.RegionEngineOptions;
import com.google.common.collect.Maps;
import net.engining.pg.rheakv.props.KvServerProperties;
import net.engining.pg.storage.rheakv.KvServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

@Configuration
@ConditionalOnProperty(prefix = "pg.rheakv.server", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({
        KvServerProperties.class,
})
public class RheaKVServerAutoConfiguration {

    @Bean
    public KvServer server(KvServerProperties kvServerProperties, Environment environment
    ) {
        Map<Long, List<StateListener>> map = Maps.newHashMap();
        List<RegionEngineOptions> optionsList =
                kvServerProperties.getStoreOptions().getStoreEngineOptions().getRegionEngineOptionsList();
        //bootstrapMap.forEach((k, v) -> optionsList.forEach(
        //        regionEngineOptions -> {
        //            if (k.equals(regionEngineOptions.getStartKey())) {
        //
        //            }
        //        }
        //
        //));
        KvServer kvServer = new KvServer(kvServerProperties, environment, map);
        kvServer.start();
        return kvServer;
    }

}
