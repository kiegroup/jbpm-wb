/*
 * Copyright 2012 JBoss Inc
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
package org.jbpm.console.ng.pr.client.editors.documents.list;

import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.gc.client.list.base.AbstractListPresenter;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView.BasicListView;
import org.jbpm.console.ng.pr.model.DocumentSummary;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceSelectionEvent;
import org.jbpm.console.ng.pr.service.DocumentsService;
import org.uberfire.paging.PageResponse;

@Dependent
public class ProcessDocumentListPresenter extends AbstractListPresenter<DocumentSummary> {

    public interface ProcessDocumentListView extends BasicListView<DocumentSummary> {

        void init( final ProcessDocumentListPresenter presenter );
    }

    @Inject
    private ProcessDocumentListView view;

    @Inject
    private Caller<DocumentsService> documentsServices;

    private String processInstanceId;
    private String processDefId;
    private String deploymentId;
    private String documentId;

    @PostConstruct
    public void init() {
        dataProvider = new AsyncDataProvider<DocumentSummary>() {

            @Override
            protected void onRangeChanged( HasData<DocumentSummary> display ) {
                if ( processInstanceId != null && deploymentId != null ) {
                    final Range visibleRange = display.getVisibleRange();
                    ColumnSortList columnSortList = view.getListGrid().getColumnSortList();
                    if ( currentFilter == null ) {
                        currentFilter = new PortableQueryFilter( visibleRange.getStart(),
                                                                 visibleRange.getLength(),
                                                                 false, "",
                                                                 ( columnSortList.size() > 0 ) ? columnSortList.get( 0 )
                                                                         .getColumn().getDataStoreName() : "",
                                                                 ( columnSortList.size() > 0 ) ? columnSortList.get( 0 )
                                                                         .isAscending() : true );
                    }
                    // If we are refreshing after a search action, we need to go back to offset 0
                    if ( currentFilter.getParams() == null || currentFilter.getParams().isEmpty()
                            || currentFilter.getParams().get( "textSearch" ) == null || currentFilter.getParams().get( "textSearch" ).equals( "" ) ) {
                        currentFilter.setOffset( visibleRange.getStart() );
                        currentFilter.setCount( visibleRange.getLength() );
                    } else {
                        currentFilter.setOffset( 0 );
                        currentFilter.setCount( view.getListGrid().getPageSize() );
                    }
                    //Applying screen specific filters
                    if ( currentFilter.getParams() == null ) {
                        currentFilter.setParams( new HashMap<String, Object>() );
                    }
                    currentFilter.getParams().put( "processInstanceId", processInstanceId );
                    currentFilter.getParams().put( "processDefId", processDefId );
                    currentFilter.getParams().put( "deploymentId", deploymentId );

                    currentFilter.setOrderBy( ( columnSortList.size() > 0 ) ? columnSortList.get( 0 )
                            .getColumn().getDataStoreName() : "" );
                    currentFilter.setAscending( ( columnSortList.size() > 0 ) ? columnSortList.get( 0 )
                            .isAscending() : true );

                    documentsServices.call( new RemoteCallback<PageResponse<DocumentSummary>>() {
                        @Override
                        public void callback( PageResponse<DocumentSummary> response ) {
                            dataProvider.updateRowCount( response.getTotalRowSize(),
                                                         response.isTotalRowSizeExact() );
                            dataProvider.updateRowData( response.getStartRowIndex(),
                                                        response.getPageRowList() );
                        }
                    }, new ErrorCallback<Message>() {
                        @Override
                        public boolean error( Message message,
                                              Throwable throwable ) {
                            view.hideBusyIndicator();
                            view.displayNotification( "Error: Getting Process Definitions: " + message );
                            GWT.log( throwable.toString() );
                            return true;
                        }
                    } ).getData( currentFilter );
                }

            }
        };
        view.init( this );
    }

    public IsWidget getWidget() {
        return view;
    }

    public void onProcessInstanceSelectionEvent( @Observes final ProcessInstanceSelectionEvent event ) {
        this.processInstanceId = String.valueOf( event.getProcessInstanceId() );
        this.processDefId = event.getProcessDefId();
        this.deploymentId = event.getDeploymentId();
        refreshGrid();
    }

}
