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

package org.jbpm.workbench.common.client.util;

import java.util.Date;

import org.jboss.errai.databinding.client.api.Converter;
import org.jbpm.workbench.common.client.resources.i18n.Constants;

public class DateTimeNAConverter implements Converter<Date, String> {

    private Constants constants = Constants.INSTANCE;

    @Override
    public Date toModelValue(final String value) {
        if (value == null || value.equals("")) {
            return null;
        }

        return DateUtils.createDate(value,
                                    DateUtils.getDateTimeFormatMask());
    }

    @Override
    public String toWidgetValue(final Date date) {
        final String dateTimeStr = DateUtils.getDateTimeStr(date);
        return dateTimeStr.trim().isEmpty() ? constants.NA() : dateTimeStr;
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