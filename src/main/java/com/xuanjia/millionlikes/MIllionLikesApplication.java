package com.xuanjia.millionlikes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MIllionLikesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MIllionLikesApplication.class, args);
    }

}
