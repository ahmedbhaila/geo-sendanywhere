package com.mycompany;

import java.util.Set;

public class DocumentResult {
	protected String clientLocation;
	protected Set<DocumentDetail> documentDetail;
	public String getClientLocation() {
		return clientLocation;
	}
	public void setClientLocation(String clientLocation) {
		this.clientLocation = clientLocation;
	}
	public Set<DocumentDetail> getDocumentDetail() {
		return documentDetail;
	}
	public void setDocumentDetail(Set<DocumentDetail> documentDetail) {
		this.documentDetail = documentDetail;
	}
	
	
}
