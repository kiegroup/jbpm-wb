/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.ga.model;

import java.util.Map;

public interface QueryFilter  {
    Integer getOffset();
    void setOffset(Integer offset);
    Integer getCount();
    void setCount(Integer count);
    boolean isSingleResult();
    void setIsSingleResult(boolean isSingleResult);
    String getLanguage();
    void setLanguage(String language);
    String getOrderBy();
    void setOrderBy(String orderBy);
    String getFilterParams();
    void setFilterParams(String filterParams);
    Map<String, Object> getParams();
    void setParams(Map<String, Object> params);
    Boolean isAscending();
    void setIsAscending(Boolean isAscending);

}
