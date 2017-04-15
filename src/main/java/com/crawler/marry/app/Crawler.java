package com.crawler.marry.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by finup on 2017/2/17.
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan (value = "com.crawler.marry")

public class Crawler {

    public static void main(String[] args) {
        SpringApplication.run(Crawler.class,args);

    }
}
