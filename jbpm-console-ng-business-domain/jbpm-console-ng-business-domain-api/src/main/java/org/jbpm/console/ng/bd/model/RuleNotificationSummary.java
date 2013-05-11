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
package org.jbpm.console.ng.bd.model;

import java.io.Serializable;
import java.util.Date;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RuleNotificationSummary implements Serializable {

    private long id;
    private String notification;
    private int sessionId;
    private Date dataTimeStamp;

    public RuleNotificationSummary() {
    }

    
    public RuleNotificationSummary(long id, String notification, int sessionId, Date dataTimeStamp) {
        this.id = id;
        this.notification = notification;
        this.sessionId = sessionId;
        this.dataTimeStamp = dataTimeStamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public Date getDataTimeStamp() {
        return dataTimeStamp;
    }

    public void setDataTimeStamp(Date dataTimeStamp) {
        this.dataTimeStamp = dataTimeStamp;
    }

    @Override
    public String toString() {
        return "RuleNotificationSummary{" + "id=" + id + ", notification=" + notification + ", sessionId=" + sessionId + ", dataTimeStamp=" + dataTimeStamp + '}';
    }
    
    
}
