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
package org.jbpm.console.ng.shared.events;

import java.io.Serializable;
import java.util.List;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 *
 */
@Portable
public class TaskChangedEvent implements Serializable {

    private long taskId;
    private String userId;
    private List<String> groupsId;

    public TaskChangedEvent(long taskId, String userId, List<String> groupsId) {
        this.taskId = taskId;
        this.userId = userId;
        this.groupsId = groupsId;
    }

    public TaskChangedEvent(long taskId, String userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    public TaskChangedEvent() {
    }

    public long getTaskId() {
        return taskId;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getGroupsId() {
        return groupsId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setGroupsId(List<String> groupsId) {
        this.groupsId = groupsId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (int) (this.taskId ^ (this.taskId >>> 32));
        hash = 71 * hash + (this.userId != null ? this.userId.hashCode() : 0);
        hash = 71 * hash + (this.groupsId != null ? this.groupsId.hashCode() : 0);
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
        final TaskChangedEvent other = (TaskChangedEvent) obj;
        if (this.taskId != other.taskId) {
            return false;
        }
        if ((this.userId == null) ? (other.userId != null) : !this.userId.equals(other.userId)) {
            return false;
        }
        if (this.groupsId != other.groupsId && (this.groupsId == null || !this.groupsId.equals(other.groupsId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TaskChangedEvent{" + "taskId=" + taskId + ", userId=" + userId + ", groupsId=" + groupsId + '}';
    }
}
