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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessDefSelectionEvent;
import org.jbpm.workbench.pr.service.ProcessImageService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;

@Dependent
public class ProcessDiagramPresenter {

    @Inject
    private ProcessDiagramWidgetView view;

    private Caller<ProcessImageService> processImageService;

    private Constants constants = Constants.INSTANCE;

    @Inject
    public void setProcessImageService(final Caller<ProcessImageService> processImageService) {
        this.processImageService = processImageService;
    }

    public void onProcessSelectionEvent(@Observes final ProcessDefSelectionEvent event) {
        view.showBusyIndicator(constants.Loading());
        String containerId = event.getDeploymentId();
        String serverTemplateId = event.getServerTemplateId();
        String processId = event.getProcessId();
        processImageService.call((String svgContent) -> displayImage(svgContent,
                                                                     containerId)).getProcessDiagram(serverTemplateId,
                                                                                                     containerId,
                                                                                                     processId);
    }

    public void displayImage(final String svgContent, final String containerId) {
        if (svgContent == null || svgContent.isEmpty()) {
            view.displayMessage(constants.Process_Diagram_Not_FoundContainerShouldBeAvailable(containerId));
        } else {
            view.displayImage(svgContent);
        }
        view.hideBusyIndicator();
    }

    public void expandDiagramContainer() {
        view.expandDiagramContainer();
    }

    @WorkbenchPartTitle
    public String getName() {
        return constants.Diagram();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }
}
