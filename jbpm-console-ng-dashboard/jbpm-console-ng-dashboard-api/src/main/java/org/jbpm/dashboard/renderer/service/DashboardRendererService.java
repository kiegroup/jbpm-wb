package org.jbpm.dashboard.renderer.service;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface DashboardRendererService {
    ConnectionStatus getAppStatus(String anUrl);
}
