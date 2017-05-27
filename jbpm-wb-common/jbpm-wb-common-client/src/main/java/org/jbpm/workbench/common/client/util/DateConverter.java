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
import org.jboss.errai.databinding.client.api.Converter;

public class DateConverter implements Converter<Date, String> {

    public static String DEFAULT_DATE_FORMAT_MASK = DateUtils.getDateFormatMask();

    public static String getDateStr(final Date date) {
        if (date != null) {
            final DateTimeFormat format = DateTimeFormat.getFormat(DEFAULT_DATE_FORMAT_MASK);
            return format.format(date);
        }
        return "";
    }

    public static Date createDate(final String dateString) {
        final DateTimeFormat fmt = DateTimeFormat.getFormat(DEFAULT_DATE_FORMAT_MASK);
        return fmt.parse(dateString);
    }

    @Override
    public Date toModelValue(final String widgetValue) {
        if (widgetValue == null || widgetValue.equals("")) {
            return null;
        }

        return createDate(widgetValue);
    }

    @Override
    public String toWidgetValue(final Date modelValue) {
        return getDateStr(modelValue);
    }

    @Override
    public Class<Date> getModelType() {
        return Date.class;
    }

    @Override
    public Class<String> getComponentType() {
        return String.class;
    }
}