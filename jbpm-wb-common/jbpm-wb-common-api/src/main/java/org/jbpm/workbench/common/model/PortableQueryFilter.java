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

package org.jbpm.workbench.common.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PortableQueryFilter implements QueryFilter,
                                            Serializable {

    private int offset;
    private int count;
    private boolean isSingleResult;
    private String language;
    private String orderBy;
    private boolean isAscending;
    private String filterParams;
    private Map<String, Object> params = new HashMap<String, Object>();

    public PortableQueryFilter() {
    }

    public PortableQueryFilter(int offset,
                               int count,
                               boolean isSingleResult,
                               String language,
                               String orderBy,
                               boolean isAscending) {
        this.offset = offset;
        this.count = count;
        this.isSingleResult = isSingleResult;
        this.language = language;
        this.orderBy = orderBy;
        this.isAscending = isAscending;
    }

    public PortableQueryFilter(int offset,
                               int count,
                               boolean isSingleResult,
                               String language,
                               String orderBy,
                               boolean isAscending,
                               String filterParams,
                               Map<String, Object> params) {
        this.offset = offset;
        this.count = count;
        this.isSingleResult = isSingleResult;
        this.language = language;
        this.orderBy = orderBy;
        this.isAscending = isAscending;
        this.filterParams = filterParams;
        this.params = params;
    }

    @Override
    public Integer getOffset() {
        return offset;
    }

    @Override
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    @Override
    public Integer getCount() {
        return count;
    }

    @Override
    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public boolean isSingleResult() {
        return isSingleResult;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String getOrderBy() {
        return orderBy;
    }

    @Override
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public String getFilterParams() {
        return filterParams;
    }

    @Override
    public void setFilterParams(String filterParams) {
        this.filterParams = filterParams;
    }

    @Override
    public Map<String, Object> getParams() {
        return params;
    }

    @Override
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    @Override
    public Boolean isAscending() {
        return isAscending;
    }

    @Override
    public void setIsSingleResult(boolean isSingleResult) {
        this.isSingleResult = isSingleResult;
    }

    @Override
    public void setIsAscending(Boolean isAscending) {
        this.isAscending = isAscending;
    }

    @Override
    public String toString() {
        return "PortableQueryFilter{" +
                "offset=" + offset +
                ", count=" + count +
                ", isSingleResult=" + isSingleResult +
                ", language=" + language +
                ", orderBy=" + orderBy +
                ", isAscending=" + isAscending +
                ", filterParams=" + filterParams +
                ", params=" + params + '}';
    }
}
