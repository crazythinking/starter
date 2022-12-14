package net.engining.sftp.autoconfigure.support;

import com.google.common.base.Preconditions;
import com.jcraft.jsch.ChannelSftp;
import net.engining.pg.disruptor.event.GenericDisruptorApplicationEvent;
import net.engining.pg.support.core.exception.ErrorCode;
import net.engining.pg.support.core.exception.ErrorMessageException;
import net.engining.pg.support.utils.ValidateUtilExt;
import net.engining.sftp.autoconfigure.props.SftpProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.filters.LastModifiedFileListFilter;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.dsl.Sftp;
import org.springframework.integration.sftp.dsl.SftpInboundChannelAdapterSpec;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.messaging.Message;

import java.io.File;
import java.time.Duration;
import java.util.Objects;
import java.util.Properties;

import static org.springframework.integration.context.IntegrationContextUtils.ERROR_CHANNEL_BEAN_NAME;

public final class SftpConfigUtils {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SftpConfigUtils.class);

    public static final String TEMPORARY_FILE_SUFFIX = ".writing";
    public static final String SFTP_REMOTE_FILE_TEMPLATE_MAP = "sftpRemoteFileTemplateMap";
    public static final String DEFAULT = "default";

    public static final String SFTP_SYNCHRONIZER_INBOUND_CHANNEL_ADAPTER = "SftpSyncInboundChannelAdapter";

    public static CachingSessionFactory<ChannelSftp.LsEntry> buildCachingSessionFactory(SftpProperties sftpProperties) {
        Preconditions.checkArgument(
                ValidateUtilExt.isNotNullOrEmpty(sftpProperties.getUser()),
                "SFTP user must be set"
        );
        Preconditions.checkArgument(
                ValidateUtilExt.isNotNullOrEmpty(sftpProperties.getHost()),
                "SFTP host must be set"
        );

        DefaultSftpSessionFactory sftpSessionFactory = new DefaultSftpSessionFactory(true);
        sftpSessionFactory.setAllowUnknownKeys(true);
        //设置更底层的配置
        Properties properties = new Properties();
        //连接server的key在本地不存在，那么就自动添加到文件中（默认是known_hosts），并且给出一个警告
        properties.put( "StrictHostKeyChecking" , "no" );
        sftpSessionFactory.setSessionConfig(properties);
        sftpSessionFactory.setHost(sftpProperties.getHost());
        sftpSessionFactory.setPort(sftpProperties.getPort());
        sftpSessionFactory.setUser(sftpProperties.getUser());
        sftpSessionFactory.setPassword(sftpProperties.getPassword());
        //socket超时时间，以及默认的连接超时时间
        sftpSessionFactory.setTimeout(sftpProperties.getTimeout());
        //Jsch源码中设置ServerAliveInterval会同时覆盖timeout参数，所以这里取同一个值
        sftpSessionFactory.setServerAliveInterval(sftpProperties.getTimeout());
        sftpSessionFactory.setChannelConnectTimeout(Duration.ofMillis(sftpProperties.getTimeout()));
        sftpSessionFactory.setServerAliveCountMax(sftpProperties.getServerAliveCountMax());

        CachingSessionFactory<ChannelSftp.LsEntry> cachingSessionFactory =
                new CachingSessionFactory<>(sftpSessionFactory, sftpProperties.getSessionCacheSize());
        cachingSessionFactory.setSessionWaitTimeout(sftpProperties.getCachingSessionWaitTimeout());
        cachingSessionFactory.setTestSession(true);
        return cachingSessionFactory;
    }

    public static SftpRemoteFileTemplate buildSftpRemoteFileTemplate(BeanFactory beanFactory, SftpProperties sftpProperties,
                                                                     SessionFactory<ChannelSftp.LsEntry> cachingSessionFactory
    ) {
        SftpRemoteFileTemplate sftpRemoteFileTemplate = new SftpRemoteFileTemplate(cachingSessionFactory);
        sftpRemoteFileTemplate.setAutoCreateDirectory(true);
        sftpRemoteFileTemplate.setTemporaryFileSuffix(TEMPORARY_FILE_SUFFIX);
        sftpRemoteFileTemplate.setUseTemporaryFileName(true);
        sftpRemoteFileTemplate.setCharset(sftpProperties.getCharset());
        sftpRemoteFileTemplate.setRemoteDirectoryExpression(
                new LiteralExpression(sftpProperties.getDefaultRemoteDirectory())
        );
        sftpRemoteFileTemplate.setBeanFactory(beanFactory);
        sftpRemoteFileTemplate.afterPropertiesSet();

        return sftpRemoteFileTemplate;
    }

    public static IntegrationFlow buildIntegrationFlow(String key, SftpProperties sftpProperties,
                                                       SessionFactory<ChannelSftp.LsEntry> cachingSessionFactory,
                                                       ApplicationContext applicationContext
    ) {
        SftpInboundChannelAdapterSpec inboundChannelAdapterSpec =
                Sftp.inboundAdapter(cachingSessionFactory)
                        .preserveTimestamp(true)
                        .remoteDirectory(sftpProperties.getDefaultRemoteDirectory())
                        .deleteRemoteFiles(sftpProperties.isDeleteRemoteFiles())
                        //.regexFilter(".*\\.txt$")
                        .regexFilter(sftpProperties.getSyncFileNameRegex())
                        .temporaryFileSuffix(TEMPORARY_FILE_SUFFIX)
                        .autoCreateLocalDirectory(true)
                        .localDirectory(new File(sftpProperties.getDefaultLocalDirectory()))
                        //LastModifiedFileListFilter根据最后修改时间过滤，相应的服务端的deleteRemoteFiles就应该设置为false；
                        //AcceptOnceFileListFilter根据文件名过滤，相应的服务端的deleteRemoteFiles就应该设置为true，否则会一直告警；
                        .localFilter(new LastModifiedFileListFilter())
                        .maxFetchSize(sftpProperties.getMaxFetchSize());
        //对指定的同步本地目录启用WatchService(linux下使用内核inotify)监听目录内增删改事件
        inboundChannelAdapterSpec.get().setUseWatchService(true);

        return IntegrationFlows
                .from(
                        inboundChannelAdapterSpec,
                        sourcePollingChannelAdapterSpec ->
                                sourcePollingChannelAdapterSpec.id(key + SFTP_SYNCHRONIZER_INBOUND_CHANNEL_ADAPTER)
                                        .autoStartup(true)
                                        .poller(
                                                Pollers.fixedDelay(sftpProperties.getPollingInterval())
                                                        .maxMessagesPerPoll(sftpProperties.getMaxMessagesPerPoll())
                                                        .errorChannel(ERROR_CHANNEL_BEAN_NAME)
                                        )
                )
                //此处监听的事件是上面设置的本地目录内文件变化事件，只监听了Create和Modify
                .handle(message -> {
                    LOGGER.debug(
                            "Received message headers:{}, payload:{}",
                            message.getHeaders(),
                            message.getPayload()
                    );
                    //文件的操作通常都比较重，因此使用Disruptor来处理，避免阻塞
                    if (ValidateUtilExt.isNotNullOrEmpty(message.getHeaders().getId())){
                        GenericDisruptorApplicationEvent<File> event =
                                new GenericDisruptorApplicationEvent<>(message);
                        event.setTopicKey(key);
                        event.setKey(message.getHeaders().getId().toString());
                        event.setBind((File) message.getPayload());
                        applicationContext.publishEvent(event);
                    }
                    else {
                        throw new ErrorMessageException(
                                ErrorCode.CheckError,
                                "message.getHeaders().getId() should not be null");
                    }

                })
                .get();
    }

}
