package com.example.demo.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sample-app-2")
public class SampleAppResource2 {
	
	@GetMapping
	@RequestMapping("{name}")
	public String getMessage(@PathVariable final String name) {
		return "Hi " + name;
	}

}
