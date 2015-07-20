package org.jbpm.console.ng.pr.client.editors.instance.list.basic;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.pr.client.editors.instance.list.BaseProcessInstanceListViewImpl;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;

import com.github.gwtbootstrap.client.ui.SplitDropdownButton;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class BasicProcessInstanceListViewImpl extends BaseProcessInstanceListViewImpl
        implements BasicProcessInstanceListPresenter.BasicProcessInstanceListView {

    interface BasicProcessInstanceListViewBinder extends
            UiBinder<Widget, BasicProcessInstanceListViewImpl> {
    }

    private static BasicProcessInstanceListViewBinder uiBinder = GWT.create( BasicProcessInstanceListViewBinder.class );

    @Override
    protected void controlBulkOperations() {
        if ( selectedProcessInstances != null && selectedProcessInstances.size() > 0 ) {
            bulkAbortNavLink.setDisabled( false );
        } else {
            bulkAbortNavLink.setDisabled( true );
        }
    }

    @Override
    protected void initSpecificCells(List<HasCell<ProcessInstanceSummary, ?>> cells) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void initSpecificBulkActionsDropDown( final ExtendedPagedTable extendedPagedTable, final SplitDropdownButton bulkActions ) {
        // TODO Auto-generated method stub
        
    }
}
