package org.jbpm.console.ng.es.model.events;

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RequestCreatedEvent implements Serializable {

	private Long requestId;
	
    public RequestCreatedEvent() {
    }

	public RequestCreatedEvent(Long requestId) {
		this();
		this.requestId = requestId;
	}
    
    public Long getRequestId() {
		return requestId;
	}
    
    public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}
}
