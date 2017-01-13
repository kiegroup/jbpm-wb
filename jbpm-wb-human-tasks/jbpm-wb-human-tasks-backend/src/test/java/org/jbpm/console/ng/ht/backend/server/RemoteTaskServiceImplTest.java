/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.jbpm.console.ng.ht.model.TaskEventSummary;
import org.jbpm.console.ng.ht.service.TaskService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.internal.identity.IdentityProvider;
import org.kie.server.api.model.instance.TaskEventInstance;
import org.kie.server.api.model.instance.TaskInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.lang.String.format;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemoteTaskServiceImplTest {

    private static final String CURRENT_USER = "Jan";
    private static final String OTHER_USER = "OTHER_USER";

    @InjectMocks
    private RemoteTaskServiceImpl remoteTaskService;

    @Mock
    IdentityProvider identityProvider;

    @Before
    public void initMocks() {
        when(identityProvider.getName()).thenReturn(CURRENT_USER);
    }

    @Test
    public void allowDelegateStatusCompleted() {
        final TaskInstance task = new TaskInstance();
        task.setStatus("Completed");

        assertFalse(remoteTaskService.isDelegationAllowed(task));
    }

    @Test
    public void allowDelegateActualOwner() {
        final TaskInstance task = new TaskInstance();
        task.setActualOwner(CURRENT_USER);

        assertTrue(remoteTaskService.isDelegationAllowed(task));
    }

    @Test
    public void allowDelegateActualOwnerNotCurrentUser() {
        final TaskInstance task = new TaskInstance();
        task.setActualOwner(OTHER_USER);

        assertFalse(remoteTaskService.isDelegationAllowed(task));
    }

    @Test
    public void allowDelegateCreatedBy() {
        final TaskInstance task = new TaskInstance();
        task.setCreatedBy(CURRENT_USER);

        assertTrue(remoteTaskService.isDelegationAllowed(task));
    }

    @Test
    public void allowDelegateCreatedByNotCurrentUser() {
        final TaskInstance task = new TaskInstance();
        task.setCreatedBy(OTHER_USER);

        assertFalse(remoteTaskService.isDelegationAllowed(task));
    }

    @Test
    public void allowDelegatePotentialOwner() {
        final TaskInstance task = new TaskInstance();
        task.setPotentialOwners(Arrays.asList(CURRENT_USER));
        when(identityProvider.getRoles()).thenReturn(Arrays.asList(CURRENT_USER));

        assertTrue(remoteTaskService.isDelegationAllowed(task));
    }

    @Test
    public void allowDelegatePotentialOwnerNotCurrentUser() {
        final TaskInstance task = new TaskInstance();
        task.setPotentialOwners(Arrays.asList(OTHER_USER));
        when(identityProvider.getRoles()).thenReturn(Arrays.asList(CURRENT_USER));

        assertFalse(remoteTaskService.isDelegationAllowed(task));
    }

    @Test
    public void allowDelegateBusinessAdmins() {
        final TaskInstance task = new TaskInstance();
        task.setBusinessAdmins(Arrays.asList(CURRENT_USER));
        when(identityProvider.getRoles()).thenReturn(Arrays.asList(CURRENT_USER));

        assertTrue(remoteTaskService.isDelegationAllowed(task));
    }

    @Test
    public void allowDelegateBusinessAdminsNotCurrentUser() {
        final TaskInstance task = new TaskInstance();
        task.setBusinessAdmins(Arrays.asList(OTHER_USER));
        when(identityProvider.getRoles()).thenReturn(Arrays.asList(CURRENT_USER));

        assertFalse(remoteTaskService.isDelegationAllowed(task));
    }

    @Test
    public void testBuildTaskEventSummary() {
        final TaskEventInstance event =
                TaskEventInstance.builder().
                        id(1l).
                        taskId(2l).
                        type("UPDATED").
                        user("admin").
                        date(new Date()).
                        workItemId(3l).
                        message("message").build();

        final TaskEventSummary summary = remoteTaskService.build(event);

        assertNotNull(summary);
        assertEquals(event.getId(), summary.getEventId());
        assertEquals(event.getTaskId(), summary.getTaskId());
        assertEquals(event.getType(), summary.getType());
        assertEquals(event.getUserId(), summary.getUserId());
        assertEquals(event.getLogTime(), summary.getLogTime());
        assertEquals(event.getWorkItemId(), summary.getWorkItemId());
        assertEquals(event.getMessage(), summary.getMessage());
    }

    @Test
    public void testInvalidServerTemplate() throws Exception {
        final Method[] methods = TaskService.class.getMethods();
        for (Method method : methods) {
            final Class<?> returnType = method.getReturnType();
            final Object[] args = new Object[method.getParameterCount()];
            Object result = method.invoke(remoteTaskService, args);

            assertMethodResult(method, returnType, result);

            args[0] = "";
            result = method.invoke(remoteTaskService, args);
            assertMethodResult(method, returnType, result);
        }
    }

    private void assertMethodResult(final Method method, final Class<?> returnType, final Object result) {
        if (Collection.class.isAssignableFrom(returnType)) {
            assertNotNull(format("Returned collection for method %s should not be null", method.getName()), result);
            assertTrue(format("Returned collection for method %s should be empty", method.getName()), ((Collection) result).isEmpty());
        } else {
            assertNull(format("Returned object for method %s should be null", method.getName()), result);
        }
    }

}