/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.pr.client.editors.variables.history;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.VariableSummary;
import org.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;

@Dependent
@WorkbenchPopup(identifier = "Variable History Popup")
public class VariableHistoryPresenter {

    private Constants constants = GWT.create( Constants.class );

    public interface PopupView extends UberView<VariableHistoryPresenter> {

        void displayNotification( String text );

        void setProcessInstanceId( long processInstanceId );

        long getProcessInstanceId();

        void setVariableId( String variableId );

        String getVariableId();
    }

    @Inject
    private PopupView view;

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    private PlaceRequest place;

    private ListDataProvider<VariableSummary> dataProvider = new ListDataProvider<VariableSummary>();

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    @PostConstruct
    public void init() {

    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Variable_History();
    }

    @WorkbenchPartView
    public UberView<VariableHistoryPresenter> getView() {
        return view;
    }

    @OnOpen
    public void onOpen() {
        view.setProcessInstanceId( Long.parseLong( place.getParameter( "processInstanceId", "-1" ).toString() ) );
        view.setVariableId( place.getParameter( "variableId", "-1" ).toString() );

        loadVariableHistory();
    }

    public void close() {
        closePlaceEvent.fire( new BeforeClosePlaceEvent( this.place ) );
    }

    public void loadVariableHistory() {
        dataServices.call( new RemoteCallback<List<VariableSummary>>() {
            @Override
            public void callback( List<VariableSummary> processInstances ) {
                dataProvider.getList().clear();
                dataProvider.getList().addAll( processInstances );
                dataProvider.refresh();
            }
        }, new ErrorCallback<Message>() {
               @Override
               public boolean error( Message message, Throwable throwable ) {
                   ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
                   return true;
               }
           } ).getVariableHistory( view.getProcessInstanceId(), view.getVariableId() );
    }

    public void addDataDisplay( HasData<VariableSummary> display ) {
        dataProvider.addDataDisplay( display );
    }

    public ListDataProvider<VariableSummary> getDataProvider() {
        return dataProvider;
    }

    public void refreshData() {
        dataProvider.refresh();
    }

}
