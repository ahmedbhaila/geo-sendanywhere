package com.mycompany;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.RedisTemplate;
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
	private static final String GET_FILE_RECV_KEY = "https://send-anywhere.com/web/v1/key/{key}";
	private static final String DROP_BOX_DOMAIN = "dropbox";
	private Random random = new Random();
	
	@Value("${sendanywhere.api.key}")
	String apiKey;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	DocumentMapStore mapStore;
	
	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	
	//public static Map<String, List<String>> fileTransferMap = new HashMap<String, List<String>>();

	public String test;
	
	//@Async
	//public Future<String> prepareTransfer(String profileName, DocumentDetail document) {
		
	public void prepareTransfer(String profileName, DocumentDetail document) {
				
		
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
		
		
		
		String result = null;
		if(key.getBody().getLink() != null) {
			document.setUrl(key.getBody().getLink());
			document.setWebLink(key.getBody().getWebLink());
			System.out.println("****Document links *****" + document.getWebLink() + " " + document.getUrl());
			
			/*MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
			parts.add("file", new FileSystemResource(document.getName()));
			
			
			// Post
			result = restTemplate.postForObject(key.getBody().getWebLink(), parts, String.class);
			System.out.println("Result is " + result);
			
			// remove document from the list
			System.out.println("Removing document from list");
			mapStore.documentMap.get(profileName).getDocumentDetail().remove(document);*/
		}
		else {
			// delete this entry since SendAnywhere didnt respond with a link
			mapStore.documentMap.get(profileName).getDocumentDetail().remove(document);
			System.out.println("****NO LINKS WERE FOUND*******");
		}
		//return new AsyncResult<String>(result);		
	}
	
	@Async
	public Future<String> startTransfer(String profileName, String transferURL, String name, String docIndex) {
		try{
			if(name.startsWith("http")) {
				
				// parse file extension
				String ext = name.substring(name.lastIndexOf("."), name.length());
				// download the file
				String tempFilename = "temp_" + String.valueOf(System.currentTimeMillis()) + random.nextInt() + ext;
				if(name.contains(DROP_BOX_DOMAIN)) {
					// dropbox links can be directly download by change dl=1
					name = name.replace("dl=0", "dl=1");
				}
				FileUtils.copyURLToFile(new URL(name), new File(tempFilename));
				name = tempFilename;
			}
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", new FileSystemResource(name));
		
		
		// Post
		String result = restTemplate.postForObject(transferURL, parts, String.class);
		System.out.println("Result is " + result);
		
		// remove document from the list
		System.out.println("Removing document from list");
		DocumentResult docs = mapStore.documentMap.get(profileName);
		docs.setDocumentDetail(docs.getDocumentDetail().stream().filter(d -> !d.getWebLink().equals(transferURL)).collect(Collectors.toSet()));
		String documentsKey = profileName + ":document";
		redisTemplate.opsForHash().delete(documentsKey + docIndex, "name");
		redisTemplate.opsForHash().delete(documentsKey + docIndex, "placeId");
		redisTemplate.opsForHash().delete(documentsKey + docIndex, "placeName");
		redisTemplate.opsForHash().delete(documentsKey + docIndex, "time");
		redisTemplate.opsForHash().delete(documentsKey + docIndex, "timeZone");
		redisTemplate.opsForHash().delete(documentsKey + docIndex, "url");
		redisTemplate.opsForHash().delete(documentsKey + docIndex, "webLink");
		redisTemplate.opsForHash().delete(documentsKey + docIndex, "latlng");
		redisTemplate.opsForHash().delete(documentsKey + docIndex, "index");
	
		
		return new AsyncResult<String>(result);
	}
}
