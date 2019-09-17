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

import java.util.Arrays;

import org.jbpm.workbench.ht.util.TaskStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.internal.identity.IdentityProvider;
import org.kie.server.api.model.instance.TaskInstance;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskAssignmentSummaryMapperTest {

    private static final String CURRENT_USER = "CURRENT_USER";
    private static final String OTHER_USER = "OTHER_USER";

    @Mock
    private IdentityProvider identityProvider;

    @Before
    public void initMocks() {
        when(identityProvider.getName()).thenReturn(CURRENT_USER);
    }

    @Test
    public void allowDelegateOwnedTasks() {
        final TaskInstance task = new TaskInstance();
        task.setActualOwner(CURRENT_USER);

        assertDelegationAllowed(task, identityProvider, TaskStatus.TASK_STATUS_RESERVED);
        assertDelegationAllowed(task, identityProvider, TaskStatus.TASK_STATUS_IN_PROGRESS);

        assertDelegationDenied(task, identityProvider, TaskStatus.TASK_STATUS_READY);
        assertDelegationDenied(task, identityProvider, TaskStatus.TASK_STATUS_COMPLETED);
    }

    @Test
    public void allowDelegateActualOwnerNotCurrentUser() {
        final TaskInstance task = new TaskInstance();
        task.setActualOwner(OTHER_USER);

        assertDelegationDenied(task, identityProvider, TaskStatus.TASK_STATUS_RESERVED);
        assertDelegationDenied(task, identityProvider, TaskStatus.TASK_STATUS_IN_PROGRESS);
        assertDelegationDenied(task, identityProvider, TaskStatus.TASK_STATUS_READY);
        assertDelegationDenied(task, identityProvider, TaskStatus.TASK_STATUS_COMPLETED);
    }

    @Test
    public void allowDelegatePotentialOwnerOnReadyTasks() {
        final TaskInstance task = new TaskInstance();
        task.setPotentialOwners(Arrays.asList(CURRENT_USER));
        when(identityProvider.getRoles()).thenReturn(Arrays.asList(CURRENT_USER));

        assertDelegationAllowed(task, identityProvider, TaskStatus.TASK_STATUS_READY);

        assertDelegationDenied(task, identityProvider, TaskStatus.TASK_STATUS_RESERVED);
        assertDelegationDenied(task, identityProvider, TaskStatus.TASK_STATUS_IN_PROGRESS);
        assertDelegationDenied(task, identityProvider, TaskStatus.TASK_STATUS_COMPLETED);
    }

    @Test
    public void allowDelegatePotentialOwnerNotCurrentUser() {
        final TaskInstance task = new TaskInstance();
        task.setPotentialOwners(Arrays.asList(OTHER_USER));
        when(identityProvider.getRoles()).thenReturn(Arrays.asList(CURRENT_USER));

        assertDelegationDenied(task, identityProvider, TaskStatus.TASK_STATUS_READY);
        assertDelegationDenied(task, identityProvider, TaskStatus.TASK_STATUS_RESERVED);
        assertDelegationDenied(task, identityProvider, TaskStatus.TASK_STATUS_IN_PROGRESS);
        assertDelegationDenied(task, identityProvider, TaskStatus.TASK_STATUS_COMPLETED);
    }

    @Test
    public void allowDelegateBusinessAdmins() {
        final TaskInstance task = new TaskInstance();
        task.setBusinessAdmins(Arrays.asList(CURRENT_USER));
        task.setStatus(TaskStatus.TASK_STATUS_READY.getIdentifier());
        when(identityProvider.getRoles()).thenReturn(Arrays.asList(CURRENT_USER));

        assertDelegationAllowed(task, identityProvider, TaskStatus.TASK_STATUS_READY);
        assertDelegationAllowed(task, identityProvider, TaskStatus.TASK_STATUS_RESERVED);
        assertDelegationAllowed(task, identityProvider, TaskStatus.TASK_STATUS_IN_PROGRESS);

        assertDelegationDenied(task, identityProvider, TaskStatus.TASK_STATUS_COMPLETED);
    }

    @Test
    public void allowDelegateBusinessAdminsNotCurrentUser() {
        final TaskInstance task = new TaskInstance();
        task.setBusinessAdmins(Arrays.asList(OTHER_USER));
        when(identityProvider.getRoles()).thenReturn(Arrays.asList(CURRENT_USER));

        assertDelegationDenied(task, identityProvider, TaskStatus.TASK_STATUS_READY);
        assertDelegationDenied(task, identityProvider, TaskStatus.TASK_STATUS_RESERVED);
        assertDelegationDenied(task, identityProvider, TaskStatus.TASK_STATUS_IN_PROGRESS);
        assertDelegationDenied(task, identityProvider, TaskStatus.TASK_STATUS_COMPLETED);
    }

    @Test
    public void allowForwardActualOwner() {
        final TaskInstance task = new TaskInstance();
        task.setActualOwner(CURRENT_USER);

        assertForwardAllowed(task, identityProvider, TaskStatus.TASK_STATUS_RESERVED);
        assertForwardAllowed(task, identityProvider, TaskStatus.TASK_STATUS_IN_PROGRESS);

        assertForwardDenied(task, identityProvider, TaskStatus.TASK_STATUS_READY);
        assertForwardDenied(task, identityProvider, TaskStatus.TASK_STATUS_COMPLETED);
    }

    @Test
    public void allowForwardActualOwnerNotCurrentUser() {
        final TaskInstance task = new TaskInstance();
        task.setActualOwner(OTHER_USER);

        assertForwardDenied(task, identityProvider, TaskStatus.TASK_STATUS_RESERVED);
        assertForwardDenied(task, identityProvider, TaskStatus.TASK_STATUS_IN_PROGRESS);
        assertForwardDenied(task, identityProvider, TaskStatus.TASK_STATUS_READY);
        assertForwardDenied(task, identityProvider, TaskStatus.TASK_STATUS_COMPLETED);
    }

    @Test
    public void allowForwardPotentialOwner() {
        final TaskInstance task = new TaskInstance();
        task.setPotentialOwners(Arrays.asList(CURRENT_USER));
        when(identityProvider.getRoles()).thenReturn(Arrays.asList(CURRENT_USER));

        assertForwardAllowed(task, identityProvider, TaskStatus.TASK_STATUS_READY);

        assertForwardDenied(task, identityProvider, TaskStatus.TASK_STATUS_RESERVED);
        assertForwardDenied(task, identityProvider, TaskStatus.TASK_STATUS_IN_PROGRESS);
        assertForwardDenied(task, identityProvider, TaskStatus.TASK_STATUS_COMPLETED);
    }

    @Test
    public void allowForwardPotentialOwnerNotCurrentUser() {
        final TaskInstance task = new TaskInstance();
        task.setPotentialOwners(Arrays.asList(OTHER_USER));
        when(identityProvider.getRoles()).thenReturn(Arrays.asList(CURRENT_USER));

        assertForwardDenied(task, identityProvider, TaskStatus.TASK_STATUS_READY);
        assertForwardDenied(task, identityProvider, TaskStatus.TASK_STATUS_RESERVED);
        assertForwardDenied(task, identityProvider, TaskStatus.TASK_STATUS_IN_PROGRESS);
        assertForwardDenied(task, identityProvider, TaskStatus.TASK_STATUS_COMPLETED);
    }

    @Test
    public void allowForwardBusinessAdmins() {
        final TaskInstance task = new TaskInstance();
        task.setBusinessAdmins(Arrays.asList(CURRENT_USER));
        when(identityProvider.getRoles()).thenReturn(Arrays.asList(CURRENT_USER));

        assertForwardAllowed(task, identityProvider, TaskStatus.TASK_STATUS_READY);
        assertForwardAllowed(task, identityProvider, TaskStatus.TASK_STATUS_RESERVED);
        assertForwardAllowed(task, identityProvider, TaskStatus.TASK_STATUS_IN_PROGRESS);

        assertForwardDenied(task, identityProvider, TaskStatus.TASK_STATUS_COMPLETED);
    }

    @Test
    public void allowForwardBusinessAdminsNotCurrentUser() {
        final TaskInstance task = new TaskInstance();
        task.setBusinessAdmins(Arrays.asList(OTHER_USER));
        when(identityProvider.getRoles()).thenReturn(Arrays.asList(CURRENT_USER));

        assertForwardDenied(task, identityProvider, TaskStatus.TASK_STATUS_READY);
        assertForwardDenied(task, identityProvider, TaskStatus.TASK_STATUS_RESERVED);
        assertForwardDenied(task, identityProvider, TaskStatus.TASK_STATUS_IN_PROGRESS);
        assertForwardDenied(task, identityProvider, TaskStatus.TASK_STATUS_COMPLETED);
    }

    private void assertDelegationAllowed(TaskInstance task, IdentityProvider identityProvider, TaskStatus taskStatus) {
        task.setStatus(taskStatus.getIdentifier());
        assertTrue(new TaskAssignmentSummaryMapper().isDelegationAllowed(task, identityProvider));
    }

    private void assertDelegationDenied(TaskInstance task, IdentityProvider identityProvider, TaskStatus taskStatus) {
        task.setStatus(taskStatus.getIdentifier());
        assertFalse(new TaskAssignmentSummaryMapper().isDelegationAllowed(task, identityProvider));
    }

    private void assertForwardAllowed(TaskInstance task, IdentityProvider identityProvider, TaskStatus taskStatus) {
        task.setStatus(taskStatus.getIdentifier());
        assertTrue(new TaskAssignmentSummaryMapper().isForwardAllowed(task, identityProvider));
    }

    private void assertForwardDenied(TaskInstance task, IdentityProvider identityProvider, TaskStatus taskStatus) {
        task.setStatus(taskStatus.getIdentifier());
        assertFalse(new TaskAssignmentSummaryMapper().isForwardAllowed(task, identityProvider));
    }
}