package com.example.demo.webflux.resource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.webflux.service.WebFluxService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/webflux")
public class WebFluxDemoController2 {
	
	@Autowired
	WebFluxService service;

	@RequestMapping(value = "/{path}", method = RequestMethod.GET, produces="application/json")
	public Mono<String> getConfigFile(@PathVariable final String path) throws IOException, NoHeadException, GitAPIException, URISyntaxException {
		return service.getConfigFile(path);
	}
	
	@PutMapping()
	@RequestMapping(value = "/{path}", method = RequestMethod.PUT, produces="application/json")
	public Mono<Map> putConfigFile(@PathVariable final String path, @RequestBody final String fileContent) 
		throws IOException {
		return service.putConfigFile(path, fileContent);
	}
	
	@RequestMapping(value = "/jgit/{path}", method = RequestMethod.GET, produces="application/json")
	public Mono<String> getConfigFileUsingJGit(@PathVariable final String path) throws IOException, NoHeadException, GitAPIException, URISyntaxException {
		return service.getConfigFileUsingJGit(path);
	}
	
	@PutMapping()
	@RequestMapping(value = "/jgit/{path}", method = RequestMethod.PUT, produces="application/json")
	public Mono<String> putConfigFileUsingJGit(@PathVariable final String path, @RequestBody final String fileContent) 
		throws IOException, NoHeadException, GitAPIException, URISyntaxException {
		return service.putConfigFileUsingJGit(path, fileContent);
	}
}
