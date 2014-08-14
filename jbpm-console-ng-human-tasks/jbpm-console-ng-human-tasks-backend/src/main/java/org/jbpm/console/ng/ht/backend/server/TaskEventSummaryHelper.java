/*
 * Copyright 2012 JBoss by Red Hat.
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

package org.jbpm.console.ng.ht.backend.server;

import java.util.ArrayList;
import java.util.List;
import org.jbpm.console.ng.ht.model.TaskEventSummary;


public class TaskEventSummaryHelper {
    public static List<TaskEventSummary> adaptCollection(List<org.kie.internal.task.api.model.TaskEvent> taskEventSums) {
        List<TaskEventSummary> taskEventSummaries = new ArrayList<TaskEventSummary>(taskEventSums.size());
        for (org.kie.internal.task.api.model.TaskEvent taskEventSum: taskEventSums) {
            taskEventSummaries.add(adapt(taskEventSum));
        }
        return taskEventSummaries;
    }
    
    public static TaskEventSummary adapt(org.kie.internal.task.api.model.TaskEvent taskEventSum) {
        return new TaskEventSummary(
                    taskEventSum.getId(), 
                    taskEventSum.getTaskId() ,
                    taskEventSum.getType().name(), 
                    taskEventSum.getUserId(), 
                    taskEventSum.getWorkItemId(),
                    taskEventSum.getLogTime());
    
    }

}
