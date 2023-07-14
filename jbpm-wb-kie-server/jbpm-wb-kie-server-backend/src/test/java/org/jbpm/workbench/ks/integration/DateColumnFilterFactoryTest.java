/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.ks.integration;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.dashbuilder.dataset.filter.CoreFunctionFilter;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.junit.Before;
import org.junit.Test;

public class DateColumnFilterFactoryTest {

    private ColumnGroup cg;

    @Before
    public void prepare() {
        cg = new ColumnGroup("test", "test");

    }

    @Test
    public void testDateColumnFilterFactorySecond() {
        cg.setIntervalSize(DateIntervalType.SECOND.name());
        CoreFunctionFilter filter = (CoreFunctionFilter) DateColumnFilterFactory.createFilter(cg, Arrays.asList(
                "2023-10-01 01:01:01", "2023-11-02 02:02:02"));
        assertEquals("test", filter.getColumnId());
        assertEquals("2023-10-01 01:01:01", filter.getParameters().get(0));
        assertEquals("2023-11-02 02:02:02", filter.getParameters().get(1));
    }

    @Test
    public void testDateColumnFilterFactoryMinute() {
        cg.setIntervalSize(DateIntervalType.MINUTE.name());

        CoreFunctionFilter filter = (CoreFunctionFilter) DateColumnFilterFactory.createFilter(cg, Arrays.asList(
                "2023-10-01 01:01", "2023-11-02 02:02"));
        assertEquals("test", filter.getColumnId());
        assertEquals("2023-10-01 01:01:00", filter.getParameters().get(0));
        assertEquals("2023-11-02 02:03:00", filter.getParameters().get(1));
    }

    @Test
    public void testDateColumnFilterFactoryHour() {
        cg.setIntervalSize(DateIntervalType.HOUR.name());

        CoreFunctionFilter filter = (CoreFunctionFilter) DateColumnFilterFactory.createFilter(cg, Arrays.asList(
                "2023-10-01 01", "2023-11-02 02"));
        assertEquals("test", filter.getColumnId());
        assertEquals("2023-10-01 01:00:00", filter.getParameters().get(0));
        assertEquals("2023-11-02 03:00:00", filter.getParameters().get(1));
    }

    @Test
    public void testDateColumnFilterFactoryDay() {
        cg.setIntervalSize(DateIntervalType.DAY.name());

        CoreFunctionFilter filter = (CoreFunctionFilter) DateColumnFilterFactory.createFilter(cg, Arrays.asList(
                "2023-10-01", "2023-11-02"));
        assertEquals("test", filter.getColumnId());
        assertEquals("2023-10-01 00:00:00", filter.getParameters().get(0));
        assertEquals("2023-11-03 00:00:00", filter.getParameters().get(1));
    }

    @Test
    public void testDateColumnFilterFactoryDayOrder() {
        cg.setIntervalSize(DateIntervalType.DAY.name());

        CoreFunctionFilter filter = (CoreFunctionFilter) DateColumnFilterFactory.createFilter(cg, Arrays.asList(
                "2023-11-02", "2023-10-01"));
        assertEquals("test", filter.getColumnId());
        assertEquals("2023-10-01 00:00:00", filter.getParameters().get(0));
        assertEquals("2023-11-03 00:00:00", filter.getParameters().get(1));
    }

    @Test
    public void testDateColumnFilterFactoryDayEqualsValue() {
        cg.setIntervalSize(DateIntervalType.DAY.name());

        CoreFunctionFilter filter = (CoreFunctionFilter) DateColumnFilterFactory.createFilter(cg, Arrays.asList(
                "2023-10-01", "2023-10-01"));
        assertEquals("test", filter.getColumnId());
        assertEquals("2023-10-01 00:00:00", filter.getParameters().get(0));
        assertEquals("2023-10-02 00:00:00", filter.getParameters().get(1));
    }

    @Test
    public void testDateColumnFilterFactoryMonth() {
        cg.setIntervalSize(DateIntervalType.MONTH.name());

        CoreFunctionFilter filter = (CoreFunctionFilter) DateColumnFilterFactory.createFilter(cg, Arrays.asList(
                "2023-10", "2023-11"));
        assertEquals("test", filter.getColumnId());
        assertEquals("2023-10-01 00:00:00", filter.getParameters().get(0));
        assertEquals("2023-12-01 00:00:00", filter.getParameters().get(1));
    }

    @Test
    public void testDateColumnFilterFactoryYear() {
        cg.setIntervalSize(DateIntervalType.YEAR.name());

        CoreFunctionFilter filter = (CoreFunctionFilter) DateColumnFilterFactory.createFilter(cg, Arrays.asList(
                "2023", "2024"));
        assertEquals("test", filter.getColumnId());
        assertEquals("2023-01-01 00:00:00", filter.getParameters().get(0));
        assertEquals("2025-01-01 00:00:00", filter.getParameters().get(1));
    }

}
