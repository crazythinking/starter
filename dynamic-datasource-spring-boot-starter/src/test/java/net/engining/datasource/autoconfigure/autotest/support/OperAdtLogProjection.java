package net.engining.datasource.autoconfigure.autotest.support;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

/**
 * 使用“@Value("#{target.xxx}")”时该类是Open Projections，target表示对应的Entity对象，xxx表示对应的属性名称，且可以组合；
 * 注意Open Projections无法被spring-data查询优化，性能上会有所损失；
 *
 * 不使用“@Value("#{target.xxx}")”时该类是Close Projections，此时定义的方法必须与Entity对象的getter方法相同命名；
 * 注意Close Projections可以被spring-data查询优化，性能上会有所提升；另外Spring-data 2.6.0以后，Close Projections存在bug；
 * 详见：<a href="https://github.com/spring-projects/spring-data-jpa/issues/2408">NPE Projections</a>，2.6.2或2.7-M3之后修复；
 *
 * @author Eric Lu
 * @date 2021-08-24 11:27
 **/
public interface OperAdtLogProjection {

    @Value("#{target.id + '-' + target.loginId}")
    String getFullId();

    @Value("#{target.id}")
    Integer getId();

    @Value("#{target.loginId}")
    String getLoginId();

    @Value("#{target.requestUri}")
    String getRequestUri();

    @Value("#{target.operTime}")
    Date getOperTime();
}
