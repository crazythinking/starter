package net.engining.sftp.autoconfigure;

import com.google.common.collect.Maps;
import com.jcraft.jsch.ChannelSftp;
import net.engining.sftp.autoconfigure.config.FileSynchronizerContextConfig;
import net.engining.sftp.autoconfigure.props.MutiSftpProperties;
import net.engining.sftp.autoconfigure.support.SftpConfigUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.file.remote.session.DelegatingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;

import java.util.Map;

import static net.engining.sftp.autoconfigure.support.SftpConfigUtils.DEFAULT;
import static net.engining.sftp.autoconfigure.support.SftpConfigUtils.SFTP_REMOTE_FILE_TEMPLATE_MAP;

@Configuration
@ConditionalOnProperty(prefix = "pg.sftp", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties({
        MutiSftpProperties.class,
})
@Import({
        FileSynchronizerContextConfig.class,
})
@EnableIntegration
public class SftpIntegrationAutoConfiguration {

    @Autowired
    ApplicationContext applicationContext;

    @Bean
    public DelegatingSessionFactory<ChannelSftp.LsEntry> delegatingSessionFactory(MutiSftpProperties mutiSftpProperties) {
        SessionFactory<ChannelSftp.LsEntry> defaultCachingSessionFactory =
                SftpConfigUtils.buildCachingSessionFactory(mutiSftpProperties.getDefaultSftpProperties());

        Map<Object, SessionFactory<ChannelSftp.LsEntry>> sessionFactoryMap = Maps.newHashMap();
        mutiSftpProperties.getNamedSftpProperties().forEach((name, sftpProperties) -> {
            sessionFactoryMap.put(name, SftpConfigUtils.buildCachingSessionFactory(sftpProperties));
        });

        return new DelegatingSessionFactory<>(sessionFactoryMap, defaultCachingSessionFactory);
    }

    @Bean(SFTP_REMOTE_FILE_TEMPLATE_MAP)
    public Map<String, SftpRemoteFileTemplate> sftpRemoteFileTemplateMap(MutiSftpProperties mutiSftpProperties,
                                                                         DelegatingSessionFactory<ChannelSftp.LsEntry> delegatingSessionFactory
    ){
        DefaultListableBeanFactory beanFactory =
                (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

        Map<String, SftpRemoteFileTemplate> sftpRemoteFileTemplateMap = Maps.newHashMap();
        sftpRemoteFileTemplateMap.put(
                DEFAULT,
                SftpConfigUtils.buildSftpRemoteFileTemplate(
                        beanFactory,
                        mutiSftpProperties.getDefaultSftpProperties(),
                        delegatingSessionFactory.getFactoryLocator().getSessionFactory(DEFAULT)
                )
        );
        mutiSftpProperties.getNamedSftpProperties().forEach((name, sftpProperties) -> {
            sftpRemoteFileTemplateMap.put(
                    name,
                    SftpConfigUtils.buildSftpRemoteFileTemplate(beanFactory, sftpProperties, delegatingSessionFactory)
            );
        });

        sftpRemoteFileTemplateMap.forEach((name, sftpRemoteFileTemplate) -> {
            //注册DebeziumEngine到Spring容器
            beanFactory.registerSingleton(name+"SftpRemoteFileTemplate", sftpRemoteFileTemplate);
        });

        return sftpRemoteFileTemplateMap;
    }


}
