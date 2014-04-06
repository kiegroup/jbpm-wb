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

package org.jbpm.console.ng.ht.model;

import java.io.Serializable;
import java.util.Date;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class AttachmentSummary implements Serializable {
    
    private long id;
    
    private String name;
    
    private String contentType;
    
    private String attachedBy;
    
    private Date attachedAt;
    
    private int size;

    public AttachmentSummary() {
    }

    public AttachmentSummary(long id, String name, String contentType, String attachedBy, Date attachedAt, int size) {
        this.id = id;
        this.name = name;
        this.contentType = contentType;
        this.attachedBy = attachedBy;
        this.attachedAt = attachedAt;
        this.size = size;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getAttachedBy() {
        return attachedBy;
    }

    public void setAttachedBy(String attachedBy) {
        this.attachedBy = attachedBy;
    }

    public Date getAttachedAt() {
        return attachedAt;
    }

    public void setAttachedAt(Date attachedAt) {
        this.attachedAt = attachedAt;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "AttachmentSummary{" + "id=" + id + ", name=" + name + ", contentType=" + contentType + ", attachedBy=" 
                + attachedBy + ", attachedOn=" + attachedAt + ", size=" + size + '}';
    }

}
