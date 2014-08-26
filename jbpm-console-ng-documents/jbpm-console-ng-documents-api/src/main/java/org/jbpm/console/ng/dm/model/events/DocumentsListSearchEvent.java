package org.jbpm.console.ng.dm.model.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.console.ng.dm.model.CMSContentSummary;

@Portable
public class DocumentsListSearchEvent {
    
	private CMSContentSummary summary;

    public DocumentsListSearchEvent() {
    }

    public DocumentsListSearchEvent(CMSContentSummary summary) {
        this.summary = summary;
    }
    public CMSContentSummary getSummary() {
		return summary;
	}
    
    public void setSummary(CMSContentSummary summary) {
		this.summary = summary;
	}
}
