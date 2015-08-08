package com.mycompany;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class GeoDocService {
	
	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	GooglePlacesService placeService;
	
	@Autowired
	SendAnywhereService sendAnywhereService;
	
	
	public void associateDoc(String placeId, String fileLocation) {
		redisTemplate.opsForValue().set(placeId, fileLocation);
	}
	
	public String getAssociatedDoc(String placeId) {
		//return redisTemplate.opsForValue().get(placeId);
		return "image.jpg";
	}
	
	public boolean findDocument(String profileName, String latLng) {
		boolean docsFound = false;
		List<PlacesResults> results = placeService.getPlacesData(latLng);
		List<String> docs = results.stream().map(result -> getAssociatedDoc(result.getId())).collect(Collectors.toList());
		if(docs.size() != 0) {
			docsFound = true;
			// start async process for these files to be downloaded
			docs.forEach(doc -> {sendAnywhereService.prepareTransfer(profileName, doc); 
				//sendAnywhereService.setupReceive(sendAnywhereService.test.split("@")[0], sendAnywhereService.test.split("@")[0]);
				});	
		}
		System.out.println(" I am out of there");
		return docsFound;
	}
}
