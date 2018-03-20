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

package org.jbpm.workbench.common.client.filters.active;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ActiveFiltersViewImplTest {

    @Mock
    Popover saveFilterPopover;

    @Mock
    SaveFilterPopoverView saveFilterPopoverView;

    @Mock
    DataBinder<List<ActiveFilterItem>> activeFiltersList;

    @InjectMocks
    @Spy
    ActiveFiltersViewImpl view;

    @Test
    public void testSaveFilterCallback() {
        final String name = "filterName";
        doAnswer(invocation -> {
            ParameterizedCommand<String> callback = (ParameterizedCommand<String>) invocation.getArguments()[0];
            callback.execute(name);
            return null;
        }).when(saveFilterPopoverView).setSaveCallback(any());

        view.setSaveFilterPopoverCallback();

        verify(view).saveFilter(name);
    }

    @Test
    public void testCancelFilterCallback() {
        doAnswer(invocation -> {
            Command callback = (Command) invocation.getArguments()[0];
            callback.execute();
            return null;
        }).when(saveFilterPopoverView).setCancelCallback(any());

        view.setSaveFilterPopoverCallback();

        verify(saveFilterPopover).hide();
    }

    @Test
    public void testRemoveActiveFilter(){
        final List<ActiveFilterItem> model = new ArrayList<>();
        when(activeFiltersList.getModel()).thenReturn(model);
        model.add(new ActiveFilterItem<>("key1", null, null, null, null));
        model.add(new ActiveFilterItem<>("key2", null, null, null, null));

        view.removeActiveFilter(new ActiveFilterItem<>("key1", null, null, null, null));
        view.removeActiveFilter(new ActiveFilterItem<>("key3", null, null, null, null));

        assertEquals(1, model.size());
        verify(activeFiltersList, times(2)).getModel();
    }
}
