/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workbench.ks.integration.event;

import org.kie.server.controller.api.model.runtime.ServerInstance;

/**
 * Event fired after successful registration of server instance in console
 */
public class ServerInstanceRegistered {

    private ServerInstance serverInstance;

    public ServerInstanceRegistered() {
    }

    public ServerInstanceRegistered(ServerInstance serverInstance) {
        this.serverInstance = serverInstance;
    }

    public ServerInstance getServerInstance() {
        return this.serverInstance;
    }

    public void setServerInstance(ServerInstance serverInstance) {
        this.serverInstance = serverInstance;
    }

    public String toString() {
        return "ServerInstanceRegistered{serverInstance=" + this.serverInstance + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ServerInstanceRegistered that = (ServerInstanceRegistered) o;
            if (this.serverInstance != null) {
                if (!this.serverInstance.equals(that.serverInstance)) {
                    return false;
                }
            } else if (that.serverInstance != null) {
                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.serverInstance != null ? this.serverInstance.hashCode() : 0;
    }
}
