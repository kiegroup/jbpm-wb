package org.jbpm.console.ng.es.model;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RequestDetails implements Serializable {

	private RequestSummary request;
	private List<ErrorSummary> errors;
	private List<RequestParameterSummary> params;
	
	public RequestDetails() {
	}
	
	public RequestDetails(RequestSummary request, List<ErrorSummary> errors, List<RequestParameterSummary> params) {
		this();
		this.request = request;
		this.errors = new Vector<ErrorSummary>(errors);
		this.params = new Vector<RequestParameterSummary>(params);
	}

	public RequestSummary getRequest() {
		return request;
	}

	public void setRequest(RequestSummary request) {
		this.request = request;
	}

	public List<ErrorSummary> getErrors() {
		return errors;
	}

	public void setErrors(List<ErrorSummary> errors) {
		this.errors = new Vector<ErrorSummary>(errors);
	}

	public List<RequestParameterSummary> getParams() {
		return params;
	}

	public void setParams(List<RequestParameterSummary> params) {
		this.params = new Vector<RequestParameterSummary>(params);
	}
}
