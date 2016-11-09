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

package org.jbpm.console.ng.cm.backend.server;

import java.util.Date;

import org.jbpm.console.ng.cm.model.CaseCommentSummary;
import org.junit.Test;
import org.kie.server.api.model.cases.CaseComment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CaseCommentMapperTest {

    public static void assertCaseComment(final CaseComment cc, final CaseCommentSummary ccs) {
        assertNotNull(ccs);

        assertEquals(cc.getId(), ccs.getId());
        assertEquals(cc.getAuthor(), ccs.getAuthor());
        assertEquals(cc.getText(), ccs.getText());
        assertEquals(cc.getAddedAt(), ccs.getAddedAt());
    }

    @Test
    public void testCaseCommentMapper_mapCaseComment() {
        final CaseComment cc = CaseComment.builder()
                .id("commentId")
                .author("admin")
                .text("commentText")
                .addedAt(new Date())
                .build();

        final CaseCommentSummary ccs = new CaseCommentMapper().apply(cc);

        assertCaseComment(cc, ccs);
    }

    @Test
    public void testCaseCommentMapper_mapNull() {
        final CaseComment cc = null;
        final CaseCommentSummary ccs = new CaseCommentMapper().apply(cc);
        assertNull(ccs);
    }
}
