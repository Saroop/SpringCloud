package com.example.demo.webflux.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.webflux.resource.GitFile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import reactor.core.publisher.Mono;

@Service
public class WebFluxService {
	
	/*
	 * @Value("${git.access.userName}") private String userName;
	 */
	
	@Value("${git.access.token}")
	private String token;
	
	@Autowired
	WebClient webClient;

	public Mono<String> getConfigFile(final String path) throws JsonMappingException, JsonProcessingException {
		@SuppressWarnings("rawtypes")
		Mono<GitFile> result = webClient.get()
				.uri("/repos/Saroop/SpringCloud/contents/config-repo/configuration/" + path + "/application.yml")
				.header("Authorization", "token " + token)
				.retrieve()
				.bodyToMono(GitFile.class);
		
		return convertEncodedYamlToJsonString(result);
	}

	public Mono<Map> putConfigFile(final String path, final String content) throws IOException {
		System.out.println("1");
		@SuppressWarnings("rawtypes")
		Mono<Map> getResult = webClient.get()
				.uri("/repos/Saroop/SpringCloud/contents/config-repo/configuration/" + path + "/application.yml")
				.header("Authorization", "token " + token)
				.retrieve()
				.bodyToMono(Map.class);
		System.out.println("2");
		String sha = (String)getResult.block().get("sha");
		System.out.println("3");
		Mono<String> encodedString =  convertJsonToEncodedYaml(content);
        GitFile newFile = new GitFile("Updated configuration", encodedString.block(), sha);
        
        return webClient.put()
				.uri("/repos/Saroop/SpringCloud/contents/config-repo/configuration/" + path + "/application.yml")
				.header("Authorization", "token " + token)
				.body(Mono.just(newFile), GitFile.class)
				.retrieve()
				.bodyToMono(Map.class);
        
	}
	
	private Mono<String> convertEncodedYamlToJsonString(Mono<GitFile> result)
			throws JsonProcessingException, JsonMappingException {
		Base64.Decoder decoder = Base64.getMimeDecoder();
        String decodedString = new String(decoder.decode(((String)result.block().getContent()).getBytes(StandardCharsets.US_ASCII)));
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        Object obj = yamlMapper.readValue(decodedString, Object.class);
        ObjectMapper jsonMapper = new ObjectMapper();
        return Mono.just(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj));
	}
	
	private Mono<String> convertJsonToEncodedYaml(String content)
			throws IOException {
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        File tempFile = File.createTempFile("application", ".tmp");
        yamlMapper.writeValue(tempFile, content);
        byte[] byteArray = Base64.getMimeEncoder().encode(Files.readAllBytes(FileSystems.getDefault().getPath(tempFile.getPath())));
        return Mono.just(new String(byteArray, StandardCharsets.US_ASCII));
	}
}
