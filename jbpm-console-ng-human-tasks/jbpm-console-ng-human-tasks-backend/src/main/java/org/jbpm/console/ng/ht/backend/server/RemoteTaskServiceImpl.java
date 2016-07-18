/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.ht.backend.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.bd.integration.AbstractKieServerService;
import org.jbpm.console.ng.ht.model.CommentSummary;
import org.jbpm.console.ng.ht.model.TaskAssignmentSummary;
import org.jbpm.console.ng.ht.model.TaskEventSummary;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskService;
import org.kie.internal.identity.IdentityProvider;
import org.kie.server.api.model.instance.TaskComment;
import org.kie.server.api.model.instance.TaskEventInstance;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.client.KieServicesException;
import org.kie.server.client.UserTaskServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@ApplicationScoped
public class RemoteTaskServiceImpl extends AbstractKieServerService implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(RemoteTaskServiceImpl.class);

    @Inject
    private IdentityProvider identityProvider;

    @Override
    public List<TaskSummary> getActiveTasks(String serverTemplateId, Integer page, Integer pageSize) {
        List<TaskSummary> taskSummaries = new ArrayList<TaskSummary>();

        if (serverTemplateId == null || serverTemplateId.isEmpty()) {
            return taskSummaries;
        }

        UserTaskServicesClient client = getClient(serverTemplateId, UserTaskServicesClient.class);

        List<org.kie.server.api.model.instance.TaskSummary> tasks = client.findTasksAssignedAsPotentialOwner(identityProvider.getName(), page, pageSize);

        for (org.kie.server.api.model.instance.TaskSummary task : tasks) {
            TaskSummary taskSummary = build(task);

            taskSummaries.add(taskSummary);
        }

        return taskSummaries;
    }

    @Override
    public TaskSummary getTask(String serverTemplateId, String containerId, Long taskId) {
        UserTaskServicesClient client = getClient(serverTemplateId, UserTaskServicesClient.class);
        try {
            TaskInstance task = client.getTaskInstance(containerId, taskId);

            return build(task);
        } catch (KieServicesException e) {
            // task not found
            return null;
        }
    }

    @Override
    public void updateTask(String serverTemplateId, String containerId, Long taskId, Integer priority, String description, Date dueDate) {
        UserTaskServicesClient client = getClient(serverTemplateId, UserTaskServicesClient.class);
        // TODO update only when it actually changed
        client.setTaskDescription(containerId, taskId, description);
        client.setTaskPriority(containerId, taskId, priority);
        client.setTaskExpirationDate(containerId, taskId, dueDate);
    }

    @Override
    public void claimTask(String serverTemplateId, String containerId, Long taskId) {
        UserTaskServicesClient client = getClient(serverTemplateId, UserTaskServicesClient.class);

        client.claimTask(containerId, taskId, identityProvider.getName());
    }

    @Override
    public void releaseTask(String serverTemplateId, String containerId, Long taskId) {
        UserTaskServicesClient client = getClient(serverTemplateId, UserTaskServicesClient.class);

        client.releaseTask(containerId, taskId, identityProvider.getName());
    }

    @Override
    public void startTask(String serverTemplateId, String containerId, Long taskId) {
        UserTaskServicesClient client = getClient(serverTemplateId, UserTaskServicesClient.class);

        client.startTask(containerId, taskId, identityProvider.getName());
    }

    @Override
    public void completeTask(String serverTemplateId, String containerId, Long taskId, Map<String, Object> output) {
        UserTaskServicesClient client = getClient(serverTemplateId, UserTaskServicesClient.class);

        client.completeTask(containerId, taskId, identityProvider.getName(), output);
    }

    @Override
    public void saveTaskContent(String serverTemplateId, String containerId, Long taskId, Map<String, Object> output) {
        UserTaskServicesClient client = getClient(serverTemplateId, UserTaskServicesClient.class);

        client.saveTaskContent(containerId, taskId, output);
    }

    @Override
    public void addTaskComment(String serverTemplateId, String containerId, Long taskId, String text, Date addedOn) {
        UserTaskServicesClient client = getClient(serverTemplateId, UserTaskServicesClient.class);

        client.addTaskComment(containerId, taskId, text, identityProvider.getName(), addedOn);
    }

    @Override
    public void deleteTaskComment(String serverTemplateId, String containerId, Long taskId, Long commentId) {
        UserTaskServicesClient client = getClient(serverTemplateId, UserTaskServicesClient.class);

        client.deleteTaskComment(containerId, taskId, commentId);
    }

    @Override
    public List<CommentSummary> getTaskComments(String serverTemplateId, String containerId, Long taskId) {
        List<CommentSummary> commentSummaries = new ArrayList<CommentSummary>();
        UserTaskServicesClient client = getClient(serverTemplateId, UserTaskServicesClient.class);

        List<TaskComment> comments = client.getTaskCommentsByTaskId(containerId, taskId);

        for (TaskComment comment : comments) {
            CommentSummary summary = build(comment);

            commentSummaries.add(summary);
        }

        return commentSummaries;
    }

    @Override
    public List<TaskEventSummary> getTaskEvents(String serverTemplateId, String containerId, Long taskId) {
        List<TaskEventSummary> eventSummaries = new ArrayList<TaskEventSummary>();
        UserTaskServicesClient client = getClient(serverTemplateId, UserTaskServicesClient.class);

        List<TaskEventInstance> events = client.findTaskEvents(taskId, 0, 1000);

        for (TaskEventInstance event : events) {
            TaskEventSummary summary = build(event);

            eventSummaries.add(summary);
        }

        return eventSummaries;
    }

    @Override
    public void delegate(String serverTemplateId, String containerId, long taskId, String entity) {
        UserTaskServicesClient client = getClient(serverTemplateId, UserTaskServicesClient.class);

        client.delegateTask(containerId, taskId, identityProvider.getName(), entity);
    }

    @Override
    public TaskAssignmentSummary getTaskAssignmentDetails(String serverTemplateId, String containerId, long taskId) {
        UserTaskServicesClient client = getClient(serverTemplateId, UserTaskServicesClient.class);
        try {
            TaskInstance task = client.getTaskInstance(containerId, taskId, false, false, true);
            TaskAssignmentSummary summary = new TaskAssignmentSummary();
            summary.setTaskId(task.getId());
            summary.setActualOwner(task.getActualOwner());
            summary.setTaskName(task.getName());
            summary.setPotOwnersString(task.getPotentialOwners());
            summary.setCreatedBy(task.getCreatedBy());
            summary.setBusinessAdmins(task.getBusinessAdmins());
            summary.setStatus(task.getStatus());
            summary.setDelegationAllowed(isDelegationAllowed(task));
            return summary;
        } catch (KieServicesException e) {
            // task not found
            return null;
        }
    }

    protected Boolean isDelegationAllowed(final TaskInstance task) {

        if (task == null) {
            return false;
        }

        if ("Completed".equals(task.getStatus())) {
            return false;
        }

        final String actualOwner = task.getActualOwner();
        if (actualOwner != null && actualOwner.equals(identityProvider.getName())) {
            return true;
        }

        final String initiator = task.getCreatedBy();
        if (initiator != null && initiator.equals(identityProvider.getName())) {
            return true;
        }

        List<String> roles = identityProvider.getRoles();

        //TODO Needs to check if po or ba string is a group or a user
        final List<String> potentialOwners = task.getPotentialOwners();
        if (potentialOwners != null && Collections.disjoint(potentialOwners, roles) == false) {
            return true;
        }

        final List<String> businessAdministrators = task.getBusinessAdmins();
        if (businessAdministrators != null && Collections.disjoint(businessAdministrators, roles) == false) {
            return true;
        }

        return false;

    }

    @Override
    public void executeReminderForTask(long taskId,String fromUser) {

    }

    protected TaskSummary build(org.kie.server.api.model.instance.TaskSummary task) {
        TaskSummary taskSummary = new TaskSummary(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getActualOwner(),
                task.getCreatedBy(),
                task.getCreatedOn(),
                task.getActivationTime(),
                task.getExpirationTime(),
                task.getProcessId(),
                -1,
                task.getProcessInstanceId(),
                task.getContainerId(),
                task.getParentId(),
                false
        );

        return taskSummary;
    }

    protected TaskSummary build(TaskInstance task) {
        if (task == null) {
            return null;
        }

        TaskSummary taskSummary = new TaskSummary(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getActualOwner(),
                task.getCreatedBy(),
                task.getCreatedOn(),
                task.getActivationTime(),
                task.getExpirationDate(),
                task.getProcessId(),
                -1,
                task.getProcessInstanceId(),
                task.getContainerId(),
                task.getParentId(),
                false
        );

        return taskSummary;
    }

    protected CommentSummary build(TaskComment comment) {
        CommentSummary summary = new CommentSummary(
                comment.getId(),
                comment.getText(),
                comment.getAddedBy(),
                comment.getAddedAt()
        );

        return summary;
    }

    protected TaskEventSummary build(TaskEventInstance event) {

        TaskEventSummary summary = new TaskEventSummary(
                event.getId(),
                event.getTaskId(),
                event.getType(),
                event.getUserId(),
                event.getWorkItemId(),
                event.getLogTime(),
                ""
        );

        return summary;
    }

}