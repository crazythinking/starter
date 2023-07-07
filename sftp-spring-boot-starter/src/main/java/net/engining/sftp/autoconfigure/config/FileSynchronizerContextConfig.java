package net.engining.sftp.autoconfigure.config;

import com.google.common.collect.Maps;
import com.jcraft.jsch.ChannelSftp;
import net.engining.sftp.autoconfigure.props.MutiSftpProperties;
import net.engining.sftp.autoconfigure.support.SftpConfigUtils;
import net.engining.sftp.autoconfigure.support.SimpleCountSynchronizerChecker;
import net.engining.sftp.autoconfigure.support.SynchronizerChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.file.remote.session.DelegatingSessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;

import java.util.Map;

import static net.engining.sftp.autoconfigure.support.SftpConfigUtils.*;

/**
 * @author Eric Lu
 */
@Configuration
@ConditionalOnProperty(prefix = "pg.sftp.synchronizer", name = "enabled", havingValue = "true")
@EnableIntegration
public class FileSynchronizerContextConfig {

    public static final String SYNCHRONIZER_CHECKER_MAP = "synchronizerCheckerMap";

    @Autowired
    ApplicationContext applicationContext;

    /**
     * 创建sftp默认连接的同步器，需要打开同步器的开关；
     * 其他的sftp连接，需要打开同步器的开关，并且需要在实际项目中自行创建IntegrationFlow（参考该方法）；
     */
    @Bean
    public IntegrationFlow defaultSftpIntegrationFlow(MutiSftpProperties mutiSftpProperties,
                                                      DelegatingSessionFactory<ChannelSftp.LsEntry> delegatingSessionFactory
    ){
        if (mutiSftpProperties.getDefaultSftpProperties().isSyncEnabled()){
            return SftpConfigUtils.buildIntegrationFlow(
                    SftpConfigUtils.DEFAULT,
                    mutiSftpProperties.getDefaultSftpProperties(),
                    delegatingSessionFactory.getFactoryLocator().getSessionFactory(SftpConfigUtils.DEFAULT),
                    applicationContext
            );
        }
        return null;
    }

    @Bean(SYNCHRONIZER_CHECKER_MAP)
    public Map<String, SynchronizerChecker> synchronizerCheckerMap(MutiSftpProperties mutiSftpProperties,
                                                                   @Qualifier(SFTP_REMOTE_FILE_TEMPLATE_MAP)
                                                                   Map<String, SftpRemoteFileTemplate> sftpRemoteFileTemplateMap
    ){
        Map<String, SynchronizerChecker> synchronizerCheckerMap = Maps.newHashMap();

        DefaultListableBeanFactory beanFactory =
                (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

        if (mutiSftpProperties.getDefaultSftpProperties().isSyncEnabled()){
            synchronizerCheckerMap.put(
                    DEFAULT,
                    new SimpleCountSynchronizerChecker(
                            DEFAULT, mutiSftpProperties.getDefaultSftpProperties(), sftpRemoteFileTemplateMap.get(DEFAULT))
            );
        }

        for (String key: mutiSftpProperties.getNamedSftpProperties().keySet()){
            if (mutiSftpProperties.getNamedSftpProperties().get(key).isSyncEnabled()){
                SftpRemoteFileTemplate sftpRemoteFileTemplate = sftpRemoteFileTemplateMap.get(key);
                SynchronizerChecker synchronizerChecker = new SimpleCountSynchronizerChecker(
                        key, mutiSftpProperties.getNamedSftpProperties().get(key), sftpRemoteFileTemplate
                );
                synchronizerCheckerMap.put(key, synchronizerChecker);
            }
        }

        for (String key: synchronizerCheckerMap.keySet()){
            beanFactory.registerSingleton(
                    key+ SYNCHRONIZER_CHECKER_SUFFIX,
                    synchronizerCheckerMap.get(key)
            );
        }

        return synchronizerCheckerMap;
    }

}
