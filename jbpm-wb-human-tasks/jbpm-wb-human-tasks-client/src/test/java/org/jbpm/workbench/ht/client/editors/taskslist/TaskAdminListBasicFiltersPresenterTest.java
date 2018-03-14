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

package org.jbpm.workbench.ht.client.editors.taskslist;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.common.client.filters.basic.AbstractBasicFiltersPresenterTest;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TaskAdminListBasicFiltersPresenterTest extends AbstractBasicFiltersPresenterTest {

    @InjectMocks
    TaskAdminListBasicFiltersPresenter presenter;

    @Override
    public TaskAdminListBasicFiltersPresenter getPresenter() {
        return presenter;
    }

    @Override
    @Test
    public void testLoadFilters() {
        presenter.loadFilters();

        verify(getView()).addNumericFilter(eq(Constants.INSTANCE.Id()),
                                           any(),
                                           any());
        verify(getView()).addTextFilter(eq(Constants.INSTANCE.Task()),
                                        any(),
                                        any());
        verify(getView()).addSelectFilter(eq(Constants.INSTANCE.Status()),
                                          any(),
                                          any(),
                                          any());
        verify(getView()).addTextFilter(eq(Constants.INSTANCE.Process_Instance_Correlation_Key()),
                                        any(),
                                        any());
        verify(getView()).addTextFilter(eq(Constants.INSTANCE.Actual_Owner()),
                                        any(),
                                        any());
        verify(getView()).addTextFilter(eq(Constants.INSTANCE.Process_Instance_Description()),
                                        any(),
                                        any());
        verify(getView()).addDataSetSelectFilter(eq(Constants.INSTANCE.Process_Definition_Id()),
                                                 any(),
                                                 any(),
                                                 any(),
                                                 any());
        verify(getView()).addDateRangeFilter(eq(Constants.INSTANCE.Created_On()),
                                             any(),
                                             any(),
                                             any());
    }
}
