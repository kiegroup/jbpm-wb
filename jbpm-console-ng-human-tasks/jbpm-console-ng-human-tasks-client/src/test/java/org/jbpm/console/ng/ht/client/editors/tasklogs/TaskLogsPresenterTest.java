/*
 * Copyright 2015 JBoss by Red Hat.
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
package org.jbpm.console.ng.ht.client.editors.tasklogs;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.ht.model.events.TaskRefreshedEvent;
import org.jbpm.console.ng.ht.model.events.TaskSelectionEvent;
import org.jbpm.console.ng.ht.service.TaskAuditService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.paging.PageResponse;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class TaskLogsPresenterTest {

    private static final Long TASK_ID = 1L;

    @Mock
    private TaskAuditService taskAuditServiceMock;

    private Caller<TaskAuditService> taskAuditService;

    @Mock
    private TaskLogsPresenter.TaskLogsView taskLogsView;

    private TaskLogsPresenter presenter;

    @Before
    public void setupMocks() {
        taskAuditService = new CallerMock<TaskAuditService>( taskAuditServiceMock );
        presenter = new TaskLogsPresenter( taskLogsView, taskAuditService );
        when( taskAuditServiceMock.getData( any( QueryFilter.class ) ) ).thenReturn( mock( PageResponse.class ) );
    }

    @Test
    public void logsUpdatedWhenTaskSelected() {
        //When task selected
        presenter.onTaskSelectionEvent( new TaskSelectionEvent( TASK_ID ) );

        //Logs retrieved and text area refreshed
        verify( taskAuditServiceMock ).getData( any( QueryFilter.class ) );
        verify( taskLogsView ).setLogTextAreaText( "" );
    }

    @Test
    public void logsUpdatedWhenTaskRefreshed() {
        //When task selected
        presenter.onTaskSelectionEvent( new TaskSelectionEvent( TASK_ID ) );

        //When task refreshed
        presenter.onTaskRefreshedEvent( new TaskRefreshedEvent( TASK_ID ) );

        //Logs retrieved and text area refreshed
        verify( taskAuditServiceMock, times( 2 ) ).getData( any( QueryFilter.class ) );
        verify( taskLogsView, times( 2 ) ).setLogTextAreaText( "" );
    }

    @Test
    public void logsNotUpdatedWhenDifferentTaskRefreshed() {
        //When task selected
        presenter.onTaskSelectionEvent( new TaskSelectionEvent( TASK_ID ) );

        //When task refreshed
        presenter.onTaskRefreshedEvent( new TaskRefreshedEvent( TASK_ID + 1 ) );

        //Logs retrieved and text area refreshed
        verify( taskAuditServiceMock ).getData( any( QueryFilter.class ) );
        verify( taskLogsView ).setLogTextAreaText( "" );
    }

}
