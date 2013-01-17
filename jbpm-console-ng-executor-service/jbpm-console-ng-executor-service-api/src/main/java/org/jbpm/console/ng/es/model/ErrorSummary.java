/*
 * Copyright 2012 JBoss by Red Hat.
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

/**
 *
 * @author salaboy
 */
@Portable
public class ErrorSummary implements Serializable{

    private Long id;
    private Date time;
    private String message;
    private String stacktrace;
    private Long requestInfoId;

    public ErrorSummary() {
    }

    public ErrorSummary(Long id, Date time, String message, String stacktrace, Long requestInfoId) {
        this.id = id;
        this.time = time;
        this.message = message;
        this.stacktrace = stacktrace;
        this.requestInfoId = requestInfoId;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    public Long getRequestInfoId() {
        return requestInfoId;
    }

    public void setRequestInfoId(Long requestInfoId) {
        this.requestInfoId = requestInfoId;
    }
    
    
}
