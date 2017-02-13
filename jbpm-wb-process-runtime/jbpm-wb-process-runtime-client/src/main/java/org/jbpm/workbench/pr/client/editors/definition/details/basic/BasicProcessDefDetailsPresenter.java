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

package org.jbpm.workbench.pr.client.editors.definition.details.basic;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.workbench.pr.model.ProcessDefinitionKey;
import org.jbpm.workbench.pr.model.ProcessSummary;
import org.jbpm.workbench.pr.client.editors.definition.details.BaseProcessDefDetailsPresenter;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;

@Dependent
public class BasicProcessDefDetailsPresenter extends BaseProcessDefDetailsPresenter {

    public interface BasicProcessDefDetailsView extends
            BaseProcessDefDetailsPresenter.BaseProcessDefDetailsView {

    }

    @Inject
    private BasicProcessDefDetailsView view;

    @Inject
    private Caller<ProcessRuntimeDataService> processRuntimeDataService;

    @Override
    public IsWidget getWidget() {
        return view;
    }

    @Override
    protected void refreshView(  final String serverTemplateId, String currentProcessDefId, String currentDeploymentId ) {
        view.getProcessIdText().setText( currentProcessDefId );
        view.getDeploymentIdText().setText( currentDeploymentId );
    }

    private void refreshProcessItems( final String serverTemplateId, final String deploymentId, final String processId ) {

        processRuntimeDataService.call(new RemoteCallback<ProcessSummary>() {

            @Override
            public void callback( ProcessSummary process ) {
                if (process != null) {
                    view.getProcessNameText().setText( process.getName() );

                    changeStyleRow( process.getName(), process.getVersion() );
                } else {
                    // set to empty to ensure it's clear state
                    view.getProcessNameText().setText( "" );
                }
            }
        } ).getProcess(serverTemplateId, new ProcessDefinitionKey(serverTemplateId, deploymentId, processId));

    }

    @Override
    protected void refreshProcessDef(  final String serverTemplateId, String deploymentId, String processId ) {
        refreshProcessItems( serverTemplateId, deploymentId, processId );
    }
}
