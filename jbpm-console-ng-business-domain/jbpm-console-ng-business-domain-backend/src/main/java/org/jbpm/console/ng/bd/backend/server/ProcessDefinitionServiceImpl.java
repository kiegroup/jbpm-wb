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
package org.jbpm.console.ng.bd.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.bd.service.ProcessDefinitionService;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.pr.backend.server.ProcessHelper;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.kie.services.api.RuntimeDataService;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;

/**
 *
 * @author salaboy
 */
@Service
@ApplicationScoped
public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {

  @Inject
  private RuntimeDataService dataService;

  @Override
  public List<ProcessSummary> getData(final QueryFilter filter) {
    Collection<ProcessAssetDesc> processDefs = dataService.getProcesses();
    List<ProcessSummary> processDefsSums = new ArrayList<ProcessSummary>(processDefs.size());
    for (ProcessAssetDesc pd : processDefs) {

      if (filter.getParams() == null || filter.getParams().get("name") == null || ((String) filter.getParams().get("name")).isEmpty()) {
        processDefsSums.add(ProcessHelper.adapt(pd));
      } else if (pd.getName().toLowerCase().contains((String) filter.getParams().get("name"))) {
        processDefsSums.add(ProcessHelper.adapt(pd));
      }
    }

    if (!processDefsSums.isEmpty() && processDefsSums.size() > (filter.getCount() + filter.getOffset())) {
      return new ArrayList<ProcessSummary>(processDefsSums.subList(filter.getOffset(), filter.getOffset() + filter.getCount()));
    } else {
      return new ArrayList<ProcessSummary>(processDefsSums.subList(filter.getOffset(), processDefsSums.size()));
    }

  }

  private void sort(List<ProcessSummary> processDefsSums, final QueryFilter filter) {
    if (filter.getOrderBy().equals("Name")) {
      Collections.sort(processDefsSums, new Comparator<ProcessSummary>() {

        @Override
        public int compare(ProcessSummary o1, ProcessSummary o2) {
          if (o1 == o2) {
            return 0;
          }

          // Compare the name columns.
          int diff = -1;
          if (o1 != null) {
            diff = (o2 != null) ? o1.getName().compareTo(o2.getName()) : 1;
          }
          return filter.isAscending() ? diff : -diff;

        }
      });
    }else if(filter.getOrderBy().equals("Version")){
      Collections.sort(processDefsSums, new Comparator<ProcessSummary>() {

        @Override
        public int compare(ProcessSummary o1, ProcessSummary o2) {
          if (o1 == o2) {
            return 0;
          }

          // Compare the name columns.
          int diff = -1;
          if (o1 != null) {
            diff = (o2 != null) ? o1.getVersion().compareTo(o2.getVersion()) : 1;
          }
          return filter.isAscending() ? diff : -diff;

        }
      });
    }
  }

  @Override
  public int getDataCount() {
    return dataService.getProcesses().size();
  }

}
