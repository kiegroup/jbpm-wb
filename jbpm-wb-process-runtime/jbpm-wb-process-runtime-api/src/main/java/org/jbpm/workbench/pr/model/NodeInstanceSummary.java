/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.model;

import java.io.Serializable;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class NodeInstanceSummary implements Serializable {

    private long id;
    private long processId;
    private String nodeName;
    private String nodeUniqueName;
    private String type;
    private String timestamp;
    private String connection;
    private boolean completed;

    public NodeInstanceSummary(long id, long processId, String nodeName, String nodeUniqueName, String type, String timestamp,
            String connection, boolean completed) {
        super();
        this.id = id;
        this.processId = processId;
        this.nodeName = nodeName;
        this.nodeUniqueName = nodeUniqueName;
        this.type = type;
        this.timestamp = timestamp;
        this.connection = connection;
        this.completed = completed;
    }

    public NodeInstanceSummary() {
    }

    public long getId() {
        return id;
    }

    public long getProcessId() {
        return processId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setProcessId(long processId) {
        this.processId = processId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNodeUniqueName() {
        return nodeUniqueName;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String incomingConnection) {
        this.connection = incomingConnection;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

}
