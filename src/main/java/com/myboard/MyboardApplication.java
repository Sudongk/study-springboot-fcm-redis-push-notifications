package com.myboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


//@EnableConfigurationProperties(RsaKey.class)
@EnableJpaAuditing
@EnableAspectJAutoProxy
@SpringBootApplication
public class MyboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyboardApplication.class, args);
    }

}
