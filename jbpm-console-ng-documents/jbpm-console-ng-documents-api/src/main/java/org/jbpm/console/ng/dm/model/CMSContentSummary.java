/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
