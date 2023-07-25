package com.feed.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class FeedEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeedEngineApplication.class, args);
	}

}
