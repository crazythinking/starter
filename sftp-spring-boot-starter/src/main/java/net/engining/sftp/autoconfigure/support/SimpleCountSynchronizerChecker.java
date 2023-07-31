package net.engining.sftp.autoconfigure.support;

import cn.hutool.core.io.FileUtil;
import com.google.common.collect.Maps;
import com.jcraft.jsch.ChannelSftp;
import net.engining.pg.support.utils.ThreadUtilsExt;
import net.engining.pg.support.utils.ValidateUtilExt;
import net.engining.sftp.autoconfigure.props.SftpProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.integration.sftp.filters.SftpRegexPatternFileListFilter;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author : Eric Lu
 * @version :
 * @since : 2023-07-06 14:00
 **/
public class SimpleCountSynchronizerChecker implements SynchronizerChecker, SmartLifecycle {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCountSynchronizerChecker.class);
    public static final String LOCAL_FILES_COUNT = "localFilesCount";
    public static final String REMOTE_FILES_COUNT = "remoteFilesCount";

    ScheduledExecutorService executorService;

    ScheduledFuture<?> scheduledFuture;

    Map<String, Object> result = Maps.newHashMap();

    String key;

    SftpProperties sftpProperties;

    SftpRemoteFileTemplate sftpRemoteFileTemplate;

    SftpRegexPatternFileListFilter fileListFilter;

    Runnable task;

    protected boolean running = false;

    public SimpleCountSynchronizerChecker(String key, SftpProperties sftpProperties,
                                          SftpRemoteFileTemplate sftpRemoteFileTemplate) {
        this.key = key;
        this.sftpProperties = sftpProperties;
        this.sftpRemoteFileTemplate = sftpRemoteFileTemplate;
        init();
    }

    @Override
    public Map<String, Object> check() {
        return result;
    }

    @Override
    public boolean isOk() {
        if (ValidateUtilExt.isNotNullOrEmpty(result.get(LOCAL_FILES_COUNT))
                && ValidateUtilExt.isNotNullOrEmpty(result.get(REMOTE_FILES_COUNT))) {
            return (int) result.get(LOCAL_FILES_COUNT) == (int) result.get(REMOTE_FILES_COUNT);
        }
        return false;
    }

    private void init() {
        fileListFilter = new SftpRegexPatternFileListFilter(sftpProperties.getSyncFileNameRegex());

        executorService = ThreadUtilsExt.newSingleThreadScheduledExecutor(key+"-synchronizer-checker", true);
        task = () -> {
            // 执行定时任务的逻辑
            File[] localFiles = FileUtil.ls(sftpProperties.getDefaultLocalDirectory());

            List<ChannelSftp.LsEntry> remoteFiles = fileListFilter.filterFiles(
                    sftpRemoteFileTemplate.list(sftpProperties.getDefaultRemoteDirectory())
            );
            LOGGER.info("Sftp synchronizer {}: local files count {}, remote files count {}",
                    key, localFiles.length, remoteFiles.size()
            );
            result.put(LOCAL_FILES_COUNT, localFiles.length);
            result.put(REMOTE_FILES_COUNT, remoteFiles.size());
        };
    }


    @Override
    public void start() {
        try {
            scheduledFuture = executorService.scheduleAtFixedRate(
                    task, 1, sftpProperties.getCheckerInterval(), TimeUnit.MINUTES
            );
        } finally {
            running = true;
        }
    }

    @Override
    public void stop() {
        try {
            scheduledFuture.cancel(true);
            executorService.shutdown();
        } finally {
            running = false;
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

}
