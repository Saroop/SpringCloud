package com.example.demo.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ConfigManagementService {

	public String getConfigData(String appName) throws IOException {
		
		if (appName != null) {
			appName = appName + "/";
		} else {
			appName = "";
		}
		
		URL url = new URL("https://raw.githubusercontent.com/Saroop/SpringCloud/master/config-repo/configuration/" + appName + "application.yml");
		
		InputStream is = url.openConnection().getInputStream();
		Object configData = new Yaml().loadAs(is, Object.class);
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(configData);
	}
	
}
