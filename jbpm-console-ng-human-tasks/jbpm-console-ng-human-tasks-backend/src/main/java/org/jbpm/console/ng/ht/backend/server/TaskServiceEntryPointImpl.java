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
import org.jbpm.console.ng.ht.model.Day;
import org.jbpm.console.ng.ht.model.IdentitySummary;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.services.task.impl.model.CommentImpl;
import org.jbpm.services.task.impl.model.UserImpl;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.model.InternalComment;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.SubTasksStrategy;

/**
 *
 *
 */
@Service
@ApplicationScoped
@Transactional
public class TaskServiceEntryPointImpl implements TaskServiceEntryPoint {
    
    @Inject
    private InternalTaskService taskService;
    
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
    public List<TaskSummary> getTasksOwned(String userId, String language) {
        return TaskSummaryHelper.adaptCollection(taskService.getTasksOwned(userId, language));
    }
    
    @Override
    public List<TaskSummary> getTasksOwnedByStatus(String userId, List<String> status, String language) {
        List<Status> statuses = new ArrayList<Status>();
        for(String s : status){
          statuses.add(Status.valueOf(s));
        }
        return TaskSummaryHelper.adaptCollection(taskService.getTasksOwnedByStatus(userId, statuses, language));
    }
    
    @Override
    public List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language) {
        return TaskSummaryHelper.adaptCollection(taskService.getSubTasksAssignedAsPotentialOwner(parentId, userId, language));
    }
    
    @Override
    public List<TaskSummary> getTasksAssignedByGroup(String groupId, String language) {
        return TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedByGroup(groupId, language));
    }
    
    @Override
    public List<TaskSummary> getTasksAssignedByGroups(List<String> groupIds, String language) {
        
        return TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedByGroups(groupIds, language));
    }
    
    @Override
    public List<TaskSummary> getTasksAssignedPersonalAndGroupTasks(String userId, String groupId, String language) {
        List<TaskSummary> groupTasks = TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedByGroup(groupId, language));
        List<TaskSummary> personalTasks = TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedAsPotentialOwner(userId, language));
        groupTasks.addAll(personalTasks);
        return groupTasks;
    }
    
    @Override
    public List<TaskSummary> getTasksAssignedPersonalAndGroupsTasks(String userId, List<String> groupIds, String language) {
        List<TaskSummary> groupTasks = TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedByGroups(groupIds, language));
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Ready);
        statuses.add(Status.InProgress);
        statuses.add(Status.Reserved);
        statuses.add(Status.Created);
        List<TaskSummary> personalTasks = TaskSummaryHelper.adaptCollection(taskService.getTasksOwnedByStatus(userId, statuses, language));
        groupTasks.addAll(personalTasks);
        return groupTasks;
    }
    
    @Override
    public Map<Day,List<TaskSummary>> getTasksAssignedFromDateToDatePersonalAndGroupsTasksByDays(String userId, List<String> groupIds, Date from, Date to, String language) {
        Map<Day, List<TaskSummary>> tasksAssignedByGroupsByDay = getTasksAssignedFromDateToDateByGroupsByDays(groupIds, from, to, language);
        Map<Day, List<TaskSummary>> tasksOwnedByDay = getTasksOwnedFromDateToDateByDays(userId, from, to, language);
        for(Day day : tasksOwnedByDay.keySet()) {
            tasksOwnedByDay.get(day).addAll(tasksAssignedByGroupsByDay.get(day));
          }
          return tasksOwnedByDay;
    }
    
    @Override
    public Map<Day,List<TaskSummary>> getTasksAssignedFromDateToDatePersonalAndGroupsTasksByDays(String userId, List<String> groupIds, Date from, int nrOfDaysTotal, String language) {
        return getTasksAssignedFromDateToDatePersonalAndGroupsTasksByDays(userId, groupIds, from, new Date(from.getTime() + (nrOfDaysTotal - 1) * DateTimeConstants.MILLIS_PER_DAY), language);
    }
    
    @Override
    public Map<Day,List<TaskSummary>> getTasksAssignedFromDateToDateByGroupsByDays(List<String> groupIds, Date from, Date to, String language) {
        Map<Day, List<TaskSummary>> tasksByDay = new LinkedHashMap<Day, List<TaskSummary>>();
        List<TaskSummary> firstDayTasks = TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedByGroupsByExpirationDateOptional(groupIds, language, from));
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE dd");
        tasksByDay.put(new Day(from, dayFormat.format(from)), firstDayTasks);
        int nrOfDays = Days.daysBetween(new LocalDate(from), new LocalDate(to)).getDays();
        for (int i = 1; i <= nrOfDays; i++) {
            Date currentDay = new Date(from.getTime() + i * DateTimeConstants.MILLIS_PER_DAY);
            List<TaskSummary> dayTasks = TaskSummaryHelper.adaptCollection(taskService.getTasksAssignedByGroupsByExpirationDate(groupIds, language, currentDay));
            tasksByDay.put(new Day(currentDay, dayFormat.format(currentDay)), dayTasks);
        }
        return tasksByDay;
        
    }
    
    @Override
    public Map<Day,List<TaskSummary>> getTasksAssignedFromDateToDateByGroupsByDays(List<String> groupIds, Date from, int nrOfDaysTotal, String language) {
        return getTasksAssignedFromDateToDateByGroupsByDays(groupIds, from, new Date(from.getTime() + (nrOfDaysTotal - 1) * DateTimeConstants.MILLIS_PER_DAY), language);
    }
    
    @Override
    public Map<Day, List<TaskSummary>> getTasksOwnedFromDateToDateByDays(String userId, List<String> strStatuses, Date from, Date to, String language) {
        List<Status> statuses = new ArrayList<Status>();
        for(String s : strStatuses){
          statuses.add(Status.valueOf(s));
        }
        Map<Day, List<TaskSummary>> tasksByDay = new LinkedHashMap<Day, List<TaskSummary>>();
        List<TaskSummary> firstDayTasks = TaskSummaryHelper.adaptCollection(taskService.getTasksOwnedByExpirationDateOptional(userId, statuses, from));
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE dd");
        tasksByDay.put(new Day(from, dayFormat.format(from)), firstDayTasks);
        int nrOfDays = Days.daysBetween(new LocalDate(from), new LocalDate(to)).getDays();
        for (int i = 1; i <= nrOfDays; i++) {
            Date currentDay = new Date(from.getTime() + i * DateTimeConstants.MILLIS_PER_DAY);
            List<TaskSummary> dayTasks = TaskSummaryHelper.adaptCollection(taskService.getTasksOwnedByExpirationDate(userId, statuses, currentDay ));
            tasksByDay.put(new Day(currentDay, dayFormat.format(currentDay)), dayTasks);
        }
        return tasksByDay;
    }
    
    @Override
    public Map<Day, List<TaskSummary>> getTasksOwnedFromDateToDateByDays(String userId, List<String> strStatuses, Date from, int nrOfDaysTotal, String language) {
        return getTasksOwnedFromDateToDateByDays(userId, strStatuses, from, new Date(from.getTime() + (nrOfDaysTotal - 1) * DateTimeConstants.MILLIS_PER_DAY), language);
    }
    
    @Override
    public Map<Day, List<TaskSummary>> getTasksOwnedFromDateToDateByDays(String userId, Date from, Date to, String language) {
        List<String> statuses = new ArrayList<String>();
        statuses.add("InProgress");
        statuses.add("Reserved");
        statuses.add("Created");
        return getTasksOwnedFromDateToDateByDays(userId, statuses, from, to, language);
    }
    
    @Override
    public Map<Day, List<TaskSummary>> getTasksOwnedFromDateToDateByDays(String userId, Date from, int nrOfDaysTotal, String language) {
        return getTasksOwnedFromDateToDateByDays(userId, from, new Date(from.getTime() + (nrOfDaysTotal - 1) * DateTimeConstants.MILLIS_PER_DAY), language);
    }
    
    @Override
    public List<TaskSummary> getSubTasksByParent(long parentId) {
        return TaskSummaryHelper.adaptCollection(taskService.getSubTasksByParent(parentId));
    }
    
    @Override
    public long addTask(String taskString, Map<String, Object> inputs, Map<String, Object> templateVars) {
        Task task = TaskFactory.evalTask(taskString, templateVars);
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
    
    @Override
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
    
    @Override
    public void setPriority(long taskId, int priority) {
        taskService.setPriority(taskId, priority);
    }
    
    public void setTaskNames(long taskId, List<String> taskNames) {
        taskService.setTaskNames(taskId, TaskI18NHelper.adaptI18NList(taskNames) );
    }
    
    @Override
    public void setExpirationDate(long taskId, Date date) {        
        taskService.setExpirationDate(taskId, date);
    }
    
    @Override
    public void setDescriptions(long taskId, List<String> descriptions) {
        taskService.setDescriptions(taskId, TaskI18NHelper.adaptI18NList(descriptions));
    }
    
    @Override
    public void setSkipable(long taskId, boolean skipable) {
        taskService.setSkipable(taskId, skipable);
    }
    
    @Override
    public void setSubTaskStrategy(long taskId, String strategy) {
        taskService.setSubTaskStrategy(taskId, SubTasksStrategy.valueOf(strategy));
    }
    
    @Override
    public int getPriority(long taskId) {
        return taskService.getPriority(taskId);
    }
    
    @Override
    public Date getExpirationDate(long taskId) {
        return taskService.getExpirationDate(taskId);
    }
    
    @Override
    public List<String> getDescriptions(long taskId) {
        return TaskI18NHelper.adaptStringList(taskService.getDescriptions(taskId));
    }
    
    @Override
    public boolean isSkipable(long taskId) {
        return taskService.isSkipable(taskId);
    }
    
    @Override
    public String getSubTaskStrategy(long taskId) {
        return taskService.getSubTaskStrategy(taskId).name();
    }
    
    @Override
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
                ((InternalTask) task).getSubTaskStrategy().name(),
                (int) task.getTaskData().getParentId(),potOwnersString);
    }
    
    @Override
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
    
    @Override
    public Map<String, String> getContentListById(long contentId) {
        Content contentById = getContentById(contentId);
        Object unmarshall = ContentMarshallerHelper.unmarshall(contentById.getContent(), null);
        return (Map<String, String>) unmarshall;
    }
    
    @Override
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
    
    @Override
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
    
    @Override
    public int getCompletedTaskByUserId(String userId) {
        return taskService.getCompletedTaskByUserId(userId);
    }
    
    @Override
    public int getPendingTaskByUserId(String userId) {
        return taskService.getPendingTaskByUserId(userId);
    }
    
    @Override
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

    @Override
    public long addComment(long taskId, String text, String addedBy, Date addedOn) {
        InternalComment comment = new CommentImpl();
        comment.setText(text);
        comment.setAddedAt(addedOn);
        comment.setAddedBy(new UserImpl(addedBy));
        return taskService.addComment(taskId, comment);
    }

    @Override
    public void deleteComment(long taskId, long commentId) {
        taskService.deleteComment(taskId, commentId);
    }

    @Override
    public List<CommentSummary> getAllCommentsByTaskId(long taskId) {
        return CommentSummaryHelper.adaptCollection(taskService.getAllCommentsByTaskId(taskId));
    }

    @Override
    public CommentSummary getCommentById(long commentId) {
        return CommentSummaryHelper.adapt(taskService.getCommentById(commentId));
    }

    @Override
    public void updateSimpleTaskDetails(long taskId, List<String> taskNames, int priority, List<String> taskDescription, 
            //String subTaskStrategy, 
            Date dueDate) {
        //TODO: update only the changed bits
        setPriority(taskId, priority);
        setTaskNames(taskId, taskNames);
        setDescriptions(taskId, taskDescription);
      //  setSubTaskStrategy(taskId, subTaskStrategy);
        setExpirationDate(taskId, dueDate);
    }

    @Override
    public void claimBatch(List<Long> taskIds, String user) {
        for(Long taskId : taskIds){
            taskService.claim(taskId, user);
        }
    }

    @Override
    public void completeBatch(List<Long> taskIds, String user, Map<String, Object> params) {
        for(Long taskId : taskIds){
            taskService.complete(taskId, user, params);
        }
    }

    @Override
    public void releaseBatch(List<Long> taskIds, String user) {
        for(Long taskId : taskIds){
            taskService.release(taskId, user);
        }
    }
    
}
