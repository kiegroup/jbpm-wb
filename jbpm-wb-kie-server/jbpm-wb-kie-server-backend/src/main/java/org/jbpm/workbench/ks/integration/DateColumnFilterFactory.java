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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;

import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DateIntervalType;

/**
 * Produces filter for date intervals
 *
 */
public class DateColumnFilterFactory {

    private static final DateTimeFormatter MONTH_INTERVAL_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM")
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1).toFormatter();
    private static final DateTimeFormatter YEAR_INTERVAL_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("yyyy")
            .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1).toFormatter();

    private DateColumnFilterFactory() {
        // empty
    }

    public static ColumnFilter createFilter(ColumnGroup cg, List<Comparable> names) {
        DateIntervalType intervalSize = DateIntervalType.getByName(cg.getIntervalSize());
        ColumnFilter defaultFilter = FilterFactory.equalsTo(cg.getSourceId(), names);
        LocalDate i;
        LocalDate ii;
        if (names != null && !names.isEmpty() && intervalSize != null) {
            String value = names.get(0).toString();
            switch (intervalSize) {
                case DAY:
                case DAY_OF_WEEK:
                case WEEK:
                    i = LocalDate.parse(value);
                    ii = i.plusDays(1l);
                    break;
                case MONTH:
                case QUARTER:
                    i = LocalDate.parse(value, MONTH_INTERVAL_FORMAT).withDayOfMonth(1);
                    ii = i.plusMonths(1);
                    break;
                case YEAR:
                    i = LocalDate.parse(value, YEAR_INTERVAL_FORMAT).withDayOfYear(1);
                    ii = i.plusYears(1);
                    break;
                default:
                    return defaultFilter;
            }
            return FilterFactory.between(cg.getSourceId(), i.toString(), ii.toString());
        }

        return defaultFilter;

    }

}
