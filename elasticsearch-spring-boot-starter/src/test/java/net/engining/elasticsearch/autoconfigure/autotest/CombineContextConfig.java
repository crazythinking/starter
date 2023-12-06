package net.engining.elasticsearch.autoconfigure.autotest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 通用Context配置
 *
 * @author Eric Lu
 */
@Configuration
@EnableElasticsearchRepositories({
        "net.engining.elasticsearch.autoconfigure.autotest.support"
})
public class CombineContextConfig {

    @Bean
    public String indexDateSuffix(){
        //根据日期生成index
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }

}
