/*
 * Copyright 2012 JBoss Inc
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
package org.jbpm.console.ng.pr.client.editors.definition.details;

import com.google.gwt.user.client.Window;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.bd.service.KnowledgeDomainServiceEntryPoint;
import org.jbpm.console.ng.ht.model.TaskDefSummary;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.shared.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "Process Definition Details")
public class ProcessDefDetailsPresenter {

    private PlaceRequest place;

  

    public interface InboxView
            extends
            UberView<ProcessDefDetailsPresenter> {

        void displayNotification( String text );

        TextBox getNroOfHumanTasksText();

        TextBox getProcessNameText();

        ListBox getHumanTasksListBox();

        ListBox getUsersGroupsListBox();

        ListBox getProcessDataListBox();

        ListBox getSubprocessListBox();
    }

    @Inject
    private PlaceManager placeManager;
    @Inject
    InboxView                                view;
    @Inject
    Caller<TaskServiceEntryPoint>            taskServices;
    @Inject
    Caller<KnowledgeDomainServiceEntryPoint> domainServices;

    @OnStart
    public void onStart( final PlaceRequest place ) {
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Process Definition Details";
    }

    @WorkbenchPartView
    public UberView<ProcessDefDetailsPresenter> getView() {
        return view;
    }
    
    void openProcessDiagram(final String processDefId) {
      
      domainServices.call( new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback( Map<String, String> availableProcessesPaths ) {
               Window.open("http://localhost:8080/designer/editor?profile=jbpm&pp=&uuid=git://jbpm-playground" 
                       +availableProcessesPaths.get(processDefId), "_blank", "");
            }
        } ).getAvailableProcessesPath();
    
  }

    public void refreshProcessDef( final String processId ) {

        domainServices.call( new RemoteCallback<List<TaskDefSummary>>() {
            @Override
            public void callback( List<TaskDefSummary> tasks ) {
                view.getNroOfHumanTasksText().setText( String.valueOf( tasks.size() ) );
                for ( TaskDefSummary t : tasks ) {
                    view.getHumanTasksListBox().addItem( t.getName(), String.valueOf( t.getId() ) );
                }
            }
        } ).getAllTasksDef( processId );
        domainServices.call( new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback( Map<String, String> entities ) {

                for ( String key : entities.keySet() ) {
                    view.getUsersGroupsListBox().addItem( entities.get( key ) + "- " + key, key );
                }
            }
        } ).getAssociatedEntities( processId );
        domainServices.call( new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback( Map<String, String> inputs ) {

                for ( String key : inputs.keySet() ) {
                    view.getProcessDataListBox().addItem( key + "- " + inputs.get( key ), key );
                }
            }
        } ).getRequiredInputData( processId );

        domainServices.call( new RemoteCallback<Collection<String>>() {
            @Override
            public void callback( Collection<String> subprocesses ) {

                for ( String key : subprocesses ) {
                    view.getSubprocessListBox().addItem( key, key );
                }
            }
        } ).getReusableSubProcesses( processId );
    }

    @OnReveal
    public void onReveal() {
        String processId = place.getParameter( "processId", "" );
        view.getProcessNameText().setText( processId );

        refreshProcessDef( processId );
    }
}
