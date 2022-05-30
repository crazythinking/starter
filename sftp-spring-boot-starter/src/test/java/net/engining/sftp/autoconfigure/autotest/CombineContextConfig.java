package net.engining.sftp.autoconfigure.autotest;

import com.jcraft.jsch.ChannelSftp;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import net.engining.sftp.autoconfigure.props.MutiSftpProperties;
import net.engining.sftp.autoconfigure.support.SftpConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.file.remote.session.DelegatingSessionFactory;

/**
 * 通用Context配置
 *
 * @author Eric Lu
 */
@Configuration
public class CombineContextConfig {

    /**
     * ApplicationContext的静态辅助Bean，建议项目必须注入
     */
    @Bean
    @Lazy(value = false)
    public ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }

    /**
     * sftp-test1连接的同步器，需要打开同步器的开关；
     */
    @Bean
    public IntegrationFlow sftpTest1IntegrationFlow(MutiSftpProperties mutiSftpProperties,
                                               DelegatingSessionFactory<ChannelSftp.LsEntry> delegatingSessionFactory
    ){
        return SftpConfigUtils.buildIntegrationFlow(
                "sftp-test1",
                mutiSftpProperties.getNamedSftpProperties().get("sftp-test1"),
                delegatingSessionFactory.getFactoryLocator().getSessionFactory("sftp-test1")
        );
    }
}