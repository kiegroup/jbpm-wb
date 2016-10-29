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
public class CaseInstanceSearchRequest {

    private Integer status = 1;

    private CaseInstanceSortBy sortBy = CaseInstanceSortBy.CASE_ID;

    public CaseInstanceSearchRequest() {
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(final Integer status) {
        this.status = status;
    }

    public CaseInstanceSortBy getSortBy() {
        return sortBy;
    }

    public void setSortBy(final CaseInstanceSortBy sortBy) {
        this.sortBy = sortBy;
    }

    @Override
    public String toString() {
        return "CaseInstanceSearchRequest{" +
                "status=" + status +
                ", sortBy=" + sortBy +
                '}';
    }
}
