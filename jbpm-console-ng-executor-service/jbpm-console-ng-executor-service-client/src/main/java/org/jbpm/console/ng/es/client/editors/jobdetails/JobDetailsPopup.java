/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.es.client.editors.jobdetails;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.gwtbootstrap3.client.shared.event.ModalShownEvent;
import org.gwtbootstrap3.client.shared.event.ModalShownHandler;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.es.client.i18n.Constants;
import org.jbpm.console.ng.es.client.util.ResizableHeader;
import org.jbpm.console.ng.es.model.ErrorSummary;
import org.jbpm.console.ng.es.model.RequestDetails;
import org.jbpm.console.ng.es.model.RequestParameterSummary;
import org.jbpm.console.ng.es.model.RequestSummary;
import org.jbpm.console.ng.es.service.ExecutorServiceEntryPoint;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;

@Dependent
public class JobDetailsPopup extends BaseModal {

    interface Binder
            extends
            UiBinder<Widget, JobDetailsPopup> {

    }

    final Constants constants = Constants.INSTANCE;

    @UiField
    public Label jobRetries;

    @UiField
    public DataGrid<RequestParameterSummary> executionParametersGrid;

    @UiField
    public VerticalPanel errorsOccurredList;

    @UiField
    public FormGroup errorControlGroup;

    @Inject
    private Caller<ExecutorServiceEntryPoint> executorServices;

    private ListDataProvider<RequestParameterSummary> dataProvider = new ListDataProvider<RequestParameterSummary>();

    private static Binder uiBinder = GWT.create( Binder.class );

    public JobDetailsPopup() {
        setTitle( Constants.INSTANCE.Job_Request_Details() );

        setBody( uiBinder.createAndBindUi( this ) );
        init();
        final GenericModalFooter footer = new GenericModalFooter();
        footer.addButton( Constants.INSTANCE.Ok(),
                          new Command() {
                              @Override
                              public void execute() {
                                  closePopup();
                              }
                          }, null,
                          ButtonType.PRIMARY );

        add( footer );
    }

    public void show( String jobId ) {
        cleanForm( jobId );
        super.show();
    }

    public void init() {
        Column<RequestParameterSummary, String> paramKeyColumn = new Column<RequestParameterSummary, String>( new TextCell() ) {
            @Override
            public String getValue( RequestParameterSummary rowObject ) {
                return rowObject.getKey();
            }
        };
        executionParametersGrid.setHeight( "200px" );

        // Set the message to display when the table is empty.
        executionParametersGrid.setEmptyTableWidget( new com.google.gwt.user.client.ui.Label( constants.No_Parameters_added_yet() ) );
        executionParametersGrid.addColumn( paramKeyColumn, new ResizableHeader<RequestParameterSummary>( constants.Key(),
                                                                                                         executionParametersGrid, paramKeyColumn ) );

        Column<RequestParameterSummary, String> paramValueColumn = new Column<RequestParameterSummary, String>( new TextCell() ) {
            @Override
            public String getValue( RequestParameterSummary rowObject ) {
                return rowObject.getValue();
            }
        };
        executionParametersGrid.addColumn( paramValueColumn, new ResizableHeader<RequestParameterSummary>( constants.Value(),
                                                                                                           executionParametersGrid, paramValueColumn ) );

        this.dataProvider.addDataDisplay( executionParametersGrid );
    }

    public void cleanForm( String requestId ) {
        this.addShownHandler( new ModalShownHandler() {
            @Override
            public void onShown( ModalShownEvent shownEvent ) {
                refreshTable();
            }
        } );
        this.executorServices.call( new RemoteCallback<RequestDetails>() {
            @Override
            public void callback( RequestDetails response ) {
                setRequest( response.getRequest(), response.getErrors(), response.getParams() );
            }
        } ).getRequestDetails( Long.valueOf( requestId ) );
    }

    public void closePopup() {
        hide();
        super.hide();
    }

    public void setRequest( RequestSummary r,
                            List<ErrorSummary> errors,
                            List<RequestParameterSummary> params ) {
        this.jobRetries.setText( String.valueOf( r.getExecutions() ) );
        if ( ( errors != null ) && errors.size() > 0 ) {
            errorsOccurredList.clear();
            errorControlGroup.setVisible( true );
            for ( ErrorSummary error : errors ) {
                String html = "<strong>" + error.getMessage() + "</strong><br/>" + error.getStacktrace();
                this.errorsOccurredList.add( new HTML( SafeHtmlUtils.fromTrustedString( html ) ) );
            }
        } else {
            errorControlGroup.setVisible( false );
        }
        if ( params != null ) {
            this.dataProvider.getList().clear();
            this.dataProvider.getList().addAll( params );
            this.dataProvider.refresh();
        }
    }

    public void refreshTable() {
        executionParametersGrid.redraw();
    }

}
