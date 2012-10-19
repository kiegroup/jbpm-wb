package org.jbpm.console.ng.shared.fb.events;

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.form.builder.ng.model.client.form.EditionContext;

@Portable
public class PaletteItemUpdatedEvent implements Serializable{

	private EditionContext context;
	
	public PaletteItemUpdatedEvent(EditionContext context) {
		this();
		this.context = context;
	}
	
	public PaletteItemUpdatedEvent() {
	}

	public EditionContext getContext() {
		return context;
	}

	public void setContext(EditionContext context) {
		this.context = context;
	}
}
