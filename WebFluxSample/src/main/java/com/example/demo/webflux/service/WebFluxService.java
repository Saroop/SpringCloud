package com.example.demo.webflux.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.webflux.resource.Committer;
import com.example.demo.webflux.resource.GitFileRead;
import com.example.demo.webflux.resource.GitFileWrite;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import reactor.core.publisher.Mono;

@Service
public class WebFluxService {
	
	@Value("${git.access.token}")
	private String token;
	
	@Autowired
	WebClient webClient;
	
	@Autowired
	Repository repository;

	public Mono<String> getConfigFile(final String path) throws IOException, NoHeadException, GitAPIException {
		Git git = new Git(repository);
		Iterable<RevCommit> logs = git.log().all().call();
	      for (RevCommit rev : logs) {
	        System.out.print(Instant.ofEpochSecond(rev.getCommitTime()));
	        System.out.print(": ");
	        System.out.print(rev.getFullMessage());
	        System.out.println();
	        System.out.println(rev.getId().getName());
	        System.out.print(rev.getAuthorIdent().getName());
	        System.out.println(rev.getAuthorIdent().getEmailAddress());
	        System.out.println("-------------------------");
	      }
	      
	      File newFile = new File(repository.getDirectory(), "myNewFile");
	      newFile.createNewFile();
	      git.add().addFilepattern("myNewFile").call();

	      // Now, we do the commit with a message
	      RevCommit rev = git.commit().setAuthor("gildas", "gildas@example.com").setMessage("My first commit").call();
		
		return Mono.just("getConfigFile");
	}
	
	/*public Mono<String> getConfigFile(final String path) throws JsonMappingException, JsonProcessingException {
		Mono<GitFileRead> result = webClient.get()
				.uri("/repos/Saroop/SpringCloud/contents/config-repo/configuration/" + path + "/application.yml")
				.header("Authorization", "token " + token)
				.retrieve()
				.bodyToMono(GitFileRead.class);
		
		return result.then(convertEncodedYamlToJsonString(result));
	}*/

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

	private GitFileWrite constructUpdateReqBody(final String content, Mono<Map> getResult) throws IOException {
		String sha = (String)getResult.block().get("sha");
		JsonNode jsonNodeTree = new ObjectMapper().readTree(content);
        String jsonAsYaml = new YAMLMapper().writeValueAsString(jsonNodeTree);
		Mono<String> encodedString =  Mono.just(Base64.getEncoder().encodeToString(jsonAsYaml.getBytes()));
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
