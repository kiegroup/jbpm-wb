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

package org.jbpm.dashboard.dataset.editor.impl;

import static org.mockito.Mockito.when;

import org.dashbuilder.client.widgets.common.LoadingBox;
import org.dashbuilder.client.widgets.dataset.editor.DataSetDefColumnsFilterEditor;
import org.dashbuilder.client.widgets.dataset.editor.DataSetDefPreviewTable;
import org.dashbuilder.client.widgets.dataset.editor.DataSetEditor;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefBackendCacheAttributesEditor;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefBasicAttributesEditor;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefClientCacheAttributesEditor;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefRefreshAttributesEditor;
import org.dashbuilder.client.widgets.dataset.event.ErrorEvent;
import org.dashbuilder.client.widgets.dataset.event.TabChangedEvent;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.jbpm.workbench.ks.integration.KieServerDataSetProviderType;
import org.jbpm.workbench.ks.integration.RemoteDataSetDef;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class RemoteDataSetEditorTest  {

    @Mock protected DataSetDefBasicAttributesEditor basicAttributesEditor;
    @Mock protected DataSetDefColumnsFilterEditor columnsAndFilterEditor;
    @Mock protected DataSetDefPreviewTable previewTable;
    @Mock protected DataSetDefBackendCacheAttributesEditor backendCacheAttributesEditor;
    @Mock protected DataSetDefClientCacheAttributesEditor clientCacheAttributesEditor;
    @Mock protected DataSetDefRefreshAttributesEditor refreshEditor;
    @Mock protected DataSetClientServices clientServices;
    @Mock protected LoadingBox loadingBox;
    @Mock protected EventSourceMock<ErrorEvent> errorEvent;
    @Mock protected EventSourceMock<TabChangedEvent> tabChangedEvent;
    @Mock protected DataSetEditor.View view;
    @Mock protected RemoteDataSetDef dataSetDef;
    @Mock RemoteDataSetDefAttributesEditor attributesEditor;
    RemoteDataSetEditor presenter;
    
    @Before
    public void setup() throws Exception {
        this.presenter = new RemoteDataSetEditor(basicAttributesEditor, attributesEditor, columnsAndFilterEditor, 
                previewTable, backendCacheAttributesEditor, clientCacheAttributesEditor, refreshEditor, clientServices,
                loadingBox, errorEvent, tabChangedEvent, view);
        when(dataSetDef.getProvider()).thenReturn(new KieServerDataSetProviderType());
    }

    @Test
    public void testDataSource() {
        Assert.assertEquals(attributesEditor.dataSource, presenter.dataSource());
    }

    @Test
    public void testServerTemplate() {
        Assert.assertEquals(attributesEditor.serverTemplateId, presenter.serverTemplateId());
    }

    @Test
    public void testQueryTarget() {
        Assert.assertEquals(attributesEditor.queryTarget, presenter.queryTarget());
    }

    @Test
    public void testDbSQL() {
        Assert.assertEquals(attributesEditor.dbSQL, presenter.dbSQL());
    }
    
}
