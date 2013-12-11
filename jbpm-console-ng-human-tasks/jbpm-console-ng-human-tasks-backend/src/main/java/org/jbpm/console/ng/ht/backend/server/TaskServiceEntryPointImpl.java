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
import javax.interceptor.Interceptors;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.seam.transaction.TransactionInterceptor;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.console.ng.ht.model.CommentSummary;
import org.jbpm.console.ng.ht.model.Day;
import org.jbpm.console.ng.ht.model.TaskEventSummary;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
import org.jbpm.services.task.audit.TaskAuditService;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.services.task.impl.model.CommentImpl;
import org.jbpm.services.task.impl.model.UserImpl;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
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

@Service
@ApplicationScoped
@Transactional
@Interceptors({TransactionInterceptor.class})
public class TaskServiceEntryPointImpl implements TaskServiceEntryPoint {

    @Inject
    private InternalTaskService taskService;
    
    @Inject
    private TaskAuditService taskAudit;

    @Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwnerByExpirationDateOptional(String userId,
            List<String> status, Date from, String language) { //@TODO: MUST ADD LANGUAGE FILTER
        List<Status> statuses = new ArrayList<Status>();
        for (String s : status) {
            statuses.add(Status.valueOf(s));
        }
        List<TaskSummary> taskSummaries = null;
        if (from != null) {
            taskSummaries = TaskSummaryHelper.adaptCollection(
                    taskService.getTasksAssignedAsPotentialOwnerByExpirationDateOptional(
                            userId, statuses, from));
        } else {
            taskSummaries = TaskSummaryHelper.adaptCollection(
                    taskService.getTasksAssignedAsPotentialOwnerByStatus(userId, statuses, "en-UK"));
        }
        setPotentionalOwners(taskSummaries);
        return taskSummaries;
    }

    private void setPotentionalOwners(List<TaskSummary> taskSummaries) {
        //This is a hack we need to find a way to get the PotentialOwners in a performant way
        List<Long> taskIds = new ArrayList<Long>(taskSummaries.size());
        for (TaskSummary ts : taskSummaries) {
            taskIds.add(ts.getId());
        }
        if (taskIds.size() > 0) {
            Map<Long, List<String>> potentialOwnersForTaskIds = getPotentialOwnersForTaskIds(taskIds);
            for (TaskSummary ts : taskSummaries) {
                ts.setPotentialOwners(potentialOwnersForTaskIds.get(ts.getId()));
            }
        }
    }

    @Override
    public List<TaskSummary> getTasksOwnedByExpirationDateOptional(String userId, List<String> status, Date from,
            String language) { //@TODO: MUST ADD LANGUAGE FILTER
        List<Status> statuses = new ArrayList<Status>();
        for (String s : status) {
            statuses.add(Status.valueOf(s));
        }
        List<TaskSummary> taskSummaries = TaskSummaryHelper.adaptCollection(
                taskService.getTasksOwnedByStatus(
                        userId, statuses, "en-UK"));
        setPotentionalOwners(taskSummaries);
        return taskSummaries;
    }


    @Override
    public Map<Long, List<String>> getPotentialOwnersForTaskIds(List<Long> taskIds) {
        Map<Long, List<OrganizationalEntity>> potentialOwnersForTaskIds = taskService.getPotentialOwnersForTaskIds(
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
            Date dateFrom, Date dateTo, String language) {
        LocalDate dayFrom = new LocalDate(dateFrom);
        LocalDate dayTo = new LocalDate(dateTo);

        LocalDate today = new LocalDate();
        int nrOfDaysTotal = getNumberOfDaysWithinDateRange(dayFrom, dayTo);
        Map<LocalDate, List<TaskSummary>> tasksByDay = createDaysMapAndInitWithEmptyListForEachDay(dayFrom, nrOfDaysTotal);

        List<TaskSummary> taskSummaries = adaptTaskSummaryCollection(
                taskService.getTasksOwnedByStatus(userId, convertStatuses(strStatuses), language));

        setPotentionalOwners(taskSummaries);
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
            Date dayDate = entry.getKey().toDateMidnight().toDate();
            tasksByDay.put(new Day(dayDate, dayFormat.format(dayDate)), entry.getValue());
        }
        return tasksByDay;
    }

    @Override
    public Map<Day, List<TaskSummary>> getTasksAssignedAsPotentialOwnerFromDateToDateByDays(String userId,
            List<String> strStatuses, Date from, int nrOfDaysTotal, String language) {
        Date toDate = getEndDateBasedOnStartDateAndNumberOfDaysBetweenThem(from, nrOfDaysTotal);
        return getTasksAssignedAsPotentialOwnerFromDateToDateByDays(userId, strStatuses, from,
                toDate, language);
    }

