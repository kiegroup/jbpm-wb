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
public class TaskRefreshedEvent implements Serializable {
    private long taskId;
    private String taskName;
    public TaskRefreshedEvent(long taskId, String taskName) {
        this.taskId = taskId;
        this.taskName = taskName;
        
    }

    public TaskRefreshedEvent(long taskId) {
        this.taskId = taskId;
    }

    public TaskRefreshedEvent() {
    }


    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    

}
