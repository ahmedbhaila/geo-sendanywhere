package com.mycompany;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class GooglePlacesService {
	private static final String GOOGLE_PLACES_API_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location={location}&radius={radius}&key={key}";
	private static final String GOOGLE_GEO_API = "https://maps.googleapis.com/maps/api/geocode/json?address={address}&key={key}";
	@Autowired
	RestTemplate restTemplate;
	
	@Value("${google.place.api.key}")
	String apiKey;
	
	@Value("${google.place.api.search.radius}")
	Integer searchRadius;
	
	@Autowired
	GeoDocService geoDocService;
	
	public Set<PlacesResults> getPlacesData(String latLng) {
		Set<PlacesResults> places = null;
		ResponseEntity<GooglePlacesResults> results = restTemplate.getForEntity(GOOGLE_PLACES_API_URL, GooglePlacesResults.class, latLng, searchRadius, apiKey);
		if(results.getStatusCode().equals(HttpStatus.OK)) {
			places = new HashSet<PlacesResults>(results.getBody().getResults());
		}
		places.stream().forEach(p -> p.setAssociated(geoDocService.isDocAssiciated(p.getId()) == null ? false : true));
		return places;
	}
	
	public Set<PlacesResults> getPlacesDataFromAddress(String address) {
		Set<PlacesResults> results = null;
		ResponseEntity<GeoCode> geoCode = restTemplate.getForEntity(GOOGLE_GEO_API, GeoCode.class, address, apiKey);
		if(geoCode.getStatusCode().equals(HttpStatus.OK)) {
			if(geoCode != null && geoCode.getBody().getResults() != null && geoCode.getBody().getResults().size() != 0) {
				results = getPlacesData(geoCode.getBody().getResults().get(0).getGeometry().getLocation().getLat() + "," + geoCode.getBody().getResults().get(0).getGeometry().getLocation().getLng());
			}
		}
		return results;
		
	}
}
