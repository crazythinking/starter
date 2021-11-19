package net.engining.drools.autoconfigure.core;

import net.engining.drools.autoconfigure.props.DroolsProperties;
import net.engining.drools.autoconfigure.props.GavConverter;
import net.engining.drools.autoconfigure.props.GroupedDroolsProperties;
import net.engining.pg.support.core.exception.ErrorCode;
import net.engining.pg.support.core.exception.ErrorMessageException;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.command.Command;
import org.kie.api.io.Resource;
import org.kie.api.management.GAV;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieContainerSessionsPool;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-11-16 14:59
 * @since :
 **/
public class KieEngine implements InitializingBean, BeanClassLoaderAware, DisposableBean {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(KieEngine.class);

    /**
     * 缓存 KieContainer；可通过目录隔离不同的KieContainer
     */
    private static final Map<String, KieContainer> KIE_CONTAINERS = new ConcurrentHashMap<>();

    /**
     * 缓存 KieScanner
     */
    private static final Map<String, KieScanner> KIE_SCANNERS = new ConcurrentHashMap<>();

    /**
     * 缓存 KieContainerSessionsPool
     */
    private static final Map<String, KieContainerSessionsPool> KIE_CONTAINER_SESSIONS_POOL = new ConcurrentHashMap<>();

    /**
     * 当前JVM所属的KieServices的单例，每个JVM进程只需要一个
     */
    private static final KieServices kieServices = KieServices.Factory.get();

    /**
     * 规则文件后缀
     */
    public static final String SUFFIX_DRL = "drl";

    /**
     * 决策表后缀 excel 97-03版本的后缀 drools同样支持
     */
    public static final String SUFFIX_EXCEL = "xls";

    /**
     * 决策表后缀 excel 2007版本以后的后缀 drools同样支持
     */
    public static final String SUFFIX_EXCEL_2007 = "xlsx";

    /**
     * CSV后缀
     */
    public static final String SUFFIX_CSV = "csv";

    private ClassLoader classLoader;

    private GroupedDroolsProperties groupedDroolsProperties;

    public KieEngine(GroupedDroolsProperties groupedDroolsProperties) {
        this.groupedDroolsProperties = groupedDroolsProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * 获取kieContainerId对应的KieContainer，如果存在缓存中则直接返回，否则创建新的；其会在配置的绝对路径下根据GAV动态更新Kjar；
     * kieContainerId必须符合jar版本格式，即"G:A:V"的字符串组成；
     *
     * @param kieContainerId    container id
     * @return KieContainer
     */
    public KieContainer getKieContainer(String kieContainerId) throws Exception {
        if (ValidateUtilExt.isNotNullOrEmpty(KIE_CONTAINERS.get(kieContainerId))){
            return KIE_CONTAINERS.get(kieContainerId);
        }
        else {
            GAV gav = GavConverter.getGav(kieContainerId);
            DroolsProperties properties = groupedDroolsProperties.getGavDroolsProperties().get(gav);
            if (ValidateUtilExt.isNotNullOrEmpty(properties)){
                String fullPath = groupedDroolsProperties.getBasePath() + properties.getPath();
                KieFileSystem kfs = kieServices.newKieFileSystem();
                Resource resource = ResourceFactory.newFileResource(fullPath);
                kfs.write(resource);
                //构建Kjar中的Asset
                KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
                List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
                if (errors.size() > 0) {
                    //error msg
                    errors.forEach(message -> {
                        LOGGER.error(message.toString());
                    });
                    throw new ErrorMessageException(
                            ErrorCode.CheckError,
                            "Can not create container cause by build error!"
                    );
                }
                //一个KieContainer对应一个Kjar
                ReleaseId releaseId = kieServices.newReleaseId(
                        gav.getGroupId(),
                        gav.getArtifactId(),
                        gav.getVersion()
                );
                //要支持不同的版本
                KieContainer kieContainer = kieServices.newKieContainer(gav.toString(), releaseId);
                //一个Session Pool 对应一个KieContainer
                KieContainerSessionsPool sessionsPool = kieContainer.newKieSessionsPool(properties.getPoolSize());

                //一个Scanner对应一个KieContainer
                KieScanner kieScanner = kieServices.newKieScanner(kieContainer, fullPath);
                kieScanner.start(groupedDroolsProperties.getInterval());

                //缓存
                KIE_CONTAINERS.putIfAbsent(gav.toString(), kieContainer);
                KIE_CONTAINER_SESSIONS_POOL.putIfAbsent(gav.toString(), sessionsPool);
                KIE_SCANNERS.putIfAbsent(gav.toString(), kieScanner);
                return kieContainer;
            }
            else {
                throw new IllegalArgumentException("the kieContainerId is not match pattern 'G:A:V'");
            }
        }
    }

    /**
     * 获取 StatefulSession
     * @param containerId   KieContainer唯一标识
     * @param sessionName   session Name
     * @return              StatefulSession
     */
    public KieSession getStatefulSession(String containerId, String sessionName) {
        return KIE_CONTAINER_SESSIONS_POOL.get(containerId).newKieSession(sessionName);
    }

    /**
     * 获取 StatelessSession
     * @param containerId   KieContainer唯一标识
     * @param sessionName   session Name
     * @return              StatelessSession
     */
    public StatelessKieSession getStatelessSession(String containerId, String sessionName) {
        return KIE_CONTAINER_SESSIONS_POOL.get(containerId).newStatelessKieSession(sessionName);
    }

    public KieScanner getKieScanner(String kieContainerId) {
        return KIE_SCANNERS.get(kieContainerId);
    }

    public void statelessExecute(String containerId, String sessionName, Object fact){
        getStatelessSession(containerId, sessionName).execute(fact);
    }

    public void statelessExecute(String containerId, String sessionName, Iterable facts){
        getStatelessSession(containerId, sessionName).execute(facts);
    }

    public <T> void statelessExecute(String containerId, String sessionName, Command<T> command){
        getStatelessSession(containerId, sessionName).execute(command);
    }

    public void statefulExecute(String containerId, String sessionName, Object ... facts) {
        KieSession session = getStatefulSession(containerId, sessionName);
        for (Object fact : facts) {
            session.insert(fact);
        }
        session.fireAllRules();

    }

    @Override
    public void destroy() throws Exception {
        KIE_SCANNERS.forEach((s, kieScanner) -> {
            kieScanner.stop();
        });
        KIE_CONTAINERS.forEach((s, kieContainer) -> {
            kieContainer.dispose();
        });
    }
}
