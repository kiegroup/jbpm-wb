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

package org.jbpm.console.ng.he.client.event;
import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class HumanEvent implements Serializable {

    private static final long serialVersionUID = 6364112766319847857L;
    
    String key;
    
    String user;
    
    ActionHumanEvent event;
    
    StatusHumanEvent status;
    
    LevelHumanEvent level;
    
    
    public HumanEvent(){
        
    }

    public HumanEvent(String key, String user, ActionHumanEvent event, StatusHumanEvent status, LevelHumanEvent level) {
        this.key = key;
        this.user = user;
        this.event = event;
        this.status = status;
        this.level = level;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ActionHumanEvent getEvent() {
        return event;
    }

    public void setEvent(ActionHumanEvent event) {
        this.event = event;
    }

    public StatusHumanEvent getStatus() {
        return status;
    }

    public void setStatus(StatusHumanEvent status) {
        this.status = status;
    }

    public LevelHumanEvent getLevel() {
        return level;
    }

    public void setLevel(LevelHumanEvent level) {
        this.level = level;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((event == null) ? 0 : event.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((level == null) ? 0 : level.hashCode());
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
        HumanEvent other = (HumanEvent) obj;
        if (event != other.event)
            return false;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        if (level != other.level)
            return false;
        if (status != other.status)
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
        return "HumanEvent [key=" + key + ", user=" + user + ", event=" + event + ", status=" + status + ", level=" + level
                + "]";
    }

}
