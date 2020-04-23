package com.example.demo.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping("/sample-app-1")
public class SampleAppResource {
	
	@Autowired
	RestTemplate restTemplate;
	
	@GetMapping()
	@RequestMapping("/{name}")
	@HystrixCommand(fallbackMethod="fallbackForGetMessage")
	public String getMessage(@PathVariable final String name) {
		
		ResponseEntity<String> response = restTemplate.getForEntity("http://sample-app2/sample-app-2/" + name, String.class);
		
		return response.getBody();
	}
	
	public String fallbackForGetMessage(final String name) {
		return "SampleApp2 service is down, try after sometime !!!";
	}

}
