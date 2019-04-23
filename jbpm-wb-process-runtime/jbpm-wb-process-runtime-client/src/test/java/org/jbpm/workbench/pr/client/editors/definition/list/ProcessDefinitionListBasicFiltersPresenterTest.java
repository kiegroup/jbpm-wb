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

package org.jbpm.workbench.pr.client.editors.definition.list;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jbpm.workbench.common.client.filters.basic.AbstractBasicFiltersPresenterTest;
import org.jbpm.workbench.common.client.filters.basic.BasicFiltersPresenter;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessDefinitionListBasicFiltersPresenterTest extends AbstractBasicFiltersPresenterTest {

    @InjectMocks
    ProcessDefinitionListBasicFiltersPresenter processDefinitionListBasicFiltersPresenter;

    @Override
    public BasicFiltersPresenter getPresenter() {
        return processDefinitionListBasicFiltersPresenter;
    }

    @Test
    public void testLoadFilters() {
        processDefinitionListBasicFiltersPresenter.loadFilters();

        verify(getView()).addTextFilter(eq(Constants.INSTANCE.Name()),
                                        eq(Constants.INSTANCE.Default_Process_Definition_Name()),
                                        eq(true),
                                        any());
    }
}
