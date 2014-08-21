package org.jbpm.console.ng.dm.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class FolderSummary extends CMSContentSummary {

	public FolderSummary() {
	}
	
	public FolderSummary(String name, String id, String path) {
		super(name, id, path);
	}

	@Override
	public ContentType getContentType() {
		return ContentType.FOLDER;
	}
}
