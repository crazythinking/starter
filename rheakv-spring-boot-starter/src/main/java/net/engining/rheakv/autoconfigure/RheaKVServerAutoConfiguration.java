package net.engining.rheakv.autoconfigure;

import com.alipay.sofa.jraft.rhea.RegionEngine;
import com.alipay.sofa.jraft.rhea.StateListener;
import com.alipay.sofa.jraft.rhea.StoreEngine;
import com.alipay.sofa.jraft.rhea.client.DefaultRheaKVStore;
import com.alipay.sofa.jraft.rhea.metrics.KVMetrics;
import com.alipay.sofa.jraft.rhea.options.RegionEngineOptions;
import com.alipay.sofa.jraft.util.ThreadPoolMetricRegistry;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.engining.pg.rheakv.props.KvServerProperties;
import net.engining.pg.storage.rheakv.KvServer;
import net.engining.pg.support.utils.ValidateUtilExt;
import net.engining.rheakv.autoconfigure.facility.StateListener4Spring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(RheaKVServerAutoConfiguration.class);
    public static final String REGIONS_STATE_LISTENERS = "regionsStateListeners";

    @Autowired
    ApplicationContext applicationContext;

    @Bean(name = REGIONS_STATE_LISTENERS)
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
                map.put(-1L, listeners);
                stateListeners.forEach(stateListener -> {
                    if (stateListener.getRegionId() != -1L){
                        LOGGER.warn("StateListener {} is not used in all region(-1), please check it", stateListener);
                    }
                    //强制覆盖regionId
                    stateListener.setRegionId(-1L);
                    stateListener.setApplicationContext(applicationContext);
                    listeners.add(stateListener);
                });
                break;
            }
            else {
                List<StateListener> listeners = Lists.newArrayList();
                map.put(regionEngineOptions.getRegionId(), listeners);
                stateListeners.forEach(stateListener -> {
                    if (Objects.equals(regionEngineOptions.getRegionId(), stateListener.getRegionId())) {
                        stateListener.setApplicationContext(applicationContext);
                        listeners.add(stateListener);
                    }
                });
            }
        }

        return map;
    }

    @Bean
    public KvServer server(KvServerProperties kvServerProperties, Environment environment,
                           @Qualifier(REGIONS_STATE_LISTENERS) Map<Long, List<StateListener>> regionsStateListeners) {
        KvServer kvServer = new KvServer(kvServerProperties, environment, regionsStateListeners);
        kvServer.start();
        return kvServer;
    }

    @Bean
    public List<MetricRegistry> rheaKvMetricRegistries(KvServer kvServer) {
        List<MetricRegistry> rheaKvMetricRegistries = Lists.newArrayList();
        //添加整体KV的MetricRegistry
        rheaKvMetricRegistries.add(KVMetrics.metricRegistry());
        //添加整体线程的MetricRegistry
        rheaKvMetricRegistries.add(ThreadPoolMetricRegistry.metricRegistry());
        //添加各个RegionEngine的MetricRegistry
        StoreEngine storeEngine = ((DefaultRheaKVStore)kvServer.getRheaKVStore()).getStoreEngine();
        List<RegionEngine> regionEngines = storeEngine.getAllRegionEngines();
        regionEngines.forEach(regionEngine -> {
            MetricRegistry metricRegistry = regionEngine.getNode().getNodeMetrics().getMetricRegistry();
            if (ValidateUtilExt.isNotNullOrEmpty(metricRegistry)) {
                rheaKvMetricRegistries.add(metricRegistry);
            }
        });

        return rheaKvMetricRegistries;
    }

}
