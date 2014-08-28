/*
 * Copyright 2014 JBoss by Red Hat.
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

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.console.ng.ga.service.ItemKey;

/**
 *
 * @author salaboy
 */
@Portable
public class TaskKey implements ItemKey {
   private Long taskId;

  public TaskKey(Long taskId) {
    this.taskId = taskId;
  }

  public TaskKey() {
  }

  public Long getTaskId() {
    return taskId;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 47 * hash + (this.taskId != null ? this.taskId.hashCode() : 0);
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
    final TaskKey other = (TaskKey) obj;
    if (this.taskId != other.taskId && (this.taskId == null || !this.taskId.equals(other.taskId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "TaskKey{" + "taskId=" + taskId + '}';
  }
   
   
}
