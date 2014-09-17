package org.jbpm.console.ng.bd.dd.model;

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

    public Overview getOverview() {
        return overview;
    }

    public void setOverview(Overview overview) {
        this.overview = overview;
    }
}
