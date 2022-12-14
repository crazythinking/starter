package net.engining.sftp.autoconfigure.autotest.cases;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.extra.ssh.JschSessionPool;
import cn.hutool.extra.ssh.JschUtil;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import net.engining.pg.support.utils.ExceptionUtilsExt;
import net.engining.pg.support.utils.ValidateUtilExt;
import net.engining.sftp.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.sftp.autoconfigure.props.MutiSftpProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static net.engining.sftp.autoconfigure.support.SftpConfigUtils.DEFAULT;
import static net.engining.sftp.autoconfigure.support.SftpConfigUtils.SFTP_REMOTE_FILE_TEMPLATE_MAP;

/**
 * @author Eric Lu
 * @date 2019-09-21 23:58
 **/
@ActiveProfiles(profiles = {
        "sftp.common"
})
public class SimpleTestCase extends AbstractTestCaseTemplate {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTestCase.class);

    @Resource(name = SFTP_REMOTE_FILE_TEMPLATE_MAP)
    Map<String, SftpRemoteFileTemplate> sftpRemoteFileTemplateMap;

    @Autowired
    MutiSftpProperties mutiSftpProperties;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {

    }

    @Override
    public void testProcess() throws Exception {
        SftpRemoteFileTemplate sftpRemoteFileTemplate = sftpRemoteFileTemplateMap.get(DEFAULT);
        sftpRemoteFileTemplate.send(MessageBuilder.withPayload("test").build());
        LOGGER.info("file exists:{}", sftpRemoteFileTemplate.exists("/home/test"));
        sftpRemoteFileTemplate.send(MessageBuilder.withPayload("test2").build());

        sftpRemoteFileTemplate.executeWithClient(client -> {
            LOGGER.info("client:{}", client);
            try {
                //????????? 10?????? ????????????????????? 8?????? ??????
                ((ChannelSftp) client).chmod(Integer.parseInt("755", 8), "/home/luxue/sftp-test/1.txt");
                ((ChannelSftp) client).chmod(Integer.parseInt("755", 8), "/home/luxue/sftp-test/sub");
            } catch (SftpException e) {
                ExceptionUtilsExt.dump(e);
            }
            return null;
        });

        if (ValidateUtilExt.isNotNullOrEmpty(JschSessionPool.INSTANCE.get(DEFAULT))) {
            String res = JschUtil.exec(
                    JschSessionPool.INSTANCE.get(DEFAULT),
                    "ls -l /home/luxue/sftp-test | grep 1.txt",
                    CharsetUtil.CHARSET_UTF_8,
                    System.err
            );
            LOGGER.info("exec res:{}", res);
        }

        TimeUnit.SECONDS.sleep(600);
    }

    @Override
    public void end() throws Exception {
        JschSessionPool.INSTANCE.closeAll();
    }
}
