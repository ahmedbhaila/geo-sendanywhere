package com.mycompany;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
	private static final String GET_FILE_RECV_KEY = "https://send-anywhere.com/web/v1/{key}";
	
	@Value("${sendanywhere.api.key}")
	String apiKey;
	
	@Autowired
	RestTemplate restTemplate;
	
	
	public static Map<String, List<String>> fileTransferMap = new HashMap<String, List<String>>();

	public String test;
	
	@Async
	public Future<String> prepareTransfer(String profileName, String document) {
		HttpEntity<String> response = restTemplate.exchange(GET_DEVICE_KEY_URL, HttpMethod.GET, null, String.class, apiKey, profileName);
		
		List<String> cookies = response.getHeaders().get("Set-Cookie");
		String deviceIdCookie = cookies.get(1).split(";")[0];
		String deviceId = (String)deviceIdCookie.subSequence(deviceIdCookie.indexOf("=") + 1, deviceIdCookie.length());
		
		// get key
		HttpHeaders headers = new HttpHeaders();
		
		headers.set("Cookie", "device_key=" + deviceId);
		
		
		
		
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		
		HttpEntity<SendAnywhereKey> key = restTemplate.exchange(GET_FILE_TRANSFER_KEY, HttpMethod.GET, entity, SendAnywhereKey.class);
		System.out.println("Key is " + key.getBody().getKey());
		System.out.println("Cookie is " + "Cookie: device_key=" + deviceId);
				
//		test = deviceId + "@" +  key.getBody().getKey();
//	    
//        HttpEntity<SendAnywhereKey> recvKeyObj = restTemplate.exchange(GET_FILE_RECV_KEY, HttpMethod.GET, entity, SendAnywhereKey.class, key.getBody().getKey());
//		String recvLink = recvKeyObj.getBody().getWebLink();
//		System.out.println("RecvLink is " + recvLink);
	    
//	    HttpEntity<String> recvKeyObj = restTemplate.exchange("https://send-anywhere.com/web/v1/"+ key.getBody().getKey(), HttpMethod.GET, entity2, String.class);
//
//		
//		org.apache.commons.httpclient.HttpMethod method = new GetMethod("https://send-anywhere.com/web/v1/"+ key.getBody().getKey());
//		method.setRequestHeader("Cookie", "device_key=" + deviceId);
//		
//		HttpClient client = new HttpClient();
//		try{
//			client.executeMethod(method);
//		}
//		catch(Exception e) {
//			System.out.println(e.getMessage());
//		}
		
		
		// store client request
		if(fileTransferMap.containsKey(profileName)) {
			fileTransferMap.get(profileName).add(document + "@" + key.getBody().getLink());
		}
		else {
			List<String> files = new ArrayList<String>();
			files.add(document + "@" + key.getBody().getLink());
			fileTransferMap.put(profileName, files);
		}
		
		
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", new FileSystemResource(document));
		
		
		// Post
		String result = restTemplate.postForObject(key.getBody().getWebLink(), parts, String.class);
		
		System.out.println("Result is " + result);
		return new AsyncResult<String>(result);
		
	}
}
