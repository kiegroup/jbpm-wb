/*
 * Copyright 2013 JBoss Inc
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

package org.jbpm.console.ng.he.model;


public enum ActionHistoryEnum {

	TEST("test", "Test"),
    NEW_TASK("New Task", "Create"),
    TASK_CREATED("Task Created", "Create"),
    TASK_CREATED_STARTED("Task Created and Started", "Create"),
    FILTER_EVENT("Filter Event", "Search");
    
    private String description;
    private String type;
    
    ActionHistoryEnum(String description, String type){
        this.description = description;
        this.type = type;
    }
    

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }
    
}
