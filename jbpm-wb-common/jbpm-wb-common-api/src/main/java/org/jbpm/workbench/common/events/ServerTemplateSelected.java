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

package org.jbpm.workbench.common.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.server.controller.api.model.spec.ServerTemplate;

/**
 * Event fired when a Server Template is selected
 */
@Portable
public class ServerTemplateSelected {

    private ServerTemplate serverTemplate;

    public ServerTemplateSelected() {
    }

    public ServerTemplateSelected(ServerTemplate serverTemplate) {
        this.serverTemplate = serverTemplate;
    }

    public String getServerTemplateId() {
        return serverTemplate == null ? null : serverTemplate.getId();
    }

    public ServerTemplate getServerTemplate() {
        return serverTemplate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerTemplateSelected)) {
            return false;
        }

        ServerTemplateSelected that = (ServerTemplateSelected) o;

        if (getServerTemplate() == null && that.getServerTemplate() == null) {
            return true;
        }

        if (getServerTemplate() != null && that.getServerTemplate() == null) {
            return false;
        }

        if (getServerTemplate() == null && that.getServerTemplate() != null) {
            return false;
        }

        return getServerTemplate().getId().equals(that.getServerTemplate().getId());
    }

    @Override
    @SuppressWarnings("PMD.AvoidMultipleUnaryOperators")
    public int hashCode() {
        int result = serverTemplate == null ? 0 : serverTemplate.getId().hashCode();
        result = ~~result;
        return result;
    }

    @Override
    public String toString() {
        return "ServerTemplateSelected{" +
                "serverTemplate='" + serverTemplate + '\'' +
                '}';
    }
}