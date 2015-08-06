package com.mycompany;

import java.net.URI;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

@SpringBootApplication
@EnableAsync
public class GeoSendAnywhereApplication {

	@Bean
	public SendAnywhereService sendAnywhereService() {
		return new SendAnywhereService();
	}

	@Bean
	public GooglePlacesService googlePlacesService() {
		return new GooglePlacesService();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
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
	public void init() {
		// sendAnywhereService().transferFile("testProfile");
		// System.out.println("File transfer started");
		// googlePlacesService().getPlacesData("41.969651,-87.742525");
	}

	public static void main(String[] args) {
		SpringApplication.run(GeoSendAnywhereApplication.class, args);
	}
}
