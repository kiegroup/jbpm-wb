package org.jbpm.console.ng.documents.backend.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.bindings.spi.webservices.SunRIPortProvider;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.MimeTypes;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.dm.model.CMSContentSummary;
import org.jbpm.console.ng.dm.model.DocumentSummary;
import org.jbpm.console.ng.dm.model.FolderSummary;

@Service
@ApplicationScoped
public class DocumentServiceCMISImpl implements DocumentService {

	private Map<String, String> parameters;

	private Session session;

	@Override
	public void init() {
		parameters = new HashMap<String, String>();
		String webServicesACLServices = "http://localhost:8080/magnoliaAuthor/.magnolia/cmisws/ACLService?wsdl";
		String webServicesDiscoveryServices = "http://localhost:8080/cmis/services/DiscoveryService?wsdl";
		String webServicesMultifilingServices = "http://localhost:8080/magnoliaAuthor/.magnolia/cmisws/MultiFilingService?wsdl";
		String webServicesNavigationServices = "http://localhost:8080/magnoliaAuthor/.magnolia/cmisws/NavigationService?wsdl";
		String webServicesObjectServices = "http://localhost:8080/magnoliaAuthor/.magnolia/cmisws/ObjectService?wsdl";
		String webServicesPolicyServices = "http://localhost:8080/magnoliaAuthor/.magnolia/cmisws/PolicyService?wsdl";
		String webServicesRelationshipServices = "http://localhost:8080/magnoliaAuthor/.magnolia/cmisws/RelationshipService?wsdl";
		String webServicesRepositoryServices = "http://localhost:8080/magnoliaAuthor/.magnolia/cmisws/RepositoryService?wsdl";
		String webServicesVersioningServices = "http://localhost:8080/magnoliaAuthor/.magnolia/cmisws/VersioningService?wsdl";
		String repositoryID = "dms";
		String user = "superuser";
		String password = "superuser";


		// user credentials
		parameters.put(SessionParameter.USER, "superuser");
		parameters.put(SessionParameter.PASSWORD, "superuser");

		// connection settings
		parameters.put(SessionParameter.BINDING_TYPE,
				BindingType.WEBSERVICES.value());
		parameters.put(SessionParameter.WEBSERVICES_ACL_SERVICE,
				webServicesACLServices);
		parameters.put(SessionParameter.WEBSERVICES_DISCOVERY_SERVICE,
				webServicesDiscoveryServices);
		parameters.put(SessionParameter.WEBSERVICES_MULTIFILING_SERVICE,
				webServicesMultifilingServices);
		parameters.put(SessionParameter.WEBSERVICES_NAVIGATION_SERVICE,
				webServicesNavigationServices);
		parameters.put(SessionParameter.WEBSERVICES_OBJECT_SERVICE,
				webServicesObjectServices);
		parameters.put(SessionParameter.WEBSERVICES_POLICY_SERVICE,
				webServicesPolicyServices);
		parameters.put(SessionParameter.WEBSERVICES_RELATIONSHIP_SERVICE,
				webServicesRelationshipServices);
		parameters.put(SessionParameter.WEBSERVICES_REPOSITORY_SERVICE,
				webServicesRepositoryServices);
		parameters.put(SessionParameter.WEBSERVICES_VERSIONING_SERVICE,
				webServicesVersioningServices);
		parameters.put(SessionParameter.REPOSITORY_ID, repositoryID);
		parameters.put(SessionParameter.WEBSERVICES_PORT_PROVIDER_CLASS,
				SunRIPortProvider.class.getName());

	}

	@Override
	public Map<String, String> getConfigurationParameters() {
		return parameters;
	}

	@Override
	public void setConfigurationParameters(Map<String, String> parameters) {
		parameters.put(SessionParameter.BINDING_TYPE,
				BindingType.WEBSERVICES.value());
		parameters.put(SessionParameter.WEBSERVICES_PORT_PROVIDER_CLASS,
				SunRIPortProvider.class.getName());
		this.parameters = parameters;
		createSession();

	}

	private Session getSession() {
		if (session == null) {
			session = createSession();
		}
		return session;
	}

	private Session createSession() {
		try {
			SessionFactory factory = SessionFactoryImpl.newInstance();

			// create session
			session = factory.createSession(parameters);
		} catch (Exception e) {
			session = null;
		}
		return session;
	}

