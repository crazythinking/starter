package net.engining.datasource.autoconfigure.aop;

import cn.hutool.core.util.ReflectUtil;
import com.google.common.collect.Maps;
import net.engining.pg.support.core.context.OrganizationContextHolder;
import net.engining.pg.support.utils.ValidateUtilExt;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import javax.persistence.EntityManager;
import java.util.Map;


/**
 * 实现 @SwitchOrg4HibernateFilter注解
 *
 * @author luxue
 */
@Aspect
@Order
public class SwitchOrg4HibernateFilterHandler {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(SwitchOrg4HibernateFilterHandler.class);

    private Map<String, EntityManager> entityManagerCache = Maps.newHashMap();

    public SwitchOrg4HibernateFilterHandler() {
    }

    @Pointcut("@annotation(switchOrg4HibernateFilter)")
    public void switchOrg4HibernateFilterPointcut(SwitchOrg4HibernateFilter switchOrg4HibernateFilter){
    }

    @Before("switchOrg4HibernateFilterPointcut(switchOrg4HibernateFilter)")
    public void beforeAdvice(JoinPoint joinPoint, SwitchOrg4HibernateFilter switchOrg4HibernateFilter) {
        LOGGER.debug("get into the SwitchOrg4HibernateFilter aspect before-advice ");
        EntityManager emFieldValue = getEntityManagerFieldValue(joinPoint, switchOrg4HibernateFilter);

        if (ValidateUtilExt.isNotNullOrEmpty(emFieldValue)){
            OrganizationContextHolder.enableOrgFilter(emFieldValue);
        }
        else {
            LOGGER.warn("could not found EntityManager for switching org");
        }

    }

    /**
     * 获取EntityManager；可以从缓存获取，以提高性能；
     *
     * @param joinPoint                     链接点
     * @param switchOrg4HibernateFilter     SwitchOrg4HibernateFilter注解
     * @return                              EntityManager
     */
    private EntityManager getEntityManagerFieldValue(JoinPoint joinPoint, SwitchOrg4HibernateFilter switchOrg4HibernateFilter) {
        String entityManagerFieldName = switchOrg4HibernateFilter.value();
        Object serviceObj = joinPoint.getTarget();
        //从缓存取，提高性能
        Object emFieldValue;
        if (entityManagerCache.containsKey(serviceObj.toString())){
            emFieldValue = entityManagerCache.get(serviceObj.toString());
        }
        else {
            emFieldValue = ReflectUtil.getFieldValue(serviceObj, entityManagerFieldName);
            if (ValidateUtilExt.isNotNullOrEmpty(emFieldValue)){
                if (emFieldValue instanceof EntityManager){
                    entityManagerCache.put(serviceObj.toString(), (EntityManager) emFieldValue);
                }
                else {
                    LOGGER.warn("could not found EntityManager for switched org");
                }
            }
            else {
                LOGGER.warn("could not found EntityManager for switched org");
            }


        }
        return (EntityManager) emFieldValue;
    }

    @After("switchOrg4HibernateFilterPointcut(switchOrg4HibernateFilter)")
    public void afterAdvice(JoinPoint joinPoint, SwitchOrg4HibernateFilter switchOrg4HibernateFilter){
        LOGGER.debug("get into the SwitchOrg4HibernateFilter aspect after-advice ");
        EntityManager emFieldValue = getEntityManagerFieldValue(joinPoint, switchOrg4HibernateFilter);
        if (ValidateUtilExt.isNotNullOrEmpty(emFieldValue)){
            OrganizationContextHolder.disableOrgFilter(emFieldValue);
        }
        else {
            LOGGER.warn("could not found EntityManager for switched org");
        }
    }
}
