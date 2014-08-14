/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.console.ng.ht.backend.server;

import java.util.ArrayList;
import java.util.List;
import org.jbpm.console.ng.ht.model.AuditTaskSummary;
import org.jbpm.services.task.audit.impl.model.api.AuditTask;

public class AuditTaskSummaryHelper {

  public static List<AuditTaskSummary> adaptCollection(List<AuditTask> auditTaskSums) {
    List<AuditTaskSummary> auditTaskSummaries = new ArrayList<AuditTaskSummary>(auditTaskSums.size());
    for (AuditTask taskSum : auditTaskSums) {
      auditTaskSummaries.add(adapt(taskSum));
    }
    return auditTaskSummaries;
  }

  public static AuditTaskSummary adapt(AuditTask auditTaskSum) {
    return new AuditTaskSummary(auditTaskSum.getTaskId(),
            auditTaskSum.getStatus(), auditTaskSum.getActivationTime(), auditTaskSum.getName(),
            auditTaskSum.getDescription(), auditTaskSum.getPriority(), auditTaskSum.getCreatedBy(), auditTaskSum.getActualOwner(),
            auditTaskSum.getCreatedOn(), auditTaskSum.getDueDate(), auditTaskSum.getProcessInstanceId(), auditTaskSum.getProcessId(),
            auditTaskSum.getProcessSessionId(), auditTaskSum.getParentId(), auditTaskSum.getDeploymentId()
    );

  }

}
