package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

@SpringBootApplication
public class WebFluxSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebFluxSampleApplication.class, args);
	}

}
