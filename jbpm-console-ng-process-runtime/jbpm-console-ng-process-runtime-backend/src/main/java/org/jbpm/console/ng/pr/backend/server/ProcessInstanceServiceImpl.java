/*
 * Copyright 2014 JBoss by Red Hat.
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

package org.jbpm.console.ng.pr.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.service.ProcessInstanceService;
import org.jbpm.kie.services.api.RuntimeDataService;
import org.jbpm.kie.services.impl.model.ProcessInstanceDesc;
import org.uberfire.paging.PageResponse;

/**
 *
 * @author salaboy
 */
@Service
@ApplicationScoped
public class ProcessInstanceServiceImpl implements ProcessInstanceService{

  
  @Inject
  private RuntimeDataService dataService;
  
  @Override
  public PageResponse<ProcessInstanceSummary> getData(QueryFilter filter) {
    PageResponse<ProcessInstanceSummary> response = new PageResponse<ProcessInstanceSummary>();
    List<Integer> states = null;
    String initiator = "";
    if (filter.getParams() != null){
      states = (List<Integer>) filter.getParams().get("states");
      initiator = (String) filter.getParams().get("initiator");
    } 
    Collection<ProcessInstanceDesc> processInstances = dataService.getProcessInstances(states, initiator);
    List<ProcessInstanceSummary> processInstancesSums = new ArrayList<ProcessInstanceSummary>(processInstances.size());
    for (ProcessInstanceDesc pi : processInstances) {

      if (filter.getParams().get("textSearch") == null || ((String) filter.getParams().get("textSearch")).isEmpty()) {
        processInstancesSums.add(ProcessInstanceHelper.adapt(pi));
      } else if (pi.getProcessName().toLowerCase().contains((String) filter.getParams().get("textSearch"))) {
        processInstancesSums.add(ProcessInstanceHelper.adapt(pi));
      }
    }
    sort(processInstancesSums,filter);
    
    response.setStartRowIndex(filter.getOffset());
    response.setTotalRowSize(processInstancesSums.size());
    response.setTotalRowSizeExact(true);
    
    if (!processInstancesSums.isEmpty() && processInstancesSums.size() > (filter.getCount() + filter.getOffset())) {
      response.setPageRowList(new ArrayList<ProcessInstanceSummary>(processInstancesSums.subList(filter.getOffset(), filter.getOffset() + filter.getCount())));
      response.setLastPage(false);
      
    } else {
      response.setPageRowList(new ArrayList<ProcessInstanceSummary>(processInstancesSums.subList(filter.getOffset(), processInstancesSums.size())));
      response.setLastPage(true);
      
    }
    return response;

  }

  private void sort(List<ProcessInstanceSummary> processInstancesSums, final QueryFilter filter) {
    if (filter.getOrderBy().equals("Name")) {
      Collections.sort(processInstancesSums, new Comparator<ProcessInstanceSummary>() {

        @Override
        public int compare(ProcessInstanceSummary o1, ProcessInstanceSummary o2) {
          if (o1 == o2) {
            return 0;
          }

          // Compare the name columns.
          int diff = -1;
          if (o1 != null) {
            diff = (o2 != null) ? o1.getProcessName().compareTo(o2.getProcessName()) : 1;
          }
          return filter.isAscending() ? diff : -diff;

        }
      });
    }else if(filter.getOrderBy().equals("Version")){
      Collections.sort(processInstancesSums, new Comparator<ProcessInstanceSummary>() {

        @Override
        public int compare(ProcessInstanceSummary o1, ProcessInstanceSummary o2) {
          if (o1 == o2) {
            return 0;
          }

          // Compare the name columns.
          int diff = -1;
          if (o1 != null) {
            diff = (o2 != null) ? o1.getProcessVersion().compareTo(o2.getProcessVersion()) : 1;
          }
          return filter.isAscending() ? diff : -diff;

        }
      });
    }
  }
  
}
