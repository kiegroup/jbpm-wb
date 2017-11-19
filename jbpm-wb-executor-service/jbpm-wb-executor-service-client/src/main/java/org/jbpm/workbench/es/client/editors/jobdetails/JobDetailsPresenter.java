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
package org.jbpm.workbench.es.client.editors.jobdetails;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.es.client.editors.events.JobSelectedEvent;
import org.jbpm.workbench.es.client.i18n.Constants;
import org.jbpm.workbench.es.model.ErrorSummary;
import org.jbpm.workbench.es.model.RequestDetails;
import org.jbpm.workbench.es.model.RequestParameterSummary;
import org.jbpm.workbench.es.model.RequestSummary;
import org.jbpm.workbench.es.service.ExecutorService;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.ext.widgets.common.client.menu.RefreshMenuBuilder;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

@Dependent
@WorkbenchScreen(identifier = JobDetailsPresenter.SCREEN_ID, preferredWidth = 655)
public class JobDetailsPresenter implements RefreshMenuBuilder.SupportsRefresh {

    public static final String SCREEN_ID = "Job Details";

    @Inject
    public JobDetailsView view;

    @Inject
    PlaceManager placeManager;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    private PlaceRequest place;
    private String serverTemplateId;
    private String deploymentId;
    private Long jobId;

    @Inject
    private Caller<ExecutorService> executorServices;

    @WorkbenchPartView
    public UberElement<JobDetailsPresenter> getView() {
        return view;
    }

    @DefaultPosition
    public Position getPosition() {
        return CompassPosition.EAST;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.Job_Details();
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    public void onJobSelectedEvent(@Observes JobSelectedEvent event) {
        this.serverTemplateId = event.getServerTemplateId();
        this.deploymentId = event.getDeploymentId();
        this.jobId = event.getJobId();

        refreshJobDetailsDataRemote(serverTemplateId,
                                    deploymentId,
                                    jobId);
    }

    @Override
    public void onRefresh() {
        refreshJobDetailsDataRemote(serverTemplateId,
                                    deploymentId,
                                    jobId);
    }

    public void refreshJobDetailsDataRemote(final String serverTemplateId,
                                            final String deploymentId,
                                            final Long jobId) {
        executorServices.call(
                (RequestDetails requestDetails) -> {
                    if (requestDetails != null) {
                        view.setBasicDetails(requestDetails.getRequest());
                        view.setParameters(requestDetails.getParams());
                        List<ErrorSummary> errors = requestDetails.getErrors();
                        if (errors != null && errors.size() > 0) {
                            view.setErrors(errors);
                        }
                        changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(this.place,
                                                                               getJobDetailTitle(requestDetails.getRequest())));
                    }
                })
                .getRequestDetails(serverTemplateId,
                                   deploymentId,
                                   jobId);
    }

    protected String getJobDetailTitle(RequestSummary summary) {
        String title = summary.getId().toString() ;

        if (summary.getKey() != null) {
            title += " - " + summary.getKey();
        }

        return title;
    }

    @Inject
    public void setExecutorService(final Caller<ExecutorService> executorServices) {
        this.executorServices = executorServices;
    }

    @Inject
    public void setChangeTitleWidgetEvent(final Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent) {
        this.changeTitleWidgetEvent = changeTitleWidgetEvent;
    }

    public interface JobDetailsView extends UberElement<JobDetailsPresenter> {

        void setBasicDetails(RequestSummary requestSummary);

        void setParameters(List<RequestParameterSummary> requestParameterSummaries);

        void setErrors(List<ErrorSummary> errors);
    }
}