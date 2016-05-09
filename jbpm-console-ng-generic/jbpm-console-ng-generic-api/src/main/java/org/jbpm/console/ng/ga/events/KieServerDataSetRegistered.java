/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.ga.events;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Event fired after all data sets are registered into a server instance.
 */
@Portable
public class KieServerDataSetRegistered {

    private String serverInstanceId;

    private String serverTemplateId;

    public KieServerDataSetRegistered() {
    }

    public KieServerDataSetRegistered(String serverInstanceId, String serverTemplateId) {
        this.serverInstanceId = serverInstanceId;
        this.serverTemplateId = serverTemplateId;
    }

    public String getServerInstanceId() {
        return serverInstanceId;
    }

    public void setServerInstanceId(String serverInstanceId) {
        this.serverInstanceId = serverInstanceId;
    }

    public String getServerTemplateId() {
        return serverTemplateId;
    }

    public void setServerTemplateId(String serverTemplateId) {
        this.serverTemplateId = serverTemplateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KieServerDataSetRegistered that = (KieServerDataSetRegistered) o;

        if (!serverInstanceId.equals(that.serverInstanceId)) return false;
        return serverTemplateId.equals(that.serverTemplateId);

    }

    @Override
    public int hashCode() {
        int result = serverInstanceId.hashCode();
        result = 31 * result + serverTemplateId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "KieServerDataSetRegistered{" +
                "serverInstanceId='" + serverInstanceId + '\'' +
                ", serverTemplateId='" + serverTemplateId + '\'' +
                '}';
    }

}