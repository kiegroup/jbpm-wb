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
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.pr.model.ProcessDefinitionKey;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.service.ProcessDefinitionService;

import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.task.query.QueryFilterImpl;
import org.uberfire.paging.PageResponse;

/**
 * @author salaboy
 */
@Service
@ApplicationScoped
public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {

    @Inject
    private RuntimeDataService dataService;

    @Override
    public PageResponse<ProcessSummary> getData(final QueryFilter filter) {
        PageResponse<ProcessSummary> response = new PageResponse<ProcessSummary>();
        // append 1 to the count to check if there are further pages
        org.kie.internal.query.QueryFilter qf = new QueryFilterImpl(filter.getOffset(), filter.getCount()+1,
                filter.getOrderBy(), filter.isAscending());
        Collection<ProcessDefinition> processDefs = dataService.getProcesses(qf);
        List<ProcessSummary> processDefsSums = new ArrayList<ProcessSummary>(processDefs.size());
        for (ProcessDefinition pd : processDefs) {

            if (filter.getParams() == null || filter.getParams().get("textSearch") == null || ((String) filter.getParams().get("textSearch")).isEmpty()) {
                processDefsSums.add(ProcessHelper.adapt(pd));
            } else if (pd.getName().toLowerCase().contains((String) filter.getParams().get("textSearch"))) {
                processDefsSums.add(ProcessHelper.adapt(pd));
            }
        }
        response.setStartRowIndex(filter.getOffset());
        response.setTotalRowSize(processDefsSums.size()-1);
        if(processDefsSums.size() > filter.getCount()){
            response.setTotalRowSizeExact(false);
        } else{
            response.setTotalRowSizeExact(true);
        }
        response.setPageRowList(processDefsSums);

        if (!processDefsSums.isEmpty() && processDefsSums.size() > (filter.getCount() + filter.getOffset())) {
            response.setPageRowList(new ArrayList<ProcessSummary>(processDefsSums.subList(filter.getOffset(), filter.getOffset() + filter.getCount())));
            response.setLastPage(false);

        } else {
            response.setPageRowList(new ArrayList<ProcessSummary>(processDefsSums));
            response.setLastPage(true);

        }
        return response;

    }

    @Override
    public ProcessSummary getItem(ProcessDefinitionKey key) {
        return ProcessHelper.adapt(dataService.getProcessesByDeploymentIdProcessId(key.getDeploymentId(), key.getProcessId()));
    }

}
