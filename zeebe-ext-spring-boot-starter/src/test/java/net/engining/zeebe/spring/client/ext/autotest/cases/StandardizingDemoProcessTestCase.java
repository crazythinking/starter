package net.engining.zeebe.spring.client.ext.autotest.cases;

import cn.hutool.core.lang.Console;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import net.engining.zeebe.spring.client.ext.autotest.support.AbstractTestCaseTemplate;
import net.engining.zeebe.spring.client.ext.bean.DefaultRequestHeader;
import net.engining.zeebe.spring.client.ext.bean.DefaultResponseHeader;
import net.engining.zeebe.spring.client.ext.bean.ZeebeContext;
import net.engining.zeebe.spring.client.ext.bean.ZeebeContextBuilder;
import net.engining.zeebe.spring.client.ext.bean.ZeebeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;

/**
 * @author : Eric Lu
 * @date : 2020-10-31 18:25
 **/
@ActiveProfiles(profiles = {
        "zeebe",
        "zeebe.dev"
})
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
public class StandardizingDemoProcessTestCase extends AbstractTestCaseTemplate {

    @Autowired
    StandardizingDemoProcessStarterService starterService;

    @Override
    public void initTestData() throws Exception {

    }

    @Override
    public void assertResult() throws Exception {

    }

    @Override
    public void testProcess() throws Exception {

        DefaultRequestHeader header = new DefaultRequestHeader();
        header.setGlobalId(UUID.fastUUID().toString());
        header.setChannelId("StandardizingDemoProcessTestCase");
        header.setTimestamp(new Date());
        header.setSvPrId("zeebe-starter-test");
        header.setTxnSerialNo(UUID.fastUUID().toString(true));
        Foo foo = new Foo();
        foo.setF1("f1");
        foo.setF2(RandomUtil.randomBigDecimal(BigDecimal.ONE, new BigDecimal("200.00").setScale(2, RoundingMode.DOWN)));
        ZeebeContext<DefaultRequestHeader, Foo> request =
                new ZeebeContextBuilder<DefaultRequestHeader, Foo>().build()
                        .setRequestHead(header)
                        .setRequestData(foo)
                ;

        ZeebeResponse<DefaultResponseHeader, Map<String, Object>> response = starterService.startProcessUntilCompletion(
                request,
                null
        );
        Console.log(JSON.toJSONString(response));

        //等待Worker线程获取到消息并处理
        Thread.sleep(20000);

    }


    @Override
    public void end() throws Exception {

    }
}
