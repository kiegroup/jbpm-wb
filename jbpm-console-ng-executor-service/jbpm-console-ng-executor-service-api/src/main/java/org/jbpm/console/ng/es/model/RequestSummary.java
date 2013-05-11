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

package org.jbpm.console.ng.es.model;

import java.io.Serializable;
import java.util.Date;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RequestSummary implements Serializable {

    private Long id;
    private Date time;
    private String status;
    private String commandName;
    private String message;
    // Business Key for callback
    private String key;
    // Number of times that this request must be retryied
    private int retries = 0;
    // Number of times that this request has been executed
    private int executions = 0;

    public RequestSummary() {
    }

    public RequestSummary(Long id, Date time, String status, String commandName, String message, String key) {
        this.id = id;
        this.time = time;
        this.status = status;
        this.commandName = commandName;
        this.message = message;
        this.key = key;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getExecutions() {
        return executions;
    }

    public void setExecutions(int executions) {
        this.executions = executions;
    }

}
