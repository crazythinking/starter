package net.engining.datasource.autoconfigure.aop;

import net.engining.pg.support.core.context.DataSourceContextHolder;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于指定操作数据库方法对应的数据源;
 * 注意：由于Spring AOP的限制只作用于method-execute，而不能作用于method-call；
 *      当结合@Transactional使用时，如果与其在同一方法上标注，会存在顺序的问题，会被@Transactional的Aspect包住，从而造成DataSource切换失效；
 *      因此建议此注解只用在执行数据库操作方法的Caller方法上；
 *
 * @author Eric Lu
 * @date 2020-05-02 21:10
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Documented
public @interface SpecifiedDataSource {

    /**
     * DataSource对应的名称，默认default
     * @return
     */
    String value() default DataSourceContextHolder.DEFAULT_DATASOURCE;

}
