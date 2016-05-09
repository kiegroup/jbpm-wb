/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.console.ng.pr.client.editors.diagram;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.service.ProcessImageService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

@Dependent
@WorkbenchPopup(identifier = "jBPM Process Diagram", size = WorkbenchPopup.WorkbenchPopupSize.LARGE)
public class ProcessDiagramPopUpPresenter extends Composite implements RequiresResize {

    public interface View
            extends
            HasBusyIndicator,
            IsWidget {

    }

    @Inject
    private ProcessDiagramWidgetView designerWidget;

    @Inject
    private Caller<ProcessImageService> processImageService;

    private FlowPanel container = new FlowPanel();

    private Constants constants = GWT.create( Constants.class );

    @PostConstruct
    public void init() {
        container.clear();
        container.add( designerWidget );
    }

    @OnOpen
    public void onOpen() {

    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {

        if ( place instanceof PathPlaceRequest ) {

            String serverTemplateId = place.getParameter("serverTemplateId", null);
            String containerId = place.getParameter("containerId", null);
            String processId = place.getParameter("processId", null);

            String processInstanceId = place.getParameter("processInstanceId", null);

            if (processInstanceId != null && !processInstanceId.isEmpty()) {
                processImageService.call(new RemoteCallback<String>() {
                    @Override
                    public void callback(final String svgContent) {
                        designerWidget.displayImage(svgContent);
                    }
                }).getProcessInstanceDiagram(serverTemplateId, containerId, Long.parseLong(processInstanceId));
            } else {
                processImageService.call(new RemoteCallback<String>() {
                    @Override
                    public void callback(final String svgContent) {
                        designerWidget.displayImage(svgContent);
                    }
                }).getProcessDiagram(serverTemplateId, containerId, processId);
            }
        }
    }

    @WorkbenchPartTitle
    public String getName() {
        return constants.Process_Diagram();
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
        getContainer().setHeight( height + "px");
    }

    public FlowPanel getContainer() {
        return this.container;
    }

}
