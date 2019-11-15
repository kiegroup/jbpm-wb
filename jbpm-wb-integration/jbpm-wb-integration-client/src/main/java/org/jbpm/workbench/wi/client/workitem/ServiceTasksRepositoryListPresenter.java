/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.wi.client.workitem;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jbpm.workbench.wi.client.i18n.Constants;
import org.jbpm.workbench.wi.workitems.model.ServiceTaskSummary;
import org.jbpm.workbench.wi.workitems.model.ServiceTasksConfiguration;
import org.jbpm.workbench.wi.workitems.service.ServiceTaskService;
import org.kie.workbench.common.widgets.client.popups.alert.AlertPopupView;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.views.pfly.widgets.ConfirmPopup;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@WorkbenchScreen(identifier = ServiceTasksRepositoryListPresenter.SCREEN_ID)
public class ServiceTasksRepositoryListPresenter {

    public static final String SCREEN_ID = "ServiceTasksRepositoryPreferences";

    private Caller<ServiceTaskService> serviceTaskService;
    
    private Constants constants = Constants.INSTANCE;

    private Event<NotificationEvent> notificationEvent;
    
    protected ServiceTasksRepositoryListView view;
    
    protected SyncBeanManager iocManager;

    private ConfirmPopup confirmPopup;

    private AlertPopupView cannotRemovePopup;

    public ServiceTasksRepositoryListPresenter(){

    }

    @Inject
    public ServiceTasksRepositoryListPresenter(final Event<NotificationEvent> notificationEvent, final ServiceTasksRepositoryListView view,
                                               final Caller<ServiceTaskService> serviceTaskService, final SyncBeanManager iocManager) {
        this.notificationEvent = notificationEvent;
        this.view = view;
        this.serviceTaskService = serviceTaskService;
        this.iocManager = iocManager;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.ServiceTaskList();
    }

    @PostConstruct
    public void init() {
        view.init(this); 
        retrieveConfig();
        refreshData();
    }

    @WorkbenchPartView
    public ServiceTasksRepositoryListView getView() {
        return view;
    }

    @Inject
    public void setConfirmPopup(final ConfirmPopup confirmPopup){
        this.confirmPopup = confirmPopup;
    }

    @Inject
    public void setCannotRemovePopup(final AlertPopupView cannotRemovePopup) {
        this.cannotRemovePopup = cannotRemovePopup;
    }

    protected void refreshData() {
        serviceTaskService.call((List<ServiceTaskSummary> serviceTasks) -> view.setServiceTaskList(serviceTasks)).getServiceTasks();
    }
    
    protected void retrieveConfig() {
        serviceTaskService.call((ServiceTasksConfiguration configuration) -> view.setConfiguration(configuration)).getConfiguration();
    }    

    protected void saveConfiguration(ServiceTasksConfiguration configuration) {
        serviceTaskService.call().saveConfiguration(configuration);
    }
    
    public void enableService(String id) {
        serviceTaskService.call().enableServiceTask(id);
    }

    public void disableService(String id) {
        serviceTaskService.call().disableServiceTask(id);
    }
    
    public void openUploadDialog() {
        ServiceTaskUploadFormPresenter uploadFormPresenter = iocManager.lookupBean(ServiceTaskUploadFormPresenter.class).newInstance();
        uploadFormPresenter.showView(() -> refreshData());
    }

    protected void refreshServiceTaskList(ServiceTaskSummary serviceTaskSummary) {
        serviceTaskService.call((String serviceTaskName) -> {
            notificationEvent.fire(new NotificationEvent(getRemoveTaskSuccess(serviceTaskName)));
            refreshData();
        }).removeServiceTask(serviceTaskSummary);
    }

    protected String getRemoveTaskSuccess(String serviceTaskName) {
        return constants.RemoveTaskSuccess(serviceTaskName);
    }

    public interface ServiceTasksRepositoryListView extends UberElement<ServiceTasksRepositoryListPresenter> {

        void setServiceTaskList(List<ServiceTaskSummary> serviceTaskList);
        
        void setConfiguration(ServiceTasksConfiguration configuration);

    }

    public void openRemoveServiceTaskConfirmPopup(ServiceTaskSummary serviceTaskSummary) {
        if (serviceTaskSummary.getInstalledOn().size() > 0) {
            cannotRemovePopup.alert(constants.RemoveServiceTaskTitle(), constants.CanNotRemoveServiceTaskMessage(serviceTaskSummary.getName()));
        } else {
            confirmPopup.show(constants.RemoveServiceTaskTitle(), constants.RemoveServiceTaskAction(),
                              constants.RemoveServiceTaskConfirmMessage(serviceTaskSummary.getName()), () -> refreshServiceTaskList(serviceTaskSummary));
        }
    }
}
