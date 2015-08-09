package com.mycompany;

import java.util.Objects;
import java.util.Set;
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
		return redisTemplate.opsForValue().get(placeId);
		//return "image.jpg";
	}
	
	public boolean findDocument(String profileName, String latLng) {
		boolean docsFound = false;
		Set<PlacesResults> results = placeService.getPlacesData(latLng);
		Set<String> docs = results.stream().filter(Objects::nonNull).map(result -> getAssociatedDoc(result.getId())).collect(Collectors.toSet());
		docs = docs.stream().filter(Objects::nonNull).collect(Collectors.toSet());
		if(docs.size() != 0 && !docs.contains(null)) {
			docsFound = true;
			// start async process for these files to be downloaded
			docs.stream().filter(Objects::nonNull).forEach(doc -> {sendAnywhereService.prepareTransfer(profileName, doc); 
				//sendAnywhereService.setupReceive(sendAnywhereService.test.split("@")[0], sendAnywhereService.test.split("@")[0]);
				});	
		}
		System.out.println(" I am out of there");
		return docsFound;
	}
}
