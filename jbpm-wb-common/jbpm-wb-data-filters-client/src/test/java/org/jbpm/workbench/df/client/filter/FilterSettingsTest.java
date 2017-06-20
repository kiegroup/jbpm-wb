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

package org.jbpm.workbench.df.client.filter;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.junit.Before;
import org.junit.Test;

import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.junit.Assert.*;

public class FilterSettingsTest {

    private FilterSettings settings;

    @Before
    public void setup() {
        settings = new FilterSettings();
        settings.setDataSetLookup(new DataSetLookup());
    }

    @Test
    public void testAddRemoveColumnFilter() {
        final ColumnFilter filterA = equalsTo("columnidA",
                                              "value");
        final ColumnFilter filterB = equalsTo("columnidB",
                                              "value");

        settings.addColumnFilter(filterA);

        assertEquals(1,
                     settings.getDataSetLookup().getFirstFilterOp().getColumnFilterList().size());
        assertTrue(settings.getDataSetLookup().getFirstFilterOp().getColumnFilterList().contains(filterA));

        settings.addColumnFilter(filterB);

        assertEquals(2,
                     settings.getDataSetLookup().getFirstFilterOp().getColumnFilterList().size());
        assertTrue(settings.getDataSetLookup().getFirstFilterOp().getColumnFilterList().contains(filterB));

        settings.removeColumnFilter(filterA);

        assertEquals(1,
                     settings.getDataSetLookup().getFirstFilterOp().getColumnFilterList().size());
        assertTrue(settings.getDataSetLookup().getFirstFilterOp().getColumnFilterList().contains(filterB));

        //Removing again should do nothing
        settings.removeColumnFilter(filterA);

        assertEquals(1,
                     settings.getDataSetLookup().getFirstFilterOp().getColumnFilterList().size());
        assertTrue(settings.getDataSetLookup().getFirstFilterOp().getColumnFilterList().contains(filterB));

        settings.removeColumnFilter(filterB);

        assertEquals(0,
                     settings.getDataSetLookup().getFirstFilterOp().getColumnFilterList().size());
    }
}
