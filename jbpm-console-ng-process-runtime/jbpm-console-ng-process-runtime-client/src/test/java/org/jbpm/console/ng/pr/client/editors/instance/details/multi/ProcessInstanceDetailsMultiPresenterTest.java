/*
 * Copyright 2016 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.pr.client.editors.instance.details.multi;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.console.ng.pr.client.editors.documents.list.ProcessDocumentListPresenter;
import org.jbpm.console.ng.pr.client.editors.instance.details.ProcessInstanceDetailsPresenter;
import org.jbpm.console.ng.pr.client.editors.instance.log.RuntimeLogPresenter;
import org.jbpm.console.ng.pr.client.editors.variables.list.ProcessVariableListPresenter;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceSelectionEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInstanceDetailsMultiPresenterTest {

    private static final Long PI_ID = 1L;
    private static final String PI_DEPLOYMENTID = "deploymentIdTest";
    private static final String PI_PROCESS_DEF_ID = "processDefIdTest";
    private static final String PI_PROCESS_DEF_NAME = "processDefNameTest";
    @Mock
    public ProcessInstanceDetailsMultiPresenter.ProcessInstanceDetailsMultiView view;


    @Mock
    private ProcessInstanceDetailsPresenter detailsPresenter;

    @Mock
    private ProcessVariableListPresenter variableListPresenter;

    @Mock
    private ProcessDocumentListPresenter documentListPresenter;

    @Mock
    private RuntimeLogPresenter runtimeLogPresenter;

    @Spy
    Event<ProcessInstanceSelectionEvent> processInstanceSelected = new EventSourceMock<ProcessInstanceSelectionEvent>();

    @Spy
    Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent = new EventSourceMock<ChangeTitleWidgetEvent>();

    @InjectMocks
    private ProcessInstanceDetailsMultiPresenter presenter;

    @Before
    public void setupMocks() {
        doNothing().when(changeTitleWidgetEvent).fire(any(ChangeTitleWidgetEvent.class));
        doNothing().when(processInstanceSelected).fire(any(ProcessInstanceSelectionEvent.class));
    }

    @Test
    public void isForLogRemainsEnabledAfterRefresh() {
        //When task selected with logOnly
        presenter.onProcessSelectionEvent(new ProcessInstanceSelectionEvent(PI_DEPLOYMENTID, PI_ID, PI_PROCESS_DEF_ID,
                PI_PROCESS_DEF_NAME, 0, true));
        //Then only tab log is displayed
        verify(view).displayOnlyLogTab();
        assertTrue(presenter.isForLog());

        presenter.onRefresh();
        assertTrue(presenter.isForLog());
    }

    @Test
    public void isForLogRemainsDisabledAfterRefresh() {
        //When task selected without logOnly
        presenter.onProcessSelectionEvent(new ProcessInstanceSelectionEvent(PI_DEPLOYMENTID, PI_ID, PI_PROCESS_DEF_ID,
                PI_PROCESS_DEF_NAME, 0, false));

        //Then alltabs are displayed
        verify(view).displayAllTabs();
        assertFalse(presenter.isForLog());

        presenter.onRefresh();
        assertFalse(presenter.isForLog());
    }

}
