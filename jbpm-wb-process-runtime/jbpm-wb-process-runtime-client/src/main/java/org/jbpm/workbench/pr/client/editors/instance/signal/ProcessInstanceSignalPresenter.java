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

package org.jbpm.workbench.pr.client.editors.instance.signal;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.events.ProcessInstancesUpdateEvent;
import org.jbpm.workbench.pr.service.ProcessService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchPopup(identifier = ProcessInstanceSignalPresenter.SIGNAL_PROCESS_POPUP)
public class ProcessInstanceSignalPresenter {

    public static final String SIGNAL_PROCESS_POPUP = "Signal Process Popup";

    private Constants constants = GWT.create( Constants.class );

    public interface PopupView extends UberView<ProcessInstanceSignalPresenter> {

        void displayNotification( String text );

        void addProcessInstanceId( long processInstanceId );

        String getSignalRefText();

        String getEventText();

        void setAvailableSignals( Collection<String> signals );

    }

    @Inject
    private PopupView view;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<ProcessInstancesUpdateEvent> processInstancesUpdatedEvent;
    
    private PlaceRequest place;

    @Inject
    private Caller<ProcessService> processService;

    private String serverTemplateId;
    private String[] deploymentId;

    @PostConstruct
    public void init() {

    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Signaling_Process_Instance();
    }

    @WorkbenchPartView
    public UberView<ProcessInstanceSignalPresenter> getView() {
        return view;
    }

    public void signalProcessInstances(List<Long> processInstanceIds) {
        processService.call(
                new RemoteCallback<Void>() {
                    @Override
                    public void callback(Void v) {
                        processInstancesUpdatedEvent.fire(new ProcessInstancesUpdateEvent(0L));
                        placeManager.closePlace(place);
                    }
                }
        ).signalProcessInstances( serverTemplateId, Arrays.asList(deploymentId), processInstanceIds, view.getSignalRefText(), view.getEventText());
    }

    @OnOpen
    public void onOpen() {
        serverTemplateId = place.getParameter( "serverTemplateId", "" ).toString();
        deploymentId = place.getParameter( "deploymentId", "" ).toString().split(",");
        String processInstanceIds = place.getParameter( "processInstanceId", "-1" ).toString();
        String[] ids = processInstanceIds.split( "," );
        for ( String id : ids ) {
            long processInstanceId = Long.parseLong( id );
            view.addProcessInstanceId( processInstanceId );
        }

        // for single process instance load available signals
        if ( ids.length == 1 && Long.parseLong( ids[ 0 ] ) != -1 ) {
            getAvailableSignals(Long.parseLong( ids[ 0 ] ) );
        }
    }

    public void getAvailableSignals(long processInstanceId) {
        processService.call(
                new RemoteCallback<Collection<String>>() {
                    @Override
                    public void callback(Collection<String> signals) {
                        view.setAvailableSignals(signals);

                    }
                }
        ).getAvailableSignals(serverTemplateId, deploymentId[0], processInstanceId);
    }
}
