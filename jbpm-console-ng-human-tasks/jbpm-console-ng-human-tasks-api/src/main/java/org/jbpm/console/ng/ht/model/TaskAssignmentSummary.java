/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.console.ng.ga.model.GenericSummary;
@Portable
public class TaskAssignmentSummary extends GenericSummary {
    
    private Long taskId;
    private String taskName;
    private String actualOwner;
    private List<String> potOwnersString;
    
    public TaskAssignmentSummary(){
        
    }
    public TaskAssignmentSummary(Long taskId,
                                String taskName,
                                String actualOwner,
                                List<String> potOwnersString) {
        super();
        this.taskId = taskId;
        this.taskName = taskName;
        this.actualOwner = actualOwner;
        this.potOwnersString = potOwnersString;
    }
    public Long getTaskId() {
        return taskId;
    }
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public String getActualOwner() {
        return actualOwner;
    }
    public void setActualOwner(String actualOwner) {
        this.actualOwner = actualOwner;
    }
    public List<String> getPotOwnersString() {
        return potOwnersString;
    }
    public void setPotOwnersString(List<String> potOwnersString) {
        this.potOwnersString = potOwnersString;
    }
    
    
}
