package org.jbpm.console.ng.documents.backend.server;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.dm.model.CMSContentSummary;
import org.jbpm.console.ng.dm.model.DocumentSummary;
import org.jbpm.console.ng.dm.service.DocumentServiceEntryPoint;

@Service
@ApplicationScoped
public class DocumentServiceEntryPointImpl implements DocumentServiceEntryPoint {

	@Inject
	private DocumentService documentService;
	
	@Override
	public List<CMSContentSummary> getDocuments(String id) {
		return this.documentService.getChildren(id);
	}

	@Override
	public CMSContentSummary getDocument(String id) {
		return this.documentService.getDocument(id);
	}
	
	@Override
	public void removeDocument(String id) {
		this.documentService.removeDocument(id);
	}
	
	@Override
	public void addDocument(DocumentSummary doc) {
		doc.setContent("test".getBytes());
		this.documentService.createDocument(doc);
	}
	
	@Override
	public Map<String, String> getConfigurationParameters() {
		return documentService.getConfigurationParameters();
	}
	
	@Override
	public Long setConfigurationParameters(Map<String, String> parameters) {
		documentService.setConfigurationParameters(parameters);
		return 0l;
		
	}

	@Override
	public Boolean testConnection() {
		return documentService.testConnection();
	}

}
