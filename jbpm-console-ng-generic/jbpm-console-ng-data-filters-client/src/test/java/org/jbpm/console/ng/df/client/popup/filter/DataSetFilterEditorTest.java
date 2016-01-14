/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.df.client.popup.filter;

import com.google.gwtmockito.GwtMockitoTestRunner;
import static org.junit.Assert.assertFalse;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;


@RunWith(GwtMockitoTestRunner.class)
public class DataSetFilterEditorTest {

    protected DataSetFilterEditor dataSetFilterEditor;


    @Mock
    DataSetMetadata metadata;


    @Before
    public void setup() {
        dataSetFilterEditor = new DataSetFilterEditor();
    }

    @Test
    public void initEmptyListFilterTest() {
        //After initialization want to ckeck if existing DataSet Filter Editor filter List panel is empty
        DataSetFilter filter = new DataSetFilter();
        dataSetFilterEditor.init(metadata, filter, null);
        assertFalse(dataSetFilterEditor.filterListPanel.iterator().hasNext());

    }
    @Test
    public void initEmptyFilterHidingMandatoryFiltersTest() {
        //After initialization want to ckeck if existing DataSet Filter Editor filter List panel is empty
        //even the filter contains data
        DataSetFilter filter = new DataSetFilter();
        ColumnFilter filter1 = FilterFactory.equalsTo("column1", "Test");
        filter.addFilterColumn(filter1);
        dataSetFilterEditor.init(metadata, filter, null);

        assertFalse(dataSetFilterEditor.filterListPanel.iterator().hasNext());

    }



}
