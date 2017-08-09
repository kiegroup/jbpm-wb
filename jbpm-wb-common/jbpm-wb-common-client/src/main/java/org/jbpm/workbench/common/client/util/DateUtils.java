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

package org.jbpm.workbench.common.client.util;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;

/**
 * Provides utility methods for manipulating with {@link Date}s.
 */
public class DateUtils {

    public static String DEFAULT_DATE_FORMAT_MASK = "dd/MM/yyyy";
    public static String DEFAULT_DATE_AND_TIME_FORMAT_MASK = "dd/MM/yyyy HH:mm";
    public static String DEFAULT_TIME_FORMAT_MASK = "HH:mm";

    /**
     * Creates new {@link Date} object using default format - "yyyy-MM-dd" (e.g. 2013-04-25).
     * @param dateString string representation of date
     * @return new {@link Date} create from string representation
     */
    public static Date createDate(String dateString) {
        return createDate(dateString,
                          "yyyy-MM-dd");
    }

    /**
     * Creates new {@link Date} object from specified date string and date format.
     * @param dateString string representation
     * @param dateFormat format of the date
     * @return new {@link Date} create from string representation
     */
    public static Date createDate(String dateString,
                                  String dateFormat) {
        DateTimeFormat fmt = DateTimeFormat.getFormat(dateFormat);
        return fmt.parse(dateString);
    }

    /**
     * Check for the system property override, if it isn't exists
     */
    public static String getDateFormatMask() {
        try {
            String fmt = ApplicationPreferences.getDroolsDateFormat();
            return fmt != null ? fmt : DEFAULT_DATE_FORMAT_MASK;
        } catch (Exception e) {
            return DEFAULT_DATE_FORMAT_MASK;
        }
    }

    /**
     * Check for the system property override, if it isn't exists
     */
    public static String getDateTimeFormatMask() {
        try {
            String fmt = ApplicationPreferences.getDroolsDateTimeFormat();
            return fmt != null ? fmt : DEFAULT_DATE_AND_TIME_FORMAT_MASK;
        } catch (Exception e) {
            return DEFAULT_DATE_AND_TIME_FORMAT_MASK;
        }
    }

    public static String getDateStr(Date date) {
        if (date != null) {
            DateTimeFormat format = DateTimeFormat.getFormat(getDateFormatMask());
            return format.format(date);
        }
        return "";
    }

    public static String getDateTimeStr(Date date) {
        if (date != null) {
            DateTimeFormat format = DateTimeFormat.getFormat(getDateTimeFormatMask());
            return format.format(date);
        }
        return "";
    }

}