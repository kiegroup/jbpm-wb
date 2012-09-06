/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.console.ng.shared;

import java.util.Date;
import org.jbpm.console.ng.client.model.TaskSummary;
import java.util.List;
import java.util.Map;
import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.task.Content;

/**
 *
 */
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

    List<TaskSummary> getTasksOwned(String userId);

    List<TaskSummary> getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language);

    List<TaskSummary> getSubTasksByParent(long parentId);

    public long addTask(String taskString, Map<String, Object> params);

    public void start(long taskId, String user);

    public void claim(long taskId, String user);

    public void complete(long taskId, String user, Map<String, Object> params);

    public void release(long taskId, String user);

    void setPriority(long taskId, int priority);

    void setExpirationDate(long taskId, Date date);

    public void setDescriptions(long taskId, List<String> descriptions);

    public void setSkipable(long taskId, boolean skipable);

    void setSubTaskStrategy(long taskId, String strategy);

    int getPriority(long taskId);

    Date getExpirationDate(long taskId);

    List<String> getDescriptions(long taskId);

    boolean isSkipable(long taskId);

    String getSubTaskStrategy(long taskId);

    TaskSummary getTaskDetails(long taskId);

    public long saveContent(long taskId, Map<String, String> values);

    public Map<String, String> getContentListById(long contentId);

    public Map<String, String> getTaskOutputContentByTaskId(long taskId);

    public Map<String, String> getContentListByTaskId(long taskId);

    public int getCompletedTaskByUserId(String userId);

    public int getPendingTaskByUserId(String userId);
}
