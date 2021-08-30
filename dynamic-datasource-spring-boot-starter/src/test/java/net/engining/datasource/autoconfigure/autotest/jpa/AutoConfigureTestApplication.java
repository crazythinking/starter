package net.engining.datasource.autoconfigure.autotest.jpa;

import cn.hutool.core.util.ArrayUtil;
import com.google.common.collect.Lists;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

/**
 *
 * @author luxue
 *
 */
@SpringBootApplication(
        excludeName = {
                "org.apache.shardingsphere.shardingjdbc.spring.boot.SpringBootConfiguration"
        })
public class AutoConfigureTestApplication {

    public static void main(String[] args) {
    	SpringApplication
                .run(AutoConfigureTestApplication.class, args);

    }

}
