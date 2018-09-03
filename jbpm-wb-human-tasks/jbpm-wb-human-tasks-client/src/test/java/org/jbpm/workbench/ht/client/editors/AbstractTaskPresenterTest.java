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

package org.jbpm.workbench.ht.client.editors;

import org.jbpm.workbench.ht.model.events.TaskCompletedEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;
import org.junit.Test;

import static org.junit.Assert.*;

public abstract class AbstractTaskPresenterTest {

    public abstract AbstractTaskPresenter getPresenter();

    @Test
    public void testTaskIsSameFromEvent() {
        final String serverTemplateId = "serverTemplateId";
        final String containerId = "containerId";
        final Long taskId = 1L;
        TaskSelectionEvent event = new TaskSelectionEvent(serverTemplateId,
                                                          containerId,
                                                          taskId,
                                                          "task",
                                                          false,
                                                          false);

        getPresenter().setSelectedTask(event);

        assertTrue(getPresenter().isSameTaskFromEvent().test(new TaskCompletedEvent(serverTemplateId,
                                                                                    containerId,
                                                                                    taskId)));
        assertTrue(getPresenter().isSameTaskFromEvent().test(new TaskSelectionEvent(serverTemplateId,
                                                                                    containerId,
                                                                                    taskId)));
        assertFalse(getPresenter().isSameTaskFromEvent().test(new TaskCompletedEvent(serverTemplateId,
                                                                                     containerId,
                                                                                     2l)));
        assertFalse(getPresenter().isSameTaskFromEvent().test(new TaskCompletedEvent("anotherServerTemplateId",
                                                                                     containerId,
                                                                                     taskId)));
        assertFalse(getPresenter().isSameTaskFromEvent().test(new TaskCompletedEvent(serverTemplateId,
                                                                                     "anotherContainerId",
                                                                                     taskId)));
    }
}
