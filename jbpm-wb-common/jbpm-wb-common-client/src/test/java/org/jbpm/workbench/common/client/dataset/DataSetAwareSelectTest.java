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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.jboss.errai.common.client.dom.OptionsCollection;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataSetAwareSelectTest {

    private static final String VALUE_COLUMN_ID = "value";
    private static final String TEXT_COLUMN_ID = "text";

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
        final OptionsCollection options = mock(OptionsCollection.class);
        final AtomicInteger optionsLength = new AtomicInteger(0);
        when(options.getLength()).thenAnswer(a -> optionsLength.get());
        when(select.getOptions()).thenReturn(options);
        doAnswer(invocationOnMock -> optionsLength.incrementAndGet()).when(select).addOption(anyString(), anyString());
    }

    @Test
    public void testEmptyServerTemplate() {
        filterSettings.setKey("key");

        dataSetAwareSelect.onDataSetReady(new DataSetReadyEvent(filterSettings, null));

        verifyZeroInteractions(dataSetClientServices);
        verify(select).removeAllOptions();
    }

    @Test
    public void testLookupDataSetDifferentUUID() {
        final String dataUUID = "dataUUID";
        final String serverTemplateId = "test";
        
        filterSettings.setKey("key");
        filterSettings.setServerTemplateId(serverTemplateId);
                
        final DataSetLookup lookup = mock(DataSetLookup.class);
        when(lookup.getDataSetUUID()).thenReturn(dataUUID);
        dataSetAwareSelect.setDataSetLookup(lookup);
        dataSetAwareSelect.setValueColumnId(VALUE_COLUMN_ID);
        dataSetAwareSelect.setTextColumnId(TEXT_COLUMN_ID);

        dataSetAwareSelect.onDataSetReady(new DataSetReadyEvent(filterSettings, "anotherUUID"));

        verifyZeroInteractions(dataSetClientServices);
    }


    @Test
    public void testLookupDataSet() throws Exception {
        final String key = "key";
        final String serverTemplateId = "test";
        final String dataUUID = "dataUUID";
        final String columnValue = "processNameValue";
        final String columnText = "processNameText";

        filterSettings.setKey(key);
        filterSettings.setServerTemplateId(serverTemplateId);
        final DataSetLookup lookup = mock(DataSetLookup.class);
        when(lookup.getDataSetUUID()).thenReturn(dataUUID);
        dataSetAwareSelect.setDataSetLookup(lookup);
        dataSetAwareSelect.setValueColumnId(VALUE_COLUMN_ID);
        dataSetAwareSelect.setTextColumnId(TEXT_COLUMN_ID);

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                final DataSet dataSet = mock(DataSet.class);
                when(dataSet.getRowCount()).thenReturn(1);
                when(dataSet.getValueAt(0, VALUE_COLUMN_ID)).thenReturn(columnValue);
                when(dataSet.getValueAt(0, TEXT_COLUMN_ID)).thenReturn(columnText);
                ((DataSetReadyCallback) invocation.getArguments()[1]).callback(dataSet);
                return null;
            }
        }).when(dataSetClientServices).lookupDataSet(any(DataSetLookup.class), any(DataSetReadyCallback.class));

        dataSetAwareSelect.onDataSetReady(new DataSetReadyEvent(filterSettings, dataUUID));

        final ArgumentCaptor<DataSetLookup> captor = ArgumentCaptor.forClass(DataSetLookup.class);
        verify(dataSetClientServices).lookupDataSet(captor.capture(), any(DataSetReadyCallback.class));
        assertTrue(captor.getValue() instanceof ConsoleDataSetLookup);
        final ConsoleDataSetLookup cdsl = (ConsoleDataSetLookup) captor.getValue();
        assertEquals(serverTemplateId, cdsl.getServerTemplateId());
        assertEquals(dataUUID, cdsl.getDataSetUUID());
        verify(select).addOption(columnText, columnValue);
        verify(select).enable();
        verify(select).removeAllOptions();
    }

    @Test
    public void testLookupDataSetEmptyStringOrNull() throws Exception {
        final String key = "key";
        final String serverTemplateId = "test";
        final String dataUUID = "dataUUID";
        final String columnValue = "processNameValue";
        final String columnText = "processNameText";

        filterSettings.setKey(key);
        filterSettings.setServerTemplateId(serverTemplateId);
        final DataSetLookup lookup = mock(DataSetLookup.class);
        when(lookup.getDataSetUUID()).thenReturn(dataUUID);
        dataSetAwareSelect.setDataSetLookup(lookup);
        dataSetAwareSelect.setValueColumnId(VALUE_COLUMN_ID);
        dataSetAwareSelect.setTextColumnId(TEXT_COLUMN_ID);

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                final DataSet dataSet = mock(DataSet.class);
                when(dataSet.getRowCount()).thenReturn(3);
                when(dataSet.getValueAt(0, VALUE_COLUMN_ID)).thenReturn(columnValue);
                when(dataSet.getValueAt(0, TEXT_COLUMN_ID)).thenReturn(columnText);
                when(dataSet.getValueAt(1, VALUE_COLUMN_ID)).thenReturn("");
                when(dataSet.getValueAt(1, TEXT_COLUMN_ID)).thenReturn("");
                when(dataSet.getValueAt(2, VALUE_COLUMN_ID)).thenReturn(null);
                when(dataSet.getValueAt(2, TEXT_COLUMN_ID)).thenReturn(null);
                ((DataSetReadyCallback) invocation.getArguments()[1]).callback(dataSet);
                return null;
            }
        }).when(dataSetClientServices).lookupDataSet(any(DataSetLookup.class), any(DataSetReadyCallback.class));

        dataSetAwareSelect.onDataSetReady(new DataSetReadyEvent(filterSettings, dataUUID));

        final ArgumentCaptor<DataSetLookup> captor = ArgumentCaptor.forClass(DataSetLookup.class);
        verify(dataSetClientServices).lookupDataSet(captor.capture(), any(DataSetReadyCallback.class));
        assertTrue(captor.getValue() instanceof ConsoleDataSetLookup);
        final ConsoleDataSetLookup cdsl = (ConsoleDataSetLookup) captor.getValue();
        assertEquals(serverTemplateId, cdsl.getServerTemplateId());
        assertEquals(dataUUID, cdsl.getDataSetUUID());
        verify(select).addOption(columnText, columnValue);
        verify(select).enable();
        verify(select).removeAllOptions();
    }

    @Test
    public void testLookupEmptyDataSet() throws Exception {
        final String key = "key";
        final String serverTemplateId = "test";
        final String dataUUID = "dataUUID";

        filterSettings.setKey(key);
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
        }).when(dataSetClientServices).lookupDataSet(any(DataSetLookup.class), any(DataSetReadyCallback.class));

        dataSetAwareSelect.onDataSetReady(new DataSetReadyEvent(filterSettings, dataUUID));

        final ArgumentCaptor<DataSetLookup> captor = ArgumentCaptor.forClass(DataSetLookup.class);
        verify(dataSetClientServices).lookupDataSet(captor.capture(), any(DataSetReadyCallback.class));
        assertTrue(captor.getValue() instanceof ConsoleDataSetLookup);
        final ConsoleDataSetLookup cdsl = (ConsoleDataSetLookup) captor.getValue();
        assertEquals(serverTemplateId, cdsl.getServerTemplateId());
        assertEquals(dataUUID, cdsl.getDataSetUUID());
        verify(select, never()).addOption(anyString(), anyString());
        verify(select).disable();
        verify(select).removeAllOptions();
    }
}
