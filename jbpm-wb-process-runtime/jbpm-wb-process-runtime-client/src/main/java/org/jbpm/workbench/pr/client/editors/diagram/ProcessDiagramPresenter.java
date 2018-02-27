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

package org.jbpm.workbench.pr.client.editors.diagram;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessDefSelectionEvent;
import org.jbpm.workbench.pr.events.ProcessInstanceSelectionEvent;
import org.jbpm.workbench.pr.service.ProcessImageService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

@Dependent
public class ProcessDiagramPresenter extends Composite implements RequiresResize {

    @Inject
    private ProcessDiagramWidgetView designerWidget;

    @Inject
    private Caller<ProcessImageService> processImageService;

    private FlowPanel container = new FlowPanel();

    private Constants constants = GWT.create(Constants.class);

    @PostConstruct
    public void init() {
        container.clear();
        container.add(designerWidget);
    }

    public void onProcessInstanceSelectionEvent(@Observes ProcessInstanceSelectionEvent event) {
        String containerId = event.getDeploymentId();
        String processInstanceId = String.valueOf(event.getProcessInstanceId());
        String serverTemplateId = event.getServerTemplateId();

        if (processInstanceId != null && !processInstanceId.isEmpty()) {
            processImageService.call(new RemoteCallback<String>() {
                @Override
                public void callback(final String svgContent) {
                    designerWidget.displayImage(svgContent);
                }
            }).getProcessInstanceDiagram(serverTemplateId,
                                         containerId,
                                         Long.parseLong(processInstanceId));
        }
    }

    public void onProcessSelectionEvent(@Observes final ProcessDefSelectionEvent event) {
        String containerId = event.getDeploymentId();
        String serverTemplateId = event.getServerTemplateId();
        String processId = event.getProcessId();
        processImageService.call(new RemoteCallback<String>() {
            @Override
            public void callback(final String svgContent) {
                designerWidget.displayImage(svgContent);
            }
        }).getProcessDiagram(serverTemplateId,
                             containerId,
                             processId);
    }

    @WorkbenchPartTitle
    public String getName() {
        return constants.Diagram();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return container;
    }

    @Override
    public void onResize() {

        int height = getContainer().getParent().getOffsetHeight();
        int width = getContainer().getParent().getOffsetWidth();

        getContainer().setWidth(width + "px");
        getContainer().setHeight(height + "px");
    }

    public FlowPanel getContainer() {
        return this.container;
    }

    public interface View
            extends
            HasBusyIndicator,
            IsWidget {

    }
}
