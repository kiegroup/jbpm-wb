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
package org.jbpm.console.ng.ht.client.editors.quicknewtask;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.client.ui.html.Text;
import org.jboss.errai.common.client.api.Caller;

import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.ht.service.TaskFormManagementService;
import org.jbpm.console.ng.ht.service.TaskOperationsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(Text.class)
public class QuickNewTaskPopupTest {

    @Mock
    private TaskOperationsService taskOperationsServiceMock;
    private Caller<TaskOperationsService> taskOperationsService;

    @Mock
    private TaskFormManagementService taskFormManagementServiceMock;
    private Caller<TaskFormManagementService> taskFormManagementService;

    @Mock
    public TextBox taskNameText;

    @Mock
    public FormGroup taskNameControlGroup;

    @Mock
    public User identity;

    @InjectMocks
    private QuickNewTaskPopup quickNewTaskPopup;

    @Before
    public void setupMocks() {
        taskOperationsService = new CallerMock<TaskOperationsService>(taskOperationsServiceMock);
        taskFormManagementService = new CallerMock<TaskFormManagementService>(taskFormManagementServiceMock);

        quickNewTaskPopup.setTaskServices(taskOperationsService, taskFormManagementService);
    }

    @Test
    public void loadFormValuesTest() {
        quickNewTaskPopup.loadFormValues();
        verify(taskFormManagementServiceMock).getAvailableDeployments();
    }

    @Test
    public void validateFormEmptyStringAsTaskNameTest() {
        when(taskNameText.getText()).thenReturn("");
        boolean validForm = quickNewTaskPopup.validateForm();

        verify(taskNameText, times(2)).getText();
        verify(taskNameText).setFocus(true);
        verify(taskNameControlGroup).setValidationState(ValidationState.ERROR);
        assertFalse(validForm);
    }

    @Test
    public void validateFormStringAsTaskNameTest() {
        when(taskNameText.getText()).thenReturn("testTaskName");
        boolean validForm = quickNewTaskPopup.validateForm();

        verify(taskNameControlGroup).setValidationState(ValidationState.SUCCESS);

        assertTrue(validForm);
    }

}
