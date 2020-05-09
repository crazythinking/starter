package net.engining.datasource.autoconfigure.aop;

import net.engining.pg.support.core.context.DataSourceContextHolder;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 实现 @SpecifiedDataSource注解
 *
 * @author : Eric Lu
 * @version :
 * @date : 2020-05-02 21:30
 * @since :
 **/
@Aspect
public class SpecifiedDataSourceHandler {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SpecifiedDataSourceHandler.class);

    public SpecifiedDataSourceHandler(){}

    @Pointcut("@annotation(specifiedDataSource)")
    public void specifiedDataSourcePonintcut(SpecifiedDataSource specifiedDataSource){
    }

    @Before("specifiedDataSourcePonintcut(specifiedDataSource)")
    public void beforeAdvice(SpecifiedDataSource specifiedDataSource) {
        LOGGER.debug("get into the datasource aspect before-advice ");
        String datasourceKey = specifiedDataSource.value();
        //操作数据库方法前指定当前线程使用的数据库datasource
        DataSourceContextHolder.setCurrentDataSourceKey(datasourceKey);
    }

    @After("specifiedDataSourcePonintcut(specifiedDataSource)")
    public void afterAdvice(SpecifiedDataSource specifiedDataSource){
        LOGGER.debug("get into the datasource aspect after-advice ");
        //操作数据库方法后重置
        DataSourceContextHolder.removeCurrentDataSourceKey();
    }

}
