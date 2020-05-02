package com.example.demo.resource;

import static org.mockito.Mockito.times;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.demo.webflux.resource.WebFluxDemoController;
import com.example.demo.webflux.service.WebFluxService;

@WebFluxTest(controllers = WebFluxDemoController.class)
class WebFluxDemoControllerTest {
	
	@MockBean
	WebFluxService service;
	
	@Autowired
	WebTestClient webclient;
	
	@Test
	void getConfigFileTest() throws IOException {
		webclient
			.get()
			.uri("/webflux/SampleApp-2")
			.exchange()
			.expectStatus().isOk();
		Mockito.verify(service, times(1)).getConfigFile("SampleApp-2");
	}
	
	@Test
	void putConfigFileTest() throws IOException {

		String reqBody = "{\n" + 
				"   \"server\": {\"port\": 8052},\n" + 
				"   \"spring\": {\"application\": {\"name\": \"sample-app2\"}}\n" + 
				"}";
		
		webclient
			.put()
			.uri("/webflux/SampleApp-2")
			.bodyValue(reqBody)
			.exchange()
			.expectStatus().isOk();
		
		Mockito.verify(service, times(1)).putConfigFile("SampleApp-2", reqBody);
	}

}
