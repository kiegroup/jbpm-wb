package org.jbpm.console.ng.es.model.events;

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RequestChangedEvent implements Serializable {

	private Long requestId;
	
    public RequestChangedEvent() {
    }

	public RequestChangedEvent(Long requestId) {
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
