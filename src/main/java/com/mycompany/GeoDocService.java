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
	
	@Autowired
	DocumentMapStore documentStoreMap;
	
	
	public void associateDoc(String placeId, String fileLocation) {
		redisTemplate.opsForValue().set(placeId, fileLocation);
	}
	
	public String isDocAssiciated(String placeId) {
		return redisTemplate.opsForValue().get(placeId);
	}
	
	public DocumentDetail getAssociatedDoc(PlacesResults result, String timeInMillis, String timeZone) {
		String document = redisTemplate.opsForValue().get(result.getId());
		DocumentDetail docDetail = null;
		if(document != null && !document.equals("null")) {
			docDetail = new DocumentDetail();
			docDetail.setName(document);
			docDetail.setPlaceId(result.getId());
			docDetail.setPlaceName(result.getName());
			docDetail.setTime(timeInMillis);
			docDetail.setTimeZone(timeZone);
			docDetail.setLocation(result.getGeometry().getLocation().getLat() + "," + result.getGeometry().getLocation().getLng());

		}
		else{
			docDetail = null;
		}
		return docDetail;
		//return "image.jpg";
	}
	
	public boolean findDocument(String profileName, String latlng, String timeInMillis, String timeZone) {
		Set<PlacesResults> results = placeService.getPlacesData(latlng);
		Set<DocumentDetail> docs = results.stream().filter(Objects::nonNull).map(result -> getAssociatedDoc(result, timeInMillis, timeZone)).collect(Collectors.toSet());
		docs = docs.stream().filter(Objects::nonNull).collect(Collectors.toSet());
		if(docs.size() != 0 && !docs.contains(null)) {
			setupDocumentMap(profileName,latlng, docs);
			// start async process for these files to be downloaded
			docs.stream().filter(Objects::nonNull).forEach(doc -> sendAnywhereService.prepareTransfer(profileName, doc));
		}
		if(docs.size() != 0) {
			return documentStoreMap.getDocumentMap().get(profileName).getDocumentDetail().size() > 0;
		}
		else {
			return false;
		}
		
	}
	
	private void setupDocumentMap(String profile, String latlng, Set<DocumentDetail> documentDetails) {
		if(!documentStoreMap.getDocumentMap().containsKey(profile)) {
			DocumentResult docResult = new DocumentResult();
			docResult.setClientLocation(latlng);
			docResult.setDocumentDetail(documentDetails);
			documentStoreMap.getDocumentMap().put(profile, docResult);
		}
		else {
			documentStoreMap.getDocumentMap().get(profile).setClientLocation(latlng);
			documentStoreMap.getDocumentMap().get(profile).getDocumentDetail().addAll(documentDetails);
		}
	}
}
