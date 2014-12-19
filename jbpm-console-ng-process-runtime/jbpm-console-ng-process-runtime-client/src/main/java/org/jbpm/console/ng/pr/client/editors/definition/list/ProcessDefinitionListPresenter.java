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
package org.jbpm.console.ng.pr.client.editors.definition.list;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;
import org.jbpm.console.ng.gc.client.list.base.AbstractScreenListPresenter;
import org.jbpm.console.ng.ht.forms.client.display.providers.StartProcessFormDisplayProviderImpl;
import org.jbpm.console.ng.ht.forms.client.display.views.PopupFormDisplayerView;
import org.jbpm.console.ng.ht.forms.display.process.api.ProcessDisplayerConfig;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.ProcessDefinitionKey;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.service.ProcessDefinitionService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.paging.PageResponse;

@Dependent
@WorkbenchScreen(identifier = "Process Definition List")
public class ProcessDefinitionListPresenter extends AbstractScreenListPresenter<ProcessSummary> {

    @Inject
    PopupFormDisplayerView formDisplayPopUp;

    @Inject
    StartProcessFormDisplayProviderImpl startProcessDisplayProvider;

    public interface ProcessDefinitionListView extends ListView<ProcessSummary, ProcessDefinitionListPresenter> {

    }

    @Inject
    private ProcessDefinitionListView view;

    @Inject
    private Caller<ProcessDefinitionService> processDefinitionService;

    private Constants constants = GWT.create( Constants.class );

    public ProcessDefinitionListPresenter() {
        dataProvider = new AsyncDataProvider<ProcessSummary>() {

            @Override
            protected void onRangeChanged( HasData<ProcessSummary> display ) {
                view.showBusyIndicator(constants.Loading());
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

                currentFilter.setOrderBy( ( columnSortList.size() > 0 ) ? columnSortList.get( 0 )
                        .getColumn().getDataStoreName() : "" );
                currentFilter.setIsAscending( ( columnSortList.size() > 0 ) ? columnSortList.get( 0 )
                        .isAscending() : true );

                processDefinitionService.call( new RemoteCallback<PageResponse<ProcessSummary>>() {
                    @Override
                    public void callback( PageResponse<ProcessSummary> response ) {
                        view.hideBusyIndicator();
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
        };
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Process_Definitions();
    }

    @WorkbenchPartView
    public UberView<ProcessDefinitionListPresenter> getView() {
        return view;
    }

    public void openGenericForm( final String processDefId,
                                 final String deploymentId,
                                 final String processDefName ) {

        ProcessDisplayerConfig config = new ProcessDisplayerConfig(new ProcessDefinitionKey(deploymentId, processDefId), processDefName);

        formDisplayPopUp.setTitle(processDefName);

        startProcessDisplayProvider.setup(config, formDisplayPopUp);
    }

}
