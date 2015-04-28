package org.jbpm.console.ng.pr.client.editors.instance.list.advance;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.pr.client.editors.instance.list.BaseProcessInstanceListViewImpl;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.SplitDropdownButton;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class AdvancedProcessInstanceListViewImpl extends BaseProcessInstanceListViewImpl
        implements AdvancedProcessInstanceListPresenter.AdvancedProcessInstanceListView {

    interface AdvancedProcessInstanceListViewBinder extends UiBinder<Widget, AdvancedProcessInstanceListViewImpl> {
    }

    private static AdvancedProcessInstanceListViewBinder uiBinder = GWT.create( AdvancedProcessInstanceListViewBinder.class );

    private NavLink bulkSignalNavLink;

    protected void controlBulkOperations() {
        if ( selectedProcessInstances != null && selectedProcessInstances.size() > 0 ) {
            bulkAbortNavLink.setDisabled( false );
            bulkSignalNavLink.setDisabled( false );
        } else {
            bulkAbortNavLink.setDisabled( true );
            bulkSignalNavLink.setDisabled( true );
        }
    }

    private class SignalActionHasCell implements HasCell<ProcessInstanceSummary, ProcessInstanceSummary> {

        private ActionCell<ProcessInstanceSummary> cell;

        public SignalActionHasCell(String text,
                Delegate<ProcessInstanceSummary> delegate) {
            cell = new ActionCell<ProcessInstanceSummary>( text, delegate ) {

                @Override
                public void render( Cell.Context context,
                        ProcessInstanceSummary value,
                        SafeHtmlBuilder sb ) {
                    if ( value.getState() == ProcessInstance.STATE_ACTIVE ) {
                        AbstractImagePrototype imageProto = AbstractImagePrototype.create( images.signalGridIcon() );
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant( "<span title='" + constants.Signal() + "' style='margin-right:5px;'>" );
                        mysb.append( imageProto.getSafeHtml() );
                        mysb.appendHtmlConstant( "</span>" );
                        sb.append( mysb.toSafeHtml() );
                    }
                }
            };
        }

        @Override
        public Cell<ProcessInstanceSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<ProcessInstanceSummary, ProcessInstanceSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public ProcessInstanceSummary getValue( ProcessInstanceSummary object ) {
            return object;
        }
    }

    @Override
    protected void initSpecificCells( List<HasCell<ProcessInstanceSummary, ?>> cells ) {
        cells.add( new SignalActionHasCell( constants.Signal(), new Delegate<ProcessInstanceSummary>() {

            @Override
            public void execute( ProcessInstanceSummary processInstance ) {

                PlaceRequest placeRequestImpl = new DefaultPlaceRequest( "Signal Process Popup" );
                placeRequestImpl.addParameter( "processInstanceId", Long.toString( processInstance.getProcessInstanceId() ) );

                placeManager.goTo( placeRequestImpl );
            }
        } ) );
    }

    @Override
    protected void initSpecificBulkActionsDropDown( final ExtendedPagedTable extendedPagedTable, final SplitDropdownButton bulkActions ) {
        bulkSignalNavLink = new NavLink( constants.Bulk_Signal() );
        bulkSignalNavLink.setIcon( IconType.BELL );
        bulkSignalNavLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                ((AdvancedProcessInstanceListPresenter) presenter).bulkSignal( selectedProcessInstances );
                selectedProcessInstances.clear();
                extendedPagedTable.redraw();
            }
        } );

        bulkActions.add( bulkSignalNavLink );
    }
}
