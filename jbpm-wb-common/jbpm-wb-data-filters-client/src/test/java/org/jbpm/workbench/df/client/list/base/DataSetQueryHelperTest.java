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
package org.jbpm.workbench.df.client.list.base;

import javax.enterprise.event.Event;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.displayer.client.DataSetHandler;
import org.jbpm.workbench.df.client.events.DataSetReadyEvent;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.dashbuilder.dataset.sort.SortOrder.DESCENDING;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSetQueryHelperTest {

    public static final String COLUMN_1 = "columnOne";

    @Mock
    protected DataSetLookup dataSetLookup;

    @Spy
    protected FilterSettings currentTableSetting;

    @Mock
    protected DataSetHandler dataSetHandlerMock;

    @Mock
    protected DataSetClientServices dataSetClientServicesMock;

    @Mock
    protected Event<DataSetReadyEvent> event;

    @InjectMocks
    private DataSetQueryHelper dataSetQueryHelper;

    @Before
    public void setUp() throws Exception {
        dataSetQueryHelper.setCurrentTableSettings(currentTableSetting);
        dataSetQueryHelper.setDataSetHandler(dataSetHandlerMock);
    }

    @Test
    public void lookupDataSetTest() throws Exception {
        currentTableSetting.setTableDefaultSortColumnId(COLUMN_1);
        currentTableSetting.setTableDefaultSortOrder(DESCENDING);

        currentTableSetting.setTablePageSize(5);
        dataSetQueryHelper.lookupDataSet(0,
                                         new DataSetReadyCallback() {
                                             @Override
                                             public void callback(DataSet dataSet) {

                                             }

                                             @Override
                                             public void notFound() {

                                             }

                                             @Override
                                             public boolean onError(ClientRuntimeError error) {
                                                 fail(error.getMessage());
                                                 return false;
                                             }
                                         });
        verify(dataSetHandlerMock).limitDataSetRows(0,
                                                    5);
        verify(dataSetHandlerMock).sort(COLUMN_1,
                                        DESCENDING);
    }

    @Test
    public void testLookupDataSetEvent() throws Exception {
        DataSetReadyCallback callback = mock(DataSetReadyCallback.class);

        final DataSet dataSet = mock(DataSet.class);
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ((DataSetReadyCallback) invocation.getArguments()[0]).callback(dataSet);
                return null;
            }
        }).when(dataSetHandlerMock).lookupDataSet(any(DataSetReadyCallback.class));

        dataSetQueryHelper.lookupDataSet(0,
                                         callback);

        final ArgumentCaptor<DataSetReadyEvent> captor = ArgumentCaptor.forClass(DataSetReadyEvent.class);
        verify(event).fire(captor.capture());
        assertEquals(currentTableSetting,
                     captor.getValue().getFilterSettings());
    }
}
