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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TaskDetailsViewImplTest {

    @Mock
    private TaskDetailsPresenter presenter;

    @Mock
    public TextBox userText;

    @Mock
    public TextBox taskStatusText;

    @Mock
    public TextArea taskDescriptionTextArea;

    @Mock
    public Select taskPriorityListBox;

    @Mock
    public UTCDateBox dueDate;

    @Mock
    public UTCTimeBox dueDateTime;

    @Mock
    public Button updateTaskButton;


    @InjectMocks
    private TaskDetailsViewImpl view;


    @Test
    public void disableFieldsTest() {

        view.setUserEnabled(false);
        verify(userText).setEnabled(false);

        view.setDueDateEnabled(false);
        verify(dueDate).setEnabled(false);

        view.setTaskDescriptionEnabled(false);
        verify(taskDescriptionTextArea).setEnabled(false);

        view.setDueDateTimeEnabled(false);
        verify(dueDateTime).setEnabled(false);

        view.setTaskPriorityEnabled(false);
        verify(taskPriorityListBox).setEnabled(false);

        view.setTaskStatusEnabled(false);
        verify(taskStatusText).setEnabled(false);

        view.setUpdateTaskVisible(false);
        verify(updateTaskButton).setVisible(false);

    }

}
