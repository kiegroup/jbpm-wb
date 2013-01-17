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

import java.util.ArrayList;
import java.util.List;

import org.jbpm.console.ng.ht.model.CommentSummary;
import org.jbpm.task.Comment;

/**
 *
 *
 */
public class CommentSummaryHelper {

    public static List<CommentSummary> adaptCollection(List<org.jbpm.task.Comment> comments) {
        List<CommentSummary> commentsSummaries = new ArrayList<CommentSummary>(comments.size());
        for (org.jbpm.task.Comment comment : comments) {
            commentsSummaries.add(new CommentSummary(comment.getId(), comment.getText(),
                    comment.getAddedBy().toString(), comment.getAddedAt()));
        }
        return commentsSummaries;
    }

    static CommentSummary adapt(Comment comment) {
        return new CommentSummary(comment.getId(), comment.getText(),
                    comment.getAddedBy().toString(), comment.getAddedAt());
    }
}
