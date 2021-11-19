package net.engining.drools.autoconfigure;


import net.engining.drools.autoconfigure.core.KieEngine;
import net.engining.drools.autoconfigure.props.GavConverter;
import net.engining.drools.autoconfigure.props.GroupedDroolsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GroupedDroolsProperties.class)
public class DroolsAutoConfiguration {

    @Bean
    public GavConverter gavConverter() {
        return new GavConverter();
    }

    @Bean
    public KieEngine kieEngine(GroupedDroolsProperties groupedDroolsProperties) {
        return new KieEngine(groupedDroolsProperties);
    }

    //@Bean
    //@ConditionalOnMissingBean(name = "kieTemplate")
    //public KieTemplate kieTemplate(DroolsProperties droolsProperties) {
    //    KieTemplate kieTemplate = new KieTemplate();
    //    kieTemplate.setPath(droolsProperties.getPath());
    //    kieTemplate.setMode(droolsProperties.getMode());
    //    String autoUpdate = droolsProperties.getAutoUpdate();
    //    if (Objects.equals(LISTENER_CLOSE, autoUpdate)) {
    //        // 关闭自动更新
    //        kieTemplate.setUpdate(999999L);
    //    } else {
    //        // 启用自动更新
    //        kieTemplate.setUpdate(droolsProperties.getInterval());
    //    }
    //    kieTemplate.setListener(droolsProperties.getListener());
    //    kieTemplate.setVerify(droolsProperties.getVerify());
    //    return kieTemplate;
    //}


}
