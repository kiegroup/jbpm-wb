/*
 * Copyright 2013 JBoss by Red Hat.
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

package org.jbpm.console.ng.pr.model.events;

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class NewProcessInstanceEvent implements Serializable {
    
    
    private Long newProcessInstanceId;
    
    private String newProcessDefId;
    
    
    public NewProcessInstanceEvent(){
    }

    public NewProcessInstanceEvent(Long newProcessInstanceId, String newProcessDefId) {
        this.newProcessInstanceId = newProcessInstanceId;
        this.newProcessDefId = newProcessDefId;
    }

    public Long getNewProcessInstanceId() {
        return newProcessInstanceId;
    }

    public void setNewProcessInstanceId(Long newProcessInstanceId) {
        this.newProcessInstanceId = newProcessInstanceId;
    }

    public String getNewProcessDefId() {
        return newProcessDefId;
    }

    public void setNewProcessDefId(String newProcessDefId) {
        this.newProcessDefId = newProcessDefId;
    }


}
