package net.engining.groovy.autoconfigure.support;


import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用动态语言
 *
 * @author caiqy
 */
@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({
        GroovyScriptRegister.class
})
public @interface EnableGroovyScript {

    /**
     * Alias for the {@link #basePackages()} attribute. Allows for more concise annotation
     * declarations e.g.: {@code @ComponentScan("com.caiqy")} instead of
     * {@code @ComponentScan(basePackages="com.caiqy")}.
     *
     * @return the array of 'basePackages'.
     */
    String[] value() default {};

    /**
     * Base packages to scan for annotated components.
     * <p>
     * {@link #value()} is an alias for (and mutually exclusive with) this attribute.
     * <p>
     *
     * @return the array of 'basePackages'.
     */
    String[] basePackages() default {};
}
