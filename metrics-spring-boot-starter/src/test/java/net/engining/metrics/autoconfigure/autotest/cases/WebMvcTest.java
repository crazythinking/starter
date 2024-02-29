package net.engining.metrics.autoconfigure.autotest.cases;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.search.MeterNotFoundException;
import io.micrometer.core.instrument.search.Search;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import net.engining.metrics.MetricsUtils;
import net.engining.metrics.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.metrics.autoconfigure.autotest.support.AdditionalOb;
import net.engining.metrics.autoconfigure.autotest.support.BizMetrics;
import net.engining.metrics.support.StoredPushMeterRegistry;
import net.engining.metrics.support.StoredStepPushMeterRegistry;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@ActiveProfiles(profiles={
        "metrics.common"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class WebMvcTest extends AbstractTestCaseTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebMvcTest.class);

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private List<MeterRegistry> meterRegistries;

    @Autowired
    private CompositeMeterRegistry compositeMeterRegistry;

    @Autowired
    private StoredPushMeterRegistry storedPushMeterRegistry;

    @Autowired
    private StoredStepPushMeterRegistry storedStepPushMeterRegistry;

    private MeterRegistry meterRegistry;

    private MockMvc mvc;

    private Counter gbCounter1;
    private Counter gbCounter2;
    private Counter counter1;
    private Counter counter2;
    private Counter counter3;
    private Counter counter4;

    @Override
    public void initTestData() throws Exception {
        // 构造MockMvc
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        this.meterRegistry = MetricsUtils.determinedMeterRegistry((ConfigurableApplicationContext) context);

        // 注册全局的MeterRegistry, 所有的MeterRegistry都会被注册
        gbCounter1 = BizMetrics.REQUEST_TIMES.tag("uri", "/mvcecho/111").register(meterRegistry);
        gbCounter2 = BizMetrics.REQUEST_TIMES.tag("uri", "/mvcecho3").register(meterRegistry);

        // 注册自定义指标，如果只使用Prometheus作为指标存储层，则可以不注册
        //regist();
    }

    private void regist() {
        Counter.Builder builder1;
        Counter.Builder builder2;
        Counter.Builder builder3;
        Counter.Builder builder4;
        builder1 = BizMetrics.requestStepTimes("/mvcecho/111");
        builder2 = BizMetrics.requestStepTimes("/mvcecho3");
        builder3 = BizMetrics.requestTotalTimes("/mvcecho/111");
        builder4 = BizMetrics.requestTotalTimes("/mvcecho3");
        // 注册按步长计算的计数器
        counter1 = builder1.register(storedStepPushMeterRegistry);
        counter2 = builder2.register(storedStepPushMeterRegistry);
        // 注册累积计算的计数器
        counter3 = builder3.register(storedPushMeterRegistry);
        counter4 = builder4.register(storedPushMeterRegistry);
    }

    @Override
    public void assertResult() throws Exception {
        if (Metrics.globalRegistry.getRegistries().equals(compositeMeterRegistry.getRegistries())){
            LOGGER.warn("CompositeMeterRegistry is same");
        }

        meterRegistries.forEach(meterRegistry -> {
            LOGGER.warn(
                    "the meter registry[{}] number of total meters: {} ",
                    meterRegistry.getClass().getName(),
                    Search.in(meterRegistry).meters().size());
            if (meterRegistry instanceof StoredPushMeterRegistry){
                Assertions.assertThatThrownBy(() -> meterRegistry.get(BizMetrics.BIZ_MVC_CALL).tagKeys("uri").meter())
                        .isInstanceOf(MeterNotFoundException.class);
            }
            if (meterRegistry instanceof StoredStepPushMeterRegistry){
                Assert.isTrue(
                        meterRegistry.get(BizMetrics.BIZ_MVC_CALL).tagKeys("uri").meters().size() == 2,
                        "meter size must be 2");
            }
            if (meterRegistry instanceof PrometheusMeterRegistry) {
                Assert.isTrue(
                        meterRegistry.get(BizMetrics.BIZ_MVC_CALL).tagKeys("uri").meters().size() == 2,
                        "meter size must be 2");
            }
        });

    }

    @Override
    public void testProcess() throws Exception {

        for (int i = 0; i < 20; i++) {
            AdditionalOb ob = call1();
            call2(ob);
            Thread.sleep(1000L);
        }
    }

    private void call2(AdditionalOb ob) throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .post("/mvcecho3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(ob))
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
        ;
        if (ValidateUtilExt.isNotNullOrEmpty(counter2)){
            counter2.increment();
        }
        if (ValidateUtilExt.isNotNullOrEmpty(counter4)){
            counter4.increment();
        }
        //gbCounter2.increment();
    }

    private AdditionalOb call1() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .get("/mvcecho/111")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk());

        String json = "{\"aa\":\"bbb\"}";
        AdditionalOb ob = mapper.readValue(json, AdditionalOb.class);

        if (ValidateUtilExt.isNotNullOrEmpty(counter1)){
            counter1.increment();
        }
        if (ValidateUtilExt.isNotNullOrEmpty(counter3)){
            counter3.increment();
        }
        //gbCounter1.increment();

        return ob;
    }

    @Override
    public void end() throws Exception {

    }

}
