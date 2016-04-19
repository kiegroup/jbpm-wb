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

import java.util.Collections;

import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.task.impl.model.GroupImpl;
import org.jbpm.services.task.impl.model.PeopleAssignmentsImpl;
import org.jbpm.services.task.impl.model.TaskDataImpl;
import org.jbpm.services.task.impl.model.TaskImpl;
import org.jbpm.services.task.impl.model.UserImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskOperationsServiceImplTest {

    @Mock
    private UserTaskService taskService;

    @InjectMocks
    TaskOperationsServiceImpl taskOperationsService;

    @Test
    public void testAllowDelegateStatusCompleted() {
        final long takId = 1;
        final TaskImpl task = new TaskImpl();
        final TaskDataImpl taskData = new TaskDataImpl();
        taskData.setStatus(Status.Completed);
        task.setTaskData(taskData);

        when(taskService.getTask(takId)).thenReturn(task);

        assertFalse(taskOperationsService.allowDelegate(takId, null, null));
    }

    @Test
    public void testAllowDelegateNoTask() {
        assertFalse(taskOperationsService.allowDelegate(1, null, null));
    }

    @Test
    public void testAllowDelegateNoData() {
        final long takId = 1;
        final String userId = "user";
        final String groupId = "group";
        final TaskImpl task = new TaskImpl();
        final TaskDataImpl taskData = new TaskDataImpl();
        task.setTaskData(taskData);
        final PeopleAssignmentsImpl peopleAssignments = new PeopleAssignmentsImpl();
        task.setPeopleAssignments(peopleAssignments);

        when(taskService.getTask(takId)).thenReturn(task);

        assertFalse(taskOperationsService.allowDelegate(takId, userId, Collections.singleton(groupId)));
    }

    @Test
    public void testAllowDelegateActualOwner() {
        final long takId = 1;
        final String userId = "user";
        final TaskImpl task = new TaskImpl();
        final TaskDataImpl taskData = new TaskDataImpl();
        taskData.setActualOwner(new UserImpl(userId));
        task.setTaskData(taskData);
        final PeopleAssignmentsImpl peopleAssignments = new PeopleAssignmentsImpl();
        task.setPeopleAssignments(peopleAssignments);

        when(taskService.getTask(takId)).thenReturn(task);

        assertTrue(taskOperationsService.allowDelegate(takId, userId, Collections.<String>emptySet()));
    }

    @Test
    public void testAllowDelegateInitiator() {
        final long takId = 1;
        final String userId = "user";
        final TaskImpl task = new TaskImpl();
        task.setTaskData(new TaskDataImpl());
        final PeopleAssignmentsImpl peopleAssignments = new PeopleAssignmentsImpl();
        peopleAssignments.setTaskInitiator(new UserImpl(userId));
        task.setPeopleAssignments(peopleAssignments);

        when(taskService.getTask(takId)).thenReturn(task);

        assertTrue(taskOperationsService.allowDelegate(takId, userId, Collections.<String>emptySet()));
    }

    @Test
    public void testAllowDelegateUserInPotentialOwner() {
        final long takId = 1;
        final String userId = "user";
        final TaskImpl task = new TaskImpl();
        task.setTaskData(new TaskDataImpl());
        final PeopleAssignmentsImpl peopleAssignments = new PeopleAssignmentsImpl();
        peopleAssignments.setPotentialOwners(Collections.<OrganizationalEntity>singletonList(new UserImpl(userId)));
        task.setPeopleAssignments(peopleAssignments);

        when(taskService.getTask(takId)).thenReturn(task);

        assertTrue(taskOperationsService.allowDelegate(takId, userId, Collections.<String>emptySet()));
    }

    @Test
    public void testAllowDelegateGroupInPotentialOwner() {
        final long takId = 1;
        final String userId = "user";
        final String groupId = "group";
        final TaskImpl task = new TaskImpl();
        task.setTaskData(new TaskDataImpl());
        final PeopleAssignmentsImpl peopleAssignments = new PeopleAssignmentsImpl();
        peopleAssignments.setPotentialOwners(Collections.<OrganizationalEntity>singletonList(new GroupImpl(groupId)));
        task.setPeopleAssignments(peopleAssignments);

        when(taskService.getTask(takId)).thenReturn(task);

        assertTrue(taskOperationsService.allowDelegate(takId, userId, Collections.singleton(groupId)));
    }

    @Test
    public void testAllowDelegateUserInBusinessAdministrators() {
        final long takId = 1;
        final String userId = "user";
        final TaskImpl task = new TaskImpl();
        task.setTaskData(new TaskDataImpl());
        final PeopleAssignmentsImpl peopleAssignments = new PeopleAssignmentsImpl();
        peopleAssignments.setBusinessAdministrators(Collections.<OrganizationalEntity>singletonList(new UserImpl(userId)));
        task.setPeopleAssignments(peopleAssignments);

        when(taskService.getTask(takId)).thenReturn(task);

        assertTrue(taskOperationsService.allowDelegate(takId, userId, Collections.<String>emptySet()));
    }

    @Test
    public void testAllowDelegateGroupInBusinessAdministrators() {
        final long takId = 1;
        final String userId = "user";
        final String groupId = "group";
        final TaskImpl task = new TaskImpl();
        task.setTaskData(new TaskDataImpl());
        final PeopleAssignmentsImpl peopleAssignments = new PeopleAssignmentsImpl();
        peopleAssignments.setBusinessAdministrators(Collections.<OrganizationalEntity>singletonList(new GroupImpl(groupId)));
        task.setPeopleAssignments(peopleAssignments);

        when(taskService.getTask(takId)).thenReturn(task);

        assertTrue(taskOperationsService.allowDelegate(takId, userId, Collections.singleton(groupId)));
    }

}
