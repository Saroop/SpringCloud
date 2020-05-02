package com.example.demo.webflux.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.webflux.resource.Committer;
import com.example.demo.webflux.resource.GitFileRead;
import com.example.demo.webflux.resource.GitFileWrite;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import reactor.core.publisher.Mono;

@Service
public class WebFluxService {
	
	@Value("${git.access.token}")
	private String token;
	
	@Autowired
	WebClient webClient;

	public Mono<String> getConfigFile(final String path) throws JsonMappingException, JsonProcessingException {
		Mono<GitFileRead> result = webClient.get()
				.uri("/repos/Saroop/SpringCloud/contents/config-repo/configuration/" + path + "/application.yml")
				.header("Authorization", "token " + token)
				.retrieve()
				.bodyToMono(GitFileRead.class);
		
		return result.then(convertEncodedYamlToJsonString(result));
	}

	@SuppressWarnings("rawtypes")
	public Mono<Map> putConfigFile(final String path, final String content) throws IOException {
		
		Mono<Map> getResult = webClient.get()
				.uri("/repos/Saroop/SpringCloud/contents/config-repo/configuration/" + path + "/application.yml")
				.header("Authorization", "token " + token)
				.retrieve()
				.bodyToMono(Map.class);
		
		return getResult.then(webClient.put()
				.uri("/repos/Saroop/SpringCloud/contents/config-repo/configuration/" + path + "/application.yml")
				.header("Authorization", "token " + token)
				.body(Mono.just(constructUpdateReqBody(content, getResult)), GitFileWrite.class)
				.retrieve()
				.bodyToMono(Map.class));
		
	}

	private GitFileWrite constructUpdateReqBody(final String content, Mono<Map> getResult) {
		String sha = (String)getResult.block().get("sha");
		Mono<String> encodedString =  Mono.just(Base64.getEncoder().encodeToString(content.getBytes()));
		Committer committer = new Committer("Saroop", "saroopmtr@gmail.com");
        GitFileWrite newFile = new GitFileWrite("Updated configuration", encodedString.block(), sha, committer);
		return newFile;
	}
	
	private Mono<String> convertEncodedYamlToJsonString(Mono<GitFileRead> result)
			throws JsonProcessingException, JsonMappingException {
		Base64.Decoder decoder = Base64.getMimeDecoder();
        String decodedString = new String(decoder.decode(((String)result.block().getContent()).getBytes(StandardCharsets.US_ASCII)));
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        Object obj = yamlMapper.readValue(decodedString, Object.class);
        ObjectMapper jsonMapper = new ObjectMapper();
        return Mono.just(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj));
	}
}