    public Map<Day, List<TaskSummary>> getTasksAssignedAsPotentialOwnerFromDateToDateByDays(String userId,
            List<String> strStatuses, Date dateFrom, Date dateTo, String language) {
        LocalDate dayFrom = new LocalDate(dateFrom);
        LocalDate dayTo = new LocalDate(dateTo);

        LocalDate today = new LocalDate();

        int nrOfDaysTotal = getNumberOfDaysWithinDateRange(dayFrom, dayTo);
        Map<LocalDate, List<TaskSummary>> tasksByDay = createDaysMapAndInitWithEmptyListForEachDay(dayFrom, nrOfDaysTotal);

        List<TaskSummary> taskSummaries = adaptTaskSummaryCollection(
                taskService.getTasksAssignedAsPotentialOwnerByStatus(userId, convertStatuses(strStatuses), language));

        setPotentionalOwners(taskSummaries);
        fillDaysMapWithTasksBasedOnExpirationDate(tasksByDay, taskSummaries, today);
        return transformLocalDatesToDays(tasksByDay);
    }

    @Override
    public Map<Day, List<TaskSummary>> getTasksOwnedFromDateToDateByDays(String userId, List<String> strStatuses, Date from,
            int nrOfDaysTotal, String language) {
        Date toDate = getEndDateBasedOnStartDateAndNumberOfDaysBetweenThem(from, nrOfDaysTotal);
        return getTasksOwnedFromDateToDateByDays(userId, strStatuses, from, toDate,
                language);
    }

    private Date getEndDateBasedOnStartDateAndNumberOfDaysBetweenThem(Date from, int nrOfDaysTotal) {
        return new LocalDate(from.getTime()).plusDays(nrOfDaysTotal - 1).toDateMidnight().toDate();
    }


    public Map<Day, List<TaskSummary>> getTasksOwnedFromDateToDateByDays(String userId, Date from, Date to, String language) {
        List<String> statuses = new ArrayList<String>();
        statuses.add("InProgress");
        statuses.add("Reserved");
        statuses.add("Created");
        return getTasksOwnedFromDateToDateByDays(userId, statuses, from, to, language);
    }


    /**
     * Group Operations
     */
    public List<TaskSummary> getTasksAssignedByGroup(String userId, String groupId, String language) {
        List<org.kie.api.task.model.TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner(
                userId, language);
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
        return taskService.addTask(task, inputs);
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
        try{
            taskService.complete(taskId, user, params);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
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
        taskService.setTaskNames(taskId, TaskI18NHelper.adaptI18NList(taskNames));
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
        for (OrganizationalEntity e : potentialOwners) {
            potOwnersString.add(e.getId());
        }
        return new TaskSummary(task.getId(), task.getTaskData().getProcessInstanceId(),
                ((task.getNames() != null && task
                        .getNames().size() > 0)) ? task.getNames().get(0).getText() : "",
                ((task.getSubjects() != null && task
                        .getSubjects().size() > 0)) ? task.getSubjects().get(0).getText() : "",
                ((task.getDescriptions() != null && task.getDescriptions().size() > 0)) ? task.getDescriptions().get(0)
                        .getText() : "", task.getTaskData().getStatus().name(), task.getPriority(), task.getTaskData()
                .isSkipable(), (task.getTaskData().getActualOwner() != null) ? task.getTaskData().getActualOwner()
                .getId() : "", (task.getTaskData().getCreatedBy() != null) ? task.getTaskData().getCreatedBy().getId()
                : "", task.getTaskData().getCreatedOn(), task.getTaskData().getActivationTime(), task.getTaskData()
                .getExpirationTime(), task.getTaskData().getProcessId(), task.getTaskData().getProcessSessionId(),
                ((InternalTask) task).getSubTaskStrategy().name(), (int) task.getTaskData().getParentId(),
                potOwnersString);
    }

    @Override
    public long saveContent(long taskId, Map<String, String> values) {
        return addContent(taskId, (Map) values);
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
        if (documentContentId > 0) {
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
            // String subTaskStrategy,
            Date dueDate) {
        // TODO: update only the changed bits
        setPriority(taskId, priority);
        setTaskNames(taskId, taskNames);
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

    public List<TaskEventSummary> getAllTaskEvents(long taskId) {
        return TaskEventSummaryHelper.adaptCollection(taskAudit.getAllTaskEvents(taskId));
    }

}
