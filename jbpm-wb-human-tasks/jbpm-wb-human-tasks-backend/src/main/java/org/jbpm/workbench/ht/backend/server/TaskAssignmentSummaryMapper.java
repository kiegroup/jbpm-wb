/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workbench.ht.backend.server;

import java.util.Collections;
import java.util.function.BiFunction;

import org.jbpm.workbench.ht.model.TaskAssignmentSummary;
import org.jbpm.workbench.ht.util.TaskStatus;
import org.kie.internal.identity.IdentityProvider;
import org.kie.server.api.model.instance.TaskInstance;

public class TaskAssignmentSummaryMapper implements BiFunction<TaskInstance, IdentityProvider, TaskAssignmentSummary> {

    @Override
    public TaskAssignmentSummary apply(final TaskInstance task,
                                       final IdentityProvider identityProvider) {
        if (task == null) {
            return null;
        } else {
            final TaskAssignmentSummary summary = new TaskAssignmentSummary();
            summary.setTaskId(task.getId());
            summary.setActualOwner(task.getActualOwner());
            summary.setTaskName(task.getName());
            summary.setPotOwnersString(task.getPotentialOwners());
            summary.setCreatedBy(task.getCreatedBy());
            summary.setBusinessAdmins(task.getBusinessAdmins());
            summary.setStatus(task.getStatus());
            summary.setDelegationAllowed(isDelegationAllowed(task, identityProvider));
            summary.setForwardAllowed(isForwardAllowed(task, identityProvider));
            summary.setDeploymentId(task.getContainerId());
            return summary;
        }
    }

    protected Boolean isForwardAllowed(final TaskInstance task, final IdentityProvider identityProvider) {
        return isReassignmentAllowed(task, identityProvider);
    }

    protected Boolean isDelegationAllowed(final TaskInstance task, final IdentityProvider identityProvider) {
        return isReassignmentAllowed(task, identityProvider);
    }

    private Boolean isReassignmentAllowed(final TaskInstance task, final IdentityProvider identityProvider) {
        if (TaskStatus.TASK_STATUS_READY.equals(task.getStatus())) {
            if (currentUserIsPotentialOwner(identityProvider, task)) {
                return true;
            }
            if (currentUserIsBusinessAdministrator(identityProvider, task)) {
                return true;
            }
        } else if (TaskStatus.TASK_STATUS_IN_PROGRESS.equals(task.getStatus())
                || TaskStatus.TASK_STATUS_RESERVED.equals(task.getStatus())) {
            if (currentUserIsOwner(identityProvider, task)) {
                return true;
            }
            if (currentUserIsBusinessAdministrator(identityProvider, task)) {
                return true;
            }
        }
        return false;
    }

    private boolean currentUserIsPotentialOwner(IdentityProvider identityProvider, TaskInstance task) {
        if (task != null && task.getPotentialOwners() != null && identityProvider != null) {
            return !Collections.disjoint(task.getPotentialOwners(), identityProvider.getRoles());
        }
        return false;
    }

    private boolean currentUserIsBusinessAdministrator(IdentityProvider identityProvider, TaskInstance task) {
        if (task != null && task.getBusinessAdmins() != null && identityProvider != null) {
            return !Collections.disjoint(task.getBusinessAdmins(), identityProvider.getRoles());
        }
        return false;
    }

    private boolean currentUserIsOwner(IdentityProvider identityProvider, TaskInstance task) {
        return identityProvider.getName().equals(task.getActualOwner());
    }
}
