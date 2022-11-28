package net.engining.groovy.autoconfigure.props;

import com.google.common.collect.Sets;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

/**
 * @author : Eric Lu
 * @version :
 * @date : 2022-11-24 16:26
 * @since :
 **/
@ConfigurationProperties("pg.groovy")
public class GroovyProperties {

    boolean enabled = true;

    Set<String> basePackages = Sets.newHashSet();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<String> getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(Set<String> basePackages) {
        this.basePackages = basePackages;
    }
}
