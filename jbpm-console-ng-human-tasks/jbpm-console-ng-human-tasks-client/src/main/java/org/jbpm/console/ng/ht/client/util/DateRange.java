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

/**
 * Represent range of dates, with start date and end date (including).
 */
public class DateRange {
    private final Date startDate;
    private final Date endDate;
    private final int  daysInBetween;

    public DateRange(Date startDate, Date endDate, int daysInBetween) {
        super();
        this.startDate = startDate;
        this.endDate = endDate;
        this.daysInBetween = daysInBetween;
    }

    public Date getStartDate() {
        return new Date(startDate.getTime());
    }

    public Date getEndDate() {
        return new Date(endDate.getTime());
    }

    public int getDaysInBetween() {
        return daysInBetween;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.startDate != null ? this.startDate.hashCode() : 0);
        hash = 23 * hash + (this.endDate != null ? this.endDate.hashCode() : 0);
        hash = 23 * hash + this.daysInBetween;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DateRange other = (DateRange) obj;
        if (this.startDate != other.startDate && (this.startDate == null || !this.startDate.equals(other.startDate))) {
            return false;
        }
        if (this.endDate != other.endDate && (this.endDate == null || !this.endDate.equals(other.endDate))) {
            return false;
        }
        if (this.daysInBetween != other.daysInBetween) {
            return false;
        }
        return true;
    }
    
    

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DateRange [startDate=");
        builder.append(startDate);
        builder.append(", endDate=");
        builder.append(endDate);
        builder.append(", daysInBetween=");
        builder.append(daysInBetween);
        builder.append("]");
        return builder.toString();
    }

}
