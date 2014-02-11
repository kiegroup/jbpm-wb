/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.gc.client.list.base;

import java.util.Set;

import com.google.gwt.view.client.SelectionChangeEvent;

public interface GridViewContainer<T> {

    static final String GRID_STYLE = "table table-bordered table-striped table-hover";

    enum GridSelectionModel {
        SIMPLE, MULTI;
    }

    void setSelectionModel(GridSelectionModel selectionModel);

    void multiSelectionModelChange(SelectionChangeEvent event, Set<T> selectedItemsSelectionModel);

    void simpleSelectionModelChange(SelectionChangeEvent event, T selectedItemSelectionModel);

    void setGridEvents();

    void initGridColumns();

    void refreshItems();

}