package net.engining.datasource.autoconfigure.autotest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
    	SpringApplication.run(AutoConfigureTestApplication.class, args);

    }
}
