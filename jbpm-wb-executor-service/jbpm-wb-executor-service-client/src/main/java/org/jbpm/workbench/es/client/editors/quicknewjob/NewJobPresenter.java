/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.es.client.editors.quicknewjob;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;

import org.jbpm.workbench.es.client.i18n.Constants;
import org.jbpm.workbench.es.model.RequestDataSetConstants;
import org.jbpm.workbench.es.model.RequestParameterSummary;
import org.jbpm.workbench.es.model.events.RequestChangedEvent;
import org.jbpm.workbench.es.service.ExecutorService;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class NewJobPresenter {

    private final Constants constants = Constants.INSTANCE;

    private Caller<ExecutorService> executorServices;

    private Event<NotificationEvent> notification;

    private Event<RequestChangedEvent> requestCreatedEvent;

    private String serverTemplateId;

    @Inject
    protected NewJobView view;

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void openNewJobDialog(String serverTemplateId) {
        this.serverTemplateId = serverTemplateId;
        view.show();
    }

    public void createJob(String jobName,
                          Date dueDate,
                          String jobType,
                          String numberOfRetries,
                          List<RequestParameterSummary> parameters) {
        if (validateForm(jobName,
                         jobType,
                         numberOfRetries)) {
            Map<String, String> jobCtxMap = new HashMap<String, String>();
            if (parameters != null) {
                for (RequestParameterSummary param : parameters) {
                    jobCtxMap.put(param.getKey(),
                                  param.getValue());
                }
            }
            jobCtxMap.put(RequestDataSetConstants.COLUMN_RETRIES,
                          numberOfRetries); // TODO make legacy keys hard to repeat by accident
            jobCtxMap.put(RequestDataSetConstants.COLUMN_BUSINESSKEY,
                          jobName); // TODO make legacy keys hard to repeat by accident

            executorServices.call(
                    (Long requestId) -> {
                        displayNotification(constants.RequestScheduled(requestId));
                        requestCreatedEvent.fire(new RequestChangedEvent(requestId));
                        view.hide();
                    },
                    (Message message,
                     Throwable throwable) -> {
                        view.showBasicPane();
                        if (isInvalidCommandTypeError(throwable)) {
                            view.showInvalidTypeErrorMessage();
                        } else {
                            view.showInlineNotification(throwable.getCause().getMessage());
                        }
                        return false;
                    }).scheduleRequest(serverTemplateId,
                                       jobType,
                                       dueDate,
                                       jobCtxMap);
        } else {
            view.showBasicPane();
        }
    }

    protected boolean validateForm(String jobName,
                                   String jobType,
                                   String jobRetries) {
        boolean valid = true;
        view.cleanErrorMessages();
        if (jobName.isEmpty()) {
            view.showEmptyNameErrorMessage();
            valid = false;
        }

        if (jobType.isEmpty()) {
            view.showEmptyTypeErrorMessage();
            valid = false;
        }

        if (jobRetries.isEmpty()) {
            view.showEmptyRetriesErrorMessage();
            valid = false;
        }

        return valid;
    }

    private boolean isInvalidCommandTypeError(Throwable throwable) {
        String message = "";
        if (throwable.getMessage() != null) {
            message = throwable.getMessage();
        } else if (throwable.getCause() != null && throwable.getCause().getMessage() != null) {
            message = throwable.getCause().getMessage();
        }
        return message.contains("Invalid command type");
    }

    private void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @Inject
    public void setNotification(final Event<NotificationEvent> notification) {
        this.notification = notification;
    }

    @Inject
    public void setRequestChangedEvent(final Event<RequestChangedEvent> requestChangedEvent) {
        this.requestCreatedEvent = requestChangedEvent;
    }

    @Inject
    public void setExecutorService(final Caller<ExecutorService> executorService) {
        this.executorServices = executorService;
    }

    public interface NewJobView extends UberElement<NewJobPresenter> {

        void show();

        void hide();

        void showBasicPane();

        void cleanErrorMessages();

        void showEmptyNameErrorMessage();

        void showInvalidTypeErrorMessage();

        void showEmptyTypeErrorMessage();

        void showEmptyRetriesErrorMessage();

        void showInlineNotification(final String message);
    }
}