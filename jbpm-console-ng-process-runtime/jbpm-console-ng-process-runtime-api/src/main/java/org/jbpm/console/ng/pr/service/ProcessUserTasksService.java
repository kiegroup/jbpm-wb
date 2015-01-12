package org.jbpm.console.ng.pr.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.console.ng.ga.service.GenericServiceEntryPoint;
import org.jbpm.console.ng.pr.model.UserTaskSummary;
import org.jbpm.console.ng.pr.model.UserTaskkey;

@Remote
public interface ProcessUserTasksService extends GenericServiceEntryPoint<UserTaskkey, UserTaskSummary>{

}
