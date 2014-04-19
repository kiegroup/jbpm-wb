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
package org.jbpm.console.ng.mobile.pr.client.definition.details;

import com.google.inject.Inject;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.mobile.core.client.MGWTUberView;
import org.jbpm.console.ng.pr.model.ProcessSummary;

/**
 *
 * @author livthomas
 */
public class ProcessDefinitionDetailsPresenter {

    public interface ProcessDefinitionDetailsView extends MGWTUberView<ProcessDefinitionDetailsPresenter> {

        void refreshDetails(ProcessSummary process);

        void displayNotification(String title, String message);

    }

    @Inject
    private Caller<DataServiceEntryPoint> dataServices;

    @Inject
    Caller<KieSessionEntryPoint> sessionServices;

    @Inject
    private ProcessDefinitionDetailsView view;

    public ProcessDefinitionDetailsView getView() {
        return view;
    }

    public void refresh(String deploymentId, String processId) {
        dataServices.call(new RemoteCallback<ProcessSummary>() {
            @Override
            public void callback(ProcessSummary process) {
                view.refreshDetails(process);
            }
        }).getProcessById(deploymentId, processId);
    }

    public void startProcess(final String deploymentId, final String processId) {
        sessionServices.call(new RemoteCallback<Long>() {
            @Override
            public void callback(Long instanceId) {
                view.displayNotification("Success", "New process instance with id = " + instanceId + " was started!");
                refresh(deploymentId, processId);
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(Message message, Throwable throwable) {
                view.displayNotification("Unexpected error encountered", throwable.getMessage());
                return true;
            }
        }).startProcess(deploymentId, processId);
    }

}
