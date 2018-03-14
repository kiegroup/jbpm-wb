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
package org.jbpm.workbench.es.client.editors.errordetails;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.common.client.menu.RefreshMenuBuilder;
import org.jbpm.workbench.es.client.i18n.Constants;
import org.jbpm.workbench.es.model.ExecutionErrorSummary;
import org.jbpm.workbench.es.client.editors.events.ExecutionErrorSelectedEvent;
import org.jbpm.workbench.es.service.ExecutorService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

import static org.jbpm.workbench.common.client.PerspectiveIds.EXECUTION_ERROR_DETAILS_SCREEN;

@Dependent
@WorkbenchScreen(identifier = EXECUTION_ERROR_DETAILS_SCREEN)
public class ExecutionErrorDetailsPresenter implements RefreshMenuBuilder.SupportsRefresh {

    @Inject
    public ExecErrorDetailsView view;
    @Inject
    PlaceManager placeManager;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    private PlaceRequest place;

    private String serverTemplateId;
    private String deploymentId;
    private String errorId;

    @Inject
    private Caller<ExecutorService> executorServices;

    @WorkbenchPartView
    public UberElement<ExecutionErrorDetailsPresenter> getView() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.Details();
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    public static String getErrorDetailTitle(final ExecutionErrorSummary summary) {
        return summary.getProcessId() + " - " + summary.getProcessInstanceId() + " (" + summary.getDeploymentId() + ")";
    }

    public void onExecutionErrorSelectedEvent(@Observes ExecutionErrorSelectedEvent event) {
        this.serverTemplateId = event.getServerTemplateId();
        this.deploymentId = event.getDeploymentId();
        this.errorId = event.getErrorId();

        refreshExecutionErrorDataRemote(serverTemplateId,
                                        deploymentId,
                                        errorId);
    }

    @Override
    public void onRefresh() {
        refreshExecutionErrorDataRemote(serverTemplateId,
                                        deploymentId,
                                        errorId);
    }

    public void refreshExecutionErrorDataRemote(final String serverTemplateId,
                                                final String deploymentId,
                                                final String errorId) {
        executorServices.call(
                (ExecutionErrorSummary executionErrorSummary) -> {
                    view.setValue(executionErrorSummary);
                    changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(this.place,
                                                                           getErrorDetailTitle(executionErrorSummary)));
                })
                .getError(serverTemplateId,
                          deploymentId,
                          errorId);
    }

    @Inject
    public void setExecutorService(final Caller<ExecutorService> executorServices) {
        this.executorServices = executorServices;
    }

    @Inject
    public void setChangeTitleWidgetEvent(final Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent) {
        this.changeTitleWidgetEvent = changeTitleWidgetEvent;
    }

    public interface ExecErrorDetailsView extends UberElement<ExecutionErrorDetailsPresenter> {

        void setValue(ExecutionErrorSummary errorSummary);
    }
}