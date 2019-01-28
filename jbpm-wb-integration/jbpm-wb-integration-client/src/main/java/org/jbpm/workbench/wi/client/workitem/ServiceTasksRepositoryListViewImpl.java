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

package org.jbpm.workbench.wi.client.workitem;

import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.base.constants.SizeType;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.util.AbstractView;
import org.jbpm.workbench.wi.client.i18n.Constants;
import org.jbpm.workbench.wi.client.workitem.ServiceTasksRepositoryListPresenter.ServiceTasksRepositoryListView;
import org.jbpm.workbench.wi.workitems.model.ServiceTaskSummary;
import org.jbpm.workbench.wi.workitems.model.ServiceTasksConfiguration;
import org.uberfire.client.views.pfly.widgets.FormGroup;

import com.google.gwt.event.dom.client.ChangeEvent;

@Dependent
@Templated
public class ServiceTasksRepositoryListViewImpl extends AbstractView<ServiceTasksRepositoryListPresenter> implements ServiceTasksRepositoryListView {

    private Constants constants = Constants.INSTANCE;
    
    @Inject
    @DataField("empty-list-item")
    private Div emptyContainer;

    @Inject
    @DataField("list-view")
    private Div viewContainer;
    
    @Inject
    @DataField("maven-install-group")
    @SuppressWarnings("unused")
    private FormGroup mavenInstallGroup;
    
    @Inject
    @DataField("maven-install-help")
    private Span mavenInstalHelp;
    
    @Inject
    @DataField("maven-install")
    private ToggleSwitch mavenInstall;
    
    @Inject
    @DataField("install-pom-deps-group")
    @SuppressWarnings("unused")
    private FormGroup installPomDepsGroup;
    
    @Inject
    @DataField("install-pom-deps-help")
    private Span installPomDepsHelp;
    
    @Inject
    @DataField("install-pom-deps")
    private ToggleSwitch installPomDeps;
    
    @Inject
    @DataField("use-version-range-group")
    @SuppressWarnings("unused")
    private FormGroup useVersionRangeGroup;
    
    @Inject
    @DataField("use-version-range-help")
    private Span useVersionRangeHelp;
    
    @Inject
    @DataField("use-version-range")
    private ToggleSwitch useVersionRange;

    @Inject
    @AutoBound
    private DataBinder<List<ServiceTaskSummary>> serviceTaskList;

    @Inject
    @Bound
    @DataField("list-container")
    private ListComponent<ServiceTaskSummary, ServiceTaskViewImpl> list;

    @Override
    public void init(ServiceTasksRepositoryListPresenter presenter) {
        super.init(presenter);        
        list.addComponentCreationHandler(v -> v.init(presenter));
        
        mavenInstall.setSize(SizeType.MINI);        
        installPomDeps.setSize(SizeType.MINI);
        useVersionRange.setSize(SizeType.MINI);        
        
        // set help messages
        mavenInstalHelp.setTextContent(constants.MavenInstallHelp());
        installPomDepsHelp.setTextContent(constants.InstallPomDepsHelp());
        useVersionRangeHelp.setTextContent(constants.UseVersionRangeHelp());
    }


    @Override
    public void setServiceTaskList(final List<ServiceTaskSummary> serviceTaskList) {
        this.serviceTaskList.setModel(serviceTaskList);
        if (serviceTaskList.isEmpty()) {
            removeCSSClass(emptyContainer,
                           "hidden");
        } else {
            addCSSClass(emptyContainer,
                        "hidden");
        }
    }

    @Override
    public HTMLElement getElement() {
        return viewContainer;
    }
    

    @EventHandler("add-service-task")
    public void onAddServiceTaskClick(final @ForEvent("click") MouseEvent event) {
        presenter.openUploadDialog();
    }
    
    @EventHandler("maven-install")
    public void onToggleChangeMavenInstall(ChangeEvent event) {
        updateConfiguration();
    }
    
    @EventHandler("install-pom-deps")
    public void onToggleChangeInstallPomDeps(ChangeEvent event) {
        updateConfiguration();
    }
    
    @EventHandler("use-version-range")
    public void onToggleChangeVersionRange(ChangeEvent event) {
        updateConfiguration();
    }

    protected void updateConfiguration() {
        ServiceTasksConfiguration configuration = new ServiceTasksConfiguration(mavenInstall.getValue(), installPomDeps.getValue(), useVersionRange.getValue());
        presenter.saveConfiguration(configuration);
    }

    @Override
    public void setConfiguration(ServiceTasksConfiguration configuration) {
        this.mavenInstall.setValue(configuration.getMavenInstall(), false);
        this.installPomDeps.setValue(configuration.getInstallPomDeps(), false);
        this.useVersionRange.setValue(configuration.getVersionRange(), false);
    }
}