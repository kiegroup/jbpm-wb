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
package org.jbpm.workbench.common.client.list;

import java.util.Arrays;
import java.util.function.Consumer;

import javax.enterprise.event.Event;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.filter.LogicalExprFilter;
import org.dashbuilder.dataset.filter.LogicalExprType;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jbpm.workbench.common.client.filters.active.ActiveFilterItem;
import org.jbpm.workbench.common.client.filters.active.ClearAllActiveFiltersEvent;
import org.jbpm.workbench.common.client.menu.ServerTemplateSelectorMenuBuilder;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsManager;
import org.jbpm.workbench.df.client.list.DataSetQueryHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs;
import org.uberfire.mocks.EventSourceMock;

import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractMultiGridPresenterTest {

    @Mock
    FilterSettingsManager filterSettingsManager;

    @Mock
    FilterSettings filterSettingsMock;

    @Mock
    UberfireBreadcrumbs breadcrumbsMock;

    @Mock
    ListView listView;

    @Mock
    ServerTemplateSelectorMenuBuilder serverTemplateSelectorMenuBuilderMock;

    @Mock
    ServerTemplateSelectorMenuBuilder.ServerTemplateSelectorElementView serverTemplateSelectorElementViewMock;

    @Mock
    protected DataSetQueryHelper dataSetQueryHelper;

    @Spy
    Event<ClearAllActiveFiltersEvent> clearAllActiveFiltersEvent = new EventSourceMock<>();

    @Spy
    AbstractMultiGridPresenter presenter;

    @Mock
    AbstractMultiGridView view;

    @Before
    public void setupMocks() {
        when(serverTemplateSelectorMenuBuilderMock.getView()).thenReturn(serverTemplateSelectorElementViewMock);
        when(presenter.getListView()).thenReturn(listView);

        presenter.setView(view);
        presenter.setUberfireBreadcrumbs(breadcrumbsMock);
        presenter.setServerTemplateSelectorMenuBuilder(serverTemplateSelectorMenuBuilderMock);
        presenter.setFilterSettingsManager(filterSettingsManager);
        presenter.setDataSetQueryHelper(dataSetQueryHelper);
        presenter.setClearAllActiveFiltersEvent(clearAllActiveFiltersEvent);

        doNothing().when(clearAllActiveFiltersEvent).fire(any());
    }

    @Test
    public void onOpenActiveSearchTest() {
        when(presenter.existActiveSearchFilters()).thenReturn(true);
        when(filterSettingsManager.createDefaultFilterSettingsPrototype()).thenReturn(filterSettingsMock);
        when(filterSettingsMock.getKey()).thenReturn("key");

        presenter.onOpen();

        ArgumentCaptor<Consumer> activeSearchConsumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(clearAllActiveFiltersEvent).fire(any());
        verify(view).loadListTable(eq("key"), activeSearchConsumerCaptor.capture());
        verify(dataSetQueryHelper).setCurrentTableSettings(filterSettingsMock);
        verify(clearAllActiveFiltersEvent).fire(any());
    }

    @Test
    public void onOpenWithActiveSearchTest() {
        String defaultFilterKey = "defaultFilterKey";

        when(presenter.existActiveSearchFilters()).thenReturn(false);
        when(filterSettingsMock.getKey()).thenReturn(defaultFilterKey);

        presenter.onOpen();

        ArgumentCaptor<Consumer> defaultFilterConsumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(filterSettingsManager).defaultActiveFilterInit(defaultFilterConsumerCaptor.capture());
        defaultFilterConsumerCaptor.getValue().accept(filterSettingsMock);
        verify(view).removeAllActiveFilters();
        verify(dataSetQueryHelper).setCurrentTableSettings(filterSettingsMock);
        verify(view).loadListTable(eq(defaultFilterKey), any());
    }

    @Test
    public void testActiveFiltersLabel() {
        ColumnFilter testColumnFilter = equalsTo("test_columnId", 1);
        ((CoreFunctionFilter) testColumnFilter).setLabelValue("test_lable");

        ActiveFilterItem activeFilterItemOne = presenter.getActiveFilterFromColumnFilter(testColumnFilter);
        assertEquals(((CoreFunctionFilter) testColumnFilter).getLabelValue(), activeFilterItemOne.getLabelValue());

        LogicalExprFilter logicalExprFilter = new LogicalExprFilter("test_columnId", LogicalExprType.OR, Arrays.asList(testColumnFilter));
        ActiveFilterItem activeFilterItemTwo = presenter.getActiveFilterFromColumnFilter(logicalExprFilter);
        assertEquals(logicalExprFilter.toString(), activeFilterItemTwo.getLabelValue());

        ColumnFilter testColumnFilterEmpty = equalsTo("test_columnId", 1);
        ((CoreFunctionFilter) testColumnFilterEmpty).setLabelValue("");

        ActiveFilterItem activeFilterItemThree = presenter.getActiveFilterFromColumnFilter(testColumnFilterEmpty);
        assertEquals(testColumnFilterEmpty.toString(), activeFilterItemThree.getLabelValue());
    }

    @Test
    public void addActiveFiltersTest() {
        String defaultFilterKey = "defaultFilterKey";
        DataSetLookup dataSetLookupMock = mock(DataSetLookup.class);
        DataSetFilter dataSetFilterMock = mock(DataSetFilter.class);
        final ColumnFilter columnFilter = mock(ColumnFilter.class);
        final ColumnFilter columnFilter2 = mock(ColumnFilter.class);

        AsyncDataProvider dataProviderMock = mock(AsyncDataProvider.class);
        ListTable listTableMock = mock(ListTable.class);

        when(presenter.existActiveSearchFilters()).thenReturn(false);
        when(filterSettingsMock.getKey()).thenReturn(defaultFilterKey);
        when(filterSettingsMock.getDataSetLookup()).thenReturn(dataSetLookupMock);
        when(dataSetLookupMock.getFirstFilterOp()).thenReturn(dataSetFilterMock);
        when(dataSetFilterMock.getColumnFilterList()).thenReturn(Arrays.asList(columnFilter, columnFilter2));

        presenter.setDataProvider(dataProviderMock);
        presenter.addActiveFilters(filterSettingsMock);

        verify(view).removeAllActiveFilters();
        verify(dataSetQueryHelper).setCurrentTableSettings(filterSettingsMock);
        ArgumentCaptor<Consumer> listTableConsumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(view).loadListTable(eq(defaultFilterKey), listTableConsumerCaptor.capture());
        listTableConsumerCaptor.getValue().accept(listTableMock);
        verify(presenter).getActiveFilterFromColumnFilter(columnFilter);
        verify(presenter).getActiveFilterFromColumnFilter(columnFilter2);
        verify(view, times(2)).addActiveFilter(any(ActiveFilterItem.class));
        verify(presenter).addDataDisplay(listTableMock);
    }
    
    @Test
    public void testGetDataNoSorting(){
        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettingsMock);

        when(view.getListGrid()).thenReturn(mock(ListTable.class));
        when(view.getSortColumn()).thenReturn(null);
        when(view.isSortAscending()).thenReturn(null);
        
        presenter.getData(new Range(0, 10));
        
        verify(dataSetQueryHelper, never()).setLastOrderedColumn(any());
        verify(dataSetQueryHelper, never()).setLastSortOrder(any());
    }

    @Test
    public void testGetDataSorting(){
        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettingsMock);

        when(view.getListGrid()).thenReturn(mock(ListTable.class));
        final String column = "some_column";
        when(view.getSortColumn()).thenReturn(column);
        when(view.isSortAscending()).thenReturn(true);

        presenter.getData(new Range(0, 10));

        verify(dataSetQueryHelper).setLastOrderedColumn(column);
        verify(dataSetQueryHelper).setLastSortOrder(SortOrder.ASCENDING);
    }

    @Test
    public void testSaveSearchFilterSettings() {
        String key = "key";
        ListTable listTable = mock(ListTable.class);
        GridPreferencesStore gridPreferencesStore = mock(GridPreferencesStore.class);

        when(dataSetQueryHelper.getCurrentTableSettings()).thenReturn(filterSettingsMock);
        when(filterSettingsMock.getKey()).thenReturn(key);
        when(listView.getListGrid()).thenReturn(listTable);
        when(listTable.getGridPreferencesStore()).thenReturn(gridPreferencesStore);

        presenter.saveSearchFilterSettings("filterName", mock(Consumer.class));

        ArgumentCaptor<Consumer> booleanConsumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(filterSettingsManager).saveFilterIntoPreferences(eq(filterSettingsMock),
                                                                booleanConsumerCaptor.capture());
        booleanConsumerCaptor.getValue().accept(true);
        verify(gridPreferencesStore).setPreferenceKey(key);
        verify(listTable).saveGridToUserPreferences();
    }
}
