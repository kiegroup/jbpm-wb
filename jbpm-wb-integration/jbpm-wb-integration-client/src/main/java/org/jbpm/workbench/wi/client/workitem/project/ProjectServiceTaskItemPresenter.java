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
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jbpm.workbench.wi.workitems.model.ServiceTaskSummary;
import org.kie.workbench.common.widgets.client.widget.ListItemPresenter;
import org.kie.workbench.common.widgets.client.widget.ListItemView;

import com.google.gwt.user.client.Command;

@Dependent
public class ProjectServiceTaskItemPresenter extends ListItemPresenter<ServiceTaskSummary, ProjectServiceTasksPresenter, ProjectServiceTaskItemPresenter.View> {

    public interface View extends ListItemView<ProjectServiceTaskItemPresenter>,
                                  IsElement {

        void setIcon(String icon);

        void setName(String name);

        void setAdditionalInfo(String additionalInfo);
        
        void setServiceTaskId(String id);
        
        void setServiceTaskParameters(List<String> parameters);
        
        void setServiceTaskReferenceLink(String link);
        
        void setInstalled(Boolean installed);
    }

    ServiceTaskSummary serviceTask;

    ProjectServiceTasksPresenter parentPresenter;

    @Inject
    public ProjectServiceTaskItemPresenter(final View view) {
        super(view);
    }

    @Override
    public ProjectServiceTaskItemPresenter setup(final ServiceTaskSummary serviceTask,
                                         final ProjectServiceTasksPresenter parentPresenter) {

        this.serviceTask = serviceTask;
        this.parentPresenter = parentPresenter;

        view.init(this);
        view.setIcon(serviceTask.getIcon());
        view.setName(serviceTask.getName());
        view.setAdditionalInfo(serviceTask.getAdditionalInfo());
        
        view.setServiceTaskId(serviceTask.getId());
        view.setServiceTaskParameters(serviceTask.getParameters());
        view.setServiceTaskReferenceLink(serviceTask.getReferenceLink());

        boolean containsTarget = serviceTask.getInstalledOn().contains(parentPresenter.getInstallTarget());
        boolean containsBranch = serviceTask.getInstalledOnBranch().contains(parentPresenter.getBranchName());
        view.setInstalled(containsTarget && containsBranch);

        return this;
    }

    @Override
    public ServiceTaskSummary getObject() {
        return serviceTask;
    }
    
    public void installServiceTask(String serviceTaskId, List<String> parameters, String referenceLink, Command onDone) {
        parentPresenter.installServiceTask(serviceTaskId, parameters, referenceLink, onDone);
    }
    
    public void uninstallServiceTask(String serviceTaskId, Command onDone) {
        parentPresenter.uninstallServiceTask(serviceTaskId, onDone);
    }
}
