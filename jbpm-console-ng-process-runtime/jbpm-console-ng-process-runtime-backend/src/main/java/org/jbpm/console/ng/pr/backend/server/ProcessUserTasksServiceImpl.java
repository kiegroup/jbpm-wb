package org.jbpm.console.ng.pr.backend.server;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.ga.model.QueryFilter;
import org.jbpm.console.ng.pr.model.UserTaskSummary;
import org.jbpm.console.ng.pr.model.UserTaskkey;
import org.jbpm.console.ng.pr.service.ProcessUserTasksService;
import org.jbpm.services.api.RuntimeDataService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.InternalTaskService;
import org.uberfire.paging.PageResponse;


@Service
@ApplicationScoped
public class ProcessUserTasksServiceImpl implements ProcessUserTasksService {
	
	@Inject
	private InternalTaskService taskService;
	
	@Inject
    private RuntimeDataService dataService;

	@Override
	public PageResponse<UserTaskSummary> getData(QueryFilter filter) {
		 PageResponse<UserTaskSummary> response = new PageResponse<UserTaskSummary>();
	     Long processInstanceId = -1L;
	     if (filter.getParams() != null) {
	      	processInstanceId = Long.parseLong(filter.getParams().get("processInstanceId").toString());
	     }
	     org.kie.internal.query.QueryFilter qf = new org.kie.internal.query.QueryFilter(filter.getOffset(), filter.getCount() + 1,
               filter.getOrderBy(), filter.isAscending());
	     qf.setFilterParams(filter.getFilterParams());
	     String[] statusesString = new String[]{"Created", "Ready", "Reserved", "InProgress", "Completed"};
	     List<Status> statuses = new ArrayList<Status>();
	     if(statusesString != null){
	         for (String s : statusesString) {
	           statuses.add(Status.valueOf(s));
	         }
	     }
	     List<UserTaskSummary> userTaskSummaries = ProcessUserTaskHelper.adaptCollection(dataService.getTasksByStatusByProcessInstanceId(processInstanceId,statuses,qf));
	     response.setStartRowIndex(filter.getOffset());
	     response.setTotalRowSize(userTaskSummaries.size() - 1);
	     if(userTaskSummaries.size() > filter.getCount()){
	       response.setTotalRowSizeExact(false);
	     }else{
	       response.setTotalRowSizeExact(true);
	     }

	     if (!userTaskSummaries.isEmpty() && userTaskSummaries.size() > (filter.getCount() + filter.getOffset())) {
	       response.setPageRowList(new ArrayList<UserTaskSummary>(userTaskSummaries.subList(filter.getOffset(), filter.getOffset() + filter.getCount())));
	       response.setLastPage(false);

	     } else {
	       response.setPageRowList(new ArrayList<UserTaskSummary>(userTaskSummaries));
	       response.setLastPage(true);

	     }
	     return response;
	}

	@Override
	public UserTaskSummary getItem(UserTaskkey key) {
		 Task ts = taskService.getTaskById(key.getUserTaskId());
		 String actualOwner = "";
		 if (ts.getTaskData() !=null && ts.getTaskData().getActualOwner()!=null ){
			 actualOwner = ts.getTaskData().getActualOwner().getId();
		 }
		 String createdBy = "";
		 if ( ts.getTaskData() !=null && ts.getTaskData().getCreatedBy()!=null ){
			 createdBy = ts.getTaskData().getCreatedBy().getId();
		 }
		  if (ts != null) {
			  return new UserTaskSummary(ts.getId(), ts.getName(), actualOwner, ts.getTaskData().getStatus().toString(), ts.getDescription(),
					  createdBy, ts.getTaskData().getCreatedOn(),ts.getTaskData().getActivationTime(),ts.getTaskData().getExpirationTime());
		  }
		  return null;
	}

}
