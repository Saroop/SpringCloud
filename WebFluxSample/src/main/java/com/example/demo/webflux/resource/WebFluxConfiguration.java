package com.example.demo.webflux.resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebFluxConfiguration {
	
	@Bean
	public WebClient getWebClient() {
		return WebClient.builder()
		        .baseUrl("https://api.github.com")
		        .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
		        .defaultHeader(HttpHeaders.USER_AGENT, "Spring 5 WebClient")
		        .build();
	}
	
}
