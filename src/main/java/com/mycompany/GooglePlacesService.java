package com.mycompany;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class GooglePlacesService {
	private static final String GOOGLE_PLACES_API_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location={location}&radius={radius}&key={key}";
	
	@Autowired
	RestTemplate restTemplate;
	
	@Value("${google.place.api.key}")
	String apiKey;
	
	@Value("${google.place.api.search.radius}")
	Integer searchRadius;
	
	public Set<PlacesResults> getPlacesData(String latLng) {
		Set<PlacesResults> places = null;
		ResponseEntity<GooglePlacesResults> results = restTemplate.getForEntity(GOOGLE_PLACES_API_URL, GooglePlacesResults.class, latLng, searchRadius, apiKey);
		if(results.getStatusCode().equals(HttpStatus.OK)) {
			places = new HashSet<PlacesResults>(results.getBody().getResults());
		}
		return places;
	}
}
