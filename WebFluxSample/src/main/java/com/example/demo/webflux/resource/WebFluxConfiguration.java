package com.example.demo.webflux.resource;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
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
		        .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.github.v3+json")
		        .defaultHeader(HttpHeaders.USER_AGENT, "Spring 5 WebClient")
		        .build();
	}
	
	@Bean
	public Repository getRepo() throws IOException {
		return FileRepositoryBuilder.create(new File("C:\\Users\\M1026319\\git\\SpringCloud\\.git"));
	}
	
}	
