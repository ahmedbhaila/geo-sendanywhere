package com.mycompany;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

public class DocumentMapStore {
	
	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	public Map<String, DocumentResult> documentMap = new HashMap<String, DocumentResult>();

	public Map<String, DocumentResult> getDocumentMap() {
		return documentMap;
	}

	public void setDocumentMap(Map<String, DocumentResult> documentMap) {
		this.documentMap = documentMap;
	}
	
	public DocumentResult getDocumentResults(String profileName) {
		DocumentResult results = new DocumentResult();
		results.setClientLocation((String)redisTemplate.opsForHash().get(profileName + ":summary", "latlng"));
		int resultSize = Integer.valueOf((String)redisTemplate.opsForHash().get(profileName + ":summary", "count"));
		HashOperations<String, String, String> ops = redisTemplate.opsForHash();
		String documentsKey = profileName + ":document";
		Set<DocumentDetail> documentDetails = new HashSet<DocumentDetail>();
		for(int i = 0; i < resultSize; i ++) {
			DocumentDetail detail = new DocumentDetail();
			detail.setLocation(ops.get(documentsKey + i, "latlng"));
			detail.setName(ops.get(documentsKey + i, "name"));
			detail.setPlaceId(ops.get(documentsKey + i, "placeId"));
			detail.setPlaceName(ops.get(documentsKey + i, "placeName"));
			detail.setTime(ops.get(documentsKey + i, "time"));
			detail.setTimeZone(ops.get(documentsKey + i, "timeZone"));
			detail.setUrl(ops.get(documentsKey + i, "url"));
			detail.setWebLink(ops.get(documentsKey + i, "webLink"));
			detail.setIndex(ops.get(documentsKey + i, "index"));
			
			documentDetails.add(detail);
			
		}
		results.setDocumentDetail(documentDetails);
		return results;
	}
	
	
}
