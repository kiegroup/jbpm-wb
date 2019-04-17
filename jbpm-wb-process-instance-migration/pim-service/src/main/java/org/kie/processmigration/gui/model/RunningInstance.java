/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.processmigration.gui.model;

import java.util.Date;
import org.kie.server.api.model.instance.ProcessInstance;

import java.text.SimpleDateFormat;
import javax.xml.datatype.XMLGregorianCalendar;

public class RunningInstance {

    private int id;
    private Long processInstanceId;
    private String name;
    private String description;
    private Integer state;
    private String startTime;

    public RunningInstance(int i, ProcessInstance p) {
        id = i;
        processInstanceId = p.getId();
        name = p.getProcessName();
        description = p.getProcessInstanceDescription();
        state = p.getState();
        startTime = p.getDate().toString();
    }

    private String convertDate(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return null;
        }
        Date date = calendar.toGregorianCalendar().getTime();

        String result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
