package com.mycompany;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	ServletContext servletContext;
	
	@Autowired
	GooglePlacesService placesService;
	
	@Autowired
	GeoDocService geoDocService;
	
	@Autowired
	SendAnywhereService sendAnywhereService;
	
	@Autowired
	DocumentMapStore documentMapStore;
	
	
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
	public Set<PlacesResults> getPlaces(@PathVariable("lat_lng") String latLng) {
		return placesService.getPlacesData(latLng);
	}
	
	@RequestMapping("/places/associate")
	@ResponseStatus(org.springframework.http.HttpStatus.CREATED)
	public void associateDoc(@RequestBody PlaceAssociation placeAssociation) {
		geoDocService.associateDoc(placeAssociation.getPlaceId(), placeAssociation.getFileLocation());
	}
	
//	@RequestMapping("/places/{place_id}/document")
//	@ResponseBody
//	public String getDocument(@PathVariable("place_id") String placeId) {
//		return geoDocService.getAssociatedDoc(placeId);
//	}
	
	// TODO: remove after testing
	@RequestMapping("/download")
	public ResponseEntity<InputStreamResource> testphoto() throws IOException {
		InputStreamResource inputStream = new InputStreamResource(new FileInputStream("image.jpg"));

	   return ResponseEntity.ok(inputStream);
	}
	
	@RequestMapping("/places/{lat_lng:.+}/document/status")
	public boolean hasDocument(@PathVariable("lat_lng") String latLng, @RequestParam("profile") String profileName,
			@RequestParam("time") String timeInMillis, @RequestParam("timezone") String timeZone) {
		return geoDocService.findDocument(profileName, latLng, timeInMillis, timeZone);
	}
	
	@RequestMapping("/places/{client_id:.+}/documents")
	@ResponseBody
	public DocumentResult /*String*/  getDocuments(@PathVariable("client_id") String clientId) {
		return documentMapStore.getDocumentResults(clientId);
		//return documentMapStore.getDocumentMap().get(clientId);
//		return 
//				"{"+
//				"\"clientLocation\": \"41.969388,-87.741871\","+
//				"\"document\": "+
//				"["+
//				"{"+
//				"\"name\": \"image.jpg\","+
//				"\"placeId\": \"3d9b432e531aa2d62e4d42bc00431e2fb54d4937\","+
//				"\"placeName\": \"Sokol Community Hall\","+
//				"\"time\": \"1439258521\","+
//				"\"timeZone\": \"CST\","+
//				"\"url\": \"http://eoq.kr/345329\""+
//				"},"+
//				"{"+
//				"\"name\": \"image.jpg\","+
//				"\"placeId\": \"c0a3370cef8cf6e028f743f3c51faa881785835c\","+
//				"\"placeName\": \"Candace Dance\","+
//				"\"time\": \"1439258521\","+
//				"\"timeZone\": \"CST\","+
//				"\"url\": \"http://eoq.kr/354675\""+
//				"}"+
//				"]"+
//				"}";
	}
	
	@RequestMapping("/places/{client_id}/transfer")
	public void startTransfer(@PathVariable("client_id") String clientId, 
			@RequestParam("transferURL") String transferURL, @RequestParam("fileName") String fileName,
			@RequestParam("docIndex") String docIndex) {
		sendAnywhereService.startTransfer(clientId, transferURL, fileName, docIndex);
	}
	
	@RequestMapping("/places/search/{address}") 
	public Set<PlacesResults> startTransfer(@PathVariable("address") String address) {
		return placesService.getPlacesDataFromAddress(address);
	}
}
