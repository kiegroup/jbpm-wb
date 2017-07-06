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

package org.jbpm.workbench.cm.model;

import java.util.Date;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

@Bindable
@Portable
public class CaseCommentSummary {

    private String id;
    private String author;
    private String text;
    private Date addedAt;

    public CaseCommentSummary() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(final String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public Date getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(final Date addedAt) {
        this.addedAt = addedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CaseCommentSummary that = (CaseCommentSummary) o;
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

    @Override
    public String toString() {
        return "CaseCommentSummary{" +
                "id='" + id + '\'' +
                ", author='" + author + '\'' +
                ", text='" + text + '\'' +
                ", addedAt=" + addedAt +
                '}';
    }

    public static class Builder {

        private CaseCommentSummary caseComment = new CaseCommentSummary();

        public CaseCommentSummary build() {
            return caseComment;
        }

        public Builder id(final String id) {
            caseComment.setId(id);
            return this;
        }

        public Builder author(final String author) {
            caseComment.setAuthor(author);
            return this;
        }

        public Builder text(final String text) {
            caseComment.setText(text);
            return this;
        }

        public Builder addedAt(final Date addedAt) {
            caseComment.setAddedAt(addedAt);
            return this;
        }
    }
}