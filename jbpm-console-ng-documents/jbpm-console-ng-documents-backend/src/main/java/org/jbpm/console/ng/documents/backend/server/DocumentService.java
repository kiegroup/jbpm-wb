package org.jbpm.console.ng.documents.backend.server;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.jbpm.console.ng.dm.model.CMSContentSummary;
import org.jbpm.console.ng.dm.model.DocumentSummary;

public interface DocumentService {

	List<CMSContentSummary> getChildren(String id);
	
	CMSContentSummary getDocument(String id);
	
	InputStream getDocumentContent(String id);

	void removeDocument(String id);

	void createDocument(DocumentSummary doc);
	
	Map<String,String> getConfigurationParameters();
	
	void setConfigurationParameters(Map<String,String> parameters);
	
	Boolean testConnection();
}
