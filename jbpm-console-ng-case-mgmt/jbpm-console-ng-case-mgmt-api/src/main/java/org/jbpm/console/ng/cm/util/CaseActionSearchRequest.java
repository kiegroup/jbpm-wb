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

package org.jbpm.console.ng.cm.util;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

@Portable
@Bindable
public class CaseActionSearchRequest {

    private Integer status = 1;

    private CaseActionsFilterBy filterBy = CaseActionsFilterBy.AVAILABLE;

    private String sort = "createdOn";

    private boolean sortOrder = true;


    public CaseActionSearchRequest() {
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(final Integer status) {
        this.status = status;
    }

    public CaseActionsFilterBy getFilterBy() {
        return filterBy;
    }

    public void setFilterBy(CaseActionsFilterBy filterBy) {
        this.filterBy = filterBy;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public boolean isSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(boolean sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String toString() {
        return "CaseActionSearchRequest {" +
                "status=" + status +
                ", filterBy=" + filterBy +
                '}';
    }
}
