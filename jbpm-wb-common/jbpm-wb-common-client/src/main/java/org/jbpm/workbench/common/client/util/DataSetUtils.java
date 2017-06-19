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
import java.util.Optional;

import org.dashbuilder.dataset.DataSet;

public final class DataSetUtils {

    private DataSetUtils() {
    }

    public static Long getColumnLongValue(DataSet currentDataSet,
                                          String columnId,
                                          int index) {
        Object value = currentDataSet.getValueAt(index, columnId);
        return value != null ? Long.parseLong(value.toString()) : null;
    }

    public static String getColumnStringValue(DataSet currentDataSet,
                                              String columnId,
                                              int index) {
        Object value = currentDataSet.getValueAt(index, columnId);
        return value != null ? value.toString() : null;
    }

    public static Date getColumnDateValue(DataSet currentDataSet,
                                          String columnId,
                                          int index) {
        try {
            return (Date) currentDataSet.getValueAt(index, columnId);
        } catch (Exception e) {
            return null;
        }
    }

    public static Boolean getColumnBooleanValue(DataSet currentDataSet,
                                                String columnId,
                                                int index) {
        return Optional.ofNullable(Boolean.valueOf((String) currentDataSet.getValueAt(index, columnId))).orElse(null);
    }

    public static Integer getColumnIntValue(DataSet currentDataSet,
                                        String columnId,
                                        int index) {
        Object value = currentDataSet.getValueAt(index, columnId);
        return value != null ? Integer.parseInt(value.toString()) : null;
    }
}
