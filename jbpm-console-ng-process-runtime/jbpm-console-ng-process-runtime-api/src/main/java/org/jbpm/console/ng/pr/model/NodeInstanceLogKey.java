package org.jbpm.console.ng.pr.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jbpm.console.ng.ga.service.ItemKey;

@Portable
public class NodeInstanceLogKey implements ItemKey {

    private Long nodeId;

    public NodeInstanceLogKey() {
    }

    public NodeInstanceLogKey(Long nodeId) {
        this.nodeId = nodeId;
    }

    public long getNodeId() {
        return nodeId;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + String.valueOf( nodeId ).hashCode();
        return hash;
    }
}
