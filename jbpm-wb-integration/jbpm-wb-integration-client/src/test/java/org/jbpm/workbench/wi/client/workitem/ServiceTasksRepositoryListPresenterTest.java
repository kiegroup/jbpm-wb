/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.wi.client.workitem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jbpm.workbench.wi.workitems.model.ServiceTaskSummary;
import org.jbpm.workbench.wi.workitems.service.ServiceTaskService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.popups.alert.AlertPopupView;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.ConfirmPopup;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ServiceTasksRepositoryListPresenterTest {
    private static final String TEST_ST_NAME = "test1";
    private static final String TEST_ST_ID = "1";
    private CallerMock<ServiceTaskService> serviceTaskServiceCallerMock;

    @Mock
    private ServiceTaskService serviceTaskService;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent = new EventSourceMock<>();

    @Mock
    private ConfirmPopup confirmPopup;

    @Mock
    private ServiceTasksRepositoryListPresenter.ServiceTasksRepositoryListView view;

    @Mock
    private SyncBeanManager iocManager;

    @Mock
    private AlertPopupView cannotRemovePopup;

    private ServiceTasksRepositoryListPresenter presenter;

    List<ServiceTaskSummary> serviceTaskSummaries = new ArrayList<>();

    @Before
    public void init() {
        serviceTaskSummaries.add(initServiceTaskSummary(TEST_ST_NAME, TEST_ST_ID));
        serviceTaskSummaries.add(initServiceTaskSummary("test2", "2"));
        serviceTaskSummaries.add(initServiceTaskSummary("test3", "3"));
        serviceTaskServiceCallerMock = new CallerMock<>(serviceTaskService);
        presenter = spy(new ServiceTasksRepositoryListPresenter(notificationEvent, view, serviceTaskServiceCallerMock, iocManager));
        presenter.setConfirmPopup(confirmPopup);
        presenter.setCannotRemovePopup(cannotRemovePopup);
        when(serviceTaskService.getServiceTasks()).thenReturn(serviceTaskSummaries);
    }

    private ServiceTaskSummary initServiceTaskSummary(String name, String id) {
        ServiceTaskSummary summary = new ServiceTaskSummary();
        summary.setId(id);
        summary.setName(name);

        return summary;
    }

    @Test
    public void openRemoveServiceTaskConfirmPopupTestWhenCanNotRemove() {
        Set<String> installOn = new HashSet<>();
        installOn.add("branch1");
        installOn.add("branch2");
        ServiceTaskSummary st = initServiceTaskSummary(TEST_ST_NAME, TEST_ST_ID);
        st.setInstalledOn(installOn);

        presenter.openRemoveServiceTaskConfirmPopup(st);

        verify(cannotRemovePopup).alert(any(), any());
        verify(serviceTaskService, never()).removeServiceTask(any());
        assertEquals(3, serviceTaskSummaries.size());
    }

    @Test
    public void openRemoveServiceTaskConfirmPopupTestWhenCanRemove() {
        doAnswer(answer -> {
            serviceTaskSummaries.remove(0);
            return null;
        }).when(serviceTaskService).removeServiceTask(any());

        verifyConfirmPopup();

        assertEquals(2, serviceTaskSummaries.size());
        assertFalse(serviceTaskSummaries.stream().filter(s -> s.getId().equals(TEST_ST_ID)).findFirst().isPresent());
        verify(serviceTaskService).getServiceTasks();
        verify(view).setServiceTaskList(eq(serviceTaskSummaries));
    }

    @Test
    public void openRemoveServiceTaskConfirmPopupWhenCanRemoveWithNotification() {
        when(serviceTaskService.removeServiceTask(any())).thenReturn(TEST_ST_NAME);
        verifyConfirmPopup();

        verify(notificationEvent).fire(new NotificationEvent(presenter.getRemoveTaskSuccess(TEST_ST_NAME)));
    }

    private void verifyConfirmPopup() {
        Set<String> installOn = new HashSet<>();
        ServiceTaskSummary st = initServiceTaskSummary(TEST_ST_NAME, TEST_ST_ID);
        st.setInstalledOn(installOn);
        st.setEnabled(false);
        presenter.openRemoveServiceTaskConfirmPopup(st);

        ArgumentCaptor<Command> captureCommand = ArgumentCaptor.forClass(Command.class);
        verify(confirmPopup).show(any(), any(), any(), captureCommand.capture());
        captureCommand.getValue().execute();
        verify(serviceTaskService, times(1)).removeServiceTask(st);
    }
}
