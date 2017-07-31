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

package org.jbpm.workbench.cm.client.util;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.UberElement;

public abstract class AbstractPresenter<V extends UberElement> {

    protected int pageSize;
    protected int currentPage = 0;
    
    @Inject
    protected V view;

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @WorkbenchPartView
    public V getView() {
        return view;
    }
    
    public void setPageSize() {
        this.pageSize = -1;
    }
    
    public int getPageSize() {
        return pageSize;
    }

    public void setCurrentPage(int i) {
        this.currentPage = i;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }
}