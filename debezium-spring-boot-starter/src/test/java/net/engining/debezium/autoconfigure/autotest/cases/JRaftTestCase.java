package net.engining.debezium.autoconfigure.autotest.cases;


import com.alipay.sofa.jraft.rhea.StateListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.engining.debezium.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.pg.rheakv.props.KvServerProperties;
import net.engining.pg.storage.rheakv.KvServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Eric Lu
 **/
@ActiveProfiles(profiles = {
        "debezium.common",
        "debezium.xxljob.mysql",
        //"debezium.xxljob.oracle",
        "rheakv.server.common"
})
public class JRaftTestCase extends AbstractTestCaseTemplate {
    @Autowired
    Environment environment;

    @Autowired
    List<StateListener> stateListeners;

    KvServer kvServer2;

    KvServer kvServer3;

    @Override
    public void initTestData() throws Exception {
        //因为配置不同，KvServerProperties同时加载会产出覆盖冲突，所以这里为server2，server3手动读取yaml配置
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        KvServerProperties properties2 = mapper.readValue(
                new org.springframework.core.io.ClassPathResource("server2.yml").getInputStream(),
                KvServerProperties.class
        );
        properties2.afterPropertiesSet();
        kvServer2 = new KvServer(properties2, environment, stateListeners);
        kvServer2.start();

        KvServerProperties properties3 = mapper.readValue(
                new org.springframework.core.io.ClassPathResource("server3.yml").getInputStream(),
                KvServerProperties.class
        );
        properties3.afterPropertiesSet();
        kvServer3 = new KvServer(properties3, environment, stateListeners);
        kvServer3.start();
    }

    @Override
    public void assertResult() {
    }

    @Override
    public void testProcess() throws Exception {
        TimeUnit.SECONDS.sleep(100); //睡眠100秒
    }

    @Override
    public void end() {

    }

}
