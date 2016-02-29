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
package org.jbpm.console.ng.ht.client.editors.taskdetails;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jbpm.console.ng.gc.client.util.UTCDateBox;
import org.jbpm.console.ng.gc.client.util.UTCTimeBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TaskDetailsPresenterTest {

    @Mock
    private TaskDetailsViewImpl view;


    @InjectMocks
    private TaskDetailsPresenter presenter;


    @Test
    public void disableTaskDetailEditionTest() {
        presenter.setReadOnlyTaskDetail();

        verify(view).setTaskDescriptionEnabled(false);
        verify(view).setDueDateEnabled(false);
        verify(view).setUserEnabled(false);
        verify(view).setTaskStatusEnabled(false);
        verify(view).setDueDateTimeEnabled(false);
        verify(view).setTaskPriorityEnabled(false);
        verify(view).setUpdateTaskVisible(false);

    }

}
