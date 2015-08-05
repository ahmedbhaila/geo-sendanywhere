package com.mycompany;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAsync
public class GeoSendAnywhereApplication {

	@Bean
	public SendAnywhereService sendAnywhereService()  {
		return new SendAnywhereService();
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	
	@PostConstruct
	public void init() {
		//sendAnywhereService().transferFile("testProfile");
		//System.out.println("File transfer started");
	}
    public static void main(String[] args) {
        SpringApplication.run(GeoSendAnywhereApplication.class, args);
    }
}
