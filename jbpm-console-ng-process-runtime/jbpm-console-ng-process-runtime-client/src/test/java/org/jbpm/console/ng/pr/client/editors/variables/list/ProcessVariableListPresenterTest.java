/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.pr.client.editors.variables.list;

import java.util.Arrays;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.console.ng.bd.service.DataServiceEntryPoint;
import org.jbpm.console.ng.pr.model.ProcessVariableSummary;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceSelectionEvent;
import org.jbpm.console.ng.pr.service.ProcessVariablesService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessVariableListPresenterTest {

    @Mock
    ProcessVariableListPresenter.ProcessVariableListView view;

    Caller<ProcessVariablesService> variablesServicesCaller;

    @Mock
    ProcessVariablesService processVariablesService;

    Caller<DataServiceEntryPoint> dataServicesCaller;

    @Mock
    DataServiceEntryPoint dataServiceEntryPoint;

    ProcessVariableListPresenter presenter;

    @Before
    public void setup() {
        variablesServicesCaller = new CallerMock<ProcessVariablesService>(processVariablesService);
        dataServicesCaller = new CallerMock<DataServiceEntryPoint>(dataServiceEntryPoint);
        presenter = new ProcessVariableListPresenter(view, variablesServicesCaller, dataServicesCaller);
    }

    @Test
    public void testLoadVariableHistory() {
        final ParameterizedCommand callback = mock(ParameterizedCommand.class);
        final String variableName = "variable";
        final long processInstanceId = 1l;
        final ProcessVariableSummary summary = new ProcessVariableSummary(variableName, "variableInstanceId", processInstanceId, "oldValue", "newValue", System.currentTimeMillis(), "type");
        final List<ProcessVariableSummary> summaries = Arrays.asList(summary);
        when(dataServiceEntryPoint.getVariableHistory(processInstanceId, variableName)).thenReturn(summaries);
        final ProcessInstanceSelectionEvent event = new ProcessInstanceSelectionEvent("deploymentId", processInstanceId, "processDefId", "processDefName", 1);

        presenter.onProcessInstanceSelectionEvent(event);
        presenter.loadVariableHistory(callback, variableName);

        verify(callback).execute(summaries);
    }
}