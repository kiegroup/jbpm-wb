package org.jbpm.console.ng.pr.service;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface DeploymentManagerEntryPoint {

    void redeploy();
}
