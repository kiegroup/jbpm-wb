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

import java.util.function.Predicate;

import org.jbpm.workbench.ht.model.events.AbstractTaskEvent;
import org.jbpm.workbench.ht.model.events.TaskSelectionEvent;

public abstract class AbstractTaskPresenter {

    private Long taskId;
    private String serverTemplateId;
    private String containerId;

    protected Predicate<AbstractTaskEvent> isSameTaskFromEvent() {
        return e -> e.getServerTemplateId().equals(serverTemplateId) && e.getContainerId().equals(containerId) && e.getTaskId().equals(taskId);
    }

    protected void setSelectedTask(final TaskSelectionEvent event){
        taskId = event.getTaskId();
        serverTemplateId = event.getServerTemplateId();
        containerId = event.getContainerId();
    }

    public Long getTaskId() {
        return taskId;
    }

    public String getContainerId() {
        return containerId;
    }

    public String getServerTemplateId() {
        return serverTemplateId;
    }
}
