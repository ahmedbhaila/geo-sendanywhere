package com.mycompany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SendAnywhereKey {
	
	
	String key;
	
	@JsonProperty("expires_time")
	String expiresTime;
	
	@JsonProperty("link")
	String link;
	
	@JsonProperty("weblink")
	String webLink;
	
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getExpiresTime() {
		return expiresTime;
	}
	public void setExpiresTime(String expiresTime) {
		this.expiresTime = expiresTime;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getWebLink() {
		return webLink;
	}
	public void setWebLin(String webLink) {
		this.webLink = webLink;
	}
}
