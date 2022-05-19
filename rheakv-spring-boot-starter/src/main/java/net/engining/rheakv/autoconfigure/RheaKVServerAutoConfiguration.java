package net.engining.rheakv.autoconfigure;

import com.alipay.sofa.jraft.rhea.StateListener;
import com.alipay.sofa.jraft.rhea.options.RegionEngineOptions;
import com.google.common.collect.Maps;
import net.engining.pg.rheakv.props.KvServerProperties;
import net.engining.pg.storage.rheakv.KvServer;
import net.engining.rheakv.autoconfigure.facility.StateListener4Spring;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Configuration
@ConditionalOnProperty(prefix = "pg.rheakv.server", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({
        KvServerProperties.class,
})
public class RheaKVServerAutoConfiguration {

    @Autowired
    ApplicationContext applicationContext;

    @Bean
    public Map<Long, List<StateListener>> regionsStateListeners(KvServerProperties kvServerProperties,
                                                                List<StateListener4Spring> stateListeners
    ) {
        List<RegionEngineOptions> optionsList =
                kvServerProperties.getStoreOptions().getStoreEngineOptions().getRegionEngineOptionsList();
        Map<Long, List<StateListener>> map = Maps.newHashMap();
        for (RegionEngineOptions regionEngineOptions : optionsList){
            //-1指的是全部使用一个分区，所以全部添加到这个分区
            if (regionEngineOptions.getRegionId() == -1) {
                List<StateListener> listeners = Lists.newArrayList();
                stateListeners.forEach(stateListener -> {
                    stateListener.setRegionId(-1L);
                    stateListener.setApplicationContext(applicationContext);
                    listeners.add(stateListener);
                });
                map.put(-1L, listeners);
                break;
            }
            else {
                map.put(regionEngineOptions.getRegionId(), Lists.newArrayList());
                stateListeners.forEach(stateListener -> {
                    if (Objects.equals(regionEngineOptions.getRegionId(), stateListener.getRegionId())) {

                    }
                });
            }
        }

        return map;
    }

    @Bean
    public KvServer server(KvServerProperties kvServerProperties, Environment environment,
                           Map<Long, List<StateListener>> regionsStateListeners
    ) {
        KvServer kvServer = new KvServer(kvServerProperties, environment, regionsStateListeners);
        kvServer.start();
        return kvServer;
    }

}
