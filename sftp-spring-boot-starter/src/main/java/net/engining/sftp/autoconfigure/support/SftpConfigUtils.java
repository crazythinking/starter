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
        //????????????????????????
        Properties properties = new Properties();
        //??????server???key??????????????????????????????????????????????????????????????????known_hosts??????????????????????????????
        properties.put( "StrictHostKeyChecking" , "no" );
        sftpSessionFactory.setSessionConfig(properties);
        sftpSessionFactory.setHost(sftpProperties.getHost());
        sftpSessionFactory.setPort(sftpProperties.getPort());
        sftpSessionFactory.setUser(sftpProperties.getUser());
        sftpSessionFactory.setPassword(sftpProperties.getPassword());
        //socket????????????????????????????????????????????????
        sftpSessionFactory.setTimeout(sftpProperties.getTimeout());
        //Jsch???????????????ServerAliveInterval???????????????timeout????????????????????????????????????
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
                        //LastModifiedFileListFilter??????????????????????????????????????????????????????deleteRemoteFiles??????????????????false???
                        //AcceptOnceFileListFilter?????????????????????????????????????????????deleteRemoteFiles??????????????????true???????????????????????????
                        .localFilter(new LastModifiedFileListFilter())
                        .maxFetchSize(sftpProperties.getMaxFetchSize());
        //????????????????????????????????????WatchService(linux???????????????inotify)??????????????????????????????
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
                //???????????????????????????????????????????????????????????????????????????????????????Create???Modify
                .handle(message -> {
                    LOGGER.debug(
                            "Received message headers:{}, payload:{}",
                            message.getHeaders(),
                            message.getPayload()
                    );
                    //????????????????????????????????????????????????Disruptor????????????????????????
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
