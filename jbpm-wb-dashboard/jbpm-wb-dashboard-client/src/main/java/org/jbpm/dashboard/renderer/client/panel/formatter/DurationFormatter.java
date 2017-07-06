/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.dashboard.renderer.client.panel.formatter;

import java.util.Date;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.displayer.client.formatter.AbstractValueFormatter;

public class DurationFormatter extends AbstractValueFormatter {

    protected String startDateColumn = null;
    protected String endDateColumn = null;

    public DurationFormatter(String startDateColumn,
                             String endDateColumn) {
        this.startDateColumn = startDateColumn;
        this.endDateColumn = endDateColumn;
    }

    @Override
    public String formatValue(DataSet dataSet,
                              int row,
                              int column) {
        Date end = (Date) dataSet.getValueAt(row,
                                             endDateColumn);
        if (end == null) {
            end = new Date();
        }
        Date start = (Date) dataSet.getValueAt(row,
                                               startDateColumn);
        return formatValue(end.getTime() - start.getTime());
    }

    @Override
    public String formatValue(Object value) {

        if (value == null) {
            return "0s";
        }
        long milliseconds = ((Number) value).longValue();
        long seconds = milliseconds / 1000;
        milliseconds %= 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        long hours = minutes / 60;
        minutes %= 60;
        long days = hours / 24;
        hours %= 24;
        long weeks = days / 7;
        days %= 7;

        StringBuilder buf = new StringBuilder();
        if (weeks > 0) {
            buf.append(weeks).append(" weeks ");
        }
        if (days > 0) {
            buf.append(days).append("d ");
        }
        if (hours > 0) {
            buf.append(hours).append("h ");
        }
        if (minutes > 0) {
            buf.append(minutes).append("m ");
        }
        if (seconds > 0) {
            buf.append(seconds).append("s");
        }
        if (buf.length() == 0) {
            return "0s";
        }
        return buf.toString();
    }
}
