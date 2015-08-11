package com.mycompany;

import java.util.Set;

public class DocumentResult {
	protected String clientLocation;
	protected Set<DocumentDetail> document;
	public String getClientLocation() {
		return clientLocation;
	}
	public void setClientLocation(String clientLocation) {
		this.clientLocation = clientLocation;
	}
	public Set<DocumentDetail> getDocument() {
		return document;
	}
	public void setDocument(Set<DocumentDetail> document) {
		this.document = document;
	}
	
	
}
