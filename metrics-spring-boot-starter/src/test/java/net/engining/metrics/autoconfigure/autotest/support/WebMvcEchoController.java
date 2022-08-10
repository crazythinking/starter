package net.engining.metrics.autoconfigure.autotest.support;

import net.engining.pg.web.CommonWithHeaderResponseBuilder;
import net.engining.pg.web.bean.BaseResponseBean;
import net.engining.pg.web.bean.CommonWithHeaderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebMvcEchoController {

    /** logger */
    private static final Logger log = LoggerFactory.getLogger(WebMvcEchoController.class);

    @GetMapping(value = "/mvcecho/{string}")
    public @ResponseBody
    CommonWithHeaderResponse<Void, Void> echo(@PathVariable String string) {
        AdditionalOb obj = new AdditionalOb();
        obj.setAa("hello Nacos Discovery " + string);

        return new CommonWithHeaderResponseBuilder<Void, Void>()
                .build()
                .putAdditionalRepMap("say", obj);
    }

    @GetMapping(value = "/mvcecho1/{string}")
    public @ResponseBody AdditionalOb echo1(@PathVariable String string) {
        AdditionalOb obj = new AdditionalOb();
        obj.setAa("hello Nacos Discovery " + string);

        return obj;
    }

    @PostMapping(value = "/mvcecho3")
    public @ResponseBody CommonWithHeaderResponse<Void, BaseResponseBean> echo3(@RequestBody AdditionalOb ob) {

        log.debug(ob.getAa());

        BaseResponseBean baseResponseBean2 = new BaseResponseBean();
        baseResponseBean2.setReturnCode("000");
        baseResponseBean2.setReturnDesc(null);

        return new CommonWithHeaderResponseBuilder<Void, BaseResponseBean>().build().setResponseData(baseResponseBean2);
    }

    @PostMapping(value = "/mvcecho4")
    public @ResponseBody CommonWithHeaderResponse<Void, Void> echo4(@RequestHeader HttpHeaders httpHeaders) {
        String token = httpHeaders.getFirst("Authorization");

        log.debug("token: {}", token);
        return new CommonWithHeaderResponseBuilder<Void, Void>().build();
    }

    @PostMapping(value = "/mvcecho5")
    public @ResponseBody CommonWithHeaderResponse<Void, BaseResponseBean> echo5(@RequestBody AdditionalOb ob) throws Exception {

        log.debug(ob.getAa());

        BaseResponseBean baseResponseBean2 = new BaseResponseBean();
        throw new Exception("exception test");

    }

}
