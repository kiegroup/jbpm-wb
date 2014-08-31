package org.jbpm.console.ng.dm.model;

import org.jbpm.console.ng.ga.model.GenericSummary;

public abstract class CMSContentSummary extends GenericSummary{

	private String path;
	
	private CMSContentSummary parent;
	
	public CMSContentSummary(String name, String id, String path) {
		super();
		this.name = name;
		this.id = id;
		this.path = path;
	}

	public CMSContentSummary() {
	}
	
	public abstract ContentType getContentType();
	
	public String getId() {
		return (String)id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setParent(CMSContentSummary parent) {
		this.parent = parent;
	}
	
	public CMSContentSummary getParent() {
		return parent;
	}
	
	public String getPath() {
		return path;
	}
	
	
	
	public static enum ContentType {
		DOCUMENT,
		FOLDER;
	}
}