	@Override
	public List<CMSContentSummary> getChildren(String id) {
		Session session = getSession();
		if (session != null) {
			Folder folder = null;
			if (id == null || id.isEmpty()) {
				folder = session.getRootFolder();
			} else {
				folder = (Folder) session.getObject(id);
			}
			ItemIterable<CmisObject> children = folder.getChildren();
			Iterator<CmisObject> childrenItems = children.iterator();
			List<CmisObject> documents = new ArrayList<CmisObject>();
			while (childrenItems.hasNext()) {
				CmisObject item = childrenItems.next();
				documents.add(item);
			}
			return this.transform(documents, folder);
		}
		return new ArrayList<CMSContentSummary>();
	}

	@Override
	public InputStream getDocumentContent(String id) {
		Session session = getSession();
		if (session != null) {
			if (id == null || id.isEmpty()) {
				throw new IllegalArgumentException("No id provided");
			}
			Document document = (Document) session.getObject(id);
			if (document == null) {
				throw new IllegalArgumentException(
						"Document with this id does not exist");
			}
			return document.getContentStream().getStream();
		}
		return null;
	}

	@Override
	public void removeDocument(final String id) {
		Session session = getSession();
		if (session != null) {
			session.delete(new ObjectId() {
				@Override
				public String getId() {
					return id;
				}
			});
		}
	}

	public List<CMSContentSummary> transform(List<CmisObject> children,
			Folder folder) {
		List<CMSContentSummary> documents = new ArrayList<CMSContentSummary>();
		for (CmisObject item : children) {
			documents.add(transform(item, folder));
		}
		return documents;
	}

	public CMSContentSummary transform(CmisObject object, Folder parentFolder) {
		CMSContentSummary doc = null;
		if (((ObjectType) object.getType()).getId().equals("cmis:folder")) {
			Folder folder = (Folder) object;
			doc = new FolderSummary(object.getName(), object.getId(),
					folder.getPath());
			Folder parent = ((Folder) object).getParents().get(0); // for now,
																	// assume it
																	// only has
																	// one
																	// parent.
			FolderSummary parentFolderSummary = new FolderSummary(
					parent.getName(), parent.getId(), parent.getPath());
			if (parentFolder != null && parentFolder.getParents().size() > 0) {
				Folder grandParent = parentFolder.getParents().get(0);
				parentFolderSummary
						.setParent(new FolderSummary(grandParent.getName(),
								grandParent.getId(), grandParent.getPath()));
			}
			doc.setParent(parentFolderSummary);
		} else {
			doc = new DocumentSummary(object.getName(), object.getId(), null);
			Folder parent = ((Document) object).getParents().get(0); // for now,
																		// assume
																		// it
																		// only
																		// has
																		// one
																		// parent.
			FolderSummary parentFolderSummary = new FolderSummary(
					parent.getName(), parent.getId(), parent.getPath());
			if (parentFolder != null && parentFolder.getParents().size() > 0) {
				Folder grandParent = parentFolder.getParents().get(0);
				parentFolderSummary
						.setParent(new FolderSummary(grandParent.getName(),
								grandParent.getId(), grandParent.getPath()));
			}
			doc.setParent(parentFolderSummary);
		}
		return doc;
	}

	@Override
	public CMSContentSummary getDocument(String id) {
		Session session = getSession();

		if (session != null) {
			Document document = null;
			if (id != null && !id.isEmpty()) {
				document = (Document) session.getObject(id);
			}

			if (document.getParents() != null
					&& document.getParents().size() > 0) {
				return this.transform(document, document.getParents().get(0));
			} else {
				return this.transform(document, null);
			}
		}

		return new DocumentSummary();
	}

	@Override
	public void createDocument(DocumentSummary doc) {
		Session session = getSession();
		if (session != null) {
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			properties.put(PropertyIds.NAME, doc.getName());
			InputStream stream = new ByteArrayInputStream(doc.getContent());
			ContentStream contentStream = new ContentStreamImpl(doc.getName(),
					BigInteger.valueOf(doc.getContent().length),
					MimeTypes.getMIMEType(doc.getName()), stream);
			Document createdDoc = ((Folder) session.getObjectByPath(doc.getPath())).createDocument(
					properties, contentStream, VersioningState.NONE);
			doc.setId(createdDoc.getId());
		}
		throw new IllegalStateException("Could not get CMIS session");
	}

	@Override
	public Boolean testConnection() {
		Session session = getSession();

		if (session != null) {
			if (session.getRootFolder() != null) {
				return true;
			}
		}

		return false;
	}
}
