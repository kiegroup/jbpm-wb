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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.base.constants.SizeType;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.util.AbstractView;
import org.jbpm.workbench.wi.workitems.model.ServiceTaskSummary;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.TakesValue;

@Dependent
@Templated
public class ServiceTaskViewImpl extends AbstractView<ServiceTasksRepositoryListPresenter> implements TakesValue<ServiceTaskSummary>,
                                                                                             IsElement {

    private String id;
    
    @Inject
    @DataField("list-item")
    private Div row;
    
    @Inject
    @DataField("icon")
    private Span icon;

    @Inject
    @DataField("name")
    @Bound
    @SuppressWarnings("unused")
    private Span name;

    @Inject
    @DataField("description")
    @Bound    
    @SuppressWarnings("unused")
    private Div description;
    
    @Inject
    @DataField("additionalInfo")
    @Bound
    @SuppressWarnings("unused")
    private Span additionalInfo;
    
    @Inject
    @DataField("installedOn")
    private Span installedOn;
    
    @Inject
    @DataField
    private ToggleSwitch enabled;

    @Inject
    @AutoBound
    private DataBinder<ServiceTaskSummary> serviceTasks;

    @PostConstruct
    public void init() {
        this.enabled.setSize(SizeType.MINI);
    }
    
    @Override
    public void init(ServiceTasksRepositoryListPresenter presenter) {
        super.init(presenter);
    }

    @Override
    public ServiceTaskSummary getValue() {
        return serviceTasks.getModel();
    }

    @Override
    public void setValue(final ServiceTaskSummary model) {
        this.id = model.getId();
        this.enabled.setValue(model.getEnabled(), false);
        this.serviceTasks.setModel(model);
        String[] classes = model.getIcon().split(" ");
        for (String css : classes) {
            this.icon.getClassList().add(css);
        }
        this.installedOn.setTextContent(String.valueOf(model.getInstalledOn().size()));
    }

    @Override
    public HTMLElement getElement() {
        return row;
    }
    
    @EventHandler("enabled")
    public void onToggleChange(ChangeEvent event) {
        if (presenter == null) {
            return;
        }
        event.preventDefault();
        boolean enabledValue = enabled.getValue();
        
        if (enabledValue) {
            presenter.enableService(id);
            
        } else {
            presenter.disableService(id);
        }
    }
    
}