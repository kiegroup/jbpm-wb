/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.pr.client.editors.definition.details.multi;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.ext.widgets.common.client.menu.RefreshMenuBuilder;
import org.jbpm.console.ng.pr.client.editors.diagram.ProcessDiagramUtil;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.perspectives.DataSetProcessInstancesWithVariablesPerspective;
import org.jbpm.workbench.forms.client.display.providers.StartProcessFormDisplayProviderImpl;
import org.jbpm.workbench.forms.client.display.views.PopupFormDisplayerView;
import org.jbpm.workbench.forms.display.api.ProcessDisplayerConfig;
import org.jbpm.console.ng.pr.model.ProcessDefinitionKey;
import org.jbpm.console.ng.pr.events.ProcessDefSelectionEvent;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

public abstract class BaseProcessDefDetailsMultiPresenter implements RefreshMenuBuilder.SupportsRefresh {

    public interface BaseProcessDefDetailsMultiView {

        IsWidget getNewInstanceButton();

    }

    private Constants constants = GWT.create( Constants.class );

    @Inject
    protected PopupFormDisplayerView formDisplayPopUp;

    @Inject
    private PlaceManager placeManager;

    @Inject
    protected StartProcessFormDisplayProviderImpl startProcessDisplayProvider;

    @Inject
    private Event<ProcessDefSelectionEvent> processDefSelectionEvent;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    private PlaceRequest place;

    private String deploymentId = "";

    private String processId = "";

    private String processDefName = "";

    private String serverTemplateId = "";

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Details();
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    public void onProcessSelectionEvent( @Observes final ProcessDefSelectionEvent event ) {
        deploymentId = event.getDeploymentId();
        processId = event.getProcessId();
        processDefName = event.getProcessDefName();
        serverTemplateId = event.getServerTemplateId();

        changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent( this.place,
                                                                 String.valueOf( deploymentId ) + " - " + processDefName ) );
    }

    public void createNewProcessInstance() {
        final ProcessDisplayerConfig config = new ProcessDisplayerConfig( new ProcessDefinitionKey( serverTemplateId, deploymentId, processId, processDefName ), processDefName );

        formDisplayPopUp.setTitle( "" );
        startProcessDisplayProvider.setup( config, formDisplayPopUp );
    }

    public void goToProcessDefModelPopup() {
        if ( place != null && !deploymentId.equals( "" ) ) {
            placeManager.goTo( ProcessDiagramUtil.buildPlaceRequest( serverTemplateId, deploymentId, processId));
        }
    }

    public void viewProcessInstances() {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest( DataSetProcessInstancesWithVariablesPerspective.PERSPECTIVE_ID );
        placeRequestImpl.addParameter( DataSetProcessInstancesWithVariablesPerspective.PROCESS_ID, processId );
        placeManager.goTo( placeRequestImpl );
    }

    @Override
    public void onRefresh() {
        processDefSelectionEvent.fire(new ProcessDefSelectionEvent(processId, deploymentId, serverTemplateId, processDefName));
    }

    public void closeDetails() {
        placeManager.forceClosePlace( place );
    }

}
