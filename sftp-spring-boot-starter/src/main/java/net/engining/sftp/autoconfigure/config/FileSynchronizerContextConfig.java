package net.engining.sftp.autoconfigure.config;

import com.jcraft.jsch.ChannelSftp;
import net.engining.sftp.autoconfigure.props.MutiSftpProperties;
import net.engining.sftp.autoconfigure.support.SftpConfigUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.file.remote.session.DelegatingSessionFactory;

@Configuration
@ConditionalOnProperty(prefix = "pg.sftp.synchronizer", name = "enabled", havingValue = "true")
@EnableIntegration
public class FileSynchronizerContextConfig {

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

}
