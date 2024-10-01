package com.example.spring_elastic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableMongoRepositories(basePackages = "com.example.spring_elastic.repository")
public class SpringElasticApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringElasticApplication.class, args);
    }
}
