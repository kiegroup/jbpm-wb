package org.jbpm.dashboard.renderer.service;

import org.jboss.errai.common.client.api.annotations.Portable;
import java.io.Serializable;

@Portable
public class ConnectionStatus implements Serializable {
    private int status;

    public ConnectionStatus() {
    }

    public ConnectionStatus(int status) {

        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
