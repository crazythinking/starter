package net.engining.pg.disruptor.autoconfigure;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.EventTranslatorThreeArg;
import com.lmax.disruptor.EventTranslatorTwoArg;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;
import net.engining.pg.disruptor.DisruptorTemplate;
import net.engining.pg.disruptor.annotation.EventDispatchRule;
import net.engining.pg.disruptor.config.Ini;
import net.engining.pg.disruptor.context.DisruptorEventAwareProcessor;
import net.engining.pg.disruptor.event.AbstractDisruptorEvent;
import net.engining.pg.disruptor.event.DisruptorApplicationEvent;
import net.engining.pg.disruptor.event.factory.DisruptorBindEventFactory;
import net.engining.pg.disruptor.event.factory.DisruptorEventThreadFactory;
import net.engining.pg.disruptor.event.handler.DisruptorEventDispatcher;
import net.engining.pg.disruptor.event.handler.DisruptorHandler;
import net.engining.pg.disruptor.event.handler.Nameable;
import net.engining.pg.disruptor.event.handler.chain.HandlerChainManager;
import net.engining.pg.disruptor.event.handler.chain.def.DefaultHandlerChainManager;
import net.engining.pg.disruptor.event.handler.chain.def.PathMatchingHandlerChainResolver;
import net.engining.pg.disruptor.event.translator.DisruptorEventOneArgTranslator;
import net.engining.pg.disruptor.event.translator.DisruptorEventThreeArgTranslator;
import net.engining.pg.disruptor.event.translator.DisruptorEventTwoArgTranslator;
import net.engining.pg.disruptor.hooks.DisruptorShutdownHook;
import net.engining.pg.disruptor.util.StringUtils;
import net.engining.pg.disruptor.util.WaitStrategys;
import net.engining.pg.disruptor.props.DisruptorProperties;
import net.engining.pg.disruptor.props.EventHandlerDefinition;
import net.engining.pg.support.utils.ValidateUtilExt;
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
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadFactory;

/**
 * @author Eric Lu
 */
@Configuration
@ConditionalOnClass({ Disruptor.class })
@ConditionalOnProperty(prefix = DisruptorProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ DisruptorProperties.class })
public class DisruptorAutoConfiguration{

	private static final Logger LOGGER = LoggerFactory.getLogger(DisruptorAutoConfiguration.class);
	public static final String DISRUPTOR_HANDLERS = "disruptorHandlers";
	public static final String DISRUPTOR_HANDLER_CHAINS = "disruptorHandlerChains";

	@Autowired
	private ApplicationContext applicationContext;

	/**
	 * 处理器链定义的集合
	 */
	private Map<String, String> handlerChainDefinitionMap = new HashMap<>();
	
	/**
	 * 决定一个消费者将如何等待生产者将Event置入Disruptor的策略。用来权衡当生产者无法将新的事件放进RingBuffer时的处理策略。
	 * （例如：当生产者太快，消费者太慢，会导致生成者获取不到新的事件槽来插入新事件，则会根据该策略进行处理，默认会堵塞）
	 * @return {@link WaitStrategy} instance
	 */
	@Bean
	@ConditionalOnMissingBean
	public WaitStrategy waitStrategy() {
		return WaitStrategys.YIELDING_WAIT;
	}

	@Bean
	@ConditionalOnMissingBean
	public ThreadFactory threadFactory() {
		return new DisruptorEventThreadFactory();
	}

	@Bean
	@ConditionalOnMissingBean
	public EventFactory<AbstractDisruptorEvent> eventFactory() {
		return new DisruptorBindEventFactory();
	}

	/**
	 * DisruptorHandler实例集合: [beanName, instance of DisruptorHandler<DisruptorEvent>]
	 */
	@Bean(DISRUPTOR_HANDLERS)
	public Map<String, DisruptorHandler<AbstractDisruptorEvent>> disruptorHandlers() {

		Map<String, DisruptorHandler<AbstractDisruptorEvent>> disruptorPreHandlers = new LinkedHashMap<>();

		Map<String, DisruptorHandler> beansOfType = applicationContext.getBeansOfType(DisruptorHandler.class);
		if (ValidateUtilExt.isNotNullOrEmpty(beansOfType)) {
			for (Entry<String, DisruptorHandler> entry : beansOfType.entrySet()) {
				if (entry.getValue() instanceof DisruptorEventDispatcher) {
					// 跳过入口实现类
					continue;
				}

				EventDispatchRule annotationType = applicationContext.findAnnotationOnBean(
						entry.getKey(),
						EventDispatchRule.class
				);
				if (ValidateUtilExt.isNullOrEmpty(annotationType)) {
					// 注解为空，则打印错误信息
					LOGGER.error(
							"Not Found AnnotationType {} on Bean {} With Name {}",
							EventDispatchRule.class,
							entry.getValue().getClass(),
							entry.getKey()
					);

				} else {
					//处理器规则的集合: [pattern, beanName]
					handlerChainDefinitionMap.put(annotationType.value(), entry.getKey());
				}

				disruptorPreHandlers.put(entry.getKey(), entry.getValue());
			}
		}
		// BeanFactoryUtils.beansOfTypeIncludingAncestors(getApplicationContext(), EventHandler.class);

		return disruptorPreHandlers;
	}

