package com.mycompany;

import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


class HelloWorld {
	
	// more advanced used of json mapping in SendAnywhere key, this one is just a basic example
	String message;
	String code;
	
	public HelloWorld(String code, String message) {
		this.message = message;
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
}
@RestController
public class MapController {
	
	@Autowired
	GooglePlacesService placesService;
	
	@Autowired
	GeoDocService geoDocService;
	
	@RequestMapping("/test")
	@ResponseBody
	public String test() {
		// use this template if you are not interested in json, just string
		return "Hello world";
	}
	
	@RequestMapping("/return")
	@ResponseBody
	public HelloWorld hello(){
		// spring will automagically create a JSON out of this java object
		// use this if you want to return something from the backend 
		// eg: query ajax call from the back can call localhost:8080/return and then the 
		// front end piece can do something with the json
		return new HelloWorld("1", "Hello World");
	}
	
	@RequestMapping("/places/{lat_lng:.+}")
	@ResponseBody
	public List<PlacesResults> getPlaces(@PathVariable("lat_lng") String latLng) {
		return placesService.getPlacesData(latLng);
	}
	
	@RequestMapping("/places/associate")
	@ResponseStatus(org.springframework.http.HttpStatus.CREATED)
	public void addRoute(@RequestBody PlaceAssociation placeAssociation) {
		geoDocService.associateDoc(placeAssociation.getPlaceId(), placeAssociation.getFileLocation());
	}
	
	@RequestMapping("/places/{place_id}/document")
	@ResponseBody
	public String getDocument(@PathVariable("place_id") String placeId) {
		return geoDocService.getAssociatedDoc(placeId);
	}
}
