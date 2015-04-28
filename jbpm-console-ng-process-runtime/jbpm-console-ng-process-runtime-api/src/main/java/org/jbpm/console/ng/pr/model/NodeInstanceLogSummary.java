package org.jbpm.console.ng.pr.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.console.ng.ga.model.GenericSummary;

@Portable
public class NodeInstanceLogSummary extends GenericSummary {

    private long nodeId;
    private long processInstanceId;
    private String nodeName;
    private String nodeUniqueName;
    private String type;
    private String timestamp;
    private String connection;
    private boolean completed;

    public NodeInstanceLogSummary(long nodeId, long processInstanceId, String nodeName, String nodeUniqueName, String type, String timestamp,
            String connection, boolean completed) {
        super();
        this.nodeId = nodeId;
        this.processInstanceId = processInstanceId;
        this.nodeName = nodeName;
        this.nodeUniqueName = nodeUniqueName;
        this.type = type;
        this.timestamp = timestamp;
        this.connection = connection;
        this.completed = completed;
    }

    public NodeInstanceLogSummary() {
    }

    public long getNodeId() {
        return nodeId;
    }

    public long getProcessId() {
        return processInstanceId;
    }

    public void setNodeId( long nodeId ) {
        this.nodeId = nodeId;
    }

    public void setProcessInstanceId( long processInstanceId ) {
        this.processInstanceId = processInstanceId;
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setNodeUniqueName( String nodeUniqueName ) {
        this.nodeUniqueName = nodeUniqueName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName( String nodeName ) {
        this.nodeName = nodeName;
    }

    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp( String timestamp ) {
        this.timestamp = timestamp;
    }

    public String getNodeUniqueName() {
        return nodeUniqueName;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection( String incomingConnection ) {
        this.connection = incomingConnection;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted( boolean completed ) {
        this.completed = completed;
    }
}
