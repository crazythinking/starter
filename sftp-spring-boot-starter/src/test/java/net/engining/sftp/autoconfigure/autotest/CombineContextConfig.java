package net.engining.sftp.autoconfigure.autotest;

import com.jcraft.jsch.ChannelSftp;
import net.engining.pg.disruptor.props.DisruptorProperties;
import net.engining.pg.support.core.context.ApplicationContextHolder;
import net.engining.sftp.autoconfigure.autotest.support.SftpTest1Disruptor;
import net.engining.sftp.autoconfigure.props.MutiSftpProperties;
import net.engining.sftp.autoconfigure.support.SftpConfigUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
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

    public static final String SFTP_TEST_1 = "sftp-test1";
    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    DisruptorProperties properties;

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
                SFTP_TEST_1,
                mutiSftpProperties.getNamedSftpProperties().get(SFTP_TEST_1),
                delegatingSessionFactory.getFactoryLocator().getSessionFactory(SFTP_TEST_1),
                applicationContext
        );
    }

    /**
     * 注意disruptor的beanName,groupKey与sftp配置的key应始终保持一致
     */
    @Bean(SFTP_TEST_1)
    public SftpTest1Disruptor sampleFileDisruptor(){
        return new SftpTest1Disruptor(
                applicationContext,
                properties
        );
    }

}
