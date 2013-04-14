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

/**
 *
 * @author salaboy
 */
@Portable
public class CommentSummary implements Serializable{
    
    
    private long id;

    
    private String text;
    
    
    private String addedBy;
    
    private Date addedAt;  

    public CommentSummary(long id, String text, String addedBy, Date addedAt) {
        this.id = id;
        this.text = text;
        this.addedBy = addedBy;
        this.addedAt = addedAt;
    }

    public CommentSummary(String text, String addedBy, Date addedAt) {
        this.text = text;
        this.addedBy = addedBy;
        this.addedAt = addedAt;
    }
    
    

    public CommentSummary() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public Date getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
    }

    @Override
    public String toString() {
        return "CommentSummary{" + "id=" + id + ", text=" + text + ", addedBy=" + addedBy + ", addedAt=" + addedAt + '}';
    }
    
    
}
