/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.ht.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class CommentSummary implements Serializable {

    private Long id;

    private String text;

    private String addedBy;

    private Date addedAt;

    public CommentSummary(Long id,
                          String text,
                          String addedBy,
                          Date addedAt) {
        this.id = id;
        this.text = text;
        this.addedBy = addedBy;
        this.addedAt = addedAt;
    }

    public CommentSummary(String text,
                          String addedBy,
                          Date addedAt) {
        this.text = text;
        this.addedBy = addedBy;
        this.addedAt = addedAt;
    }

    public CommentSummary() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
        return "CommentSummary{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", addedBy='" + addedBy + '\'' +
                ", addedAt=" + addedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CommentSummary that = (CommentSummary) o;
        return Objects.equals(id,
                              that.id);
    }

    @Override
    @SuppressWarnings("PMD.AvoidMultipleUnaryOperators")
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = ~~result;
        return result;
    }
}
