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
package org.jbpm.console.ng.client.editors.process.instance.details.basic;

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;


import org.jboss.errai.bus.client.api.RemoteCallback;


import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.shared.model.NodeInstanceSummary;
import org.jbpm.console.ng.shared.model.ProcessInstanceSummary;
import org.jbpm.console.ng.shared.model.ProcessSummary;
import org.jbpm.console.ng.shared.model.VariableSummary;
import org.jbpm.console.ng.shared.KnowledgeDomainServiceEntryPoint;
import org.kie.runtime.process.ProcessInstance;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.shared.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "Process Instance Details")
public class ProcessInstanceDetailsPresenter {

    private PlaceRequest place;

    public interface InboxView
            extends
            UberView<ProcessInstanceDetailsPresenter> {

        void displayNotification( String text );

        ListBox getCurrentActivitiesListBox();

        TextArea getLogTextArea();

        TextBox getProcessIdText();

        TextBox getProcessNameText();

        TextBox getStateText();

        void setProcessINstance( ProcessInstanceSummary processInstance );
    }

    @Inject
    private PlaceManager placeManager;
    @Inject
    InboxView                                view;
    @Inject
    Caller<KnowledgeDomainServiceEntryPoint> domainServices;
    private             ListDataProvider<VariableSummary> dataProvider = new ListDataProvider<VariableSummary>();
    public static final ProvidesKey<VariableSummary>      KEY_PROVIDER = new ProvidesKey<VariableSummary>() {
        public Object getKey( VariableSummary item ) {
            return item == null ? null : item.getVariableId();
        }
    };

    @WorkbenchPartTitle
    public String getTitle() {
        return "Process Instance Details";
    }

    @WorkbenchPartView
    public UberView<ProcessInstanceDetailsPresenter> getView() {
        return view;
    }

    public void refreshProcessInstanceData( final String processId,
                                            final String processDefId ) {
        domainServices.call( new RemoteCallback<List<NodeInstanceSummary>>() {
            @Override
            public void callback( List<NodeInstanceSummary> details ) {
                String fullLog = "";
                for ( NodeInstanceSummary nis : details ) {
                    fullLog += nis.getTimestamp() + " - " + nis.getNodeName() + " (" + nis.getType() + ") \n";
                }
                view.getLogTextArea().setText( fullLog );
            }
        } ).getProcessInstanceHistory( 0, Long.parseLong( processId ) );
        domainServices.call( new RemoteCallback<List<NodeInstanceSummary>>() {
            @Override
            public void callback( List<NodeInstanceSummary> details ) {
                for ( NodeInstanceSummary nis : details ) {
                    view.getCurrentActivitiesListBox().addItem( nis.getTimestamp() + ":" + nis.getId() + "-" + nis.getNodeName(),
                                                                String.valueOf( nis.getId() ) );
                }
            }
        } ).getProcessInstanceActiveNodes( 0, Long.parseLong( processId ) );

        domainServices.call( new RemoteCallback<ProcessSummary>() {
            @Override
            public void callback( ProcessSummary process ) {
                view.getProcessNameText().setText( process.getId() );
            }
        } ).getProcessDesc( processDefId );

        domainServices.call( new RemoteCallback<ProcessInstanceSummary>() {
            @Override
            public void callback( ProcessInstanceSummary process ) {
                view.setProcessINstance( process );

                String statusStr = "Unknown";
                switch ( process.getState()) {
               case ProcessInstance.STATE_ACTIVE:
                   statusStr = "Active";
                   break;
               case ProcessInstance.STATE_ABORTED:
                   statusStr = "Aborted";
                   break;
               case ProcessInstance.STATE_COMPLETED:
                   statusStr = "Completed";
                   break;
               case ProcessInstance.STATE_PENDING:
                   statusStr = "Pending";
                   break;
               case ProcessInstance.STATE_SUSPENDED:
                   statusStr = "Suspended";
                   break;
               default:
                   break;
               }
               
               view.getStateText().setText(statusStr);
           }
       }).getProcessInstanceById(0, Long.parseLong(processId));
       
       loadVariables(processId, processDefId);


    }

    public void addDataDisplay(HasData<VariableSummary> display) {
        dataProvider.addDataDisplay(display);
    }

    public ListDataProvider<VariableSummary> getDataProvider() {
        return dataProvider;
    }

    public void refreshData() {
        dataProvider.refresh();
    }

    @OnStart
    public void onStart(final PlaceRequest place) {
        this.place = place;
    }

    @OnReveal
    public void onReveal() {
        String processId = place.getParameter("processInstanceId", "");
        String processDefId = place.getParameter("processDefId", "");
        view.getProcessIdText().setText(processId);
        view.getProcessNameText().setText(processDefId);
        refreshProcessInstanceData(processId, processDefId);
    }
    
    public void loadVariables(final String processId, final String processDefId) {
        domainServices.call(new RemoteCallback<List<VariableSummary>>() {
            @Override
            public void callback(List<VariableSummary> variables) {
                dataProvider.getList().clear();
                    dataProvider.getList().addAll(variables);
                    dataProvider.refresh();
            }
        }).getVariablesCurrentState(Long.parseLong(processId), processDefId);
    }
}
