/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.console.ng.ht.service;

import java.util.Date;


import java.util.List;
import java.util.Map;
import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.console.ng.ht.model.CommentSummary;
import org.jbpm.console.ng.ht.model.Day;
import org.jbpm.console.ng.ht.model.IdentitySummary;
import org.jbpm.console.ng.ht.model.TaskSummary;

@Remote
public interface TaskServiceEntryPoint {

    List<TaskSummary> getTasksAssignedAsBusinessAdministrator(String userId, String language);

    List<TaskSummary> getTasksAssignedAsExcludedOwner(String userId, String language);

    List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, String language);

    List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language);

    List<TaskSummary> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResult);

    List<TaskSummary> getTasksAssignedAsRecipient(String userId, String language);

    List<TaskSummary> getTasksAssignedAsTaskInitiator(String userId, String language);

    List<TaskSummary> getTasksAssignedAsTaskStakeholder(String userId, String language);
    
    List<TaskSummary> getTasksAssignedByGroup(String groupId, String language); 
    
    List<TaskSummary> getTasksAssignedByGroups(List<String> groupsId, String language);
    
    /**
     * Gets the mapping '{@link Day} -> list of owned tasks' from start day to end day (including).
     * 
     * Only active tasks are considered (task with status "InProgress", "Reserved" or "Created").
     * 
     * @param userId  id of the task owner
     * @param from  start day
     * @param to  end day
     * @param language
     * 
     * @return list of tasks per day for specified days (dates)
     */
    Map<Day, List<TaskSummary>> getTasksOwnedFromDateToDateByDays(String userId, Date from, Date to, String language);
    
    /**
     * Gets the mapping '{@link Day} -> list of owned tasks' starting from specified day and for specified number of days.
     * 
     * Only active tasks are considered (task with status "InProgress", "Reserved" or "Created").
     * 
     * @param userId  id of the task owner
     * @param from  start day
     * @param nrOfDaysTotal how many days to return including start date
     * @param language
     * 
     * @return list of task per day for specified days (dates)
     */
    Map<Day, List<TaskSummary>> getTasksOwnedFromDateToDateByDays(String userId, Date from, int nrOfDaysTotal, String language);
    
    /**
     * Gets the mapping '{@link Day} -> list of owned tasks' from start day to end day (including).
     * Only tasks with specified statuses are considered.
     * 
     * @param userId id of the task owner
     * @param strStatuses  list of statuses
     * @param from  start day
     * @param to end day
     * @param language
     * @return list of tasks per day for specified days (dates)
     */
    Map<Day, List<TaskSummary>> getTasksOwnedFromDateToDateByDays(String userId, List<String> strStatuses, Date from, Date to, String language);
    
    /**
     * Gets the mapping '{@link Day} -> list of owned tasks' starting from specified dayand for specified number of days.
     * Only tasks with specified statuses are considered.
     *  
     * @param userId  id of the task owner
     * @param from  start day
     * @param nrOfDaysTotal how many days to return including start date
     * @param language
     * 
     * @return list of tasks per day for specified days (dates)
     */
    Map<Day, List<TaskSummary>> getTasksOwnedFromDateToDateByDays(String userId, List<String> strStatuses, Date from, int nrOfDaysTotal, String language);
    
    /**
     * Gets the mapping '{@link Day} -> list of assigned personal and groups tasks' from start day to end day (including).
     * 
     * Only tasks with status "Ready", "InProgress", "Reserved" or "Created" are considered.
     * 
     * @param userId  id of the task owner
     * @param groupIds  list of group ids
     * @param from  start day
     * @param to  end day
     * @param language
     * 
     * @return list of tasks per day for specified days (dates)
     */
    Map<Day, List<TaskSummary>> getTasksAssignedFromDateToDatePersonalAndGroupsTasksByDays(String userId, List<String> groupIds, Date from, Date to, String language);
    
    /**
     * Gets the mapping '{@link Day} -> list of assigned personal and groups tasks' starting from specified day and for specified number of days.
     * 
     * Only tasks with status "Ready", "InProgress", "Reserved" or "Created" are considered.
     * 
     * @param userId  id of the task owner
     * @param groupIds  list of group ids
     * @param from start day
     * @param nrOfDaysTotal how many days to return including start date
     * @param language
     * 
     * @return list of tasks per day for specified days (dates)
     */
    Map<Day, List<TaskSummary>> getTasksAssignedFromDateToDatePersonalAndGroupsTasksByDays(String userId, List<String> groupIds, Date from, int nrOfDaysTotal, String language);
    
    /**
     * Gets the mapping '{@link Day} -> list of assigned groups tasks' from start day to end day (including).
     * 
     * @param groupIds  list of group ids
     * @param from start day
     * @param to  end day
     * @param language
     * 
     * @return list of tasks per day for specified days (dates)
     */
    Map<Day, List<TaskSummary>> getTasksAssignedFromDateToDateByGroupsByDays(List<String> groupIds, Date from, Date to, String language);
    
    /**
     * Gets the mapping '{@link Day} -> list of assigned groups tasks' starting from specified day and for specified number of days.
     * 
     * @param groupIds  list of group ids
     * @param from start day
     * @param nrOfDaysTotal how many days to return including start date
     * @param language
     * 
     * @return list of tasks per day for specified days (dates)
     */
    Map<Day, List<TaskSummary>> getTasksAssignedFromDateToDateByGroupsByDays(List<String> groupIds, Date from, int nrOfDaysTotal, String language);
    
    List<TaskSummary> getTasksOwned(String userId, String language);
    
    List<TaskSummary> getTasksOwnedByStatus(String userId, List<String> status, String language);
    
    List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language);

    List<TaskSummary> getSubTasksByParent(long parentId);

    long addTask(String taskString, Map<String, Object> inputs, Map<String, Object> templateInputs);
    
    long addTaskAndStart(String taskString, Map<String, Object> inputs, String userId, Map<String, Object> templateInputs);

    void start(long taskId, String user);
    
    void startBatch(List<Long> taskIds, String user);

    void claim(long taskId, String user);
    
    void claimBatch(List<Long> taskIds, String user);

    void complete(long taskId, String user, Map<String, Object> params);
    
    void completeBatch(List<Long> taskIds, String user, Map<String, Object> params);

    void release(long taskId, String user);
    
    void releaseBatch(List<Long> taskIds, String user);

    void forward(long taskId, String userId, String targetEntityId);
    
    void setPriority(long taskId, int priority);

    void setExpirationDate(long taskId, Date date);
   
    void setDescriptions(long taskId, List<String> descriptions);

    void setSkipable(long taskId, boolean skipable);

    void setSubTaskStrategy(long taskId, String strategy);

    int getPriority(long taskId);

    Date getExpirationDate(long taskId);

    List<String> getDescriptions(long taskId);

    boolean isSkipable(long taskId);

    String getSubTaskStrategy(long taskId);

    TaskSummary getTaskDetails(long taskId);

    long saveContent(long taskId, Map<String, String> values);

    Map<String, String> getContentListById(long contentId);

    Map<String, String> getTaskOutputContentByTaskId(long taskId);

    Map<String, String> getContentListByTaskId(long taskId);

    int getCompletedTaskByUserId(String userId);

    int getPendingTaskByUserId(String userId);
    
    List<TaskSummary> getTasksAssignedPersonalAndGroupTasks(String userId, String groupId, String language);
    
    List<TaskSummary> getTasksAssignedPersonalAndGroupsTasks(String userId, List<String> groupIds, String language);
    
    IdentitySummary getOrganizationalEntityById(String entityId);
    
    List<IdentitySummary> getOrganizationalEntities();
    
    long addComment(long taskId, String text, String addedBy, Date addedOn);

    void deleteComment(long taskId, long commentId);

    List<CommentSummary> getAllCommentsByTaskId(long taskId);

    CommentSummary getCommentById(long commentId);
    
    void updateSimpleTaskDetails(long taskId, List<String> taskNames, int priority, List<String> taskDescription, 
                //String subTaskStrategy, 
                Date dueDate);
    
}
