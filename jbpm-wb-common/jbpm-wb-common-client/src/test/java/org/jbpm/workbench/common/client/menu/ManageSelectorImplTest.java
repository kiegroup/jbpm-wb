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

package org.jbpm.workbench.common.client.menu;

import com.google.gwt.dev.util.collect.Sets;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ManageSelectorImplTest {

    private String selected_Perspective = PerspectiveIds.PROCESS_DEFINITIONS;

    @InjectMocks
    ManageSelectorImpl manageSelector;

    @Mock
    ManageSelectorImpl.ManageSelectorView view;

    @Mock
    PerspectiveManager perspectiveManagerMock;

    @Mock
    PerspectiveActivity perspectiveActivityMock;

    @Mock
    private PlaceManager placeManagerMock;

    @Mock
    private ActivityManager activityManagerMock;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private User identity;

    @Before
    public void setup() {
        when(perspectiveActivityMock.getIdentifier()).thenReturn(selected_Perspective);
        when(perspectiveManagerMock.getCurrentPerspective()).thenReturn(perspectiveActivityMock);
        when(view.getSelectedOption()).thenReturn(selected_Perspective);
        when(activityManagerMock.getActivities(any(DefaultPlaceRequest.class))).thenReturn(Sets.create(mock(Activity.class)));
    }

    @Test
    public void testInitManageSelector_AllPerspectivesGranted() {
        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(identity))).thenReturn(true);
        manageSelector.init();
        verify(view).removeAllOptions();
        verifyInitSelectorBasicViewInteractions();
        verify(view).addOption(Constants.INSTANCE.Process_Definitions(),
                               PerspectiveIds.PROCESS_DEFINITIONS,
                               true);
        verify(view).addOption(Constants.INSTANCE.Process_Instances(),
                               PerspectiveIds.PROCESS_INSTANCES,
                               false);
        verify(view).addOption(Constants.INSTANCE.Tasks(),
                               PerspectiveIds.TASKS_ADMIN,
                               false);
        verify(view).addOption(Constants.INSTANCE.ExecutionErrors(),
                               PerspectiveIds.EXECUTION_ERRORS,
                               false);
        verify(view).addOption(Constants.INSTANCE.Jobs(),
                               PerspectiveIds.JOBS,
                               false);
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testInitManageSelector_NotAllPerspectivesGranted() {
        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(identity))).thenReturn(true,
                                                                      true,
                                                                      false,
                                                                      false,
                                                                      false);
        manageSelector.init();
        verify(view).removeAllOptions();
        verifyInitSelectorBasicViewInteractions();
        verify(view,
               times(2)).addOption(anyString(),
                                   anyString(),
                                   anyBoolean());
        verifyNoMoreInteractions(view);
    }

    private void verifyInitSelectorBasicViewInteractions() {

        ArgumentCaptor<Command> captureCommand = ArgumentCaptor.forClass(Command.class);
        verify(view).setOptionChangeCommand(captureCommand.capture());
        captureCommand.getValue().execute();
        verify(view).getSelectedOption();
        ArgumentCaptor<DefaultPlaceRequest> captureDefaultPlaceRequest = ArgumentCaptor.forClass(DefaultPlaceRequest.class);
        verify(placeManagerMock).goTo(captureDefaultPlaceRequest.capture());
        assertEquals(selected_Perspective,
                     captureDefaultPlaceRequest.getValue().getIdentifier());

        verify(view).refresh();
    }
}