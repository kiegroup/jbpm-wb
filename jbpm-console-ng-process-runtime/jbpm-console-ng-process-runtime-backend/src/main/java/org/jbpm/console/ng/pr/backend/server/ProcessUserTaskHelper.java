package org.jbpm.console.ng.pr.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jbpm.console.ng.pr.model.UserTaskSummary;
import org.kie.api.task.model.TaskSummary;

public class ProcessUserTaskHelper {
	public static List<UserTaskSummary> adaptCollection(Collection<TaskSummary> taskSummary) {
        List<UserTaskSummary> userTaskSummary = new ArrayList<UserTaskSummary>();
        for (TaskSummary ts : taskSummary) {
        	userTaskSummary.add(adapt(ts));
        }

        return userTaskSummary;
    }

    public static UserTaskSummary adapt(TaskSummary ts) {
    
        return new UserTaskSummary(ts.getId(), ts.getName(), ts.getActualOwnerId(), ts.getStatusId(), ts.getDescription(),ts.getCreatedById(),
                  ts.getCreatedOn(),ts.getActivationTime(),ts.getExpirationTime());
        
    }
    
}
