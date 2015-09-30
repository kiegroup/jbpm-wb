package org.jbpm.console.ng.pr.client.editors.instance.list.basic;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.pr.client.editors.instance.list.BaseProcessInstanceListViewImpl;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;

import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class BasicProcessInstanceListViewImpl extends BaseProcessInstanceListViewImpl
        implements BasicProcessInstanceListPresenter.BasicProcessInstanceListView {

    @Override
    protected void controlBulkOperations() {
        if ( selectedProcessInstances != null && selectedProcessInstances.size() > 0 ) {
            bulkAbortNavLink.setEnabled( false );
        } else {
            bulkAbortNavLink.setEnabled( true );
        }
    }

    @Override
    protected void initSpecificCells(List<HasCell<ProcessInstanceSummary, ?>> cells) {
        
    }

    @Override
    protected void initSpecificBulkActionsDropDown( final ExtendedPagedTable extendedPagedTable ) {
        
    }
}