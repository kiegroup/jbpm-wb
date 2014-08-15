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

package org.jbpm.console.ng.gc.client.util;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class TaskUtils {
    
    public static enum TaskType {
        PERSONAL, ACTIVE, GROUP, ALL, ADMIN
    }

    public static enum TaskView {
        DAY(1), WEEK(7), MONTH(42), GRID(365);

        private int nrOfDaysToShow;

        TaskView(int nrOfDaysToShow) {
            this.nrOfDaysToShow = nrOfDaysToShow;
        }

        public int getNrOfDaysToShow() {
            return nrOfDaysToShow;
        }
    }

    public static List<String> getStatusByType(TaskType type) {
        ImmutableList<String> status = null;
        switch (type) {
        case ACTIVE:
            status = ImmutableList.of("Ready", "Reserved", "InProgress");
            break;
        case GROUP:
            status = ImmutableList.of("Ready");
            break;
        case ALL:
            status = ImmutableList.of("Created", "Ready", "Reserved", "InProgress", "Suspended", "Failed", "Error", "Exited",
                    "Obsolete", "Completed");
            break;
        case PERSONAL:
            status = ImmutableList.of("Ready", "InProgress", "Created", "Reserved");
            break;
        case ADMIN:
            status = ImmutableList.of("Ready", "InProgress", "Created", "Reserved");
            break;
        default:
            throw new IllegalStateException("Unrecognized view type '" + type + "'!");

        }
        return Lists.newArrayList(status);
    }
}
