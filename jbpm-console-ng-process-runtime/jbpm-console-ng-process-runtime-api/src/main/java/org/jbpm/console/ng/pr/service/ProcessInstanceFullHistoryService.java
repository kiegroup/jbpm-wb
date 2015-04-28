package org.jbpm.console.ng.pr.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.console.ng.ga.service.GenericServiceEntryPoint;
import org.jbpm.console.ng.pr.model.NodeInstanceLogKey;
import org.jbpm.console.ng.pr.model.NodeInstanceLogSummary;

@Remote
public interface ProcessInstanceFullHistoryService extends GenericServiceEntryPoint<NodeInstanceLogKey, NodeInstanceLogSummary> {

    public NodeInstanceLogSummary getItem( Long workItem );
}
