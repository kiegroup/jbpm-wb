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
	private static final long serialVersionUID = 8560376197262109541L;
	
	private long id;
	private String descriptionEvent;
	private Date eventTime;
	// Is the ID of the event(task, proccess)
	private long idEvent;
	//TODO ver si hay algun drama de que esto sea un enum
	private String typeEvent;

	public HumanEventSummary() {

	}

	public HumanEventSummary(String descriptionEvent) {
		super();
		this.descriptionEvent = descriptionEvent;
		this.eventTime = new Date();
	}

	public HumanEventSummary(String itemHistory, long idEvent) {
		super();
		this.descriptionEvent = itemHistory;
		this.idEvent = idEvent;
		this.eventTime = new Date();
	}

	public String getDescriptionEvent() {
		return descriptionEvent;
	}

	public void setDescriptionEvent(String descriptionEvent) {
		this.descriptionEvent = descriptionEvent;
	}

	public String getTypeEvent() {
		return typeEvent;
	}

	public void setTypeEvent(String typeEvent) {
		this.typeEvent = typeEvent;
	}

	public Date getEventTime() {
		return eventTime;
	}

	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

    public long getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(long idEvent) {
        this.idEvent = idEvent;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((descriptionEvent == null) ? 0 : descriptionEvent.hashCode());
        result = prime * result + ((eventTime == null) ? 0 : eventTime.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + (int) (idEvent ^ (idEvent >>> 32));
        result = prime * result + ((typeEvent == null) ? 0 : typeEvent.hashCode());
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
        if (descriptionEvent == null) {
            if (other.descriptionEvent != null)
                return false;
        } else if (!descriptionEvent.equals(other.descriptionEvent))
            return false;
        if (eventTime == null) {
            if (other.eventTime != null)
                return false;
        } else if (!eventTime.equals(other.eventTime))
            return false;
        if (id != other.id)
            return false;
        if (idEvent != other.idEvent)
            return false;
        if (typeEvent == null) {
            if (other.typeEvent != null)
                return false;
        } else if (!typeEvent.equals(other.typeEvent))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "HumanEventSummary [id=" + id + ", descriptionEvent=" + descriptionEvent + ", eventTime=" + eventTime
                + ", idEvent=" + idEvent + ", typeEvent=" + typeEvent + "]";
    }



}
