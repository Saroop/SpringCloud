package com.example.demo.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

@Service
public class ConfigManagementService {

	@SuppressWarnings("unchecked")
	public Map<String, ?> getConfigData(String appName) throws IOException {
		
		if (appName != null) {
			appName = appName + "/";
		} else {
			appName = "";
		}
		
		URL url = new URL("https://raw.githubusercontent.com/Saroop/SpringCloud/master/config-repo/configuration/" + appName + "application.yml");
		
		InputStream is = url.openConnection().getInputStream();
		DumperOptions options = new DumperOptions();
	      options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
	      options.setPrettyFlow(true);
        return new Yaml(options).loadAs(is, Map.class);
	}
	
}
