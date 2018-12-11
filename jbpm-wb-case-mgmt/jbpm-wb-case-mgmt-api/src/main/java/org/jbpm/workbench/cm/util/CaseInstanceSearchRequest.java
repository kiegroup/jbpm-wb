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

package org.jbpm.workbench.cm.util;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

@Portable
@Bindable
public class CaseInstanceSearchRequest {

    private CaseStatus status = CaseStatus.OPEN;

    private CaseInstanceSortBy sortBy = CaseInstanceSortBy.CASE_ID;

    private Boolean sortByAsc = true;

    public CaseInstanceSearchRequest() {
    }

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(final CaseStatus status) {
        this.status = status;
    }

    public CaseInstanceSortBy getSortBy() {
        return sortBy;
    }

    public void setSortBy(final CaseInstanceSortBy sortBy) {
        this.sortBy = sortBy;
    }

    public Boolean getSortByAsc() {
        return sortByAsc;
    }

    public void setSortByAsc(Boolean sortByAsc) {
        this.sortByAsc = sortByAsc;
    }

    @Override
    public String toString() {
        return "CaseInstanceSearchRequest{" +
                "status=" + status +
                ", sortBy=" + sortBy +
                ", sortByAsc=" + sortByAsc +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CaseInstanceSearchRequest)) {
            return false;
        }

        CaseInstanceSearchRequest that = (CaseInstanceSearchRequest) o;

        if (getStatus() != that.getStatus()) {
            return false;
        }
        if (getSortBy() != that.getSortBy()) {
            return false;
        }
        return getSortByAsc() != null ? getSortByAsc().equals(that.getSortByAsc()) : that.getSortByAsc() == null;
    }

    @Override
    @SuppressWarnings("PMD.AvoidMultipleUnaryOperators")
    public int hashCode() {
        int result = getStatus() != null ? getStatus().hashCode() : 0;
        result = ~~result;
        result = 31 * result + (getSortBy() != null ? getSortBy().hashCode() : 0);
        result = ~~result;
        result = 31 * result + (getSortByAsc() != null ? getSortByAsc().hashCode() : 0);
        result = ~~result;
        return result;
    }
}
