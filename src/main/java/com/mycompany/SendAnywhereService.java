package com.mycompany;

import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class SendAnywhereService {
	
	private static final String GET_DEVICE_KEY_URL = "https://send-anywhere.com/web/v1/device?api_key={api_key}&profile_name={profile_name}";
	private static final String GET_FILE_TRANSFER_KEY = "https://send-anywhere.com/web/v1/key";
	
	@Value("${sendanywhere.api.key}")
	String apiKey;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Async
	public Future<String> transferFile(String profileName) {
		HttpEntity<String> response = restTemplate.exchange(GET_DEVICE_KEY_URL, HttpMethod.GET, null, String.class, apiKey, profileName);
		
		List<String> cookies = response.getHeaders().get("Set-Cookie");
		String deviceIdCookie = cookies.get(1).split(";")[0];
		String deviceId = (String)deviceIdCookie.subSequence(deviceIdCookie.indexOf("=") + 1, deviceIdCookie.length());
		
		// get key
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", "device_key=" + deviceId);
		
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		
		HttpEntity<SendAnywhereKey> key = restTemplate.exchange(GET_FILE_TRANSFER_KEY, HttpMethod.GET, entity, SendAnywhereKey.class);
		System.out.println("Key is " + key.getBody().getWebLink());
		
		
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", new FileSystemResource("/Users/1334517/data.txt"));
		// Post
		String result = restTemplate.postForObject(key.getBody().getWebLink(), parts, String.class);
		
		System.out.println("Result is " + result);
		return new AsyncResult<String>(result);
		
	}
}
