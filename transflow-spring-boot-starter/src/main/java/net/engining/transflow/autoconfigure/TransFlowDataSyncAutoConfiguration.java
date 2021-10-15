package net.engining.transflow.autoconfigure;

import com.lmax.disruptor.dsl.Disruptor;
import net.engining.control.core.storage.DataSyncDisruptorUtils;
import net.engining.control.core.storage.InboundJournalRepositoriesService;
import net.engining.control.core.storage.OutboundJournalRepositoriesService;
import net.engining.control.core.storage.es.InboundJournal4EsRepository;
import net.engining.control.core.storage.es.InboundJournalRepositoriesServiceImpl;
import net.engining.control.core.storage.es.OutboundJournal4EsRepository;
import net.engining.control.core.storage.es.OutboundJournalRepositoriesServiceImpl;
import net.engining.control.entity.dto.CtInboundJournalDto;
import net.engining.control.entity.dto.CtOutboundJournalDto;
import net.engining.control.entity.model.elasticsearch.CtInboundJournal;
import net.engining.control.entity.model.elasticsearch.CtOutboundJournal;
import net.engining.pg.disruptor.event.DisruptorApplicationEvent;
import net.engining.pg.disruptor.event.DisruptorBizDataEvent;
import net.engining.pg.disruptor.event.GenericDisruptorApplicationEvent;
import net.engining.pg.disruptor.event.KeyDisruptorApplicationEvent;
import net.engining.pg.disruptor.event.translator.BizDataEventOneArgTranslator;
import net.engining.pg.disruptor.event.translator.BizKeyEventOneArgTranslator;
import net.engining.pg.storage.props.StorageDataSyncProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-02-22 17:25
 * @since :
 **/
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
@ConditionalOnClass({ ElasticsearchRestTemplate.class })
@ConditionalOnProperty(prefix = "pg.trans-flow.datasync", name = "enabled", havingValue = "true")
@AutoConfigureAfter({ ElasticsearchDataAutoConfiguration.class })
@EnableConfigurationProperties({
        StorageDataSyncProperties.class
})
@EnableElasticsearchRepositories({
        "net.engining.control.core.storage.es"
})
public class TransFlowDataSyncAutoConfiguration {

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(TransFlowDataSyncAutoConfiguration.class);

    @Bean
    InboundJournalRepositoriesService inboundJournalRepositoriesService(
            @Qualifier("elasticsearchTemplate") ElasticsearchOperations elasticsearchTemplate,
            InboundJournal4EsRepository inboundJournal4EsRepository
    ){
        return new InboundJournalRepositoriesServiceImpl(elasticsearchTemplate, inboundJournal4EsRepository);
    }

    @Bean
    OutboundJournalRepositoriesService outboundJournalRepositoriesService(
            @Qualifier("elasticsearchTemplate") ElasticsearchOperations elasticsearchTemplate,
            OutboundJournal4EsRepository outboundJournal4EsRepository
    ){
        return new OutboundJournalRepositoriesServiceImpl(elasticsearchTemplate, outboundJournal4EsRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    public BizKeyEventOneArgTranslator bizKeyEventOneArgTranslator() {
        return new BizKeyEventOneArgTranslator();
    }

    @Bean
    @ConditionalOnMissingBean
    public BizDataEventOneArgTranslator bizDataEventOneArgTranslator() {
        return new BizDataEventOneArgTranslator();
    }

    @Bean(DataSyncDisruptorUtils.INBOUND_JOURNAL_DATA_SYNC)
    @ConditionalOnMissingBean(name = DataSyncDisruptorUtils.INBOUND_JOURNAL_DATA_SYNC)
    public Disruptor<DisruptorBizDataEvent<CtInboundJournal>> inboundJournalDisruptor (
            ApplicationContext applicationContext, StorageDataSyncProperties properties,
            InboundJournalRepositoriesService inboundJournalRepositoriesService
    ) {
        return DataSyncDisruptorUtils.inboundJournalDataSyncEngine(
                applicationContext,
                properties,
                inboundJournalRepositoriesService
        ).start();
    }

    @Bean(DataSyncDisruptorUtils.OUTBOUND_JOURNAL_DATA_SYNC)
    @ConditionalOnMissingBean(name = DataSyncDisruptorUtils.OUTBOUND_JOURNAL_DATA_SYNC)
    public Disruptor<DisruptorBizDataEvent<CtOutboundJournal>> outboundJournalDisruptor (
            ApplicationContext applicationContext, StorageDataSyncProperties properties,
            OutboundJournalRepositoriesService outboundJournalRepositoriesService
    ) {

        return DataSyncDisruptorUtils.outboundJournalDataSyncEngine(
                applicationContext,
                properties,
                outboundJournalRepositoriesService
        ).start();
    }

    /**
     * DisruptorApplicationEvent的监听器：接收DisruptorApplicationEvent并将其携带的数据作为事件传递给Disruptor
     */
    @Bean("dataSyncApplicationEventListener")
    @ConditionalOnMissingBean(name = "dataSyncApplicationEventListener")
    public ApplicationListener<KeyDisruptorApplicationEvent> dataSyncApplicationEventListener(
            BizDataEventOneArgTranslator bizDataEventOneArgTranslator,
            @Qualifier(DataSyncDisruptorUtils.INBOUND_JOURNAL_DATA_SYNC) Disruptor inboundJournalDisruptor,
            @Qualifier(DataSyncDisruptorUtils.OUTBOUND_JOURNAL_DATA_SYNC) Disruptor outboundJournalDisruptor,
            InboundJournalRepositoriesService inboundJournalRepositoriesService,
            OutboundJournalRepositoriesService outboundJournalRepositoriesService
    ) {

        return appEvent -> {
            //利用Spring的ApplicationEvent机制，将ApplicationEvent作为Disruptor的事件数据载体，传递给Disruptor
            if (appEvent.getTopicKey().equals(DataSyncDisruptorUtils.INBOUND_JOURNAL_DATA_SYNC)){
                GenericDisruptorApplicationEvent<CtInboundJournal> event = new GenericDisruptorApplicationEvent<>(
                        appEvent.getSource()
                );
                String dataKey = appEvent.getKey();
                //CtInboundJournalDto ctInboundJournalDto = (CtOutboundJournalDto) appEvent.getBind();
                //CtInboundJournal ctInboundJournal4Es = new CtInboundJournal();
                //BeanUtils.copyProperties(ctInboundJournalDto, ctInboundJournal4Es);
                //event.setBind(ctInboundJournal4Es);
                //inboundJournalDisruptor.publishEvent(bizDataEventOneArgTranslator, event);
            }
            else if (appEvent.getTopicKey().equals(DataSyncDisruptorUtils.OUTBOUND_JOURNAL_DATA_SYNC)){
                GenericDisruptorApplicationEvent<CtOutboundJournal> event = new GenericDisruptorApplicationEvent<>(
                        appEvent.getSource()
                );
                String dataKey = appEvent.getKey();
                //CtOutboundJournalDto ctOutboundJournalDto = (CtOutboundJournalDto)
                //CtOutboundJournal ctOutboundJournal4Es = new CtOutboundJournal();
                //BeanUtils.copyProperties(ctOutboundJournalDto, ctOutboundJournal4Es);
                //event.setBind(ctOutboundJournal4Es);
                //outboundJournalDisruptor.publishEvent(bizDataEventOneArgTranslator, event);
            }
        };
    }
}