	/**
	 * DisruptorHandler Chain 的集合
	 */
	@Bean(DISRUPTOR_HANDLER_CHAINS)
	public List<DisruptorEventDispatcher> disruptorHandlerChains(
			DisruptorProperties properties,
			@Qualifier(DISRUPTOR_HANDLERS) Map<String, DisruptorHandler<AbstractDisruptorEvent>> disruptorHandlers)
	{
		// 获取定义：处理器链规则
		List<EventHandlerDefinition> handlerDefinitions = properties.getHandlerDefinitions();
		// 事件分发器集合
		List<DisruptorEventDispatcher> disruptorEventDispatchers = new ArrayList<>();
		// 未在配置文件内配置则使用@EventDispatchRule注解设置的默认规则
		if (ValidateUtilExt.isNullOrEmpty(handlerDefinitions)) {
			
			EventHandlerDefinition definition = new EventHandlerDefinition();
			
			definition.setOrder(0);
			definition.setDefinitionMap(handlerChainDefinitionMap);
			
			// 构造disruptorEventDispatchers
			disruptorEventDispatchers.add(this.createDisruptorEventHandler(definition, disruptorHandlers));
			
		} else {
			// 迭代处理器规则; 即配置的规则优先级高于
			for (EventHandlerDefinition handlerDefinition : handlerDefinitions) {
	
				// 构造DisruptorEventHandler
				disruptorEventDispatchers.add(this.createDisruptorEventHandler(handlerDefinition, disruptorHandlers));
	
			}
		}
		// 进行排序
		disruptorEventDispatchers.sort(new OrderComparator());

		return disruptorEventDispatchers;
	}

	/**
	 * 构造DisruptorEventDispatcher
	 */
	protected DisruptorEventDispatcher createDisruptorEventHandler(
			EventHandlerDefinition handlerDefinition,
			Map<String, DisruptorHandler<AbstractDisruptorEvent>> eventHandlers)
	{
		//如果在配置文件中(yml/properties)定义了dispatch规则
		if (StringUtils.isNotEmpty(handlerDefinition.getDefinitions())) {
			handlerChainDefinitionMap.putAll(this.parseHandlerChainDefinitions(handlerDefinition.getDefinitions()));
		}
		else if (ValidateUtilExt.isNotNullOrEmpty(handlerDefinition.getDefinitionMap())) {
			handlerChainDefinitionMap.putAll(handlerDefinition.getDefinitionMap());
		}

		HandlerChainManager<AbstractDisruptorEvent> manager = createHandlerChainManager(eventHandlers, handlerChainDefinitionMap);
		PathMatchingHandlerChainResolver chainResolver = new PathMatchingHandlerChainResolver();
		chainResolver.setHandlerChainManager(manager);
		return new DisruptorEventDispatcher(chainResolver, handlerDefinition.getOrder());
	}

	/**
	 * 解析Ini-formatted的键值对数据
	 */
	protected Map<String, String> parseHandlerChainDefinitions(String definitions) {
		Ini ini = new Ini();
		ini.load(definitions);
		return ini.getSection(Ini.DEFAULT_SECTION_NAME);
	}

	protected HandlerChainManager<AbstractDisruptorEvent> createHandlerChainManager(
			Map<String, DisruptorHandler<AbstractDisruptorEvent>> eventHandlers,
			Map<String, String> handlerChainDefinitionMap) {

		HandlerChainManager<AbstractDisruptorEvent> manager = new DefaultHandlerChainManager();
		if (!CollectionUtils.isEmpty(eventHandlers)) {
			for (Entry<String, DisruptorHandler<AbstractDisruptorEvent>> entry : eventHandlers.entrySet()) {
				String name = entry.getKey();
				DisruptorHandler<AbstractDisruptorEvent> handler = entry.getValue();
				if (handler instanceof Nameable) {
					((Nameable) handler).setName(name);
				}
				manager.addHandler(name, handler);
			}
		}

		if (!CollectionUtils.isEmpty(handlerChainDefinitionMap)) {
			for (Entry<String, String> entry : handlerChainDefinitionMap.entrySet()) {
				// ant匹配规则
				String rule = entry.getKey();
				String chainDefinition = entry.getValue();
				manager.createChain(rule, chainDefinition);
			}
		}

		return manager;
	}

