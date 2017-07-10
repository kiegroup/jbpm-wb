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

package org.jbpm.workbench.common.client.dataset;

import java.util.function.Consumer;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.jbpm.workbench.df.client.events.DataSetReadyEvent;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.ks.integration.ConsoleDataSetLookup;
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
import org.uberfire.client.views.pfly.widgets.Select;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSetAwareSelectTest {

    @Mock
    DataSetClientServices dataSetClientServices;

    @Spy
    FilterSettings filterSettings;

    @Mock
    Select select;

    @InjectMocks
    DataSetAwareSelect dataSetAwareSelect;

    @Before
    public void setup() {
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ((Consumer) invocation.getArguments()[0]).accept(select);
                return null;
            }
        }).when(select).refresh(any(Consumer.class));
    }

    @Test
    public void testDifferentTableKey() {
        filterSettings.setKey("anotherkey");
        dataSetAwareSelect.setTableKey("key");

        dataSetAwareSelect.onDataSetReady(new DataSetReadyEvent(filterSettings));

        verifyZeroInteractions(dataSetClientServices);
        verifyZeroInteractions(select);
    }

    @Test
    public void testFilterSettingsWithoutKey() {
        dataSetAwareSelect.setTableKey("key");

        dataSetAwareSelect.onDataSetReady(new DataSetReadyEvent(filterSettings));

        verifyZeroInteractions(dataSetClientServices);
        verifyZeroInteractions(select);
    }

    @Test
    public void testEmptyServerTemplate() {
        filterSettings.setKey("key");
        dataSetAwareSelect.setTableKey("key");

        dataSetAwareSelect.onDataSetReady(new DataSetReadyEvent(filterSettings));

        verifyZeroInteractions(dataSetClientServices);
        verify(select).removeAllOptions();
    }

    @Test
    public void testLookupDataSet() throws Exception {
        final String key = "key";
        final String serverTemplateId = "test";
        final String dataUUID = "dataUUID";
        final String columnValue = "processNameValue";
        final String columnText = "processNameText";

        filterSettings.setKey(key);
        dataSetAwareSelect.setTableKey(key);
        filterSettings.setServerTemplateId(serverTemplateId);
        final DataSetLookup lookup = mock(DataSetLookup.class);
        when(lookup.getDataSetUUID()).thenReturn(dataUUID);
        dataSetAwareSelect.setDataSetLookup(lookup);
        dataSetAwareSelect.setValueColumnId("value");
        dataSetAwareSelect.setTextColumnId("test");

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                final DataSet dataSet = mock(DataSet.class);
                when(dataSet.getRowCount()).thenReturn(1);
                when(dataSet.getValueAt(0,
                                        "value")).thenReturn(columnValue);
                when(dataSet.getValueAt(0,
                                        "test")).thenReturn(columnText);
                ((DataSetReadyCallback) invocation.getArguments()[1]).callback(dataSet);
                return null;
            }
        }).when(dataSetClientServices).lookupDataSet(any(DataSetLookup.class),
                                                     any(DataSetReadyCallback.class));

        dataSetAwareSelect.onDataSetReady(new DataSetReadyEvent(filterSettings));

        final ArgumentCaptor<DataSetLookup> captor = ArgumentCaptor.forClass(DataSetLookup.class);
        verify(dataSetClientServices).lookupDataSet(captor.capture(),
                                                    any(DataSetReadyCallback.class));
        assertTrue(captor.getValue() instanceof ConsoleDataSetLookup);
        final ConsoleDataSetLookup cdsl = (ConsoleDataSetLookup) captor.getValue();
        assertEquals(serverTemplateId,
                     cdsl.getServerTemplateId());
        assertEquals(dataUUID,
                     cdsl.getDataSetUUID());
        verify(select).addOption(columnText,
                                 columnValue);
        verify(select).enable();
        verify(select).removeAllOptions();
    }

    @Test
    public void testLookupEmptyDataSet() throws Exception {
        final String key = "key";
        final String serverTemplateId = "test";
        final String dataUUID = "dataUUID";

        filterSettings.setKey(key);
        dataSetAwareSelect.setTableKey(key);
        filterSettings.setServerTemplateId(serverTemplateId);
        final DataSetLookup lookup = mock(DataSetLookup.class);
        when(lookup.getDataSetUUID()).thenReturn(dataUUID);
        dataSetAwareSelect.setDataSetLookup(lookup);

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                final DataSet dataSet = mock(DataSet.class);
                when(dataSet.getRowCount()).thenReturn(0);
                ((DataSetReadyCallback) invocation.getArguments()[1]).callback(dataSet);
                return null;
            }
        }).when(dataSetClientServices).lookupDataSet(any(DataSetLookup.class),
                                                     any(DataSetReadyCallback.class));

        dataSetAwareSelect.onDataSetReady(new DataSetReadyEvent(filterSettings));

        final ArgumentCaptor<DataSetLookup> captor = ArgumentCaptor.forClass(DataSetLookup.class);
        verify(dataSetClientServices).lookupDataSet(captor.capture(),
                                                    any(DataSetReadyCallback.class));
        assertTrue(captor.getValue() instanceof ConsoleDataSetLookup);
        final ConsoleDataSetLookup cdsl = (ConsoleDataSetLookup) captor.getValue();
        assertEquals(serverTemplateId,
                     cdsl.getServerTemplateId());
        assertEquals(dataUUID,
                     cdsl.getDataSetUUID());
        verify(select,
               never()).addOption(anyString(),
                                  anyString());
        verify(select).disable();
        verify(select).removeAllOptions();
    }
}
