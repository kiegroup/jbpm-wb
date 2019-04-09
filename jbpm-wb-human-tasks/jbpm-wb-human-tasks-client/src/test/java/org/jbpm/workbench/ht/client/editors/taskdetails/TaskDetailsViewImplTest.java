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
package org.jbpm.workbench.ht.client.editors.taskdetails;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.process.ProcessInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TaskDetailsViewImplTest {

    @Mock
    public TextArea taskDescriptionTextArea;

    @Mock
    public Select taskPriorityListBox;

    @Mock
    public FlowPanel dateRangePickerInput;

    @Mock(name = "taskStatusText")
    public Paragraph taskStatusText;

    @Mock(name = "processInstanceIdText")
    public Paragraph processInstanceIdText;

    @Mock(name = "processIdText")
    public Paragraph processIdText;

    @Mock(name = "dueDateText")
    public Paragraph dueDateText;

    @Mock(name = "slaComplianceText")
    public Paragraph slaComplianceText;

    @Mock
    public Button updateTaskButton;

    @InjectMocks
    private TaskDetailsViewImpl view;

    @Test
    public void disableFieldsTest() {
        view.setDueDateEnabled(false);
        verify(dateRangePickerInput).setVisible(false);
        verify(dueDateText).setVisible(true);

        view.setTaskDescriptionEnabled(false);
        verify(taskDescriptionTextArea).setEnabled(false);

        view.setTaskPriorityEnabled(false);
        verify(taskPriorityListBox).setEnabled(false);

        view.setUpdateTaskVisible(false);
        verify(updateTaskButton).setVisible(false);
    }

    @Test
    public void setSlaComplianceTextTest() {
        view.setSlaCompliance(ProcessInstance.SLA_PENDING);
        verify(slaComplianceText).setText(Constants.INSTANCE.SlaPending());
    }
}
