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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.console.ng.ht.model.CommentSummary;
import org.jbpm.console.ng.ht.model.IdentitySummary;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.jbpm.task.impl.factories.TaskFactory;
import org.jbpm.task.impl.model.CommentImpl;
import org.jbpm.task.impl.model.UserImpl;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.kie.internal.task.api.model.Comment;
import org.kie.internal.task.api.model.Content;
import org.kie.internal.task.api.model.Group;
import org.kie.internal.task.api.model.OrganizationalEntity;
import org.kie.internal.task.api.model.Status;
import org.kie.internal.task.api.model.SubTasksStrategy;
import org.kie.internal.task.api.model.Task;
import org.kie.internal.task.api.model.User;

/**
 *
 *
 */
@Service
@ApplicationScoped
@Transactional
public class TaskServiceEntryPointImpl implements TaskServiceEntryPoint {
    
    @Inject
    private org.kie.internal.task.api.TaskService taskService;
    
    @Override
    public List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, String language) {
        return TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedAsBusinessAdministrator(userId, language));
    }
    
    @Override
    public List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId, String language) {
        return TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedAsExcludedOwner(userId, language));
    }
    
    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language) {
        return TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedAsPotentialOwner(userId, language));
    }
    
    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language) {
        return TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedAsPotentialOwner(userId, groupIds, language));
    }
    
    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResult) {
        return TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedAsPotentialOwner(userId, groupIds, language, firstResult, maxResult));
    }
    
    @Override
    public List<TaskSummary> getTasksAssignedAsRecipient(String userId, String language) {
        return TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedAsRecipient(userId, language));
    }
    
    @Override
    public List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId, String language) {
        return TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedAsTaskInitiator(userId, language));
    }
    
    @Override
    public List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId, String language) {
        return TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedAsTaskStakeholder(userId, language));
    }
    
    @Override
    public List<TaskSummary> getTasksOwned(String userId) {
        return TaskSummaryHelper.adaptCollection(taskService.getTasksOwned(userId));
    }
    
    @Override
    public List<TaskSummary> getTasksOwned(String userId, List<String> status, String language) {
        List<Status> statuses = new ArrayList<Status>();
        for(String s : status){
          statuses.add(Status.valueOf(s));
        }
        return TaskSummaryHelper.adaptCollection(taskService.getTasksOwned(userId, statuses, language));
    }
    
    @Override
    public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language) {
        return TaskSummaryHelper.adaptCollection(taskService.getSubTasksAssignedAsPotentialOwner(parentId, userId, language));
    }
    
    public List<TaskSummary> getTasksAssignedByGroup(String groupId, String language) {
        return TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedByGroup(groupId, language));
    }
    
    public List<TaskSummary> getTasksAssignedByGroups(List<String> groupIds, String language) {
        
        return TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedByGroups(groupIds, language));
    }
    
    public List<TaskSummary> getTasksAssignedPersonalAndGroupTasks(String userId, String groupId, String language) {
        List<TaskSummary> groupTasks = TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedByGroup(groupId, language));
        List<TaskSummary> personalTasks = TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedAsPotentialOwner(userId, language));
        groupTasks.addAll(personalTasks);
        return groupTasks;
    }
    
    public List<TaskSummary> getTasksAssignedPersonalAndGroupsTasks(String userId, List<String> groupIds, String language) {
            List<TaskSummary> groupTasks = TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedByGroups(groupIds, language));
            List<Status> statuses = new ArrayList<Status>();
            statuses.add(Status.Ready);
            statuses.add(Status.InProgress);
            statuses.add(Status.Reserved);
            statuses.add(Status.Created);
            List<TaskSummary> personalTasks = TaskSummaryHelper.adaptCollection(taskService.getTasksOwned(userId, statuses, language));
            groupTasks.addAll(personalTasks);
            return groupTasks;
      
    }
    public Map<String, List<TaskSummary>> getTasksAssignedPersonalAndGroupsTasksByDays(String userId, List<String> groupIds, String language) {
      Map<String, List<TaskSummary>> tasksAssignedByGroupsByDay = getTasksAssignedByGroupsByDay(userId, groupIds, language);
      Map<String, List<TaskSummary>> tasksOwnedByDay = getTasksOwnedByDay(userId, groupIds, language);
      for(String day : tasksOwnedByDay.keySet()){
        tasksOwnedByDay.get(day).addAll(tasksAssignedByGroupsByDay.get(day));
      }
      return tasksOwnedByDay;
    }
    
    public Map<String, List<TaskSummary>> getTasksAssignedByGroupsByDay(String userId, List<String> groupIds, String language) {
      Date currentDay = new Date();
      Map<String, List<TaskSummary>> tasksByDay = new LinkedHashMap<String, List<TaskSummary>>();
      
      List<TaskSummary> todayTasks = TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedByGroupsByExpirationDateOptional(groupIds, language, currentDay ));
      
      SimpleDateFormat todayFormat = new SimpleDateFormat("EEEE");
      tasksByDay.put(todayFormat.format(currentDay), todayTasks);
      currentDay = new Date(currentDay.getTime() + (1000 * 60 * 60 * 24));
      
      for(int i= 1; i < 5; i ++){
        List<TaskSummary> dayTasks = TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedByGroupsByExpirationDate(groupIds, language, currentDay ));
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        tasksByDay.put(dayFormat.format(currentDay), dayTasks);
        currentDay = new Date(currentDay.getTime() + (1000 * 60 * 60 * 24));
        
      }
      
      return tasksByDay;
    }
    
    public Map<String, List<TaskSummary>> getTasksOwnedByDay(String userId, List<String> groupIds, String language) {
      Date currentDay = new Date();
      Map<String, List<TaskSummary>> tasksByDay = new LinkedHashMap<String, List<TaskSummary>>();
      List<Status> statuses = new ArrayList<Status>();      
      statuses.add(Status.InProgress);
      statuses.add(Status.Reserved);
      statuses.add(Status.Created);
      
      List<TaskSummary> todayTasks = TaskSummaryHelper.adaptCollection(taskService.getTasksOwnedByExpirationDateOptional(userId, statuses, currentDay ));
      
      SimpleDateFormat todayFormat = new SimpleDateFormat("EEEE");
      tasksByDay.put(todayFormat.format(currentDay), todayTasks);
      currentDay = new Date(currentDay.getTime() + (1000 * 60 * 60 * 24));
      
      for(int i= 1; i < 5; i ++){
        List<TaskSummary> dayTasks = TaskSummaryHelper.adaptCollection(taskService.getTasksOwnedByExpirationDate(userId, statuses, currentDay ));
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        tasksByDay.put(dayFormat.format(currentDay), dayTasks);
        currentDay = new Date(currentDay.getTime() + (1000 * 60 * 60 * 24));
        
      }
      return tasksByDay;
    }
    
    
    @Override
    public List<TaskSummary> getSubTasksByParent(long parentId) {
        return TaskSummaryHelper.adaptCollection(taskService.getSubTasksByParent(parentId));
    }
    
    @Override
    public long addTask(String taskString, Map<String, Object> inputs, Map<String, Object> templateVars) {
        Task task = TaskFactory.evalTask(taskString, templateVars, true);
        return taskService.addTask(task, inputs);
    }
    
    @Override
    public long addTaskAndStart(String taskString, Map<String, Object> inputs, String userId, Map<String, Object> templateVars) {
        long taskId = addTask(taskString, inputs, templateVars);
        taskService.start(taskId, userId);
        return taskId;
    }
    
    @Override
    public void start(long taskId, String user) {
        taskService.start(taskId, user);
    }
    
    public void startBatch(List<Long> taskIds, String user){
        for(Long taskId : taskIds){
            taskService.start(taskId, user);
        }
    }

    @Override
    public void forward(long taskId, String userId, String targetEntityId) {
        taskService.forward(taskId, userId, targetEntityId);
    }
    
    @Override
    public void complete(long taskId, String user, Map<String, Object> params) {
        taskService.complete(taskId, user, params);
    }
    
    @Override
    public void claim(long taskId, String user) {
        taskService.claim(taskId, user);
    }
    
    @Override
    public void release(long taskId, String user) {
        taskService.release(taskId, user);
    }
    
    public void setPriority(long taskId, int priority) {
        taskService.setPriority(taskId, priority);
    }
    
    public void setTaskNames(long taskId, List<String> taskNames) {
        taskService.setTaskNames(taskId, TaskI18NHelper.adaptI18NList(taskNames) );
    }
    
    public void setExpirationDate(long taskId, Date date) {        
        taskService.setExpirationDate(taskId, date);
    }
    
    public void setDescriptions(long taskId, List<String> descriptions) {
        taskService.setDescriptions(taskId, TaskI18NHelper.adaptI18NList(descriptions));
    }
    
    public void setSkipable(long taskId, boolean skipable) {
        taskService.setSkipable(taskId, skipable);
    }
    
    public void setSubTaskStrategy(long taskId, String strategy) {
        taskService.setSubTaskStrategy(taskId, SubTasksStrategy.valueOf(strategy));
    }
    
    public int getPriority(long taskId) {
        return taskService.getPriority(taskId);
    }
    
    public Date getExpirationDate(long taskId) {
        return taskService.getExpirationDate(taskId);
    }
    
    public List<String> getDescriptions(long taskId) {
        return TaskI18NHelper.adaptStringList(taskService.getDescriptions(taskId));
    }
    
    public boolean isSkipable(long taskId) {
        return taskService.isSkipable(taskId);
    }
    
    public String getSubTaskStrategy(long taskId) {
        return taskService.getSubTaskStrategy(taskId).name();
    }
    
    public TaskSummary getTaskDetails(long taskId) {
        Task task = taskService.getTaskById(taskId);
        List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();
        List<String> potOwnersString = new ArrayList<String>(potentialOwners.size());
        for(OrganizationalEntity e : potentialOwners){
            potOwnersString.add(e.getId());
        }
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
                (int) task.getTaskData().getParentId(),potOwnersString);
    }
    
    public long saveContent(long taskId, Map<String, String> values) {
        return addContent(taskId, (Map)values);
    }
    
    public long addContent(long taskId, Content content) {
        return taskService.addContent(taskId, content);
    }
    
    public long addContent(long taskId, Map<String, Object> values) {
        return taskService.addContent(taskId, values);
    }
    
    public void deleteContent(long taskId, long contentId) {
        taskService.deleteContent(taskId, contentId);
    }
    
    public List<Content> getAllContentByTaskId(long taskId) {
        return taskService.getAllContentByTaskId(taskId);
    }
    
    public Content getContentById(long contentId) {
        return taskService.getContentById(contentId);
    }
    
    public Map<String, String> getContentListById(long contentId) {
        Content contentById = getContentById(contentId);
        Object unmarshall = ContentMarshallerHelper.unmarshall(contentById.getContent(), null);
        return (Map<String, String>) unmarshall;
    }
    
    public Map<String, String> getContentListByTaskId(long taskId) {
        Task taskInstanceById = taskService.getTaskById(taskId);
        long documentContentId = taskInstanceById.getTaskData().getDocumentContentId();
        Content contentById = getContentById(documentContentId);
        if (contentById == null) {
            return new HashMap<String, String>();
        }
        Object unmarshall = ContentMarshallerHelper.unmarshall(contentById.getContent(), null);
        if (unmarshall instanceof String) {
            if (((String) unmarshall).equals("")) {
                return new HashMap<String, String>();
            }
        }
        return (Map<String, String>) unmarshall;
    }
    
    public Map<String, String> getTaskOutputContentByTaskId(long taskId) {
        Task taskInstanceById = taskService.getTaskById(taskId);
        long documentContentId = taskInstanceById.getTaskData().getOutputContentId();
        if(documentContentId > 0){
            Content contentById = getContentById(documentContentId);
            if (contentById == null) {
                return new HashMap<String, String>();
            }
            Object unmarshall = ContentMarshallerHelper.unmarshall(contentById.getContent(), null);
            return (Map<String, String>) unmarshall;
        }
        return new HashMap<String, String>();
    }
    
    public int getCompletedTaskByUserId(String userId) {
        return taskService.getCompletedTaskByUserId(userId);
    }
    
    public int getPendingTaskByUserId(String userId) {
        return taskService.getPendingTaskByUserId(userId);
    }
    
    public IdentitySummary getOrganizationalEntityById(String entityId) {
        OrganizationalEntity entity = taskService.getOrganizationalEntityById(entityId);
        if (entity != null) {
            IdentitySummary idSummary = new IdentitySummary(entity.getId(), "");
            if (entity instanceof User) {
                idSummary.setType("user");
            } else {
                idSummary.setType("group");
            }
            
            return idSummary;
        }
        
        return null;
    }

    @Override
    public List<IdentitySummary> getOrganizationalEntities() {
        List<User> users = taskService.getUsers();
        List<Group> groups = taskService.getGroups();
        
        List<IdentitySummary> allEntitites = new ArrayList<IdentitySummary>();
        if (users != null) {
            for (User user : users) {
                allEntitites.add(new IdentitySummary(user.getId(), "user"));
            }
        }
        if (users != null) {
            for (Group group : groups) {
                allEntitites.add(new IdentitySummary(group.getId(), "group"));
            }
        }
        return allEntitites;
    }

    public long addComment(long taskId, String text, String addedBy, Date addedOn) {
        Comment comment = new CommentImpl();
        comment.setText(text);
        comment.setAddedAt(addedOn);
        comment.setAddedBy(new UserImpl(addedBy));
        return taskService.addComment(taskId, comment);
    }

    public void deleteComment(long taskId, long commentId) {
        taskService.deleteComment(taskId, commentId);
    }

    public List<CommentSummary> getAllCommentsByTaskId(long taskId) {
        return CommentSummaryHelper.adaptCollection(taskService.getAllCommentsByTaskId(taskId));
    }

    public CommentSummary getCommentById(long commentId) {
        return CommentSummaryHelper.adapt(taskService.getCommentById(commentId));
    }

    public void updateSimpleTaskDetails(long taskId, List<String> taskNames, int priority, List<String> taskDescription, String subTaskStrategy, Date dueDate) {
        //TODO: update only the changed bits
        setPriority(taskId, priority);
        setTaskNames(taskId, taskNames);
        setDescriptions(taskId, taskDescription);
        setSubTaskStrategy(taskId, subTaskStrategy);
        setExpirationDate(taskId, dueDate);
    }

    public void claimBatch(List<Long> taskIds, String user) {
        for(Long taskId : taskIds){
            taskService.claim(taskId, user);
        }
    }

    public void completeBatch(List<Long> taskIds, String user, Map<String, Object> params) {
        for(Long taskId : taskIds){
            taskService.complete(taskId, user, params);
        }
    }

    public void releaseBatch(List<Long> taskIds, String user) {
        for(Long taskId : taskIds){
            taskService.release(taskId, user);
        }
    }
    
}
