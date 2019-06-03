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

package org.jbpm.workbench.wi.client.workitem.project;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jbpm.workbench.wi.workitems.model.ServiceTaskSummary;
import org.jbpm.workbench.wi.workitems.service.ServiceTaskService;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.util.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionView;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.widgets.client.widget.ListPresenter;
import org.uberfire.client.promise.Promises;

import com.google.gwt.user.client.Command;

import elemental2.dom.Element;
import elemental2.promise.Promise;

public class ProjectServiceTasksPresenter extends Section<ProjectScreenModel>  {

    private final View view;
    private final ServiceTasksListPresenter serviceTasksItemPresenters;
    
    private final Caller<ServiceTaskService> serviceTasksService;
    
    private ProjectScreenModel model;
    
    private SyncBeanManager iocManager;

    public interface View extends SectionView<ProjectServiceTasksPresenter> {

        Element getServiceTasksTable();  
    }

    @Inject
    WorkspaceProjectContext workspaceProjectContext;

    @Inject
    public ProjectServiceTasksPresenter(final ProjectServiceTasksPresenter.View view,
                               final Promises promises,
                               final MenuItem<ProjectScreenModel> menuItem,
                               final Event<SettingsSectionChange<ProjectScreenModel>> settingsSectionChangeEvent,
                               final Caller<ServiceTaskService> serviceTasksService,
                               final ServiceTasksListPresenter serviceTasksItemPresenters,
                               final SyncBeanManager iocManager) {

        super(settingsSectionChangeEvent, menuItem, promises);
        this.view = view;
        this.serviceTasksItemPresenters = serviceTasksItemPresenters;
        this.serviceTasksService = serviceTasksService;
        this.iocManager = iocManager;
    }

    @Override
    public Promise<Void> setup(final ProjectScreenModel model) {
        this.model = model;
        view.init(this);
        
        return loadServiceTasks(getBranchName()).then(tasks -> promises.create((resolve, reject) -> {
            serviceTasksItemPresenters.setup(view.getServiceTasksTable(),
                                             tasks,
                                             (serviceTask, presenter) -> presenter.setup(serviceTask, this));
            resolve.onInvoke(promises.resolve());
        }));
        
    }
    
    Promise<List<ServiceTaskSummary>> loadServiceTasks(String branchName) {
        return promises.promisify(serviceTasksService, s -> {
            s.getEnabledServiceTasks(branchName);
        });
    }

    @Override
    public int currentHashCode() {
        return 0;
    }

    @Override
    public SectionView<?> getView() {
        return view;
    }

    @Dependent
    public static class ServiceTasksListPresenter extends ListPresenter<ServiceTaskSummary, ProjectServiceTaskItemPresenter> {

        @Inject
        public ServiceTasksListPresenter(final ManagedInstance<ProjectServiceTaskItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
    
    public void installServiceTask(String serviceTaskId, List<String> parameters, String referenceLink, Command onDone) {
        if (parameters.isEmpty()) {
            serviceTasksService.call((Void) -> {
                onDone.execute();
                fireChangeEvent();
            }).installServiceTask(serviceTaskId, getInstallTarget(), null, getBranchName());
        } else {
            
            ServiceTaskInstallFormPresenter installFormPresenter = iocManager.lookupBean(ServiceTaskInstallFormPresenter.class).newInstance();
            installFormPresenter.showView(onDone, serviceTaskId, getInstallTarget(), parameters, referenceLink);
        }
    }
    
    public void uninstallServiceTask(String serviceTaskId, Command onDone) {
        serviceTasksService.call((Void) -> {
            onDone.execute();
            fireChangeEvent();
        }).uninstallServiceTask(serviceTaskId, getInstallTarget(), getBranchName());
    }
    
    public String getInstallTarget() {
        String uri = model.getPathToPOM().toString().replaceAll("\\/pom.xml", "");
        
        if (uri.contains("@")) {
            return uri.substring(uri.indexOf("@") + 1);
        } else {
            return uri.substring(uri.indexOf("://") + 3);
        }
    }

    public void newBranchEvent(@Observes final NewBranchEvent newBranchEvent) {
        serviceTasksService.call((Void) -> {

        }).updateInstalledServiceTasks(newBranchEvent.getNewBranchName(), newBranchEvent.getFromBranchName());
    }

    public String getBranchName() {
        String branchName = null;
        if(workspaceProjectContext != null) {
            branchName = workspaceProjectContext.getActiveWorkspaceProject().get().getBranch().getName();
        }
        return branchName;
    }
}
