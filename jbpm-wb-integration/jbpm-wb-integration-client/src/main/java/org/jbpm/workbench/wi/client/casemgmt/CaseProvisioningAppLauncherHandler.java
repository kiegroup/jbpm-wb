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

package org.jbpm.workbench.wi.client.casemgmt;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.wi.casemgmt.events.CaseProvisioningCompletedEvent;
import org.jbpm.workbench.wi.casemgmt.events.CaseProvisioningFailedEvent;
import org.jbpm.workbench.wi.casemgmt.events.CaseProvisioningStartedEvent;
import org.jbpm.workbench.wi.casemgmt.service.CaseProvisioningService;
import org.jbpm.workbench.wi.client.i18n.Constants;
import org.kie.workbench.common.widgets.client.popups.launcher.events.AppLauncherAddEvent;
import org.uberfire.workbench.events.NotificationEvent;

import static org.jbpm.workbench.wi.casemgmt.service.CaseProvisioningStatus.COMPLETED;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.ERROR;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.SUCCESS;

@ApplicationScoped
public class CaseProvisioningAppLauncherHandler {

    private final Constants constants = Constants.INSTANCE;

    @Inject
    private Event<AppLauncherAddEvent> appLauncherAddEvent;

    @Inject
    private Event<NotificationEvent> notification;

    private Caller<CaseProvisioningService> service;

    public void verifyCaseAppStatus() {
        service.call(s -> {
            if (s == COMPLETED) {
                service.call((String ctx) -> addCaseAppLauncher(ctx)).getApplicationContext();
            }
        }).getProvisioningStatus();
    }

    public void onCaseManagementProvisioningStartedEvent(@Observes CaseProvisioningStartedEvent event) {
        notification.fire(new NotificationEvent(constants.CaseAppProvisioningStarted()));
    }

    public void onCaseManagementProvisioningCompletedEvent(@Observes CaseProvisioningCompletedEvent event) {
        notification.fire(new NotificationEvent(constants.CaseAppProvisioningCompleted(), SUCCESS));
        addCaseAppLauncher(event.getAppContext());
    }

    protected void addCaseAppLauncher(final String caseAppContext) {
        appLauncherAddEvent.fire(new AppLauncherAddEvent(constants.CaseAppName(), caseAppContext, null));
    }

    public void onCaseManagementProvisioningFailedEvent(@Observes CaseProvisioningFailedEvent event) {
        notification.fire(new NotificationEvent(constants.CaseAppProvisioningFailed(), ERROR));
    }

    @Inject
    public void setCaseProvisioningService(final Caller<CaseProvisioningService> service) {
        this.service = service;
    }

}