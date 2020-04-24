package com.example.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("sample-app2")
public interface SampleApp2ServiceInterface {

	@RequestMapping(value = "/sample-app-2/{name}", method = RequestMethod.GET)
	public String getMessage(@PathVariable final String name);
	
}
