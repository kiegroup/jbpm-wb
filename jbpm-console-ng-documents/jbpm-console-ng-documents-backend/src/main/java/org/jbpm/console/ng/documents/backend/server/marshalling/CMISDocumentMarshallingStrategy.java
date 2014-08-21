package org.jbpm.console.ng.documents.backend.server.marshalling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.drools.core.common.DroolsObjectInputStream;
import org.jbpm.console.ng.dm.model.DocumentSummary;
import org.jbpm.console.ng.documents.backend.server.DocumentService;
import org.jbpm.console.ng.documents.backend.server.DocumentServiceCMISImpl;
import org.jbpm.document.Document;
import org.kie.api.marshalling.ObjectMarshallingStrategy;

public class CMISDocumentMarshallingStrategy implements
		ObjectMarshallingStrategy {

//	@Inject TODO cannot do injection, it just does not fill it.
	private DocumentService documentService;

	public CMISDocumentMarshallingStrategy() {
		documentService = new DocumentServiceCMISImpl();
		documentService.init();
	}
	@Override
	public boolean accept(Object o) {
		return o instanceof Document;
	}

	@Override
	public void write(ObjectOutputStream os, Object object) throws IOException {
		Document document = (Document) object;
		documentService.createDocument(new DocumentSummary(document.getName(), "", "/"));
	}

	@Override
	public Object read(ObjectInputStream os) throws IOException,
			ClassNotFoundException {
        String objectId = os.readUTF();
        String canonicalName = os.readUTF();
        String link = os.readUTF();
        try {
        	DocumentSummary doc = (DocumentSummary)this.documentService.getDocument(objectId);
            Document document = (Document) Class.forName(canonicalName).newInstance();
            document.setIdentifier(objectId);
            document.setLink(link);
            document.setName(doc.getName());
            document.setSize(10);
            document.setLastModified(new Date());
            document.setAttributes(new HashMap<String, String>());
            document.setContent(doc.getContent());
            return document;
        } catch(Exception e) {
            throw new RuntimeException("Cannot read document", e);
        }
	}

	@Override
	public byte[] marshal(Context context, ObjectOutputStream os, Object object)
			throws IOException {
		String path = "/";
		Document document = (Document) object;
		DocumentSummary summary = new DocumentSummary(document.getName(), "", path);
		summary.setContent(document.getContent());
		documentService.createDocument(summary);
		ByteArrayOutputStream buff = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(buff);
		oos.writeUTF(summary.getId());
		oos.writeUTF(document.getClass().getCanonicalName());
		String link = "http://localhost:8080/magnoliaAuthor/dms" + path + document.getName();
		oos.writeUTF(link);
		oos.close();
		return buff.toByteArray();
	}

	@Override
	public Object unmarshal(Context context,
			ObjectInputStream objectInputStream, byte[] object,
			ClassLoader classLoader) throws IOException, ClassNotFoundException {

		DroolsObjectInputStream is = new DroolsObjectInputStream(
				new ByteArrayInputStream(object), classLoader);
		// first we read out the object id and class name we stored during
		// marshaling
		String objectId = is.readUTF();
		String canonicalName = is.readUTF();
		String link = is.readUTF();
		Document document = null;
		try {
			document = (Document) Class.forName(canonicalName).newInstance();
			DocumentSummary storedDoc = (DocumentSummary)this.documentService.getDocument(objectId);
			document.setIdentifier(storedDoc.getId());
			document.setName(storedDoc.getName());
			document.setLink(link);
			document.setLastModified(new Date());
			document.setSize(10);
			document.setAttributes(new HashMap<String, String>());
			InputStream stream = this.documentService.getDocumentContent(objectId);
			byte[] content = IOUtils.toByteArray(stream);
			document.setContent(content);
		} catch (Exception e) {
			throw new RuntimeException(
					"Cannot read document from storage service", e);
		}
		return document;
	}

	@Override
	public Context createContext() {
		// TODO Auto-generated method stub
		return null;
	}

}
