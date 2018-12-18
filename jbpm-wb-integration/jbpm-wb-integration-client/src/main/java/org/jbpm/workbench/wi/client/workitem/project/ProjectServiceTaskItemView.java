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

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.wi.client.i18n.Constants;

import com.google.gwt.event.dom.client.ClickEvent;

import elemental2.dom.HTMLElement;

@Templated("#root")
public class ProjectServiceTaskItemView implements ProjectServiceTaskItemPresenter.View {

    @Inject
    @Named("span")
    @DataField("icon")
    private HTMLElement icon;

    @Inject
    @Named("span")
    @DataField("name")
    private HTMLElement name;

    @Inject
    @Named("span")
    @DataField("additionalInfo")
    private HTMLElement additionalInfo;
    
    @Inject
    @Named("button")
    @DataField("action-service-task")
    private HTMLElement actionButton;

    private ProjectServiceTaskItemPresenter presenter;
    
    private String serviceTaskId;
    private Boolean installed;
    
    private Constants constants = Constants.INSTANCE;

    @EventHandler("action-service-task")
    public void onAction(final ClickEvent event) {
        if (installed) {            
            presenter.uninstallServiceTask(serviceTaskId, () -> {makeInstallButton(); installed = false;});            
        } else {            
            presenter.installServiceTask(serviceTaskId, () -> {makeUninstallButton(); installed = true;});            
        }
    }

    @Override
    public void init(final ProjectServiceTaskItemPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setIcon(final String icon) {
        this.icon.classList.add(icon.split(" "));
    }

    @Override
    public void setName(final String name) {
        this.name.textContent = name;
    }

    @Override
    public void setAdditionalInfo(final String additionalInfo) {
        this.additionalInfo.textContent = additionalInfo;
    }
    
    @Override
    public void setServiceTaskId(String serviceTaskId) {
        this.serviceTaskId = serviceTaskId;
    }

    @Override
    public void setInstalled(Boolean installed) {
        this.installed = installed;
        
        if (installed) {
            makeUninstallButton();
        } else {
            makeInstallButton();
        }
    }

    protected void makeInstallButton() {
        actionButton.classList.add("btn-primary");
        actionButton.classList.remove("btn-danger");
        
        actionButton.textContent = constants.InstallServiceTask();
    }
    
    protected void makeUninstallButton() {
        actionButton.classList.remove("btn-primary");
        actionButton.classList.add("btn-danger");
        
        actionButton.textContent = constants.UninstallServiceTask();
    }
}
