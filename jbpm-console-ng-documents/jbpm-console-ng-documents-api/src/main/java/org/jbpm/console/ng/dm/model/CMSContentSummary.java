package org.jbpm.console.ng.dm.model;

public abstract class CMSContentSummary {

	private String name;
	
	private String path;
	
	private String id;
	
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
		return id;
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
