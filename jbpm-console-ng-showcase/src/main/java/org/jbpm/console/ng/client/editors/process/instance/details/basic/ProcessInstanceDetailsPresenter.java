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
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;


import org.jboss.errai.bus.client.api.RemoteCallback;


import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.client.model.NodeInstanceSummary;
import org.jbpm.console.ng.shared.KnowledgeDomainServiceEntryPoint;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.PassThroughPlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "Process Instance Details")
public class ProcessInstanceDetailsPresenter {

    public interface InboxView
            extends
            UberView<ProcessInstanceDetailsPresenter> {

        void displayNotification(String text);

        ListBox getCurrentActivitiesListBox();

        TextArea getLogTextArea();

        TextBox getProcessIdText();
    }
    @Inject
    private PlaceManager placeManager;
    @Inject
    InboxView view;
     @Inject
    Caller<KnowledgeDomainServiceEntryPoint> domainServices;

    @WorkbenchPartTitle
    public String getTitle() {
        return "Process Instance Details";
    }

    @WorkbenchPartView
    public UberView<ProcessInstanceDetailsPresenter> getView() {
        return view;
    }

    public void refreshProcessInstanceData(final String processId) {
        domainServices.call(new RemoteCallback<List<NodeInstanceSummary>>() {
            @Override
            public void callback(List<NodeInstanceSummary> details) {
               String fullLog = "";
               for(NodeInstanceSummary nis : details){
                   fullLog += nis.getTimestamp() + " - "+nis.getNodeName() + " (" + nis.getType() + ") \n"; 
               }
               view.getLogTextArea().setText(fullLog);
            }
        }).getProcessInstanceHistory(0, Long.parseLong(processId));
        domainServices.call(new RemoteCallback<List<NodeInstanceSummary>>() {
            @Override
            public void callback(List<NodeInstanceSummary> details) {
                for(NodeInstanceSummary nis : details){
                    view.getCurrentActivitiesListBox().addItem(nis.getTimestamp() +":" + nis.getId() + "-" + nis.getNodeName(), 
                                                                String.valueOf(nis.getId()) );
                }
            }
        }).getProcessInstanceActiveNodes(0, Long.parseLong(processId));
        
    }

    @OnReveal
    public void onReveal() {
        final PlaceRequest p = placeManager.getCurrentPlaceRequest();
        String processId = (String) ((PassThroughPlaceRequest) p).getPassThroughParameter("processInstanceId", "");
        view.getProcessIdText().setText(processId);
        refreshProcessInstanceData(processId);
    }
}
