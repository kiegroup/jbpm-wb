package org.jbpm.console.ng.pr.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.console.ng.ga.model.GenericSummary;

@Portable
public class NodeInstanceLogSummary extends GenericSummary {

    public static final String NODE_TYPE_HUMANTASK="HumanTaskNode";
    public static final String NODE_TYPE_STARTNODE= "StartNode";
    public static final String NODE_TYPE_ENDNODE= "EndNode";
    public static final String NODE_TYPE_SPLIT= "Split";
    public static final String NODE_TYPE_ACTIONNODE="ActionNode";
    public static final String NODE_TYPE_WORKITEMNODE="WorkItemNode";
    public static final String NODE_TYPE_FOREACHNODE="ForEachNode";
    public static final String NODE_TYPE_JOIN="Join";
    public static final String NODE_TYPE_BOUNDARYEVENTNODE="BoundaryEventNode";
    public static final String NODE_TYPE_COMPSITECONTEXTNODE="CompositeContextNode";
    public static final String NODE_TYPE_EVENTNODE="EventNode";
    public static final String NODE_TYPE_COMPOSITENODEEND="CompositeNodeEnd";
    public static final String NODE_TYPE_COMPOSITENODESTART="CompositeNodeStart";
    public static final String NODE_TYPE_CATCHLINKNODE="CatchLinkNode";
    public static final String NODE_TYPE_FAULTNODE="FaultNode";
    public static final String NODE_TYPE_FOREACHJOINNODE="ForEachJoinNode";
    public static final String NODE_TYPE_FOREACHSPLITNODE="ForEachSplitNode";
    public static final String NODE_TYPE_MOCKNODE="MockNode";
    public static final String NODE_TYPE_COMPOSITENODE="CompositeNode";
    public static final String NODE_TYPE_MILESTONENODE="MilestoneNode";
    public static final String NODE_TYPE_RULESETNODE="RuleSetNode";
    public static final String NODE_TYPE_SUBPROCESSNODE="SubProcessNode";
    public static final String NODE_TYPE_THROWLINKNODE="ThrowLinkNode";
    public static final String NODE_TYPE_TIMERNODE="TimerNode";
    public static final String NODE_TYPE_STATEBASEDNODE="StateBasedNode";
    public static final String NODE_TYPE_ASYNCEVENTNODE="AsyncEventNode";
    
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
