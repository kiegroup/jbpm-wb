/*
 * Copyright 2014 JBoss by Red Hat.
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

import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.ht.model.CommentSummary;
import org.jbpm.console.ng.ht.service.TaskCommentsService;
import org.jbpm.services.api.UserTaskService;

/**
 *
 * @author salaboy
 */
@Service
@ApplicationScoped
public class TaskCommentsServiceImpl implements TaskCommentsService {

    @Inject
    private UserTaskService taskService;

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

}
