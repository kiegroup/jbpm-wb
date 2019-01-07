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

import java.util.Optional;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.workbench.wi.casemgmt.service.CaseProjectService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.screens.project.AddProjectPopUpPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPermissions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mocks.MockInstanceImpl;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class NewCaseProjectHandlerTest {

    @Mock
    CaseProjectService caseProjectService;

    Caller<CaseProjectService> caseProjectServiceCaller;

    @Spy
    Event<NotificationEvent> notification = new EventSourceMock<>();

    @Mock
    AddProjectPopUpPresenter addProjectPopUpPresenter;

    @Mock
    LibraryPermissions libraryPermissions;

    @Mock
    WorkspaceProjectContext context;

    Instance<AddProjectPopUpPresenter> addProjectPopUpPresenterProvider;

    @InjectMocks
    NewCaseProjectHandler newCaseProjectHandler;

    @Before
    public void setup() {
        caseProjectServiceCaller = new CallerMock<>(caseProjectService);
        addProjectPopUpPresenterProvider = spy(new MockInstanceImpl<>(addProjectPopUpPresenter));

        newCaseProjectHandler.setAddProjectPopUpPresenterProvider(addProjectPopUpPresenterProvider);
        newCaseProjectHandler.setCaseProjectService(caseProjectServiceCaller);
        newCaseProjectHandler.setNotification(notification);
        doNothing().when(notification).fire(any());

    }

    @Test
    public void testNewCaseProject() {
        final WorkspaceProject project = mock(WorkspaceProject.class);
        final ParameterizedCommand<WorkspaceProject> creationSuccessCallback = mock(ParameterizedCommand.class);
        when(addProjectPopUpPresenter.getProjectCreationSuccessCallback()).thenReturn(creationSuccessCallback);
        doAnswer(invocation -> {
            ((ParameterizedCommand<WorkspaceProject>) invocation.getArguments()[0]).execute(project);
            return null;
        }).when(addProjectPopUpPresenter).setSuccessCallback(any());

        final Callback successCallback = mock(Callback.class);
        newCaseProjectHandler.setCreationSuccessCallback(successCallback);

        newCaseProjectHandler.init();

        verify(addProjectPopUpPresenter).setSuccessCallback(any());
        verify(addProjectPopUpPresenter).show();

        verify(caseProjectService).configureNewCaseProject(project);

        verify(successCallback).callback(project);
        verify(creationSuccessCallback).execute(project);

        verify(notification).fire(any());

        verify(addProjectPopUpPresenterProvider).destroy(addProjectPopUpPresenter);
    }

    @Test
    public void testCanCreateCaseProjectWhenUserHasPermissionOnCurrentOU() {
        final OrganizationalUnit currentOU = mock(OrganizationalUnit.class);
        doReturn(Optional.of(currentOU)).when(context).getActiveOrganizationalUnit();
        doReturn(true).when(libraryPermissions).userCanCreateProject(same(currentOU));

        assertTrue(newCaseProjectHandler.canCreate());
    }

    @Test
    public void testCanNotCreateCaseProjectWhenUserHasNoPermissionOnCurrentOU() {
        final OrganizationalUnit currentOU = mock(OrganizationalUnit.class);
        doReturn(Optional.of(currentOU)).when(context).getActiveOrganizationalUnit();
        doReturn(false).when(libraryPermissions).userCanCreateProject(same(currentOU));

        assertFalse(newCaseProjectHandler.canCreate());
    }
}
