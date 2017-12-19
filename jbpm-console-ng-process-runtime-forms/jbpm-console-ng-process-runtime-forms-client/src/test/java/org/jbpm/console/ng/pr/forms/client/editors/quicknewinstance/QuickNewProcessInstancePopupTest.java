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
package org.jbpm.console.ng.pr.forms.client.editors.quicknewinstance;

import java.util.ArrayList;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.html.Text;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.pr.service.ProcessDefinitionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Text.class})
public class QuickNewProcessInstancePopupTest {

    @Mock
    private CallerMock<ProcessDefinitionService> processRuntimeDataService;

    @Mock
    private ProcessDefinitionService processDefinitionServiceMock;

    @InjectMocks
    private QuickNewProcessInstancePopup quickNewProcessInstancePopup;

    @Before
    public void setupMocks() {
        processRuntimeDataService = new CallerMock<>(processDefinitionServiceMock);
        quickNewProcessInstancePopup.setProcessRuntimeDataService(processRuntimeDataService);
    }

    @Test
    public void loadFormValuesPaginationAndSortTest() {
        when(processDefinitionServiceMock.getAll(any(QueryFilter.class))).thenReturn(new ArrayList());

        quickNewProcessInstancePopup.loadFormValues();

        final ArgumentCaptor<QueryFilter> captor = ArgumentCaptor.forClass(QueryFilter.class);
        verify(processDefinitionServiceMock).getAll(captor.capture());

        assertEquals(QuickNewProcessInstancePopup.FIELD_ID_PROCESSNAME,
                     captor.getValue().getOrderBy());
        assertEquals(Integer.MAX_VALUE - 1,
                     captor.getValue().getCount().intValue());
    }
}
