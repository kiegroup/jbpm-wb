package org.jbpm.console.ng.es.model;

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RequestParameterSummary implements Serializable {

	private String key;
	private String value;
	
	public RequestParameterSummary() {
	}

	public RequestParameterSummary(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
