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


public enum ActionsHumanEvent {

    TASK_CREATED("TASK", "CREATE"),
    TASK_CREATED_STARTED("TASK", "CREATE-START"),
    TASK_STARTED("TASK", "START"),
    TASK_RELEASED("TASK", "RELEASED"),
    TASK_COMPLETED("TASK", "COMPLETED"),
    TASK_CLAIMED("TASK", "CLAIMED");
    
    private String description;
    private String action;
    
    ActionsHumanEvent(String description, String type){
        this.description = description;
        this.action = type;
    }
    

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


	public String getAction() {
		return action;
	}


	public void setAction(String action) {
		this.action = action;
	}


    
    
}
