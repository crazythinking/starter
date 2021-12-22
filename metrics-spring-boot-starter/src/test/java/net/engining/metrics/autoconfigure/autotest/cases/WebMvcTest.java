package net.engining.metrics.autoconfigure.autotest.cases;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.search.Search;
import net.engining.metrics.autoconfigure.autotest.support.AbstractTestCaseTemplate;
import net.engining.metrics.autoconfigure.autotest.support.AdditionalOb;
import net.engining.metrics.support.StoredPushMeterRegistry;
import net.engining.metrics.support.StoredStepMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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
    private List<MeterRegistry> meterRegistrys;

    @Autowired
    private CompositeMeterRegistry compositeMeterRegistry;

    @Autowired
    private StoredPushMeterRegistry storedPushMeterRegistry;

    @Autowired
    private StoredStepMeterRegistry storedStepMeterRegistry;

    private MockMvc mvc;

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

        counter1 = Counter.builder("biz.mvc.call1")
                .tags(
                        "uri", "/mvcecho/111",
                        "type", "step"
                )
                .register(storedStepMeterRegistry)
                //.register(compositeMeterRegistry)
        ;

        counter2 = Counter.builder("biz.mvc.call2")
                .tags(
                        "uri", "mvcecho3",
                        "type", "step"
                )
                .register(storedStepMeterRegistry)
                //.register(compositeMeterRegistry)
        ;

        counter3 = Counter.builder("biz.mvc.total.call1")
                .tags(
                        "uri", "/mvcecho/111",
                        "type", "cumulative"
                )
                .register(storedPushMeterRegistry)
                //.register(compositeMeterRegistry)
        ;

        counter4 = Counter.builder("biz.mvc.total.call2")
                .tags(
                        "uri", "mvcecho3",
                        "type", "cumulative"
                )
                .register(storedPushMeterRegistry)
                //.register(compositeMeterRegistry)
        ;

    }

    @Override
    public void assertResult() throws Exception {
        if (Metrics.globalRegistry.getRegistries().equals(compositeMeterRegistry.getRegistries())){
            LOGGER.warn("CompositeMeterRegistry is same");
        }

        meterRegistrys.forEach(meterRegistry -> {
            //Search.in(meterRegistry)
            // .meters().forEach(each -> {
            //    StringBuilder builder = new StringBuilder();
            //    builder.append("name:")
            //            .append(each.getId().getName())
            //            .append(",tags:")
            //            .append(each.getId().getTags())
            //            .append(",type:").append(each.getId().getType())
            //            .append(",value:").append(each.measure());
            //})
            //;
            LOGGER.warn(
                    "the meter registry[{}] number of total meters: {} ",
                    meterRegistry.getClass().getName(),
                    Search.in(meterRegistry).meters().size());
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
        counter2.increment();
        counter4.increment();
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

        counter1.increment();
        counter3.increment();

        return ob;
    }

    @Override
    public void end() throws Exception {

    }

}
