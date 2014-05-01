/*
 * Copyright 2014 JBoss by Red Hat.
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
package org.jbpm.console.ng.mobile.pr.client.instance.details;

import com.google.gwt.user.client.ui.HasText;
import com.google.inject.Inject;
import com.googlecode.mgwt.ui.client.widget.Button;
import java.util.List;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.mobile.core.client.MGWTUberView;
import org.jbpm.console.ng.pr.model.NodeInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.kie.api.runtime.process.ProcessInstance;

/**
 *
 * @author livthomas
 */
public class ProcessInstanceDetailsPresenter {
    
    public interface ProcessInstanceDetailsView extends MGWTUberView<ProcessInstanceDetailsPresenter> {
        
        HasText getInstanceIdText();
        
        HasText getDefinitionIdText();
        
        HasText getDefinitionNameText();
        
        HasText getDefinitionVersionText();
        
        HasText getDeploymentText();
        
        HasText getInstanceStateText();
        
        HasText getCurrentActivitiesText();
        
        HasText getInstanceLogText();
        
        Button getAbortButton();
        
        void goToInstancesList();
        
        void displayNotification(String title, String message);
        
    }
    
    @Inject
    private Caller<DataServiceEntryPoint> dataServices;
    
    @Inject
    private Caller<KieSessionEntryPoint> sessionServices;
    
    @Inject
    private ProcessInstanceDetailsView view;
    
    public ProcessInstanceDetailsView getView() {
        return view;
    }
    
    public void refresh(long instanceId, String definitionId) {
        // Instance ID, Deployment, Instance State
        dataServices.call(new RemoteCallback<ProcessInstanceSummary>() {
            @Override
            public void callback(ProcessInstanceSummary process) {
                view.getInstanceIdText().setText(Long.toString(process.getId()));
                view.getDeploymentText().setText(process.getDeploymentId());
                
                String status = "Unknown";
                boolean showAbortButton = true;
                switch (process.getState()) {
                    case ProcessInstance.STATE_ACTIVE:
                        status = "Active";
                        break;
                    case ProcessInstance.STATE_ABORTED:
                        status = "Aborted";
                        showAbortButton = false;
                        break;
                    case ProcessInstance.STATE_COMPLETED:
                        status = "Completed";
                        showAbortButton = false;
                        break;
                    case ProcessInstance.STATE_PENDING:
                        status = "Pending";
                        break;
                    case ProcessInstance.STATE_SUSPENDED:
                        status = "Suspended";
                        break;
                    default:
                        break;
                }
                
                view.getInstanceStateText().setText(status);
                view.getAbortButton().setVisible(showAbortButton);
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                return true;
            }
        }).getProcessInstanceById(instanceId);

        // Definition ID, Definition Name, Definition Version
        dataServices.call(new RemoteCallback<ProcessSummary>() {
            @Override
            public void callback(ProcessSummary process) {
                view.getDefinitionIdText().setText(process.getId());
                view.getDefinitionNameText().setText(process.getName());
                view.getDefinitionVersionText().setText(process.getVersion());
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                return true;
            }
        }).getProcessDesc(definitionId);

        // Current Activities
        dataServices.call(new RemoteCallback<List<NodeInstanceSummary>>() {
            @Override
            public void callback(List<NodeInstanceSummary> details) {
                StringBuilder casb = new StringBuilder();
                for (NodeInstanceSummary nis : details) {
                    casb.append(nis.getTimestamp());
                    casb.append(" : ");
                    casb.append(nis.getId());
                    casb.append(" - ");
                    casb.append(nis.getNodeName());
                    casb.append(" (");
                    casb.append(nis.getType());
                    casb.append(" ) \n");
                }
                view.getCurrentActivitiesText().setText(casb.toString());
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                return true;
            }
        }).getProcessInstanceActiveNodes(instanceId);

        // Instance Log
        dataServices.call(new RemoteCallback<List<NodeInstanceSummary>>() {
            @Override
            public void callback(List<NodeInstanceSummary> details) {
                StringBuilder ilsb = new StringBuilder();
                for (NodeInstanceSummary nis : details) {
                    ilsb.append(nis.getTimestamp());
                    ilsb.append(" : ");
                    ilsb.append(nis.getId());
                    ilsb.append(" - ");
                    if (!nis.getNodeName().equals("")) {
                        ilsb.append(nis.getNodeName());
                        ilsb.append(" (");
                        ilsb.append(nis.getType());
                        ilsb.append(") \n");
                    } else {
                        ilsb.append(nis.getType());
                        ilsb.append('\n');
                    }
                }
                view.getInstanceLogText().setText(ilsb.toString());
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                return true;
            }
        }).getProcessInstanceHistory(instanceId);
    }
    
    public void abortProcessInstance(long instanceId) {
        sessionServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void response) {
                view.goToInstancesList();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                return true;
            }
        }).abortProcessInstance(instanceId);
    }
    
}
