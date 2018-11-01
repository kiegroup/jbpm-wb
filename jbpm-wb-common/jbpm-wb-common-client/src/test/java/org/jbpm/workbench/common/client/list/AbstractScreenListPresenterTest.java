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

package org.jbpm.workbench.common.client.list;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.jbpm.workbench.common.client.menu.ServerTemplateSelectorMenuBuilder;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.Commands;
import org.uberfire.mvp.PlaceRequest;

import static java.util.Collections.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractScreenListPresenterTest {

    private String PERSPECTIVE_ID = "perspectiveId";

    @Spy
    AbstractScreenListPresenter presenter;

    @Mock
    UberfireBreadcrumbs breadcrumbsMock;

    @Mock
    PerspectiveManager perspectiveManagerMock;

    @Mock
    PerspectiveActivity perspectiveActivityMock;

    @Mock
    ListView listView;

    @Mock
    ServerTemplateSelectorMenuBuilder serverTemplateSelectorMenuBuilderMock;

    @Mock
    ServerTemplateSelectorMenuBuilder.ServerTemplateSelectorElementView serverTemplateSelectorElementViewMock;

    @Before
    public void setup() {
        when(perspectiveActivityMock.getIdentifier()).thenReturn(PERSPECTIVE_ID);
        when(perspectiveManagerMock.getCurrentPerspective()).thenReturn(perspectiveActivityMock);
        when(serverTemplateSelectorMenuBuilderMock.getView()).thenReturn(serverTemplateSelectorElementViewMock);
        when(presenter.getListView()).thenReturn(listView);

        presenter.setPerspectiveManager(perspectiveManagerMock);
        presenter.setUberfireBreadcrumbs(breadcrumbsMock);
    }

    @Test
    public void testServerTemplateRefresh() {
        doNothing().when(presenter).refreshGrid();

        final ServerTemplate serverTemplate = new ServerTemplate("testId",
                                                                 null,
                                                                 singletonList(Capability.PROCESS.name()),
                                                                 emptyMap(),
                                                                 emptyList());
        presenter.setSelectedServerTemplate(serverTemplate);

        assertEquals("testId",
                     presenter.getSelectedServerTemplate());
        verify(presenter,
               times(1)).refreshGrid();

        presenter.setSelectedServerTemplate(serverTemplate);

        assertEquals("testId",
                     presenter.getSelectedServerTemplate());
        verify(presenter,
               times(1)).refreshGrid();

        verify(listView,
               times(2)).clearBlockingError();
        verify(listView,
               never()).displayBlockingError(any(),
                                             any());
    }

    @Test
    public void testNoServerTemplate() {
        presenter.setSelectedServerTemplate(null);

        assertEquals("",
                     presenter.getSelectedServerTemplate());

        verify(presenter,
               never()).refreshGrid();

        verify(listView).clearBlockingError();
        verify(listView).displayBlockingError(Constants.INSTANCE.ExecutionServerUnavailable(),
                                              Constants.INSTANCE.NoServerConnected());
    }

    @Test
    public void testServerTemplateWithoutProcessCapability() {
        final ServerTemplate serverTemplate = new ServerTemplate("testId",
                                                                 null,
                                                                 emptyList(),
                                                                 emptyMap(),
                                                                 emptyList());
        presenter.setSelectedServerTemplate(serverTemplate);

        assertEquals("",
                     presenter.getSelectedServerTemplate());

        verify(presenter,
               never()).refreshGrid();

        verify(listView).clearBlockingError();
        verify(listView).displayBlockingError(Constants.INSTANCE.MissingServerCapability(),
                                              Constants.INSTANCE.MissingProcessCapability());
    }

    @Test
    public void testListBreadCrumb() {
        String listLabel = "listLabel";
        PlaceManager placeManagerMock = mock(PlaceManager.class);

        presenter.setupListBreadcrumb(placeManagerMock,
                                      listLabel);

        ArgumentCaptor<Command> captureCommand = ArgumentCaptor.forClass(Command.class);
        verify(breadcrumbsMock).clearBreadcrumbs(PERSPECTIVE_ID);
        verify(breadcrumbsMock).addBreadCrumb(eq(PERSPECTIVE_ID),
                                              eq(Constants.INSTANCE.Home()),
                                              captureCommand.capture());

        captureCommand.getValue().execute();
        verify(placeManagerMock).goTo(PerspectiveIds.HOME);

        verify(breadcrumbsMock).addBreadCrumb(eq(PERSPECTIVE_ID),
                                              eq(listLabel),
                                              eq(Commands.DO_NOTHING));

        verifyNoMoreInteractions(breadcrumbsMock);
    }

    @Test
    public void testSetupDetailBreadcrumb() {
        String listLabel = "listLabel";
        String detailLabel = "detailLabel";
        String detailScreenId = "screenId";

        PlaceManager placeManagerMock = mock(PlaceManager.class);
        presenter.setPlaceManager(placeManagerMock);
        presenter.setupDetailBreadcrumb(placeManagerMock,
                                        listLabel,
                                        detailLabel,
                                        detailScreenId);

        ArgumentCaptor<Command> captureCommand = ArgumentCaptor.forClass(Command.class);

        verify(breadcrumbsMock).clearBreadcrumbs(PERSPECTIVE_ID);
        verify(breadcrumbsMock).addBreadCrumb(eq(PERSPECTIVE_ID),
                                              eq(Constants.INSTANCE.Home()),
                                              captureCommand.capture());
        captureCommand.getValue().execute();
        verify(placeManagerMock).goTo(PerspectiveIds.HOME);

        verify(breadcrumbsMock).addBreadCrumb(eq(PERSPECTIVE_ID),
                                              eq(listLabel),
                                              captureCommand.capture());

        captureCommand.getValue().execute();
        verify(placeManagerMock).closePlace(detailScreenId);

        verify(breadcrumbsMock).addBreadCrumb(eq(PERSPECTIVE_ID),
                                              eq(detailLabel),
                                              eq(Commands.DO_NOTHING));
        verifyNoMoreInteractions(breadcrumbsMock);
    }

    @Test
    public void testServerTemplateSelectorAddition() {
        presenter.setServerTemplateSelectorMenuBuilder(serverTemplateSelectorMenuBuilderMock);

        presenter.onStartup(mock(PlaceRequest.class));
        verify(breadcrumbsMock).addToolbar(PERSPECTIVE_ID,
                                           serverTemplateSelectorMenuBuilderMock.getView().getElement());
    }
}
