package com.example.demo.webflux.service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

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
				.header("Authorization", "token " + token).retrieve().bodyToMono(GitFileRead.class);

		return result.then(convertEncodedYamlToJsonString(result));
	}
	 

	@SuppressWarnings("rawtypes")
	public Mono<Map> putConfigFile(final String path, final String content) throws IOException {

		Mono<Map> getResult = webClient.get()
				.uri("/repos/Saroop/SpringCloud/contents/config-repo/configuration/" + path + "/application.yml")
				.header("Authorization", "token " + token).retrieve().bodyToMono(Map.class);

		return getResult.then(webClient.put()
				.uri("/repos/Saroop/SpringCloud/contents/config-repo/configuration/" + path + "/application.yml")
				.header("Authorization", "token " + token)
				.body(Mono.just(constructUpdateReqBody(content, getResult)), GitFileWrite.class).retrieve()
				.bodyToMono(Map.class));

	}
	
	public Mono<String> getConfigFileUsingJGit(final String path)
			throws IOException, NoHeadException, GitAPIException, URISyntaxException {
		
		Git git = Git.cloneRepository()
				  .setURI("https://github.com/Saroop/SpringCloud.git")
				  .setDirectory(Files.createTempDirectory("SpringCloud").toFile())
				  .call();
		
		//read file
		String data = readFromFile(git, path);
	    
	    deleteTempDirectory(git);
	    
	    return convertYamlToJson(data);
		
	}

	public Mono<String> putConfigFileUsingJGit(final String path, final String content)
			throws IOException, NoHeadException, GitAPIException, URISyntaxException {
		
		Git git = Git.cloneRepository()
				  .setURI("https://github.com/Saroop/SpringCloud.git")
				  .setDirectory(Files.createTempDirectory("SpringCloud").toFile())
				  .call();
		
		writeToFile(content, path, git);

		// git add, commit and push
		git.add().addFilepattern(".").call();
		git.commit().setCommitter("saroop", "saroopmtr@gmail.com").setMessage("added a test file for jgit integration").call();
		git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, "")).call();

		deleteTempDirectory(git);
		
		return Mono.just("successfully pushed the changes to git");
	}

	private GitFileWrite constructUpdateReqBody(final String content, Mono<Map> getResult) throws IOException {
		String sha = (String) getResult.block().get("sha");
		JsonNode jsonNodeTree = new ObjectMapper().readTree(content);
		String jsonAsYaml = new YAMLMapper().writeValueAsString(jsonNodeTree);
		Mono<String> encodedString = Mono.just(Base64.getEncoder().encodeToString(jsonAsYaml.getBytes()));
		Committer committer = new Committer("Saroop", "saroopmtr@gmail.com");
		GitFileWrite newFile = new GitFileWrite("Updated configuration", encodedString.block(), sha, committer);
		return newFile;
	}

	private Mono<String> convertEncodedYamlToJsonString(Mono<GitFileRead> result)
			throws JsonProcessingException, JsonMappingException {
		Base64.Decoder decoder = Base64.getMimeDecoder();
		String decodedString = new String(
				decoder.decode(((String) result.block().getContent()).getBytes(StandardCharsets.US_ASCII)));
		return convertYamlToJson(decodedString);
	}
	
	private Mono<String> convertYamlToJson(String data) throws JsonProcessingException, JsonMappingException {
		ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
		Object obj = yamlMapper.readValue(data, Object.class);
		ObjectMapper jsonMapper = new ObjectMapper();
		return Mono.just(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj));
	}
	
	private String readFromFile(Git git, final String path) throws IOException {
		Stream<String> lines = Files.lines(Paths.get(git.getRepository().getDirectory().getParent()
				+ "\\config-repo\\configuration\\" + path + "\\application.yml"));
	    String data = lines.collect(Collectors.joining("\n"));
	    lines.close();
		return data;
	}
	
	private void writeToFile(final String content, final String path, Git git)
			throws JsonProcessingException, JsonMappingException, IOException {
		JsonNode originalJsonNodeTree = new ObjectMapper().readTree(convertYamlToJson(readFromFile(git, path)).block());
		JsonNode updateJsonNodeTree = new ObjectMapper().readTree(content);
		String jsonAsYaml = new YAMLMapper().writeValueAsString(merge(originalJsonNodeTree, updateJsonNodeTree));
		Files.write(Paths.get(git.getRepository().getDirectory().getParent()
				+ "\\config-repo\\configuration\\" + path + "\\application.yml"), jsonAsYaml.getBytes());
	}

	private void deleteTempDirectory(Git git) throws IOException {
		Files.walk(Paths.get(git.getRepository().getDirectory().getParent()))
	      .sorted(Comparator.reverseOrder())
	      .map(Path::toFile)
	      .forEach(File::delete);
	}
	
	private JsonNode merge(JsonNode mainNode, JsonNode updateNode) {

	    Iterator<String> fieldNames = updateNode.fieldNames();
	    while (fieldNames.hasNext()) {
	        String updatedFieldName = fieldNames.next();
	        JsonNode valueToBeUpdated = mainNode.get(updatedFieldName);
	        JsonNode updatedValue = updateNode.get(updatedFieldName);
	        // If the node is an @ArrayNode
	        if (valueToBeUpdated != null && valueToBeUpdated.isArray() && 
	            updatedValue.isArray()) {
	            // running a loop for all elements of the updated ArrayNode
	            for (int i = 0; i < updatedValue.size(); i++) {
	                JsonNode updatedChildNode = updatedValue.get(i);
	                // Create a new Node in the node that should be updated, if there was no corresponding node in it
	                // Use-case - where the updateNode will have a new element in its Array
	                if (valueToBeUpdated.size() <= i) {
	                    ((ArrayNode) valueToBeUpdated).add(updatedChildNode);
	                }
	                // getting reference for the node to be updated
	                JsonNode childNodeToBeUpdated = valueToBeUpdated.get(i);
	                merge(childNodeToBeUpdated, updatedChildNode);
	            }
	        // if the Node is an @ObjectNode
	        } else if (valueToBeUpdated != null && valueToBeUpdated.isObject()) {
	            merge(valueToBeUpdated, updatedValue);
	        } else {
	            if (mainNode instanceof ObjectNode) {
	                ((ObjectNode) mainNode).replace(updatedFieldName, updatedValue);
	            }
	        }
	    }
	    return mainNode;
	}
}
