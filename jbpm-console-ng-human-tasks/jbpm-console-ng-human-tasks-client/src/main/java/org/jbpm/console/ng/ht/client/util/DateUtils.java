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

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * Provides utility methods for manipulating with {@link Date}s.
 */
public class DateUtils {
    /**
     * Creates new {@link Date} object using default format - "yyyy-MM-dd" (e.g. 2013-04-25).
     * 
     * @param dateString string representation of date
     * 
     * @return new {@link Date} create from string representation
     */
    public static Date createDate(String dateString) {
        return createDate(dateString, "yyyy-MM-dd");
    }

    /**
     * Creates new {@link Date} object from specified date string and date format.
     * 
     * @param dateString string representation
     * @param dateFormat format of the date
     * @return new {@link Date} create from string representation
     */
    public static Date createDate(String dateString, String dateFormat) {
        DateTimeFormat fmt = DateTimeFormat.getFormat(dateFormat);
        return fmt.parse(dateString);
    }

    /**
     * Returns a {@link DateRange} starting on first working day of week in which the specified date is and ending on last
     * working day (Friday) of that week. Total of 5 days are returned.
     * 
     * @param date date from which to get the week date range
     * @return {@link DateRange} representing the week in which the specified date is
     */
    @SuppressWarnings("deprecation")
    public static DateRange getWeekDateRange(Date date) {
        // TODO handle different week starts (Sunday X Monday)
        Date startDate = new Date(date.getTime());
        int day = startDate.getDay() - 1;
        CalendarUtil.addDaysToDate(startDate, -day);

        Date endDate = new Date(startDate.getTime());
        CalendarUtil.addDaysToDate(endDate, 4);
        return new DateRange(startDate, endDate);
    }

    /**
     * Returns a {@link DateRange} starting on first day of month in which the specified date is and ending on last day of that
     * month.
     * 
     * @param date date from which to get the mont date range
     * @return {@link DateRange} representing the month in which the specified date is
     */
    @SuppressWarnings("deprecation")
    public static DateRange getMonthDateRange(Date date) {
        Date startDate = new Date(date.getTime());
        CalendarUtil.setToFirstDayOfMonth(startDate);
        // the above method will set hours to 12
        startDate.setHours(0);

        Date endDate = new Date(date.getTime());
        CalendarUtil.setToFirstDayOfMonth(endDate);
        CalendarUtil.addMonthsToDate(endDate, 1);
        CalendarUtil.addDaysToDate(endDate, -1);
        endDate.setHours(0);
        return new DateRange(startDate, endDate);
    }
}
