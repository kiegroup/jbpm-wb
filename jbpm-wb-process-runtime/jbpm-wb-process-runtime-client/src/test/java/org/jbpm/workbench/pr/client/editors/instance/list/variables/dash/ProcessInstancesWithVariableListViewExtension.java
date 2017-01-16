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

package org.jbpm.workbench.pr.client.editors.instance.list.variables.dash;

import org.jbpm.console.ng.df.client.list.base.DataSetEditorManager;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.widgets.common.client.tables.FilterPagedTable;

public class ProcessInstancesWithVariableListViewExtension extends DataSetProcessInstanceWithVariablesListViewImpl{
    ExtendedPagedTable  mockExtendedPagedTable;

    public void setUpMocks(ExtendedPagedTable extendedPagedTable,
            FilterPagedTable filterPagedTable,
            DataSetEditorManager dataSetEditorManager,
            DataSetProcessInstanceWithVariablesListPresenter presenter){
        this.mockExtendedPagedTable = extendedPagedTable;
        this.filterPagedTable = filterPagedTable;
        this.dataSetEditorManager =dataSetEditorManager;
        this.presenter=presenter;
    }

    public ExtendedPagedTable createGridInstance( final GridGlobalPreferences preferences,
            final String key ) {
        return mockExtendedPagedTable;
    }

    public void applyFilterOnPresenter( String key ){
    }

}
