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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.pr.model.DocumentKey;
import org.jbpm.console.ng.pr.model.DocumentSummary;
import org.jbpm.console.ng.pr.model.ProcessVariableSummary;
import org.jbpm.console.ng.pr.service.DocumentsService;
import org.jbpm.document.Document;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.task.query.QueryFilterImpl;
import org.uberfire.paging.PageResponse;

/**
 * @author salaboy
 */
@Service
@ApplicationScoped
public class DocumentsServiceImpl implements DocumentsService {

  @Inject
  private RuntimeDataService dataService;

  @Inject
  private DefinitionService bpmn2Service;
  
  @Inject
  private ProcessService processService;
  

  @Override
  public PageResponse<DocumentSummary> getData(QueryFilter filter) {
    PageResponse<DocumentSummary> response = new PageResponse<DocumentSummary>();
    Long processInstanceId = null;
    String processId = "";
    String deploymentId = "";
    if (filter.getParams() != null) {
      processInstanceId = Long.valueOf((String) filter.getParams().get("processInstanceId"));
      processId = (String) filter.getParams().get("processDefId");
      deploymentId = (String) filter.getParams().get("deploymentId");
    }
    // append 1 to the count to check if there are further pages
    org.kie.internal.query.QueryFilter qf = new QueryFilterImpl(filter.getOffset(), filter.getCount() + 1,
            filter.getOrderBy(), filter.isAscending());

    Map<String, String> properties = new HashMap<String, String>(bpmn2Service.getProcessVariables(deploymentId, processId));
    Collection<ProcessVariableSummary> processVariables = VariableHelper.adaptCollection(dataService.getVariablesCurrentState(processInstanceId), properties,
            processInstanceId);
    
    List<DocumentSummary> documents = new ArrayList<DocumentSummary>();
    for (ProcessVariableSummary pv : processVariables) {
      if("org.jbpm.document.Document".equals(pv.getType()){
        Document document = (Document)processService.getProcessInstanceVariable(processInstanceId, pv.getName());
        if(document != null){
          documents.add(new DocumentSummary(document.getName(), document.getLastModified(), document.getSize(), document.getLink()));
        }
      }
    }

//    List<ProcessVariableSummary> processVariablesSums = new ArrayList<ProcessVariableSummary>(processVariables.size());
//    for (ProcessVariableSummary pv : processVariables) {
//
//      if (filter.getParams().get("textSearch") == null || ((String) filter.getParams().get("textSearch")).isEmpty()) {
//        processVariablesSums.add(pv);
//      } else if (pv.getVariableId().toLowerCase().contains((String) filter.getParams().get("textSearch"))) {
//        processVariablesSums.add(pv);
//      }
//    }

    response.setStartRowIndex(filter.getOffset());
    response.setTotalRowSize(documents.size() - 1);
    if (documents.size() > filter.getCount()) {
      response.setTotalRowSizeExact(false);
    } else {
      response.setTotalRowSizeExact(true);
    }

    if (!documents.isEmpty() && documents.size() > (filter.getCount() + filter.getOffset())) {
      response.setPageRowList(new ArrayList<DocumentSummary>(documents.subList(filter.getOffset(), filter.getOffset() + filter.getCount())));
      response.setLastPage(false);

    } else {
      response.setPageRowList(new ArrayList<DocumentSummary>(documents));
      response.setLastPage(true);

    }
    return response;

  }

  @Override
  public DocumentSummary getItem(DocumentKey key) {
    return null;
  }

}
