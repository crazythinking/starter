package net.engining.sftp.autoconfigure.autotest.cases;

import net.engining.sftp.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@ActiveProfiles(profiles={
        "sftp.common"
})
public class SimpleTestCase extends AbstractTestCaseTemplate {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTestCase.class);

    @Resource(name = SFTP_REMOTE_FILE_TEMPLATE_MAP)
    Map<String, SftpRemoteFileTemplate> sftpRemoteFileTemplateMap;

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
        TimeUnit.SECONDS.sleep(60);
    }

    @Override
    public void end() throws Exception {
    }
}
