package org.jbpm.console.ng.ga.model;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ContextualSwtichMode {
	public static final String BASIC_MODE="Basic";
	public static final String ADVANCED_MODE="Advanced";
	
	private String modeName = BASIC_MODE;

	public String getModeName() {
		return modeName;
	}

	public void setModeName(String modeName) {
		this.modeName = modeName;
	}
}
