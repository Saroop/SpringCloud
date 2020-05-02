package com.example.demo.webflux.resource;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.webflux.service.WebFluxService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/webflux")
public class WebFluxDemoController {
	
	@Autowired
	WebFluxService service;

	@RequestMapping(value = "/{path}", method = RequestMethod.GET, produces="application/json")
	public Mono<String> getConfigFile(@PathVariable final String path) throws JsonMappingException, JsonProcessingException {
		return service.getConfigFile(path);
	}
	
	@PutMapping()
	@RequestMapping(value = "/{path}", method = RequestMethod.PUT, produces="application/json")
	public Mono<Map> putConfigFile(@PathVariable final String path, @RequestBody final String fileContent) 
		throws IOException {
		return service.putConfigFile(path, fileContent);
	}
}
