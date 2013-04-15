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
package org.jbpm.console.ng.ht.model;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.concurrent.Immutable;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.marshalling.client.api.annotations.MapsTo;

@Immutable
@Portable
public class Day implements Serializable {
    private final Date date;
    private final String dayOfWeekName;
    
    public Day(@MapsTo("date") Date date, @MapsTo("dayOfWeekName") String dayOfWeekName) {
        this.date = date;
        this.dayOfWeekName = dayOfWeekName;
    }
    
    public Date getDate() {
        return new Date(date.getTime());
    }

    public String getDayOfWeekName() {
        return dayOfWeekName;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (!(other instanceof Day)) return false;
        Day otherDay = (Day)other;
        return this.dayOfWeekName.equals(otherDay.dayOfWeekName);
    }
    
    @Override
    public int hashCode() {
        return dayOfWeekName.hashCode();
    }
}
