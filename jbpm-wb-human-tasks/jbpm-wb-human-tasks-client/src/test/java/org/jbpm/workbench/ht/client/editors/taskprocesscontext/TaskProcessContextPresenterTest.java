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
package org.jbpm.workbench.ht.client.editors.taskprocesscontext;

import java.util.Collections;
import java.util.Date;

import com.google.common.collect.Sets;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskProcessContextPresenterTest {

    private static final String PROCESS_ID = "ProcessId";
    private static final Long PROCESS_INSTANCE_ID = 1L;

    @Mock
    public User identity;

    @Mock
    private TaskProcessContextPresenter.TaskProcessContextView viewMock;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private ActivityManager activityManager;

    @Mock
    private AuthorizationManager authorizationManager;

    private TaskProcessContextPresenter presenter;

    @Before
    public void before() {
        presenter = new TaskProcessContextPresenter(
                viewMock,
                placeManager,
                activityManager,
                authorizationManager,
                identity);
    }
    @Test
    public void testSetProcessContextData() {

        presenter.setProcessContextData(PROCESS_INSTANCE_ID,
                                        PROCESS_ID);

        verify(viewMock).setProcessId(PROCESS_ID);
        verify(viewMock).setProcessInstanceId(PROCESS_INSTANCE_ID.toString());
        verify(viewMock).enablePIDetailsButton(true);

        presenter.setProcessContextData(null,
                                        null);

        verify(viewMock).setProcessId("None");
        verify(viewMock).setProcessInstanceId("None");
        verify(viewMock).enablePIDetailsButton(false);
    }

    @Test
    public void processContex_whenTaskSelected() {
        TaskSelectionEvent testTaskSelectionEvent =
                new TaskSelectionEvent("serverTemplateId",
                                       "containerId",
                                       Long.valueOf(1),
                                       "taskName",
                                       true,
                                       true,
                                       "description",
                                       new Date(),
                                       "Ready",
                                       "actualOwner",
                                       3,
                                       PROCESS_INSTANCE_ID,
                                       PROCESS_ID);
        presenter.onTaskSelectionEvent(testTaskSelectionEvent);

        verify(viewMock).setProcessId(PROCESS_ID);
        verify(viewMock).setProcessInstanceId(PROCESS_INSTANCE_ID.toString());
        verify(viewMock).enablePIDetailsButton(true);
    }

    @Test
    public void processContex_whenTaskSelectedEmptyProcessInstanceId() {
        TaskSelectionEvent testTaskSelectionEvent =
                new TaskSelectionEvent("serverTemplateId",
                                       "containerId",
                                       Long.valueOf(1),
                                       "taskName",
                                       true,
                                       true,
                                       "description",
                                       new Date(),
                                       "Ready",
                                       "actualOwner",
                                       3,
                                       null,
                                       null);
        presenter.onTaskSelectionEvent(testTaskSelectionEvent);

        verify(viewMock).setProcessId("None");
        verify(viewMock).setProcessInstanceId("None");
        verify(viewMock).enablePIDetailsButton(false);
    }


    @Test
    public void testGoToProcessInstanceDetails() {
        TaskSelectionEvent testTaskSelectionEvent =
                new TaskSelectionEvent("serverTemplateId",
                                       "containerId",
                                       Long.valueOf(1),
                                       "taskName",
                                       true,
                                       true,
                                       "description",
                                       new Date(),
                                       "Ready",
                                       "actualOwner",
                                       3,
                                       PROCESS_INSTANCE_ID,
                                       PROCESS_ID);

        presenter.onTaskSelectionEvent(testTaskSelectionEvent);
        presenter.goToProcessInstanceDetails();

        final ArgumentCaptor<PlaceRequest> captor = ArgumentCaptor.forClass(PlaceRequest.class);
        verify(placeManager).goTo(captor.capture());
        assertEquals(1,
                     captor.getAllValues().size());

        assertEquals(PerspectiveIds.PROCESS_INSTANCES,
                     captor.getAllValues().get(0).getIdentifier());
        assertEquals(PROCESS_INSTANCE_ID.toString(),
                     captor.getAllValues().get(0).getParameter(PerspectiveIds.SEARCH_PARAMETER_PROCESS_INSTANCE_ID,
                                                               ""));
    }

    @Test
    public void testProcessContextEnabled() {
        when(authorizationManager.authorize(any(Resource.class),
                                            eq(identity))).thenReturn(true);
        when(activityManager.getActivities(any(PlaceRequest.class))).thenReturn(Sets.newHashSet(mock(Activity.class)));

        presenter.init();

        verify(viewMock).enablePIDetailsButton(true);
    }

    @Test
    public void testProcessContextDisabled() {
        when(authorizationManager.authorize(any(Resource.class),
                                            eq(identity))).thenReturn(true);
        when(activityManager.getActivities(any(PlaceRequest.class))).thenReturn(Collections.<Activity>emptySet());

        presenter.init();

        verify(viewMock).enablePIDetailsButton(false);
    }

    @Test
    public void testProcessContextDisabledWhenUserHasNoPermission() {
        when(authorizationManager.authorize(any(Resource.class),
                                            eq(identity))).thenReturn(false);

        presenter.init();

        verify(viewMock).enablePIDetailsButton(false);
    }
}