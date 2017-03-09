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

package org.jbpm.workbench.wi.client.handlers;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.wi.casemgmt.service.CaseProjectService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class NewCaseProjectHandlerTest {

    @Mock
    private CaseProjectService caseProjectService;
    private Caller<CaseProjectService> caseProjectServiceCaller;

    @Mock
    private EventSourceMock<NotificationEvent> notification;

    private NewCaseProjectHandler newCaseProjectHandler;

    @Before
    public void setup() {
        caseProjectServiceCaller = new CallerMock<>(caseProjectService);

        newCaseProjectHandler = new NewCaseProjectHandler();
        newCaseProjectHandler.setCaseProjectService(caseProjectServiceCaller);
        newCaseProjectHandler.setNotification(notification);
    }

    @Test
    public void configureCaseProjectCallbackTest() {
        final Project project = mock(Project.class);
        final Callback<Project> creationSuccessCallback = mock(Callback.class);

        newCaseProjectHandler.setCreationSuccessCallback(creationSuccessCallback);
        newCaseProjectHandler.configureCaseProjectCallback.callback(project);

        verify(creationSuccessCallback).callback(project);
    }
}
