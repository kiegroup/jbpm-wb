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
package org.jbpm.workbench.pr.client.editors.definition.list;

import java.util.List;

import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.common.client.util.ConditionalKebabActionCell;
import org.jbpm.workbench.common.preferences.ManagePreferences;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.widgets.table.client.ColumnMeta;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProcessDefinitionListViewTest {

    @Mock
    ExtendedPagedTable currentListGrid;

    @Mock
    ManagedInstance<ConditionalKebabActionCell> conditionalKebabActionCell;

    @Mock
    ManagePreferences preferences;

    CallerMock<UserPreferencesService> userPreferencesService;

    @Mock
    UserPreferencesService userPreferencesServiceMock;

    @Mock
    ProcessDefinitionListPresenter presenter;

    @InjectMocks
    @Spy
    ProcessDefinitionListViewImpl view;

    @Before
    public void setup() {
        when(conditionalKebabActionCell.get()).thenReturn(mock(ConditionalKebabActionCell.class));
        userPreferencesService = new CallerMock<>(userPreferencesServiceMock);
        view.setPreferencesService(userPreferencesService);
        doNothing().when(view).addNewTableToColumn(any());
    }

    @Test
    public void testDataStoreNameIsSet() {
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                final List<ColumnMeta> columns = (List<ColumnMeta>) invocationOnMock.getArguments()[0];
                for (ColumnMeta columnMeta : columns) {
                    assertNotNull(columnMeta.getColumn().getDataStoreName());
                }
                return null;
            }
        }).when(currentListGrid).addColumns(anyList());
        when(currentListGrid.getColumnSortList()).thenReturn(new ColumnSortList());

        view.initColumns(currentListGrid);

        verify(currentListGrid).addColumns(anyList());
    }

    @Test
    public void testGlobalPreferences() {
        doAnswer((InvocationOnMock inv) -> {
            ((ParameterizedCommand<ManagePreferences>) inv.getArguments()[0]).execute(new ManagePreferences().defaultValue(new ManagePreferences()));
            return null;
        }).when(preferences).load(any(ParameterizedCommand.class),
                                  any(ParameterizedCommand.class));

        view.init(presenter);

        ArgumentCaptor<GridGlobalPreferences> captor = ArgumentCaptor.forClass(GridGlobalPreferences.class);
        verify(view).createListGrid(captor.capture());
        assertNotNull(captor.getValue());
        assertEquals(ManagePreferences.DEFAULT_PAGINATION_OPTION.intValue(),
                     captor.getValue().getPageSize());
    }
}
