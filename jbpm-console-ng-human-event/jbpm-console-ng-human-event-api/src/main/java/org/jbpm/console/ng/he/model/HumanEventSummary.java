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

import java.io.Serializable;
import java.util.Date;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class HumanEventSummary implements Serializable {

    private static final long serialVersionUID = 1003998546166596096L;

    private String idEvent;
    private String componentEvent;
    private Date eventTime;
    private String action;
    private String user;
    private String status;
    private String level;
    private String module;

    public HumanEventSummary() {
        this.status = StatusHumanEvent.NONE.toString();
        this.level = LevelsHumanEvent.INFO.toString();
        this.eventTime = new Date();
    }

    public HumanEventSummary(String user, ActionsHumanEvent event) {
        this.componentEvent = event.getComponent();
        this.action = event.getAction();
        this.user = user;
        this.status = StatusHumanEvent.NONE.toString();
        this.level = LevelsHumanEvent.INFO.toString();
        this.module = event.getModule();
        this.eventTime = new Date();
    }

    public HumanEventSummary(String idEvent, String user, ActionsHumanEvent event) {
        this.componentEvent = event.getComponent();
        this.action = event.getAction();
        this.idEvent = idEvent;
        this.user = user;
        this.status = StatusHumanEvent.NONE.toString();
        this.level = LevelsHumanEvent.INFO.toString();
        this.module = event.getModule();
        this.eventTime = new Date();
    }

    public HumanEventSummary(String idEvent, String user, ActionsHumanEvent event, StatusHumanEvent status,
            LevelsHumanEvent level) {
        this.componentEvent = event.getComponent();
        this.action = event.getAction();
        this.idEvent = idEvent;
        this.user = user;
        this.status = status.toString();
        this.level = level.toString();
        this.module = event.getModule();
        this.eventTime = new Date();
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }

    public String getComponentEvent() {
        return componentEvent;
    }

    public void setComponentEvent(String componentEvent) {
        this.componentEvent = componentEvent;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((action == null) ? 0 : action.hashCode());
        result = prime * result + ((componentEvent == null) ? 0 : componentEvent.hashCode());
        result = prime * result + ((eventTime == null) ? 0 : eventTime.hashCode());
        result = prime * result + ((idEvent == null) ? 0 : idEvent.hashCode());
        result = prime * result + ((level == null) ? 0 : level.hashCode());
        result = prime * result + ((module == null) ? 0 : module.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
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
        HumanEventSummary other = (HumanEventSummary) obj;
        if (action == null) {
            if (other.action != null)
                return false;
        } else if (!action.equals(other.action))
            return false;
        if (componentEvent == null) {
            if (other.componentEvent != null)
                return false;
        } else if (!componentEvent.equals(other.componentEvent))
            return false;
        if (eventTime == null) {
            if (other.eventTime != null)
                return false;
        } else if (!eventTime.equals(other.eventTime))
            return false;
        if (idEvent == null) {
            if (other.idEvent != null)
                return false;
        } else if (!idEvent.equals(other.idEvent))
            return false;
        if (level == null) {
            if (other.level != null)
                return false;
        } else if (!level.equals(other.level))
            return false;
        if (module == null) {
            if (other.module != null)
                return false;
        } else if (!module.equals(other.module))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "HumanEventSummary [idEvent=" + idEvent + ", componentEvent=" + componentEvent + ", eventTime=" + eventTime
                + ", action=" + action + ", user=" + user + ", status=" + status + ", level=" + level + ", module=" + module
                + "]";
    }

    
}
