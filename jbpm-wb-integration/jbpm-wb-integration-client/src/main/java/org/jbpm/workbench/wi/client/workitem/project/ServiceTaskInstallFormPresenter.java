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

package org.jbpm.workbench.wi.client.workitem.project;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jbpm.workbench.wi.workitems.service.ServiceTaskService;

import com.google.gwt.user.client.Command;


public class ServiceTaskInstallFormPresenter implements ServiceTaskInstallFormView.Presenter {
    
    private ServiceTaskInstallFormView view;
    
    private Command onSubmitCommand;
    private Command onClose;
    
    private final Caller<ServiceTaskService> serviceTasksService;
    
    @Inject
    public ServiceTaskInstallFormPresenter(final ServiceTaskInstallFormView view,
                                           final Caller<ServiceTaskService> serviceTasksService,
                                           final SyncBeanManager iocManager) {
        this.view = view;
        this.serviceTasksService = serviceTasksService;        
        this.onClose = () -> iocManager.destroyBean(ServiceTaskInstallFormPresenter.this);
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void showView(Command onSubmitCommand, String serviceTaskId, String target, List<String> parameters, String referenceLink) {        
        this.onSubmitCommand = onSubmitCommand;
        view.show(serviceTaskId, target, parameters, referenceLink);
    }

    @Override
    public void installWithParameters(String serviceTaskId, String target, List<String> parameterValues) {
        serviceTasksService.call((Void) -> {}).installServiceTask(serviceTaskId, target, parameterValues);
        
        onSubmitCommand.execute();
    }

    @Override
    public Command onCloseCommand() {
        return onClose;
    }
}