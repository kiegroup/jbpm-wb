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

    public static DateRange getWorkWeekDateRange(Date date) {
        return getDateRangeStartingOnMonday(date, 5);
    }

    public static DateRange getWeekDateRange(Date date) {
        return getDateRangeStartingOnMonday(date, 7);
    }

    @SuppressWarnings("deprecation")
    private static DateRange getDateRangeStartingOnMonday(Date dateWithinTheWeek, int nrOfDaysTotal) {
        Date startDate = new Date(dateWithinTheWeek.getTime());
        int day = startDate.getDay() - 1;
        int daysAfterMonday = day;
        if (day == -1) {
            // corner case when the date within the week in Sunday and thus getDay() == 0 (and day == -1), so we need Monday
            // from that week, which is 6 days back
            daysAfterMonday = 6;
        }
        CalendarUtil.addDaysToDate(startDate, -daysAfterMonday);
        Date endDate = new Date(startDate.getTime());
        CalendarUtil.addDaysToDate(endDate, nrOfDaysTotal - 1);
        return new DateRange(startDate, endDate, CalendarUtil.getDaysBetween(startDate, endDate));
    }

    /**
     * Returns a {@link DateRange} starting on first day of month in which the specified date is and ending on last day of that
     * month.
     *
     * @param date date from which to get the month date range
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

        return new DateRange(startDate, endDate, CalendarUtil.getDaysBetween(startDate, endDate));
    }

    /**
     * Determines if the specified date is within the specified range. Including both start and end date.
     *
     * @param date the date to test
     * @param dateRange date range to be tested with
     * @return true if the date is within the range, otherwise false
     */
    public static boolean isDateInRange(Date date, DateRange dateRange) {
        if (date.compareTo(dateRange.getStartDate()) >= 0 &&
                date.compareTo(dateRange.getEndDate()) <= 0) {
            return true;
        }
        return false;
    }

    /**
     * Compares two dates based only on day, month and year. Time information is not considered.
     *
     * @param firstDate first date
     * @param secondDate second date
     * @return -1 if first date if before the second, 0 if the dates are equal, otherwise 1 (first is after the second)
     */
    @SuppressWarnings("deprecation")
    public static int compareDates(Date firstDate, Date secondDate) {
        Date firstDateClone = new Date(firstDate.getYear(), firstDate.getMonth(), firstDate.getDate());
        Date secondDateClone = new Date(secondDate.getYear(), secondDate.getMonth(), secondDate.getDate());
        return firstDateClone.compareTo(secondDateClone);
    }

    /**
     * Determines if two dates are equal based only on day, month and year. Time information is not considered.
     *
     * @param firstDate first date
     * @param secondDate second date
     * @return true if the dates have identical year, month and day
     */
    public static boolean areDatesEqual(Date firstDate, Date secondDate) {
        return compareDates(firstDate, secondDate) == 0;
    }

    @SuppressWarnings("deprecation")
    public static Date getSameOrClosestDateInPreviousMonth(Date date) {
        Date desiredDate = new Date(date.getTime());
        CalendarUtil.addMonthsToDate(desiredDate, -1);
        if (desiredDate.getMonth() == date.getMonth()) {
            // did not go one month back
            // e.g. 31 May -> 1st May, because April does not have 31st and thus the day is set to 1st May
            CalendarUtil.setToFirstDayOfMonth(desiredDate);
            CalendarUtil.addDaysToDate(desiredDate, -1);
        }
        return desiredDate;
    }

    @SuppressWarnings("deprecation")
    public static Date getSameOrClosestDateInNextMonth(Date date) {
        Date desiredDate = new Date(date.getTime());
        CalendarUtil.addMonthsToDate(desiredDate, 1);
        if (desiredDate.getMonth() > date.getMonth() + 1) {
            // skipped one month, e.g. 30 January -> 2nd (or 1st for leap-year) March, because February does not have 30th
            // set the date to last day of previous month
            CalendarUtil.setToFirstDayOfMonth(desiredDate);
            CalendarUtil.addDaysToDate(desiredDate, -1);
        }
        return desiredDate;
    }

}
