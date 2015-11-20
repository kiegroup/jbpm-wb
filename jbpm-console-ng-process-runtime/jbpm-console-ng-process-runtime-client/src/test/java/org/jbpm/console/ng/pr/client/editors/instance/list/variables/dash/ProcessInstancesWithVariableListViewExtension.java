package org.jbpm.console.ng.pr.client.editors.instance.list.variables.dash;

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