	/**
	 * 
	 * <p>
	 * 创建Disruptor
	 * </p>
	 *
	 * @param properties	: 配置参数
	 * @param waitStrategy 	: 一种策略，用来均衡数据生产者和消费者之间的处理效率，默认提供了3个实现类
	 * @param threadFactory	: 线程工厂
	 * @param eventFactory	: 工厂类对象，用于创建Event， Event是实际的消费数据;
	 *                        初始化启动Disruptor的时候，Disruptor会调用该工厂方法创建消费数据实例存放到RingBuffer缓冲区,
	 *                        创建的对象个数为ringBufferSize指定;
	 * @param disruptorEventDispatchers	: 事件分发器
	 * @return {@link Disruptor} instance
	 */
	@Bean
	@ConditionalOnClass({ Disruptor.class })
	@ConditionalOnProperty(prefix = DisruptorProperties.PREFIX, value = "enabled", havingValue = "true")
	public Disruptor<AbstractDisruptorEvent> disruptor(
			DisruptorProperties properties, 
			WaitStrategy waitStrategy,
			ThreadFactory threadFactory, 
			EventFactory<AbstractDisruptorEvent> eventFactory,
			@Qualifier(DISRUPTOR_HANDLER_CHAINS) List<DisruptorEventDispatcher> disruptorEventDispatchers) {
		
		// http://blog.csdn.net/a314368439/article/details/72642653?utm_source=itdadao&utm_medium=referral
		Disruptor<AbstractDisruptorEvent> disruptor;
		if (properties.isMultiProducer()) {
			disruptor = new Disruptor<>(
					eventFactory,
					properties.getRingBufferSize(),
					threadFactory,
					ProducerType.MULTI,
					waitStrategy
			);
		} else {
			disruptor = new Disruptor<>(
					eventFactory,
					properties.getRingBufferSize(),
					threadFactory,
					ProducerType.SINGLE,
					waitStrategy
			);
		}

		if (ValidateUtilExt.isNotNullOrEmpty(disruptorEventDispatchers)) {

			// 进行排序
			disruptorEventDispatchers.sort(new OrderComparator());
			
			// 使用disruptor创建消费者组
			EventHandlerGroup<AbstractDisruptorEvent> handlerGroup = null;
			for (int i = 0; i < disruptorEventDispatchers.size(); i++) {
				// 连接消费事件方法，其中EventHandler的是为消费者消费消息的实现类
				DisruptorEventDispatcher eventHandler = disruptorEventDispatchers.get(i);
				if(i < 1) {
					handlerGroup = disruptor.handleEventsWith(eventHandler);
				} else {
					// 完成前置事件处理之后执行后置事件处理
					handlerGroup.then(eventHandler);
				}
			}
		}

		// 启动
		disruptor.start();

		// 应用退出时，要调用shutdown来清理资源;
		Runtime.getRuntime().addShutdownHook(new DisruptorShutdownHook(disruptor));

		return disruptor;

	}
	
	@Bean
	@ConditionalOnMissingBean
	public EventTranslatorOneArg<AbstractDisruptorEvent, AbstractDisruptorEvent> oneArgEventTranslator() {
		return new DisruptorEventOneArgTranslator();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public EventTranslatorTwoArg<AbstractDisruptorEvent, String, String> twoArgEventTranslator() {
		return new DisruptorEventTwoArgTranslator();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public EventTranslatorThreeArg<AbstractDisruptorEvent, String, String, String> threeArgEventTranslator() {
		return new DisruptorEventThreeArgTranslator();
	}
	
	@Bean
	public DisruptorTemplate disruptorTemplate() {
		return new DisruptorTemplate();
	}

    /**
     * DisruptorApplicationEvent的监听器：接收DisruptorApplicationEvent并将其携带的数据作为DisruptorEvent传递给Disruptor
	 */
	@Bean
	public ApplicationListener<DisruptorApplicationEvent> disruptorEventListener(
			Disruptor<AbstractDisruptorEvent> disruptor,
			EventTranslatorOneArg<AbstractDisruptorEvent, AbstractDisruptorEvent> oneArgEventTranslator)
	{
		return appEvent -> {
			//利用Spring的ApplicationEvent机制，将DisruptorEvent作为ApplicationEvent的事件数据载体，传递给Disruptor
			//TODO 此处为何不用bind属性?
			AbstractDisruptorEvent event = (AbstractDisruptorEvent) appEvent.getSource();
			disruptor.publishEvent(oneArgEventTranslator, event);
		};
	}
	
	@Bean
	public DisruptorEventAwareProcessor disruptorEventAwareProcessor() {
		return new DisruptorEventAwareProcessor();
	}
	
}
