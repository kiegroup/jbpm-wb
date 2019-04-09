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
package org.jbpm.workbench.forms.client.display.process;

import java.util.Arrays;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.client.ui.html.Text;
import org.gwtbootstrap3.extras.select.client.ui.OptGroup;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;

import org.jbpm.workbench.forms.client.display.providers.StartProcessFormDisplayProviderImpl;
import org.jbpm.workbench.forms.client.i18n.Constants;
import org.jbpm.workbench.forms.display.api.ProcessDisplayerConfig;
import org.jbpm.workbench.pr.model.ProcessSummary;
import org.jbpm.workbench.pr.service.ProcessRuntimeDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;

import static org.jbpm.workbench.forms.client.display.process.QuickNewProcessInstancePopup.FIELD_ID_PROCESSNAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Text.class})
public class QuickNewProcessInstancePopupTest {

    @Mock
    public Select processDefinitionsListBox;

    @Mock
    public HelpBlock errorMessages;

    @Mock
    public FormGroup errorMessagesGroup;

    @Mock
    protected StartProcessFormDisplayProviderImpl startProcessDisplayProvider;

    @Mock
    private CallerMock<ProcessRuntimeDataService> processRuntimeDataService;

    @Mock
    private ProcessRuntimeDataService processRuntimeDataServiceMock;

    @Mock
    public FlowPanel body;

    @InjectMocks
    private QuickNewProcessInstancePopup quickNewProcessInstancePopup;

    @Before
    public void setupMocks() {
        processRuntimeDataService = new CallerMock<>(processRuntimeDataServiceMock);
        quickNewProcessInstancePopup.setProcessRuntimeDataService(processRuntimeDataService);
    }

    @Test
    public void loadFormValuesTest() {
        String serverTemplateId = "serverTemplateId";
        String deploymentId = "def_deploymentId";
        String processDefinitionId = "def_Id";

        ProcessSummary processSummary = new ProcessSummary(processDefinitionId,
                                                           "def_name",
                                                           deploymentId,
                                                           "1.0",
                                                           false);

        when(processRuntimeDataServiceMock.getProcesses(serverTemplateId,
                                                        0,
                                                        Integer.MAX_VALUE,
                                                        FIELD_ID_PROCESSNAME,
                                                        true)).thenReturn(Arrays.asList(processSummary));

        quickNewProcessInstancePopup.loadFormValues(serverTemplateId);

        verify(processDefinitionsListBox).clear();
        verify(processRuntimeDataServiceMock).getProcesses(eq(serverTemplateId),
                                                           eq(0),
                                                           eq(Integer.MAX_VALUE),
                                                           eq(FIELD_ID_PROCESSNAME),
                                                           eq(true));

        final ArgumentCaptor<OptGroup> captor = ArgumentCaptor.forClass(OptGroup.class);
        verify(processDefinitionsListBox).add(captor.capture());
        verify(captor.getValue()).setLabel(deploymentId);

        final ArgumentCaptor<Option> captorOption = ArgumentCaptor.forClass(Option.class);
        verify(captor.getValue()).add(captorOption.capture());

        verify(captorOption.getValue()).setValue(processDefinitionId);
        verify(captorOption.getValue()).setText(processDefinitionId);

    }

    @Test
    public void validateFormTest() {
        when(processDefinitionsListBox.getSelectedItem()).thenReturn(null);
        boolean valid = quickNewProcessInstancePopup.validateForm();
        assertFalse(valid);
        verify(errorMessages).setText(Constants.INSTANCE.Select_Process());
        verify(errorMessagesGroup).setValidationState(ValidationState.ERROR);

        when(processDefinitionsListBox.getSelectedItem()).thenReturn(mock(Option.class));
        valid = quickNewProcessInstancePopup.validateForm();
        assertTrue(valid);
        verify(errorMessages).setText("");
    }

    @Test
    public void createNewProcessInstance() {
        String deploymentId = "def_deploymentId";
        String processDefinitionId = "def_Id";

        OptGroup group = mock(OptGroup.class);
        Option option = mock(Option.class);
        when(option.getParent()).thenReturn(group);
        when(group.getLabel()).thenReturn(deploymentId);
        when(option.getValue()).thenReturn(processDefinitionId);
        when(processDefinitionsListBox.getSelectedItem()).thenReturn(option);

        quickNewProcessInstancePopup.createNewProcessInstance();

        final ArgumentCaptor<ProcessDisplayerConfig> captor = ArgumentCaptor.forClass(ProcessDisplayerConfig.class);
        verify(startProcessDisplayProvider).setup(captor.capture(),
                                                  eq(quickNewProcessInstancePopup));

        assertEquals(processDefinitionId,
                     captor.getValue().getKey().getProcessDefName());
        assertEquals(deploymentId,
                     captor.getValue().getKey().getDeploymentId());
    }

    @Test
    public void testClosePopup() {
        quickNewProcessInstancePopup.setFlowPanelBody(body);
        quickNewProcessInstancePopup.closePopup();
        verify(body).clear();
    }
}
