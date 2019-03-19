/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.client.editors.instance.list;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.common.client.filters.basic.AbstractBasicFiltersPresenterTest;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessInstanceListBasicFiltersPresenterTest extends AbstractBasicFiltersPresenterTest {

    @InjectMocks
    ProcessInstanceListBasicFiltersPresenter presenter;

    @Override
    public ProcessInstanceListBasicFiltersPresenter getPresenter() {
        return presenter;
    }

    @Override
    @Test
    public void testLoadFilters() {
        presenter.loadFilters();

        final InOrder inOrder = inOrder(getView());

        inOrder.verify(getView()).addNumericFilter(eq(Constants.INSTANCE.Id()), any(), any());
        inOrder.verify(getView()).addTextFilter(eq(Constants.INSTANCE.Initiator()), any(), any());
        inOrder.verify(getView()).addTextFilter(eq(Constants.INSTANCE.Correlation_Key()), any(), any());
        inOrder.verify(getView()).addTextFilter(eq(Constants.INSTANCE.Process_Instance_Description()), any(), any());
        inOrder.verify(getView()).addMultiSelectFilter(eq(Constants.INSTANCE.Errors()), any(), any());
        inOrder.verify(getView()).addMultiSelectFilter(eq(Constants.INSTANCE.State()), any(), any());
        inOrder.verify(getView()).addDataSetSelectFilter(eq(Constants.INSTANCE.Name()), any(), any(), any(), any());
        inOrder.verify(getView()).addDataSetSelectFilter(eq(Constants.INSTANCE.Process_Definition_Id()), any(), any(), any(), any());
        inOrder.verify(getView()).addDataSetSelectFilter(eq(Constants.INSTANCE.DeploymentId()), any(), any(), any(), any());
        inOrder.verify(getView()).addSelectFilter(eq(Constants.INSTANCE.SlaCompliance()), any(), any());
        inOrder.verify(getView()).addDateRangeFilter(eq(Constants.INSTANCE.Start_Date()), any(), any(), any());
        inOrder.verify(getView()).addDateRangeFilter(eq(Constants.INSTANCE.Last_Modification_Date()), any(), any(), any());
    }
}
