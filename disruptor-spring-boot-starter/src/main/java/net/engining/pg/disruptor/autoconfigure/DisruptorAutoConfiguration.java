package net.engining.pg.disruptor.autoconfigure;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import net.engining.pg.disruptor.DisruptorTemplate;
import net.engining.pg.disruptor.event.DisruptorApplicationEvent;
import net.engining.pg.disruptor.event.DisruptorBizDataEvent;
import net.engining.pg.disruptor.event.DisruptorStartedEvent;
import net.engining.pg.disruptor.event.handler.AbstractGroupedEventHandler;
import net.engining.pg.disruptor.event.handler.ExecutionMode;
import net.engining.pg.disruptor.event.translator.BizDataEventOneArgTranslator;
import net.engining.pg.disruptor.factory.DisruptorBizDataEventFactory;
import net.engining.pg.disruptor.factory.DisruptorEventThreadFactory;
import net.engining.pg.disruptor.hooks.DisruptorShutdownHook;
import net.engining.pg.disruptor.props.DisruptorProperties;
import net.engining.pg.disruptor.util.DisruptorUtils;
import net.engining.pg.disruptor.util.WaitStrategys;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.OrderComparator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

/**
 * @author Eric Lu
 */
@SuppressWarnings("rawtypes")
@Configuration
@ConditionalOnClass({Disruptor.class})
@ConditionalOnProperty(prefix = DisruptorProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({DisruptorProperties.class})
public class DisruptorAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisruptorAutoConfiguration.class);
    public static final String GROUPED_DISRUPTOR_HANDLERS = "groupedDisruptorHandlers";
    public static final String DEFAULT_DISRUPTOR_THREAD_FACTORY = "defaultDisruptorThreadFactory";
    public static final String BIZ_DATA_EVENT_ONE_ARG_TRANSLATOR = "bizDataEventOneArgTranslator";

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 决定一个消费者将如何等待生产者将Event置入Disruptor的策略。用来权衡当生产者无法将新的事件放进RingBuffer时的处理策略。
     * （例如：当生产者太快，消费者太慢，会导致生成者获取不到新的事件槽来插入新事件，则会根据该策略进行处理，默认会堵塞）
     *
     * @return {@link WaitStrategy} instance
     */
    @Bean
    @ConditionalOnMissingBean
    public WaitStrategy waitStrategy() {
        return WaitStrategys.YIELDING_WAIT;
    }

    /**
     * 定义线程工厂：默认
     */
    @Bean(DEFAULT_DISRUPTOR_THREAD_FACTORY)
    @ConditionalOnMissingBean
    public ThreadFactory defaultDisruptorThreadFactory() {
        return ThreadFactoryBuilder
                .create()
                .setNamePrefix(DisruptorEventThreadFactory.PREFIX)
                .setThreadFactory(new DisruptorEventThreadFactory())
                .build()
                ;
    }

    /**
     * 定义事件工厂
     */
    @Bean
    @ConditionalOnMissingBean
    public EventFactory<DisruptorBizDataEvent> eventFactory() {
        return new DisruptorBizDataEventFactory();
    }

    @Bean(GROUPED_DISRUPTOR_HANDLERS)
    public Map<String, Map<Integer, List<AbstractGroupedEventHandler<DisruptorBizDataEvent<?>>>>> groupedDisruptorHandlers() {

        // key作为分组键
        Map<String, Map<Integer, List<AbstractGroupedEventHandler<DisruptorBizDataEvent<?>>>>> groupedHandlers = Maps.newHashMap();
        // 所有实现EventHandler的实例
        Map<String, EventHandler> beansOfType = applicationContext.getBeansOfType(EventHandler.class);

        if (ValidateUtilExt.isNotNullOrEmpty(beansOfType)) {
            //index为key, 按key自动排序
            Map<Integer, List<AbstractGroupedEventHandler<DisruptorBizDataEvent<?>>>> gpHandlersTreeMap;
            //index相同的handlers
            List<AbstractGroupedEventHandler<DisruptorBizDataEvent<?>>> gpHandlers;
            for (EventHandler disruptorHandler : beansOfType.values()) {
                //按AbstractNameableEventHandler的GroupKey属性分组
                if (disruptorHandler instanceof AbstractGroupedEventHandler) {
                    AbstractGroupedEventHandler<DisruptorBizDataEvent<?>> gpHandler
                            = (AbstractGroupedEventHandler<DisruptorBizDataEvent<?>>) disruptorHandler;
                    //获取handler的TopicKey作为groupedHandlers的Key：“groupName-mode”
                    String gpkey = gpHandler.getTopicKey();
                    if (groupedHandlers.containsKey(gpkey)) {
                        gpHandlersTreeMap = groupedHandlers.get(gpkey);
                        gpHandlers = gpHandlersTreeMap.get(gpHandler.getListIndex());
                        //对于listIndex不等于0的子组, 需要有机会初始化
                        if (ValidateUtilExt.isNullOrEmpty(gpHandlers)){
                            gpHandlers = Lists.newArrayList();
                            gpHandlersTreeMap.put(gpHandler.getListIndex(), gpHandlers);

                        }
                        gpHandlers.add(gpHandler);
                    } else {
                        gpHandlers = Lists.newArrayList();
                        gpHandlers.add(gpHandler);
                        //TreeMap自动根据key排序
                        gpHandlersTreeMap = Maps.newTreeMap();
                        gpHandlersTreeMap.put(gpHandler.getListIndex(), gpHandlers);
                        groupedHandlers.put(gpkey, gpHandlersTreeMap);
                    }
                }

            }

            // groupedHandlers内每个以listIndex为子组的Handler进行排序
            // 注意：依赖AbstractOrderedEventHandler并设置order
            for (Map<Integer, List<AbstractGroupedEventHandler<DisruptorBizDataEvent<?>>>> mps : groupedHandlers.values()) {
                for (List<AbstractGroupedEventHandler<DisruptorBizDataEvent<?>>> handlers : mps.values()){
                    handlers.sort(new OrderComparator());
                }
            }

        }

        return groupedHandlers;
    }

    @Bean
    public Map<String, Disruptor<DisruptorBizDataEvent<?>>> disruptors(
            DisruptorProperties properties,
            WaitStrategy waitStrategy,
            @Qualifier(DEFAULT_DISRUPTOR_THREAD_FACTORY) ThreadFactory threadFactory,
            EventFactory<DisruptorBizDataEvent> eventFactory,
            @Qualifier(GROUPED_DISRUPTOR_HANDLERS)
                    Map<String, Map<Integer, List<AbstractGroupedEventHandler<DisruptorBizDataEvent<?>>>>> handlers){

        Map<String, Disruptor<DisruptorBizDataEvent<?>>> disruptors = Maps.newHashMap();

        for (String key : handlers.keySet()) {
            Map<Integer, List<AbstractGroupedEventHandler<DisruptorBizDataEvent<?>>>> handlersMap = handlers.get(key);
            String mode = StringUtils.substringAfterLast(key, "-");
            disruptors.put(key, disruptor(properties, waitStrategy, threadFactory, eventFactory, mode, handlersMap));
        }

        disruptors.forEach((s, disruptor) -> {
            // 应用退出时，要调用shutdown来清理资源;
            Runtime.getRuntime().addShutdownHook(new DisruptorShutdownHook(disruptor, applicationContext));
        });

        return disruptors;
    }

    /**
     * <p>
     * 创建Disruptor, 并启动
     * </p>
     *
     * @param properties    : 配置参数
     * @param waitStrategy  : 一种策略，用来均衡数据生产者和消费者之间的处理效率，默认提供了3个实现类
     * @param threadFactory : 线程工厂
     * @param eventFactory  : 工厂类对象，用于创建Event， Event是实际的消费数据;
     *                      初始化启动Disruptor的时候，Disruptor会调用该工厂方法创建消费数据实例存放到RingBuffer缓冲区,
     *                      创建的对象个数为ringBufferSize指定;
     * @param mode          : handler组的执行模式;
     * @param handlersMap   : handler组
     * @return {@link Disruptor} instance
     */
    private Disruptor<DisruptorBizDataEvent<?>> disruptor(
            DisruptorProperties properties,
            WaitStrategy waitStrategy,
            ThreadFactory threadFactory,
            EventFactory<DisruptorBizDataEvent> eventFactory,
            String mode,
            Map<Integer, List<AbstractGroupedEventHandler<DisruptorBizDataEvent<?>>>> handlersMap) {

        Disruptor<DisruptorBizDataEvent<?>> disruptor;
        if (properties.isMultiProducer()) {
            disruptor = new Disruptor(
                    eventFactory,
                    properties.getRingBufferSize(),
                    threadFactory,
                    ProducerType.MULTI,
                    waitStrategy
            );
        } else {
            disruptor = new Disruptor(
                    eventFactory,
                    properties.getRingBufferSize(),
                    threadFactory,
                    ProducerType.SINGLE,
                    waitStrategy
            );
        }

        //组装EventHandler
        if (mode.equals(ExecutionMode.Parallel.getValue())) {
            //并行执行模式只有listIndex为0的
            DisruptorUtils.addParallelEventHandlers(disruptor, handlersMap.get(0));
        } else if (mode.equals(ExecutionMode.SerialChain.getValue())) {
            //链式串行执行模式只有listIndex为0的
            DisruptorUtils.addSerialChainEventHandlers(disruptor, handlersMap.get(0));
        } else if (mode.equals(ExecutionMode.DependenciesDiamond.getValue())) {
            DisruptorUtils.addDependenciesDiamondEventHandlers(disruptor, handlersMap);
        } else if (mode.equals(ExecutionMode.MultiSerialChain.getValue())) {
            DisruptorUtils.addMultiSerialChainEventHandlers(disruptor, handlersMap);
        }

        // 启动
        disruptor.start();
        // 发布启动事件
        applicationContext.publishEvent(new DisruptorStartedEvent(disruptor));

        return disruptor;

    }

    @Bean(BIZ_DATA_EVENT_ONE_ARG_TRANSLATOR)
    @ConditionalOnMissingBean
    public EventTranslatorOneArg bizDataEventOneArgTranslator() {
        return new BizDataEventOneArgTranslator();
    }

    @Bean
    @ConditionalOnMissingBean
    public DisruptorTemplate bizDataEventDisruptorTemplate(Map<String, Disruptor<DisruptorBizDataEvent>> disruptors,
                                                           @Qualifier(BIZ_DATA_EVENT_ONE_ARG_TRANSLATOR)
                                                                   EventTranslatorOneArg bizDataEventOneArgTranslator) {
        return new DisruptorTemplate(disruptors, bizDataEventOneArgTranslator);
    }

    /**
     * DisruptorApplicationEvent的监听器：接收DisruptorApplicationEvent并将其携带的数据作为事件传递给Disruptor
     */
    @Bean
    public ApplicationListener<DisruptorApplicationEvent> disruptorApplicationEventListener(
            DisruptorTemplate bizDataEventDisruptorTemplate) {

        return appEvent -> {
            //利用Spring的ApplicationEvent机制，将ApplicationEvent作为Disruptor的事件数据载体，传递给Disruptor
            bizDataEventDisruptorTemplate.publishEvent(appEvent);
        };
    }

}
