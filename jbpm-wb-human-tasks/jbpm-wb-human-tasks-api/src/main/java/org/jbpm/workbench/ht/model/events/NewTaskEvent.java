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

package org.jbpm.workbench.ht.model.events;

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class NewTaskEvent implements Serializable {

    private static final long serialVersionUID = -7547942104170821133L;

    private Long newTaskId;

    private String newTaskName;

    public NewTaskEvent() {
    }

    public NewTaskEvent(Long newTaskId, String newTaskName) {
        this.newTaskId = newTaskId;
        this.newTaskName = newTaskName;
    }

    public Long getNewTaskId() {
        return newTaskId;
    }

    public void setNewTaskId(Long newTaskId) {
        this.newTaskId = newTaskId;
    }

    public String getNewTaskName() {
        return newTaskName;
    }

    public void setNewTaskName(String newTaskName) {
        this.newTaskName = newTaskName;
    }

    @Override
    @SuppressWarnings("PMD.AvoidMultipleUnaryOperators")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((newTaskId == null) ? 0 : newTaskId.hashCode());
        result = ~~result;
        result = prime * result + ((newTaskName == null) ? 0 : newTaskName.hashCode());
        result = ~~result;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NewTaskEvent other = (NewTaskEvent) obj;
        if (newTaskId == null) {
            if (other.newTaskId != null)
                return false;
        } else if (!newTaskId.equals(other.newTaskId))
            return false;
        if (newTaskName == null) {
            if (other.newTaskName != null)
                return false;
        } else if (!newTaskName.equals(other.newTaskName))
            return false;
        return true;
    }

}