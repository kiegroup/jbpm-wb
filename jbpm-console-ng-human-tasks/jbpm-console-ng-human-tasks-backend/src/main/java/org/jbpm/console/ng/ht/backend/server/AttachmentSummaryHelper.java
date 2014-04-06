/*
 * Copyright 2013 JBoss by Red Hat.
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
import org.jbpm.console.ng.ht.model.AttachmentSummary;
import org.kie.api.task.model.Attachment;

public class AttachmentSummaryHelper {
    
    public static List<AttachmentSummary> adaptCollection(List<Attachment> attachments) {
        List<AttachmentSummary> attachmentsSummaries = new ArrayList<AttachmentSummary>(attachments.size());
        for (Attachment attachment : attachments) {
            attachmentsSummaries.add(adapt(attachment));
        }
        return attachmentsSummaries;
    }
    
    public static AttachmentSummary adapt(Attachment attachment) {
        return new AttachmentSummary(attachment.getId(), attachment.getName(), attachment.getContentType(), 
                attachment.getAttachedBy().toString(), attachment.getAttachedAt(), attachment.getSize());
    }

}
