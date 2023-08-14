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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Produces filter for date intervals
 *
 */
public class DateColumnFilterFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateColumnFilterFactory.class);

    private static final DateTimeFormatter OUTPUT_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy[-MM][-dd][ HH][:mm][:ss]")
            .parseDefaulting(ChronoField.YEAR_OF_ERA, 0)
            .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter()
            .withLocale(Locale.getDefault())
            .withZone(ZoneId.from(ZoneOffset.UTC));

    private DateColumnFilterFactory() {
        // empty
    }

    @SuppressWarnings("unchecked")
    public static ColumnFilter createFilter(ColumnGroup cg, @SuppressWarnings("rawtypes") List<Comparable> names) {
        DateIntervalType intervalSize = DateIntervalType.getByName(cg.getIntervalSize());
        ColumnFilter defaultFilter = FilterFactory.equalsTo(cg.getSourceId(), names);
        Collections.sort(names);
        if (names != null && !names.isEmpty() && intervalSize != null) {
            String v1 = names.get(0).toString();
            String v2 = names.get(names.size() - 1).toString();
            LocalDateTime i = LocalDateTime.parse(v1, OUTPUT_FORMATTER);
            LocalDateTime ii = LocalDateTime.parse(v2, OUTPUT_FORMATTER);
            if (v1.equals(v2) || intervalSize != DateIntervalType.SECOND) {
                ii = advanceUnit(intervalSize, ii);
            }
            try {
                return FilterFactory.between(cg.getSourceId(), OUTPUT_FORMATTER.format(i), OUTPUT_FORMATTER.format(ii));
            } catch (Exception e) {
                LOGGER.info("Not able to parse dates for names {} and interval {}.", names, intervalSize);
                LOGGER.debug("Error parsing dates while building request to Kie Server.", e);
            }
        }
        return defaultFilter;
    }

    private static LocalDateTime advanceUnit(DateIntervalType intervalSize, LocalDateTime ii) {
        switch (intervalSize) {
            case DAY:
            case WEEK:
            case DAY_OF_WEEK:
                return ii.plus(1l, ChronoUnit.DAYS);
            case HOUR:
                return ii.plus(1l, ChronoUnit.HOURS);
            case MINUTE:
                return ii.plus(1l, ChronoUnit.MINUTES);
            case SECOND:
                return ii.plus(1l, ChronoUnit.SECONDS);
            case MONTH:
            case QUARTER:
                return ii.plus(1l, ChronoUnit.MONTHS);
            case YEAR:
                return ii.plus(1l, ChronoUnit.YEARS);
            default:
                return ii;

        }

    }

}
