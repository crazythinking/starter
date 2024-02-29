package net.engining.metrics.autoconfigure.autotest.support;

import io.micrometer.core.instrument.Counter;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2021-12-22 13:36
 * @since :
 **/
public class BizMetrics {
    private static final String BIZ_MVC = "biz.mvc.";
    public static final String BIZ_MVC_CALL = BIZ_MVC + "call";

    public static final Counter.Builder REQUEST_TIMES = Counter.builder(BIZ_MVC_CALL);

    public static Counter.Builder requestStepTimes() {
        return Counter.builder(BIZ_MVC_CALL)
                .tags(
                        "type", "step"
                )
                ;
    }

    public static Counter.Builder requestTotalTimes() {
        return Counter.builder(BIZ_MVC_CALL)
                .tags(
                        "type", "cumulative"
                )
                ;
    }
}
