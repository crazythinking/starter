package net.engining.zeebe.spring.client.ext.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 扩展ZeebeWorker
 *
 * @author Eric Lu
 * @date 2021-04-20 14:48
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZeebeWorker {

    String type() default "";

    String name() default "";

    long timeout() default -1L;

    int maxJobsActive() default -1;

    long requestTimeout() default -1L;

    long pollInterval() default -1L;

    String[] fetchVariables() default {};

}
