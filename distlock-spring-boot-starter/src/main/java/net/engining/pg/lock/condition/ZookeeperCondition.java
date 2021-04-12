package net.engining.pg.lock.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * zookeeper注入条件判断类
 * @author Eric Lu
 */
public class ZookeeperCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        final Environment environment = conditionContext.getEnvironment();
        return environment.containsProperty("pg.distlock.zookeeper.zkServers")
                || environment.containsProperty("pg.distlock.zookeeper.zk-servers");
    }
}
