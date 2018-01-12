package org.jbpm.workbench.wi.client.editors.deployment.descriptor2.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public enum RuntimeStrategy {
    SINGLETON,
    PER_REQUEST,
    PER_PROCESS_INSTANCE
}

