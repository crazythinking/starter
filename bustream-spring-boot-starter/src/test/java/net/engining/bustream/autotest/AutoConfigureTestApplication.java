package net.engining.bustream.autotest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * @author luxue
 *
 */
@SpringBootApplication
public class AutoConfigureTestApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(AutoConfigureTestApplication.class);
        //springApplication.setAdditionalProfiles();
        springApplication.run(args);

    }
}
