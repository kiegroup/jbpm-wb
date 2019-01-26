/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import static org.guvnor.m2repo.model.HTMLFileManagerFields.UPLOAD_MISSING_POM;
import static org.guvnor.m2repo.model.HTMLFileManagerFields.UPLOAD_UNABLE_TO_PARSE_POM;
import static org.guvnor.m2repo.utils.FileNameUtilities.isValid;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.base.form.AbstractForm;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jbpm.workbench.wi.workitems.service.ServiceTaskService;
import org.uberfire.workbench.events.NotificationEvent;

import com.google.gwt.user.client.Command;

@Dependent
public class ServiceTaskUploadFormPresenter implements ServiceTaskUploadFormView.Presenter {
    
    public static final String UPLOAD_FAILED = "ERROR";

    private ServiceTaskUploadFormView view;
    private Caller<ServiceTaskService> serviceTaskService;
    
    private Command onUploadCompleted;
    private Command onClose;

    private Event<NotificationEvent> notificationEvent;

    @Inject
    public ServiceTaskUploadFormPresenter(final ServiceTaskUploadFormView view,
                                          final Caller<ServiceTaskService> serviceTaskService,
                                          Event<NotificationEvent> notificationEvent,
                                          final SyncBeanManager iocManager) {
        this.view = view;
        this.serviceTaskService = serviceTaskService;
        this.notificationEvent = notificationEvent;
        this.onClose = () -> iocManager.destroyBean(ServiceTaskUploadFormPresenter.this);
        
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public void handleSubmitComplete(final AbstractForm.SubmitCompleteEvent event) {
        view.hideUploadingBusy();
        if (UPLOAD_MISSING_POM.equalsIgnoreCase(event.getResults())) {
            view.showInvalidJarNoPomWarning();
            view.hide();
        } else if (UPLOAD_UNABLE_TO_PARSE_POM.equalsIgnoreCase(event.getResults())) {
            view.showInvalidPomWarning();
            view.hide();
        } else if (UPLOAD_FAILED.equalsIgnoreCase(event.getResults())) {
            view.showUploadFailedError();
            view.hide();
        } else {            
            serviceTaskService.call((List<String> serviceTasks) -> {                
                String addTaskSuccessMsg = view.getSuccessInstallMessage();
                String addedServiceTasks = serviceTasks.stream().collect(Collectors.joining(","));
                
                notificationEvent.fire(new NotificationEvent(addTaskSuccessMsg + addedServiceTasks,
                                                             NotificationEvent.NotificationType.SUCCESS));
                onUploadCompleted.execute();
                view.hide();
            }).addServiceTasks(event.getResults());
            
        }
    }

    @Override
    public boolean isFileNameValid() {
        String fileName = view.getFileName();
        if (fileName == null || "".equals(fileName)) {
            view.showSelectFileUploadWarning();
            return false;
        } else if (!(isValid(fileName))) {
            view.showUnsupportedFileTypeWarning();
            return false;
        } else {
            view.showUploadingBusy();
            return true;
        }
    }

    public void showView(Command onUploadCompleted) {
        this.onUploadCompleted = onUploadCompleted;
        view.show();
    }

    @Override
    public Command onCloseCommand() {
        return onClose;
    }

}