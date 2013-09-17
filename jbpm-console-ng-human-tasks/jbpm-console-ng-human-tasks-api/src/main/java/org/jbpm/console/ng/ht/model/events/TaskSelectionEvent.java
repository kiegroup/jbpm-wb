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

package org.jbpm.console.ng.ht.model.events;

import java.io.Serializable;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class TaskSelectionEvent implements Serializable {
    private long taskId;
    private String taskName;
    private String place;

    public TaskSelectionEvent() {
    }

    public TaskSelectionEvent(long taskId) {
        this.taskId = taskId;
    }

    public TaskSelectionEvent(long taskId, String taskName) {
        this.taskId = taskId;
        this.taskName = taskName;
    }

    public TaskSelectionEvent(long taskId, String taskName, String place) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.place = place;
    }
    
    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
    
    public String getTaskName() {
        return taskName;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

}
