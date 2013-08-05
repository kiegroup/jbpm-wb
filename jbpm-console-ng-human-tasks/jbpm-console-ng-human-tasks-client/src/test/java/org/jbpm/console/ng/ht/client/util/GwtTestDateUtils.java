/*
 * Copyright 2013 JBoss by Red Hat.
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

package org.jbpm.console.ng.ht.client.util;

import static org.jbpm.console.ng.ht.client.util.DateUtils.createDate;

import java.util.Date;

import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;

public class GwtTestDateUtils extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.jbpm.console.ng.ht.JbpmConsoleNGHumanTasksClient";
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testCreateDateWithDefaultFormat() {
        Date date = createDate("2013-05-01");
        System.out.println(date);
        assertEquals(2013, date.getYear() + 1900);
        assertEquals(05, date.getMonth() + 1);
        assertEquals(01, date.getDate());
    }

    @Test
    public void testCreateMalformedDateWithDefaultFormat() {
        try {
            // malformed date string
            createDate("2013-kk-05");
            fail("IllegalArgumentException expected for malformed input!");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testGetWeekRange() {
        Date date = createDate("2013-05-01");
        DateRange weekRange = DateUtils.getWorkWeekDateRange(date);
        assertEquals(createDate("2013-04-29"), weekRange.getStartDate());
        assertEquals(createDate("2013-05-03"), weekRange.getEndDate());

        // part of the week is in 2013 and part in 2012
        date = createDate("2013-01-02");
        weekRange = DateUtils.getWorkWeekDateRange(date);
        assertEquals(createDate("2012-12-31"), weekRange.getStartDate());
        assertEquals(createDate("2013-01-04"), weekRange.getEndDate());
        // same as above, but the specified date is in 2012
        date = createDate("2012-12-31");
        weekRange = DateUtils.getWorkWeekDateRange(date);
        assertEquals(createDate("2012-12-31"), weekRange.getStartDate());
        assertEquals(createDate("2013-01-04"), weekRange.getEndDate());

        date = createDate("2012-12-31");
        weekRange = DateUtils.getWeekDateRange(date);
        assertEquals(createDate("2012-12-31"), weekRange.getStartDate());
        assertEquals(createDate("2013-01-06"), weekRange.getEndDate());

        date = createDate("2013-09-01");
        weekRange = DateUtils.getWeekDateRange(date);
        assertEquals(createDate("2013-08-26"), weekRange.getStartDate());
        assertEquals(createDate("2013-09-01"), weekRange.getEndDate());
    }

    @Test
    public void testGetMonthRange() {
        Date date = createDate("2013-04-25");
        DateRange monthRange = DateUtils.getMonthDateRange(date);
        assertEquals(createDate("2013-04-01"), monthRange.getStartDate());
        assertEquals(createDate("2013-04-30"), monthRange.getEndDate());

        // December as last month
        date = createDate("2013-12-31");
        monthRange = DateUtils.getMonthDateRange(date);
        assertEquals(createDate("2013-12-01"), monthRange.getStartDate());
        assertEquals(createDate("2013-12-31"), monthRange.getEndDate());

        // January as first month
        date = createDate("2013-01-01");
        monthRange = DateUtils.getMonthDateRange(date);
        assertEquals(createDate("2013-01-01"), monthRange.getStartDate());
        assertEquals(createDate("2013-01-31"), monthRange.getEndDate());
    }

    @Test
    public void testIsDateInRange() {
        // single day in range
        Date date = createDate("2013-05-15");
        DateRange dateRange = new DateRange(createDate("2013-05-15"), createDate("2013-05-15"), 0);
        assertTrue(DateUtils.isDateInRange(date, dateRange));

        // start date same as specified
        date = createDate("2013-05-15");
        dateRange = new DateRange(createDate("2013-05-15"), createDate("2014-05-19"), 0);
        assertTrue(DateUtils.isDateInRange(date, dateRange));

        // end date same as specified
        date = createDate("2013-05-15");
        dateRange = new DateRange(createDate("2013-05-13"), createDate("2013-05-15"), 0);
        assertTrue(DateUtils.isDateInRange(date, dateRange));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testCompareDates() {
        // simple case
        Date date1 = createDate("2013-04-15");
        Date date2 = createDate("2013-04-15");
        assertTrue(DateUtils.compareDates(date1, date2) == 0);

        // same date, but different time -> should be equal
        date1 = createDate("2013-04-15");
        date1.setHours(8);
        date1.setMinutes(15);
        date2 = createDate("2013-04-15");
        date2.setHours(10);
        date2.setMinutes(20);
        assertTrue(DateUtils.compareDates(date1, date2) == 0);

        // different dates
        date1 = createDate("2013-04-15");
        date2 = createDate("2013-04-18");
        assertTrue(DateUtils.compareDates(date1, date2) == -1);
        assertTrue(DateUtils.compareDates(date2, date1) == 1);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testAreDatesEqual() {
        // simple case
        Date date1 = createDate("2013-04-15");
        Date date2 = createDate("2013-04-15");
        assertTrue(DateUtils.areDatesEqual(date1, date2));

        // same date, but different time -> should be equal
        date1 = createDate("2013-04-15");
        date1.setHours(8);
        date1.setMinutes(15);
        date2 = createDate("2013-04-15");
        date2.setHours(10);
        date2.setMinutes(20);
        assertTrue(DateUtils.areDatesEqual(date1, date2));

        date1 = createDate("2013-04-15");
        date2 = createDate("2013-04-18");
        assertFalse(DateUtils.areDatesEqual(date1, date2));
    }

    @Test
    public void testGetSameOrClosestDateInPreviousMonth() {
        getAndAssertSameOrClosestDateInPreviousMonth(createDate("2013-02-28"), createDate("2013-01-28"));
        getAndAssertSameOrClosestDateInPreviousMonth(createDate("2013-06-15"), createDate("2013-05-15"));
        // corner cases
        getAndAssertSameOrClosestDateInPreviousMonth(createDate("2013-03-31"), createDate("2013-02-28"));
        getAndAssertSameOrClosestDateInPreviousMonth(createDate("2013-03-30"), createDate("2013-02-28"));
        getAndAssertSameOrClosestDateInPreviousMonth(createDate("2013-03-29"), createDate("2013-02-28"));
        getAndAssertSameOrClosestDateInPreviousMonth(createDate("2013-05-31"), createDate("2013-04-30"));
        // leap-year
        getAndAssertSameOrClosestDateInPreviousMonth(createDate("2016-03-31"), createDate("2016-02-29"));
        getAndAssertSameOrClosestDateInPreviousMonth(createDate("2016-03-30"), createDate("2016-02-29"));
        getAndAssertSameOrClosestDateInPreviousMonth(createDate("2016-03-29"), createDate("2016-02-29"));
    }

    @Test
    public void testGetSameOrClosestDateInNextMonth() {
        getAndAssertSameOrClosestDateInNextMonth(createDate("2013-01-12"), createDate("2013-02-12"));
        getAndAssertSameOrClosestDateInNextMonth(createDate("2013-12-01"), createDate("2014-01-01"));
        getAndAssertSameOrClosestDateInNextMonth(createDate("2013-01-28"), createDate("2013-02-28"));
        // corner cases
        getAndAssertSameOrClosestDateInNextMonth(createDate("2013-01-29"), createDate("2013-02-28"));
        getAndAssertSameOrClosestDateInNextMonth(createDate("2013-01-30"), createDate("2013-02-28"));
        getAndAssertSameOrClosestDateInNextMonth(createDate("2013-01-31"), createDate("2013-02-28"));
        getAndAssertSameOrClosestDateInNextMonth(createDate("2013-03-31"), createDate("2013-04-30"));
        // leap-year
        getAndAssertSameOrClosestDateInNextMonth(createDate("2016-01-29"), createDate("2016-02-29"));
        getAndAssertSameOrClosestDateInNextMonth(createDate("2016-01-30"), createDate("2016-02-29"));
        getAndAssertSameOrClosestDateInNextMonth(createDate("2016-01-31"), createDate("2016-02-29"));
    }

    private void getAndAssertSameOrClosestDateInNextMonth(Date date, Date expectedDate) {
        Date resultDate = DateUtils.getSameOrClosestDateInNextMonth(date);
        assertTrue("Expected " + expectedDate + ", got " + resultDate, DateUtils.areDatesEqual(resultDate, expectedDate));
    }

    private void getAndAssertSameOrClosestDateInPreviousMonth(Date date, Date expectedDate) {
        Date resultDate = DateUtils.getSameOrClosestDateInPreviousMonth(date);
        assertTrue("Expected " + expectedDate + ", got " + resultDate, DateUtils.areDatesEqual(resultDate, expectedDate));
    }

}
