/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.console.ng.wi.dd.model;

import java.util.List;

import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DeploymentDescriptorModel {

    private Overview overview;

    private String persistenceUnitName;
    private String auditPersistenceUnitName;

    private String auditMode;
    private String persistenceMode;

    private String runtimeStrategy;

    private List<ItemObjectModel> marshallingStrategies;

    private List<ItemObjectModel> eventListeners;

    private List<ItemObjectModel> globals;

    private List<ItemObjectModel> workItemHandlers;

    private List<ItemObjectModel> taskEventListeners;

    private List<ItemObjectModel> environmentEntries;

    private List<ItemObjectModel> configuration;

    private List<String> requiredRoles;

    private List<String> remotableClasses;

    private Boolean limitSerializationClasses;

    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    public void setPersistenceUnitName(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    public String getAuditPersistenceUnitName() {
        return auditPersistenceUnitName;
    }

    public void setAuditPersistenceUnitName(String auditPersistenceUnitName) {
        this.auditPersistenceUnitName = auditPersistenceUnitName;
    }

    public String getAuditMode() {
        return auditMode;
    }

    public void setAuditMode(String auditMode) {
        this.auditMode = auditMode;
    }

    public String getPersistenceMode() {
        return persistenceMode;
    }

    public void setPersistenceMode(String persistenceMode) {
        this.persistenceMode = persistenceMode;
    }

    public String getRuntimeStrategy() {
        return runtimeStrategy;
    }

    public void setRuntimeStrategy(String runtimeStrategy) {
        this.runtimeStrategy = runtimeStrategy;
    }

    public List<ItemObjectModel> getMarshallingStrategies() {
        return marshallingStrategies;
    }

    public void setMarshallingStrategies(List<ItemObjectModel> marshallingStrategies) {
        this.marshallingStrategies = marshallingStrategies;
    }

    public List<ItemObjectModel> getEventListeners() {
        return eventListeners;
    }

    public void setEventListeners(List<ItemObjectModel> eventListeners) {
        this.eventListeners = eventListeners;
    }

    public List<ItemObjectModel> getGlobals() {
        return globals;
    }

    public void setGlobals(List<ItemObjectModel> globals) {
        this.globals = globals;
    }

    public List<ItemObjectModel> getWorkItemHandlers() {
        return workItemHandlers;
    }

    public void setWorkItemHandlers(List<ItemObjectModel> workItemHandlers) {
        this.workItemHandlers = workItemHandlers;
    }

    public List<ItemObjectModel> getTaskEventListeners() {
        return taskEventListeners;
    }

    public void setTaskEventListeners(List<ItemObjectModel> taskEventListeners) {
        this.taskEventListeners = taskEventListeners;
    }

    public List<ItemObjectModel> getEnvironmentEntries() {
        return environmentEntries;
    }

    public void setEnvironmentEntries(List<ItemObjectModel> environmentEntries) {
        this.environmentEntries = environmentEntries;
    }

    public List<ItemObjectModel> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(List<ItemObjectModel> configuration) {
        this.configuration = configuration;
    }

    public List<String> getRequiredRoles() {
        return requiredRoles;
    }

    public void setRequiredRoles(List<String> requiredRoles) {
        this.requiredRoles = requiredRoles;
    }

    public List<String> getRemotableClasses() {
        return remotableClasses;
    }

    public void setRemotableClasses(List<String> remotableClasses) {
        this.remotableClasses = remotableClasses;
    }

    public Boolean getLimitSerializationClasses() {
        return this.limitSerializationClasses;
    }

    public void setLimitSerializationClasses(Boolean limit) {
        this.limitSerializationClasses = limit;
    }

    public Overview getOverview() {
        return overview;
    }

    public void setOverview(Overview overview) {
        this.overview = overview;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DeploymentDescriptorModel that = (DeploymentDescriptorModel) o;

        if (auditMode != null ? !auditMode.equals(that.auditMode) : that.auditMode != null) {
            return false;
        }
        if (auditPersistenceUnitName != null ? !auditPersistenceUnitName.equals(that.auditPersistenceUnitName) : that.auditPersistenceUnitName != null) {
            return false;
        }
        if (configuration != null ? !configuration.equals(that.configuration) : that.configuration != null) {
            return false;
        }
        if (environmentEntries != null ? !environmentEntries.equals(that.environmentEntries) : that.environmentEntries != null) {
            return false;
        }
        if (eventListeners != null ? !eventListeners.equals(that.eventListeners) : that.eventListeners != null) {
            return false;
        }
        if (globals != null ? !globals.equals(that.globals) : that.globals != null) {
            return false;
        }
        if (marshallingStrategies != null ? !marshallingStrategies.equals(that.marshallingStrategies) : that.marshallingStrategies != null) {
            return false;
        }
        if (persistenceMode != null ? !persistenceMode.equals(that.persistenceMode) : that.persistenceMode != null) {
            return false;
        }
        if (persistenceUnitName != null ? !persistenceUnitName.equals(that.persistenceUnitName) : that.persistenceUnitName != null) {
            return false;
        }
        if (remotableClasses != null ? !remotableClasses.equals(that.remotableClasses) : that.remotableClasses != null) {
            return false;
        }
        if (requiredRoles != null ? !requiredRoles.equals(that.requiredRoles) : that.requiredRoles != null) {
            return false;
        }
        if (runtimeStrategy != null ? !runtimeStrategy.equals(that.runtimeStrategy) : that.runtimeStrategy != null) {
            return false;
        }
        if (taskEventListeners != null ? !taskEventListeners.equals(that.taskEventListeners) : that.taskEventListeners != null) {
            return false;
        }
        if (workItemHandlers != null ? !workItemHandlers.equals(that.workItemHandlers) : that.workItemHandlers != null) {
            return false;
        }
        if (limitSerializationClasses != null ? !limitSerializationClasses.equals(that.limitSerializationClasses) : that.limitSerializationClasses != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = persistenceUnitName != null ? persistenceUnitName.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (auditPersistenceUnitName != null ? auditPersistenceUnitName.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (auditMode != null ? auditMode.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (persistenceMode != null ? persistenceMode.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (runtimeStrategy != null ? runtimeStrategy.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (marshallingStrategies != null ? marshallingStrategies.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (eventListeners != null ? eventListeners.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (globals != null ? globals.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (workItemHandlers != null ? workItemHandlers.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (taskEventListeners != null ? taskEventListeners.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (environmentEntries != null ? environmentEntries.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (configuration != null ? configuration.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (requiredRoles != null ? requiredRoles.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (remotableClasses != null ? remotableClasses.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (limitSerializationClasses != null ? limitSerializationClasses.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
