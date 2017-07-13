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

package org.jbpm.workbench.common.client.list;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.uberfire.client.views.pfly.widgets.Moment;

import static org.uberfire.client.views.pfly.widgets.Moment.Builder.moment;

public enum DatePickerRange {

    LastHour(Constants.INSTANCE.LastHour(),
             () -> moment().subtract(1,
                                     "hours"),
             () -> moment()),

    Today(Constants.INSTANCE.Today(),
          () -> moment().startOf("day"),
          () -> moment()),

    Last24Hours(Constants.INSTANCE.LastHours(24),
                () -> moment().subtract(24,
                                        "hours"),
                () -> moment()),

    Last7Days(Constants.INSTANCE.LastDays(7),
              () -> moment().subtract(7,
                                      "days").startOf("day"),
              () -> moment()),

    Last30Days(Constants.INSTANCE.LastDays(30),
               () -> moment().subtract(30,
                                       "days").startOf("day"),
               () -> moment());

    private String label;

    private Supplier<Moment> startDate;

    private Supplier<Moment> endDate;

    DatePickerRange(String label,
                    Supplier<Moment> startDate,
                    Supplier<Moment> endDate) {
        this.label = label;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Optional<DatePickerRange> getDatePickerRangeFromLabel(final String label) {
        return Arrays.stream(DatePickerRange.values())
                .filter(e -> e.getLabel().equals(label))
                .findFirst();
    }

    public String getLabel() {
        return label;
    }

    public Moment getStartDate() {
        return startDate.get();
    }

    public Moment getEndDate() {
        return endDate.get();
    }

}
