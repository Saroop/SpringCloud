package com.example.demo.resource;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	@RequestMapping(value = {"/getconfig", "/getconfig/{appName}"}, produces = "application/json")
	public ResponseEntity<String> getConfig(@PathVariable(required=false) final String appName) throws IOException {
		return new ResponseEntity<String>(service.getConfigData(appName), HttpStatus.OK);
	}

}
