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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.ht.model.AuditTaskSummary;
import org.jbpm.console.ng.ht.model.CommentSummary;
import org.jbpm.console.ng.ht.model.Day;
import org.jbpm.console.ng.ht.model.TaskEventSummary;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.task.audit.service.TaskAuditService;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.services.task.utils.TaskFluent;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.InternalTaskService;

@Service
@ApplicationScoped
public class TaskServiceEntryPointImpl implements TaskServiceEntryPoint {

    @Inject
    private InternalTaskService internalTaskService;

    @Inject
    private UserTaskService taskService;

    @Inject
    private RuntimeDataService runtimeDataService;
    
    @Inject
    private TaskAuditService taskAuditService;

    public TaskServiceEntryPointImpl() {
        
    }

    @PostConstruct
    public void init(){
        taskAuditService.setTaskService(internalTaskService);
    }
    
    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(String userId,
            List<String> status, Date from, int offset, int count) { 
        List<Status> statuses = new ArrayList<Status>();
        for (String s : status) {
            statuses.add(Status.valueOf(s));
        }
        List<TaskSummary> taskSummaries = null;
        if (from != null) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("expirationDate", from);
		QueryFilter qf = new QueryFilter( "(t.taskData.expirationTime = :expirationDate or t.taskData.expirationTime is null)", 
                            params, "order by t.id DESC", offset, count);
                
            taskSummaries = TaskSummaryHelper.adaptCollection(
                    runtimeDataService.getTasksAssignedAsPotentialOwnerByStatus(userId, statuses, qf));
        } else {
            QueryFilter qf = new QueryFilter(offset,count);
            taskSummaries = TaskSummaryHelper.adaptCollection(
                    runtimeDataService.getTasksAssignedAsPotentialOwnerByStatus(userId, statuses, qf));
        }
        return taskSummaries;
    }
    
    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDateOptional(String userId, List<String> status, Date from, int offset, int count) { 
        List<Status> statuses = new ArrayList<Status>();
        for (String s : status) {
            statuses.add(Status.valueOf(s));
        }
        List<TaskSummary> taskSummaries = TaskSummaryHelper.adaptCollection(
                runtimeDataService.getTasksOwnedByStatus(userId, statuses, new QueryFilter(offset, count)));
//        setPotentionalOwners(taskSummaries);
        return taskSummaries;
    }

    @Override
    public Map<Long, List<String>> getPotentialOwnersForTaskIds(List<Long> taskIds) {
        Map<Long, List<OrganizationalEntity>> potentialOwnersForTaskIds = internalTaskService.getPotentialOwnersForTaskIds(
                taskIds);
        Map<Long, List<String>> potentialOwnersForTaskIdsSimple = new HashMap<Long, List<String>>();
        for (Long taskId : potentialOwnersForTaskIds.keySet()) {
            List<OrganizationalEntity> orgEntities = potentialOwnersForTaskIds.get(taskId);
            List<String> orgEntitiesSimple = new ArrayList<String>(orgEntities.size());
            for (OrganizationalEntity entity : orgEntities) {
                if (entity instanceof Group) {
                    orgEntitiesSimple.add("Group:" + entity.getId());
                } else if (entity instanceof User) {
                    orgEntitiesSimple.add("User:" + entity.getId());
                }
            }
            potentialOwnersForTaskIdsSimple.put(taskId, orgEntitiesSimple);
        }
        return potentialOwnersForTaskIdsSimple;
    }

    /**
     * Day adaptors
     */
    public Map<Day, List<TaskSummary>> getTasksOwnedFromDateToDateByDays(String userId, List<String> strStatuses,
            Date dateFrom, Date dateTo) {
        LocalDate dayFrom = new LocalDate(dateFrom);
        LocalDate dayTo = new LocalDate(dateTo);

        LocalDate today = new LocalDate();
        int nrOfDaysTotal = getNumberOfDaysWithinDateRange(dayFrom, dayTo);
        Map<LocalDate, List<TaskSummary>> tasksByDay = createDaysMapAndInitWithEmptyListForEachDay(dayFrom, nrOfDaysTotal);

        List<TaskSummary> taskSummaries = adaptTaskSummaryCollection(
                runtimeDataService.getTasksOwnedByStatus(userId, convertStatuses(strStatuses), new QueryFilter(0, 0)));

//        setPotentionalOwners(taskSummaries);
        fillDaysMapWithTasksBasedOnExpirationDate(tasksByDay, taskSummaries, today);
        return transformLocalDatesToDays(tasksByDay);
    }

    private void fillDaysMapWithTasksBasedOnExpirationDate(Map<LocalDate, List<TaskSummary>> tasksByDay,
            List<TaskSummary> taskSummaries, LocalDate today) {
        for (TaskSummary taskSummary : taskSummaries) {
            LocalDate expDate;
            if (taskSummary.getExpirationTime() == null) {
                expDate = today;
            } else {
                expDate = new LocalDate(taskSummary.getExpirationTime());
            }
            if (tasksByDay.get(expDate) != null) {
                tasksByDay.get(expDate).add(taskSummary);
            }
        }
    }

    private Map<LocalDate, List<TaskSummary>> createDaysMapAndInitWithEmptyListForEachDay(LocalDate dayFrom,
            int nrOfDaysTotal) {
        Map<LocalDate, List<TaskSummary>> tasksByDay = new LinkedHashMap<LocalDate, List<TaskSummary>>();
        for (int i = 0; i < nrOfDaysTotal; i++) {
            tasksByDay.put(dayFrom.plusDays(i), new ArrayList<TaskSummary>());
        }
        return tasksByDay;
    }

    private int getNumberOfDaysWithinDateRange(LocalDate dayFrom, LocalDate dayTo) {
        Days daysBetween = Days.daysBetween(dayFrom, dayTo);
        return daysBetween.getDays() + 1;
    }

    private List<Status> convertStatuses(List<String> strStatuses) {
        List<Status> statuses = new ArrayList<Status>();
        for (String s : strStatuses) {
            statuses.add(Status.valueOf(s));
        }
        return statuses;
    }

    List<TaskSummary> adaptTaskSummaryCollection(List<org.kie.api.task.model.TaskSummary> taskSummaries) {
        return TaskSummaryHelper.adaptCollection(taskSummaries);
    }

    private Map<Day, List<TaskSummary>> transformLocalDatesToDays(Map<LocalDate, List<TaskSummary>> tasksByLocalDate) {
        Map<Day, List<TaskSummary>> tasksByDay = new LinkedHashMap<Day, List<TaskSummary>>();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE dd");
        for (Map.Entry<LocalDate, List<TaskSummary>> entry : tasksByLocalDate.entrySet()) {
            Date dayDate = entry.getKey().toDateTimeAtStartOfDay().toDate();
            tasksByDay.put(new Day(dayDate, dayFormat.format(dayDate)), entry.getValue());
        }
        return tasksByDay;
    }

    @Override
    public Map<Day, List<TaskSummary>> getTasksAssignedAsPotentialOwnerFromDateToDateByDays(String userId,
            List<String> strStatuses, Date from, int nrOfDaysTotal) {
        Date toDate = getEndDateBasedOnStartDateAndNumberOfDaysBetweenThem(from, nrOfDaysTotal);
        return getTasksAssignedAsPotentialOwnerFromDateToDateByDays(userId, strStatuses, from,
                toDate);
    }

    public Map<Day, List<TaskSummary>> getTasksAssignedAsPotentialOwnerFromDateToDateByDays(String userId,
            List<String> strStatuses, Date dateFrom, Date dateTo) {
        LocalDate dayFrom = new LocalDate(dateFrom);
        LocalDate dayTo = new LocalDate(dateTo);

        LocalDate today = new LocalDate();

        int nrOfDaysTotal = getNumberOfDaysWithinDateRange(dayFrom, dayTo);
        Map<LocalDate, List<TaskSummary>> tasksByDay = createDaysMapAndInitWithEmptyListForEachDay(dayFrom, nrOfDaysTotal);

        List<TaskSummary> taskSummaries = adaptTaskSummaryCollection(
                runtimeDataService.getTasksAssignedAsPotentialOwnerByStatus(userId, convertStatuses(strStatuses), new QueryFilter(0, 0)));

//        setPotentionalOwners(taskSummaries);
        fillDaysMapWithTasksBasedOnExpirationDate(tasksByDay, taskSummaries, today);
        return transformLocalDatesToDays(tasksByDay);
    }

    @Override
    public Map<Day, List<TaskSummary>> getTasksOwnedFromDateToDateByDays(String userId, List<String> strStatuses, Date from,
            int nrOfDaysTotal) {
        Date toDate = getEndDateBasedOnStartDateAndNumberOfDaysBetweenThem(from, nrOfDaysTotal);
        return getTasksOwnedFromDateToDateByDays(userId, strStatuses, from, toDate);
    }

    private Date getEndDateBasedOnStartDateAndNumberOfDaysBetweenThem(Date from, int nrOfDaysTotal) {
        return new LocalDate(from.getTime()).plusDays(nrOfDaysTotal - 1).toDateMidnight().toDate();
    }

    public Map<Day, List<TaskSummary>> getTasksOwnedFromDateToDateByDays(String userId, Date from, Date to) {
        List<String> statuses = new ArrayList<String>();
        statuses.add("InProgress");
        statuses.add("Reserved");
        statuses.add("Created");
        return getTasksOwnedFromDateToDateByDays(userId, statuses, from, to);
    }

    /**
     * Group Operations
     */
    public List<TaskSummary> getTasksAssignedByGroup(String userId, String groupId) {
        List<String> groupIds = new ArrayList<String>();
        groupIds.add(groupId);
        List<org.kie.api.task.model.TaskSummary> tasksAssignedAsPotentialOwner = runtimeDataService.getTasksAssignedAsPotentialOwner(
                userId, groupIds, new QueryFilter(0, 0));
        List<org.kie.api.task.model.TaskSummary> taskForGroup = new ArrayList<org.kie.api.task.model.TaskSummary>();
        for (org.kie.api.task.model.TaskSummary ts : tasksAssignedAsPotentialOwner) {
            if (ts.getPotentialOwners().contains(groupId)) {
                taskForGroup.add(ts);
            }
        }
        return TaskSummaryHelper.adaptCollection(taskForGroup);
    }

    @Override
    public long addTask(String taskString, Map<String, Object> inputs, Map<String, Object> templateVars) {
        Task task = TaskFactory.evalTask(taskString, templateVars);
        return internalTaskService.addTask(task, inputs);
    }
    
    @Override
    public long addQuickTask(
                         final String taskName,
                         int priority,
                         Date dueDate, final List<String> users, List<String> groups, String identity, boolean start, boolean claim){
        TaskFluent taskFluent = new TaskFluent().setName(taskName)
                                                .setPriority(priority)
                                                .setDueDate(dueDate);
                
        for(String user : users){
            taskFluent.addPotentialUser(user);
        }
        for(String group : groups){
            taskFluent.addPotentialGroup(group);
        }
        taskFluent.setAdminUser("Administrator");
        taskFluent.setAdminGroup("Administrators");
        long taskId = internalTaskService.addTask(taskFluent.getTask(), new HashMap<String, Object>());
        if(start){
            taskService.start(taskId, identity);
        }
        if(claim){
            taskService.claim(taskId, identity);
        }
        
        return taskId;
    }

    @Override
    public long addTaskAndStart(String taskString, Map<String, Object> inputs, String userId,
            Map<String, Object> templateVars) {
        long taskId = addTask(taskString, inputs, templateVars);
        taskService.start(taskId, userId);
        return taskId;
    }

    @Override
    public long addTaskAndClaimAndStart(String taskString, Map<String, Object> inputs, String userId,
            Map<String, Object> templateVars) {
        long taskId = addTask(taskString, inputs, templateVars);
        taskService.claim(taskId, userId);
        taskService.start(taskId, userId);
        return taskId;
    }

    @Override
    public void start(long taskId, String user) {
        taskService.start(taskId, user);
    }

    @Override
    public void startBatch(List<Long> taskIds, String user) {
        for (Long taskId : taskIds) {
            taskService.start(taskId, user);
        }
    }

    @Override
    public void forward(long taskId, String userId, String targetEntityId) {
        taskService.forward(taskId, userId, targetEntityId);
    }

    @Override
    public void delegate(long taskId, String userId, String targetEntityId) {
        taskService.delegate(taskId, userId, targetEntityId);
    }

    @Override
    public void complete(long taskId, String user, Map<String, Object> params) {
        try {
            taskService.complete(taskId, user, params);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public void claim(long taskId, String user, boolean autoStart) {
        taskService.claim(taskId, user);
        if(autoStart){
            taskService.start(taskId, user);
        }
    }

    @Override
    public void release(long taskId, String user) {
        taskService.release(taskId, user);
    }

    @Override
    public void setPriority(long taskId, int priority) {
        taskService.setPriority(taskId, priority);
    }

    public void setTaskNames(long taskId, String taskName) {
        taskService.setName(taskId, taskName);
    }

    @Override
    public void setExpirationDate(long taskId, Date date) {
        taskService.setExpirationDate(taskId, date);
    }

    @Override
    public void setDescriptions(long taskId, String description) {
        taskService.setDescription(taskId, description);
    }


    @Override
    public TaskSummary getTaskDetails(long taskId) {
        Task task = internalTaskService.getTaskById(taskId);
        if (task != null) {
            List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();
            List<String> potOwnersString = null;
            if (potentialOwners != null) {
                potOwnersString = new ArrayList<String>(potentialOwners.size());
                for (OrganizationalEntity e : potentialOwners) {
                    potOwnersString.add(e.getId());
                }
            } 
            return new TaskSummary(task.getId(), task.getName(),
                    task.getDescription(), task.getTaskData().getStatus().name(), task.getPriority(), (task.getTaskData().getActualOwner() != null) ? task.getTaskData().getActualOwner()
                    .getId() : "", (task.getTaskData().getCreatedBy() != null) ? task.getTaskData().getCreatedBy().getId()
                    : "", task.getTaskData().getCreatedOn(), task.getTaskData().getActivationTime(), task.getTaskData()
                    .getExpirationTime(), task.getTaskData().getProcessId(), task.getTaskData().getProcessSessionId(),
                    task.getTaskData().getProcessInstanceId(), task.getTaskData().getDeploymentId()
                    , (int) task.getTaskData().getParentId());
        }
        return null;
    }

    @Override
    public long saveContent(long taskId, Map<String, Object> values) {
        return addContent(taskId, (Map) values);
    }

    public long addContent(long taskId, Map<String, Object> values) {
        return taskService.saveContent(taskId, values);
    }

    public void deleteContent(long taskId, long contentId) {
        taskService.deleteContent(taskId, contentId);
    }

    @Override
    public Map<String, Object> getContentListByTaskId(long taskId) {

        Map<String, Object> inputContent = taskService.getTaskInputContentByTaskId(taskId);

        if (inputContent == null) {
            return new HashMap<String, Object>();
        }
        return inputContent;
    }

    @Override
    public Map<String, Object> getTaskOutputContentByTaskId(long taskId) {
        Map<String, Object> outputContent = taskService.getTaskOutputContentByTaskId(taskId);
        if (outputContent == null) {
            return new HashMap<String, Object>();
        }
        return outputContent;
    }

    @Override
    public long addComment(long taskId, String text, String addedBy, Date addedOn) {

        return taskService.addComment(taskId, text, addedBy, addedOn);
    }

    @Override
    public void deleteComment(long taskId, long commentId) {
        taskService.deleteComment(taskId, commentId);
    }

    @Override
    public List<CommentSummary> getAllCommentsByTaskId(long taskId) {
        return CommentSummaryHelper.adaptCollection(taskService.getCommentsByTaskId(taskId));
    }

    @Override
    public CommentSummary getCommentById(long taskId, long commentId) {
        return CommentSummaryHelper.adapt(taskService.getCommentById(taskId, commentId));
    }

    @Override
    public void updateSimpleTaskDetails(long taskId, String taskName, int priority, String taskDescription,
            // String subTaskStrategy,
            Date dueDate) {
        // TODO: update only the changed bits
        setPriority(taskId, priority);
        setTaskNames(taskId, taskName);
        setDescriptions(taskId, taskDescription);
        // setSubTaskStrategy(taskId, subTaskStrategy);
        setExpirationDate(taskId, dueDate);
    }

    @Override
    public void claimBatch(List<Long> taskIds, String user) {
        for (Long taskId : taskIds) {
            taskService.claim(taskId, user);
            taskService.start(taskId, user);
        }
    }

    @Override
    public void completeBatch(List<Long> taskIds, String user, Map<String, Object> params) {
        for (Long taskId : taskIds) {
            taskService.complete(taskId, user, params);
        }
    }

    @Override
    public void releaseBatch(List<Long> taskIds, String user) {
        for (Long taskId : taskIds) {
            taskService.release(taskId, user);
        }
    }

    @Override
    public Boolean existInDatabase(long taskId) {
        return runtimeDataService.getTaskById(taskId) == null ? false : true;
    }

    @Override
    public List<TaskEventSummary> getAllTaskEvents(long taskId, String filter) {
        return TaskEventSummaryHelper.adaptCollection(taskAuditService.getAllTaskEvents(taskId, new QueryFilter(0,0)));
    }

    @Override
    public List<TaskEventSummary> getAllTaskEventsByProcessInstanceId(long processInstanceId, String filter) {
        return TaskEventSummaryHelper.adaptCollection(taskAuditService.getAllTaskEventsByProcessInstanceId(processInstanceId, new QueryFilter(0,0)));
    }

    public List<AuditTaskSummary> getAllAuditTasks(String filter) {
        return AuditTaskSummaryHelper.adaptCollection(taskAuditService.getAllAuditTasks(new QueryFilter(0,0)));
    }

    public List<AuditTaskSummary> getAllAuditTasksByUser(String userId, String filter) {
        return AuditTaskSummaryHelper.adaptCollection(taskAuditService.getAllAuditTasksByUser(userId, new QueryFilter(0,0)));
    }

    
    
}
