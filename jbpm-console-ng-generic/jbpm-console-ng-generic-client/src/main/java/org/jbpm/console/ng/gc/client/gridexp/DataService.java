package org.jbpm.console.ng.gc.client.gridexp;

import org.jbpm.console.ng.gc.client.experimental.pagination.DataMockSummary;

import java.util.ArrayList;
import java.util.List;

public class DataService {

    List<DataMockSummary> getData() {
        List<DataMockSummary> data = new ArrayList<DataMockSummary>();
        data.add( new DataMockSummary("p1-1", "process1", "depid1", "com.dummy", "type1"));
        data.add( new DataMockSummary("p1-2", "process1", "depid2", "com.dummy", "type2"));
        data.add( new DataMockSummary("p2-1", "process2", "depid3", "com.dummy", "type1"));
        data.add( new DataMockSummary("p2-2", "process2", "depid4", "com.dummy", "type1"));
        data.add( new DataMockSummary("p2-3", "process2", "depid5", "com.dummy", "type3"));
        return  data;
    }

}
