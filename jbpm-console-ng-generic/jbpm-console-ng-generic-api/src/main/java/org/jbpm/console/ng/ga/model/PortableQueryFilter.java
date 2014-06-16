/*
 * Copyright 2014 JBoss by Red Hat.
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

package org.jbpm.console.ng.ga.model;

import java.io.Serializable;
import java.util.Map;
import org.jboss.errai.common.client.api.annotations.Portable;


/**
 *
 * @author salaboy
 */
@Portable
public class PortableQueryFilter implements QueryFilter, Serializable{

  private int offset;
  private int count;
  private boolean isSingleResult;
  private String language;
  private String orderBy;
  private boolean isAscending;
  private String filterParams;
  private Map<String, Object> params;

  public PortableQueryFilter() {
  }

  public PortableQueryFilter(int offset, int count, boolean isSingleResult, String language, String orderBy, boolean isAscending) {
    this.offset = offset;
    this.count = count;
    this.isSingleResult = isSingleResult;
    this.language = language;
    this.orderBy = orderBy;
    this.isAscending = isAscending;
  }

  public PortableQueryFilter(int offset, int count, boolean isSingleResult, String language, String orderBy, boolean isAscending, String filterParams, Map<String, Object> params) {
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
  public int getOffset() {
    return offset;
  }

  @Override
  public int getCount() {
    return count;
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
  public String getOrderBy() {
    return orderBy;
  }

  @Override
  public String getFilterParams() {
    return filterParams;
  }

  @Override
  public Map<String, Object> getParams() {
    return params;
  }

  @Override
  public boolean isAscending() {
    return isAscending;
  }

  @Override
  public void setOffset(int offset) {
    this.offset = offset;
  }

  @Override
  public void setCount(int count) {
    this.count = count;
  }

  @Override
  public void setIsSingleResult(boolean isSingleResult) {
    this.isSingleResult = isSingleResult;
  }

  @Override
  public void setLanguage(String language) {
    this.language = language;
  }

  @Override
  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }

  @Override
  public void setIsAscending(boolean isAscending) {
    this.isAscending = isAscending;
  }

  @Override
  public void setFilterParams(String filterParams) {
    this.filterParams = filterParams;
  }

  @Override
  public void setParams(Map<String, Object> params) {
    this.params = params;
  }

  
  
  @Override
  public String toString() {
    return "PortableQueryFilter{" + "offset=" + offset + ", count=" + count + ", isSingleResult=" + isSingleResult + ", language=" + language + ", orderBy=" + orderBy + ", isAscending=" + isAscending + ", filterParams=" + filterParams + ", params=" + params + '}';
  }
  
  
  
}
