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
package org.jbpm.console.ng.server.editors.jbpm.inbox;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jbpm.console.ng.shared.TaskServiceEntryPoint;

import org.jbpm.console.ng.client.model.TaskSummary;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.task.Content;
import org.jbpm.task.ContentData;
import org.jbpm.task.SubTasksStrategy;
import org.jbpm.task.Task;
import org.jbpm.task.api.TaskContentService;
import org.jbpm.task.api.TaskInstanceService;
import org.jbpm.task.api.TaskQueryService;
import org.jbpm.task.impl.factories.TaskFactory;
import org.jbpm.task.utils.ContentMarshallerHelper;

/**
 *
 *
 */
@Service
@ApplicationScoped
public class TaskServiceEntryPointImpl implements TaskServiceEntryPoint {

    @Inject
    TaskQueryService taskQueryService;
    @Inject
    TaskInstanceService taskInstanceService;
    @Inject
    TaskContentService taskContentService;

    @Override
    public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, String language) {
        return TaskSummaryHelper.adapt(taskQueryService.getTasksAssignedAsBusinessAdministrator(userId, language));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId, String language) {
        return TaskSummaryHelper.adapt(taskQueryService.getTasksAssignedAsExcludedOwner(userId, language));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language) {
        return TaskSummaryHelper.adapt(taskQueryService.getTasksAssignedAsPotentialOwner(userId, language));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language) {
        return TaskSummaryHelper.adapt(taskQueryService.getTasksAssignedAsPotentialOwner(userId, groupIds, language));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResult) {
        return TaskSummaryHelper.adapt(taskQueryService.getTasksAssignedAsPotentialOwner(userId, groupIds, language, firstResult, maxResult));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsRecipient(String userId, String language) {
        return TaskSummaryHelper.adapt(taskQueryService.getTasksAssignedAsRecipient(userId, language));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId, String language) {
        return TaskSummaryHelper.adapt(taskQueryService.getTasksAssignedAsTaskInitiator(userId, language));
    }

    @Override
    public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId, String language) {
        return TaskSummaryHelper.adapt(taskQueryService.getTasksAssignedAsTaskStakeholder(userId, language));
    }

    @Override
    public List<TaskSummary> getTasksOwned(String userId) {
        return TaskSummaryHelper.adapt(taskQueryService.getTasksOwned(userId));
    }

    @Override
    public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language) {
        return TaskSummaryHelper.adapt(taskQueryService.getSubTasksAssignedAsPotentialOwner(parentId, userId, language));
    }

    @Override
    public List<TaskSummary> getSubTasksByParent(long parentId) {
        return TaskSummaryHelper.adapt(taskQueryService.getSubTasksByParent(parentId));
    }

    @Override
    public long addTask(String taskString, Map<String, Object> params) {
        Task task = TaskFactory.evalTask(taskString, params, true);
        return taskInstanceService.addTask(task, params);
    }

    @Override
    public void start(long taskId, String user) {
        taskInstanceService.start(taskId, user);
    }

    @Override
    public void complete(long taskId, String user, Map<String, Object> params) {
        taskInstanceService.complete(taskId, user, params);
    }

    @Override
    public void claim(long taskId, String user) {
        taskInstanceService.claim(taskId, user);
    }

    @Override
    public void release(long taskId, String user) {
        taskInstanceService.release(taskId, user);
    }

    public void setPriority(long taskId, int priority) {
        taskInstanceService.setPriority(taskId, priority);
    }

    public void setExpirationDate(long taskId, Date date) {
        taskInstanceService.setExpirationDate(taskId, date);
    }

    public void setDescriptions(long taskId, List<String> descriptions) {
        taskInstanceService.setDescriptions(taskId, TaskI18NHelper.adaptI18NList(descriptions));
    }

    public void setSkipable(long taskId, boolean skipable) {
        taskInstanceService.setSkipable(taskId, skipable);
    }

    public void setSubTaskStrategy(long taskId, String strategy) {
        taskInstanceService.setSubTaskStrategy(taskId, SubTasksStrategy.valueOf(strategy));
    }

    public int getPriority(long taskId) {
        return taskInstanceService.getPriority(taskId);
    }

    public Date getExpirationDate(long taskId) {
        return taskInstanceService.getExpirationDate(taskId);
    }

    public List<String> getDescriptions(long taskId) {
        return TaskI18NHelper.adaptStringList(taskInstanceService.getDescriptions(taskId));
    }

    public boolean isSkipable(long taskId) {
        return taskInstanceService.isSkipable(taskId);
    }

    public String getSubTaskStrategy(long taskId) {
        return taskInstanceService.getSubTaskStrategy(taskId).name();
    }

    public TaskSummary getTaskDetails(long taskId) {
        Task task = taskQueryService.getTaskInstanceById(taskId);
        return new TaskSummary(task.getId(),
                task.getTaskData().getProcessInstanceId(),
                ((task.getNames() != null && task.getNames().size() > 0)) ? task.getNames().get(0).getText() : "",
                ((task.getSubjects() != null && task.getSubjects().size() > 0)) ? task.getSubjects().get(0).getText() : "",
                ((task.getDescriptions() != null && task.getDescriptions().size() > 0)) ? task.getDescriptions().get(0).getText() : "",
                task.getTaskData().getStatus().name(),
                task.getPriority(),
                task.getTaskData().isSkipable(),
                (task.getTaskData().getActualOwner() != null) ? task.getTaskData().getActualOwner().getId() : "",
                (task.getTaskData().getCreatedBy() != null) ? task.getTaskData().getCreatedBy().getId() : "",
                task.getTaskData().getCreatedOn(),
                task.getTaskData().getActivationTime(),
                task.getTaskData().getExpirationTime(),
                task.getTaskData().getProcessId(),
                task.getTaskData().getProcessSessionId(),
                task.getSubTaskStrategy().name(),
                (int) task.getTaskData().getParentId());
    }

    public long saveContent(long taskId, Map<String, String> values) {
        ContentData contentData = ContentMarshallerHelper.marshal(values, null);
        return addContent(taskId, new Content(contentData.getContent()));
    }

    public long addContent(long taskId, Content content) {
        return taskContentService.addContent(taskId, content);
    }

    public void deleteContent(long taskId, long contentId) {
        taskContentService.deleteContent(taskId, contentId);
    }

    public List<Content> getAllContentByTaskId(long taskId) {
        return taskContentService.getAllContentByTaskId(taskId);
    }

    public Content getContentById(long contentId) {
        return taskContentService.getContentById(contentId);
    }

    public Map<String, String> getContentListById(long contentId) {
        Content contentById = getContentById(contentId);
        Object unmarshall = ContentMarshallerHelper.unmarshall(contentById.getContent(), null);
        return (Map<String, String>) unmarshall;
    }

    public Map<String, String> getContentListByTaskId(long taskId) {
        Task taskInstanceById = taskQueryService.getTaskInstanceById(taskId);
        long documentContentId = taskInstanceById.getTaskData().getDocumentContentId();
        Content contentById = getContentById(documentContentId);
        if (contentById == null) {
            return new HashMap<String, String>();
        }
        Object unmarshall = ContentMarshallerHelper.unmarshall(contentById.getContent(), null);
        return (Map<String, String>) unmarshall;
    }

    public Map<String, String> getTaskOutputContentByTaskId(long taskId) {
        Task taskInstanceById = taskQueryService.getTaskInstanceById(taskId);
        long documentContentId = taskInstanceById.getTaskData().getOutputContentId();
        Content contentById = getContentById(documentContentId);
        if (contentById == null) {
            return new HashMap<String, String>();
        }
        Object unmarshall = ContentMarshallerHelper.unmarshall(contentById.getContent(), null);
        return (Map<String, String>) unmarshall;
    }
}
