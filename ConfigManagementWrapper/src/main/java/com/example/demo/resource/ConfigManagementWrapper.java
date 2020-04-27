package com.example.demo.resource;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
public class ConfigManagementWrapper {
	
	@Autowired
	ConfigManagementService service;
	
	@GetMapping
	@RequestMapping(value = {"/getconfig", "/getconfig/{appName}"})
	public Map<String, ?> getConfig(@PathVariable(required=false) final String appName) throws IOException {
		return service.getConfigData(appName);
	}

}
