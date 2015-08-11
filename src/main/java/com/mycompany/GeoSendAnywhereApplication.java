package com.mycompany;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAsync
public class GeoSendAnywhereApplication {
	
	
	@Bean
	public DocumentMapStore documentMapStore() {
		return new DocumentMapStore();
	}
	
	
	@Bean
	public SendAnywhereService sendAnywhereService() {
		return new SendAnywhereService();
	}

	@Bean
	public GooglePlacesService googlePlacesService() {
		return new GooglePlacesService();
	}
	
	@Bean 
	public HttpComponentsClientHttpRequestFactory clientFactory() {
		HttpComponentsClientHttpRequestFactory http = new HttpComponentsClientHttpRequestFactory();
		
		http.setHttpClient(HttpClients.createMinimal());
		http.setConnectTimeout(100000);
		http.setConnectionRequestTimeout(100000);
		http.setReadTimeout(100000);
		return http;
	}

	@Bean
	public RestTemplate restTemplate() {
		//return new RestTemplate();
		return new RestTemplate(clientFactory());
	}
	
	@Bean
	public JedisConnectionFactory jedisConnectionFactory() throws Exception {
		URI redisUri = new URI(System.getenv("REDISCLOUD_URL"));
		JedisConnectionFactory jedisFactory = new JedisConnectionFactory();
		jedisFactory.setHostName(redisUri.getHost());
		jedisFactory.setPort(redisUri.getPort());
		jedisFactory.setPassword(redisUri.getUserInfo().split(":",2)[1]);
		return jedisFactory;
	}
	
	

	@Bean
	public StringRedisSerializer stringSerializer() {
		return new StringRedisSerializer();
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate() throws Exception {
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory());
		redisTemplate.setKeySerializer(stringSerializer());
		redisTemplate.setHashKeySerializer(stringSerializer());
		redisTemplate.setHashValueSerializer(stringSerializer());
		redisTemplate.setValueSerializer(stringSerializer());
		return redisTemplate;
	}
	
	@Bean
	public GeoDocService geoDocService() {
		return new GeoDocService();
	}

	@PostConstruct
	public void init() throws Exception {
		// sendAnywhereService().transferFile("testProfile");
		// System.out.println("File transfer started");
		// googlePlacesService().getPlacesData("41.969651,-87.742525");
		FileUtils.copyURLToFile(new URL("http://bangkok.grand.hyatt.com/content/dam/PropertyWebsites/grandhyatt/bangh/Media/All/Grand-Hyatt-Erawan-Bangkok-Grand-King-Bedroom-1280x427.jpg"), new File("image.jpg"));
	}

	public static void main(String[] args) {
		SpringApplication.run(GeoSendAnywhereApplication.class, args);
	}
}
