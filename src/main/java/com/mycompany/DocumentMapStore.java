package com.mycompany;

import java.util.HashMap;
import java.util.Map;

public class DocumentMapStore {
	public Map<String, DocumentResult> documentMap = new HashMap<String, DocumentResult>();

	public Map<String, DocumentResult> getDocumentMap() {
		return documentMap;
	}

	public void setDocumentMap(Map<String, DocumentResult> documentMap) {
		this.documentMap = documentMap;
	}
	
	
}
