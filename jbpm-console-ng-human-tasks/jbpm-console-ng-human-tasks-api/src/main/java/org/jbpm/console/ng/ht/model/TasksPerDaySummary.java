/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.console.ng.ht.model;

import java.util.List;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.console.ng.ga.model.GenericSummary;

@Portable
public class TasksPerDaySummary extends GenericSummary {

    private Day day;
    private List<TaskSummary> tasks;
    

    public TasksPerDaySummary(Day day, List<TaskSummary> tasks) {
        super();
        this.id = day.getDayOfWeekName();
        this.name = day.getDayOfWeekName();
        this.day = day;
        this.tasks = tasks;
        
    }

    public TasksPerDaySummary() {
    }

  public Day getDay() {
    return day;
  }

  public List<TaskSummary> getTasks() {
    return tasks;
  }

    
  

}
