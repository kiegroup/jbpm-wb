package org.jbpm.console.ng.dm.model.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.console.ng.dm.model.CMSContentSummary;

@Portable
public class NewDocumentEvent {
    
	private CMSContentSummary summary;

    public NewDocumentEvent() {
    }

    public NewDocumentEvent(CMSContentSummary summary) {
        this.summary = summary;
    }
    public CMSContentSummary getSummary() {
		return summary;
	}
    
    public void setSummary(CMSContentSummary summary) {
		this.summary = summary;
	}
}
