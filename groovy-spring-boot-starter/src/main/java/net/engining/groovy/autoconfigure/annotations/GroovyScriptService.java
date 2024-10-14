package net.engining.groovy.autoconfigure.annotations;

import org.springframework.core.io.ProtocolResolver;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * groovy 动态语言component
 *
 * @author Eric Lu
 */
@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GroovyScriptService {

    /**
     * 脚本资源路径； <br>
     * 支持spring官方协议(eg. classpath:xxx.groovy, file:xxx.groovy) <br>
     * 支持自定义协议(eg. myprotocol:xxx.groovy), 需要实现对应协议的{@link ProtocolResolver} <br>
     * 支持基于占位符“${...}”的外部化配置 <br>
     * 可通过“,”分割，表示多个脚本资源，通常用于接口的多个实现类
     */
    String scriptSources() default "";

    /**
     * 更新检查延时毫秒数, 默认60秒检查一次
     */
    String refreshCheckDelay() default "60000L";


}
