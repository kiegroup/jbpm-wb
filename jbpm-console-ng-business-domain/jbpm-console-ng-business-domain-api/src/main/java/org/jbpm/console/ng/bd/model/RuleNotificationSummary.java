/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.console.ng.bd.model;

import java.io.Serializable;
import java.util.Date;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 *
 * @author salaboy
 */
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
