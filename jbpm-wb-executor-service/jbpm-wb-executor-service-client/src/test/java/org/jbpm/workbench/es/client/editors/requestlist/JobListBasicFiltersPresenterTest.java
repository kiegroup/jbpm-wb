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

package org.jbpm.workbench.es.client.editors.requestlist;

import java.util.Arrays;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.jbpm.workbench.common.client.filters.basic.AbstractBasicFiltersPresenterTest;
import org.jbpm.workbench.es.client.i18n.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class JobListBasicFiltersPresenterTest extends AbstractBasicFiltersPresenterTest {

    @InjectMocks
    JobListBasicFiltersPresenter presenter;

    @Override
    public JobListBasicFiltersPresenter getPresenter() {
        return presenter;
    }

    @Override
    @Test
    public void testLoadFilters() {
        presenter.loadFilters();

        final InOrder inOrder = inOrder(getView());

        inOrder.verify(getView()).addMultiSelectFilter(eq(Constants.INSTANCE.Status()),
                                                       any(),
                                                       any());
        inOrder.verify(getView()).addDataSetSelectFilter(eq(Constants.INSTANCE.Process_Name()),
                                                         any(),
                                                         any(),
                                                         any(),
                                                         any());
        inOrder.verify(getView()).addNumericFilter(eq(Constants.INSTANCE.Process_Instance_Id()),
                                                   any(),
                                                   any());
        inOrder.verify(getView()).addTextFilter(eq(Constants.INSTANCE.BusinessKey()),
                                                any(),
                                                any());
        inOrder.verify(getView()).addTextFilter(eq(Constants.INSTANCE.Type()),
                                                any(),
                                                any());
        inOrder.verify(getView()).addTextFilter(eq(Constants.INSTANCE.Process_Instance_Description()),
                                                any(),
                                                any());
        inOrder.verify(getView()).addDateRangeFilter(eq(Constants.INSTANCE.Due_On()),
                                                     any(),
                                                     any(),
                                                     any());
    }

    @Test
    public void onActiveFilterAddedTest() {
        ActiveFilterItem activeFilterItemMock = mock(ActiveFilterItem.class);
        when(activeFilterItemMock.getKey()).thenReturn(Constants.INSTANCE.JobId());
        presenter.onActiveFilterAdded(activeFilterItemMock);
        verify(getView(), never()).checkSelectFilter(anyString(), anyString());
        verify(activeFilterItemMock, never()).getValue();

        when(activeFilterItemMock.getKey()).thenReturn(Constants.INSTANCE.Status());
        when(activeFilterItemMock.getValue()).thenReturn(Arrays.asList(Constants.INSTANCE.Running(), Constants.INSTANCE.Queued()));
        presenter.onActiveFilterAdded(activeFilterItemMock);
        verify(getView()).checkSelectFilter(Constants.INSTANCE.Status(), Constants.INSTANCE.Running());
        verify(getView()).checkSelectFilter(Constants.INSTANCE.Status(), Constants.INSTANCE.Queued());
    }
}
