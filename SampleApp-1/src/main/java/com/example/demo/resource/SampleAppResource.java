package com.example.demo.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.feign.SampleApp2ServiceInterface;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping("/sample-app-1")
public class SampleAppResource {
	
	@Autowired
	SampleApp2ServiceInterface app2Service;
	
	/*
	 * @Autowired RestTemplate restTemplate;
	 */
	
	@GetMapping()
	@RequestMapping("/{name}")
	@HystrixCommand(fallbackMethod="fallbackForGetMessage")
	public String getMessage(@PathVariable final String name) {
		
		return app2Service.getMessage(name);
		
	}
	
	public String fallbackForGetMessage(final String name) {
		return "SampleApp2 service is down, try after sometime !!!";
	}

}
