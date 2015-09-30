package org.jbpm.console.ng.pr.client.editors.instance.list.dash.basic;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.pr.client.editors.instance.list.dash.BaseDataSetProcessInstanceListViewImpl;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;

import com.google.gwt.cell.client.HasCell;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class BasicDataSetProcessInstanceListViewImpl extends BaseDataSetProcessInstanceListViewImpl
        implements BasicDataSetProcessInstanceListPresenter.BasicDataSetProcessInstanceListView {

    interface BasicDataSetProcessInstanceListViewBinder extends
            UiBinder<Widget, BasicDataSetProcessInstanceListViewImpl> {
    }

    @Override
    protected void controlBulkOperations() {
        if ( selectedProcessInstances != null && selectedProcessInstances.size() > 0 ) {
            bulkAbortNavLink.setEnabled( false );
        } else {
            bulkAbortNavLink.setEnabled( true );
        }
    }

    @Override
    protected void initSpecificCells( final List<HasCell<ProcessInstanceSummary, ?>> cells ) {

    }

    @Override
    protected void initSpecificBulkActionsDropDown( final ExtendedPagedTable extendedPagedTable ) {

    }
}