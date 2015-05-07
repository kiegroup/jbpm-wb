package org.jbpm.console.ng.pr.backend.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.jbpm.console.ng.pr.model.NodeInstanceLogSummary;
import org.jbpm.services.api.model.NodeInstanceDesc;

public class NodeInstanceLogHelper {

    public static List<NodeInstanceLogSummary> adaptCollection( Collection<NodeInstanceDesc> nodeInstances ) {
        List<NodeInstanceLogSummary> nodeInstancesSummary = new ArrayList<NodeInstanceLogSummary>();
        for ( NodeInstanceDesc ni : nodeInstances ) {
//            if ( !ni.getNodeType().equals( "Split" ) ) {
                nodeInstancesSummary.add( adapt( ni ) );
//            }
        }
        return nodeInstancesSummary;
    }

    public static List<NodeInstanceLogSummary> adaptCollectionFiltered( Collection<NodeInstanceDesc> nodeInstances ) {
        List<NodeInstanceLogSummary> nodeInstancesSummary = new ArrayList<NodeInstanceLogSummary>();
        for ( NodeInstanceDesc ni : nodeInstances ) {
//            if ( !ni.getNodeType().equals( "Split" ) ) {
                nodeInstancesSummary.add( adapt( ni ) );
//            }
        }
        return nodeInstancesSummary;
    }

    public static NodeInstanceLogSummary adapt( NodeInstanceDesc ni ) {
        Date date = ni.getDataTimeStamp();
        String formattedDate = new SimpleDateFormat( "d/MMM/yy HH:mm:ss" ).format( date );
        return new NodeInstanceLogSummary( ni.getId(), ni.getProcessInstanceId(), ni.getName(), ni.getNodeId(), ni.getNodeType(),
                formattedDate, ni.getConnection(), ni.isCompleted() );
    }
}
