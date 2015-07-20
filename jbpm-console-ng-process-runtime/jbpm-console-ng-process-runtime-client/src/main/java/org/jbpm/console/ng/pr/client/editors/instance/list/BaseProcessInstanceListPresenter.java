package org.jbpm.console.ng.pr.client.editors.instance.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.ga.model.PortableQueryFilter;
import org.jbpm.console.ng.gc.client.list.base.AbstractScreenListPresenter;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.events.NewProcessInstanceEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesUpdateEvent;
import org.jbpm.console.ng.pr.service.ProcessInstanceService;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.paging.PageResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.Range;

public abstract class BaseProcessInstanceListPresenter extends AbstractScreenListPresenter<ProcessInstanceSummary> {

    public static String FILTER_STATE_PARAM_NAME = "states";
    public static String FILTER_PROCESS_DEFINITION_PARAM_NAME = "currentProcessDefinition";
    public static String FILTER_INITIATOR_PARAM_NAME = "initiator";

    public interface BaseProcessInstanceListView extends ListView<ProcessInstanceSummary, BaseProcessInstanceListPresenter> {

    }

    @Inject
    private Caller<ProcessInstanceService> processInstanceService;

    @Inject
    protected Caller<KieSessionEntryPoint> kieSessionServices;

    private String currentProcessDefinition;

    private List<Integer> currentActiveStates;

    private String initiator;

    protected Constants constants = GWT.create( Constants.class );

    public BaseProcessInstanceListPresenter() {
        super();
    }

    @Override
    protected ListView getListView() {
        return getSpecificView();
    }

    @Override
    public void getData( Range visibleRange ) {
        ColumnSortList columnSortList = getSpecificView().getListGrid().getColumnSortList();
        if ( currentFilter == null ) {
            currentFilter = new PortableQueryFilter( visibleRange.getStart(),
                    visibleRange.getLength(),
                    false, "",
                    (columnSortList.size() > 0) ? columnSortList.get( 0 )
                            .getColumn().getDataStoreName() : "",
                    (columnSortList.size() > 0) ? columnSortList.get( 0 )
                            .isAscending() : true );
        }
        // If we are refreshing after a search action, we need to go back to offset 0
        if ( currentFilter.getParams() == null || currentFilter.getParams().isEmpty()
                || currentFilter.getParams().get( "textSearch" ) == null || currentFilter.getParams().get( "textSearch" ).equals( "" ) ) {
            currentFilter.setOffset( visibleRange.getStart() );
            currentFilter.setCount( visibleRange.getLength() );
        } else {
            currentFilter.setOffset( 0 );
            currentFilter.setCount( getSpecificView().getListGrid().getPageSize() );
        }
        //Applying screen specific filters
        if ( currentFilter.getParams() == null ) {
            currentFilter.setParams( new HashMap<String, Object>() );
        }
        if ( initiator != null && initiator.trim().length() > 0 ) {
            currentFilter.getParams().put( FILTER_INITIATOR_PARAM_NAME, initiator );
        } else {
            currentFilter.getParams().remove( FILTER_INITIATOR_PARAM_NAME );
        }
        currentFilter.getParams().put( FILTER_STATE_PARAM_NAME, currentActiveStates );

        currentFilter.getParams().put( FILTER_PROCESS_DEFINITION_PARAM_NAME, currentProcessDefinition );

        currentFilter.setOrderBy( (columnSortList.size() > 0) ? columnSortList.get( 0 )
                .getColumn().getDataStoreName() : "" );
        currentFilter.setIsAscending( (columnSortList.size() > 0) ? columnSortList.get( 0 )
                .isAscending() : true );

        processInstanceService.call( new RemoteCallback<PageResponse<ProcessInstanceSummary>>() {

            @Override
            public void callback( PageResponse<ProcessInstanceSummary> response ) {
                updateDataOnCallback( response );
            }
        }, new ErrorCallback<Message>() {

            @Override
            public boolean error( Message message, Throwable throwable ) {
                getSpecificView().hideBusyIndicator();
                getSpecificView().displayNotification( "Error: Getting Process Definitions: " + message );
                GWT.log( throwable.toString() );
                return true;
            }
        } ).getData( currentFilter );
    }

    public void filterGrid( ArrayList<Integer> states, String currentProcessDefinition, String initiator ) {
        this.currentActiveStates = states;
        this.currentProcessDefinition = currentProcessDefinition;
        this.initiator = initiator;
        refreshGrid();

    }

    public void newInstanceCreated( @Observes NewProcessInstanceEvent pi ) {
        refreshGrid();
    }

    public void newInstanceCreated( @Observes ProcessInstancesUpdateEvent pis ) {
        refreshGrid();
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    @OnFocus
    public void onFocus() {
        refreshGrid();
    }

    public void abortProcessInstance( long processInstanceId ) {
        kieSessionServices.call( new RemoteCallback<Void>() {

            @Override
            public void callback( Void v ) {
                refreshGrid();

            }
        }, new ErrorCallback<Message>() {

            @Override
            public boolean error( Message message, Throwable throwable ) {
                ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).abortProcessInstance( processInstanceId );
    }

    public void abortProcessInstance( List<Long> processInstanceIds ) {
        kieSessionServices.call( new RemoteCallback<Void>() {

            @Override
            public void callback( Void v ) {
                refreshGrid();

            }
        }, new ErrorCallback<Message>() {

            @Override
            public boolean error( Message message, Throwable throwable ) {
                ErrorPopup.showMessage( "Unexpected error encountered : " + throwable.getMessage() );
                return true;
            }
        } ).abortProcessInstances( processInstanceIds );
    }

    public void bulkAbort( List<ProcessInstanceSummary> processInstances ) {
        if ( processInstances != null ) {
            if ( Window.confirm( "Are you sure that you want to abort the selected process instances?" ) ) {
                List<Long> ids = new ArrayList<Long>();
                for ( ProcessInstanceSummary selected : processInstances ) {
                    if ( selected.getState() != ProcessInstance.STATE_ACTIVE ) {
                        getSpecificView().displayNotification( constants.Aborting_Process_Instance_Not_Allowed() + "(id=" + selected.getId()
                                + ")" );
                        continue;
                    }
                    ids.add( selected.getProcessInstanceId() );

                    getSpecificView().displayNotification( constants.Aborting_Process_Instance() + "(id=" + selected.getId() + ")" );
                }
                abortProcessInstance( ids );

            }
        }
    }

    protected abstract BaseProcessInstanceListView getSpecificView();

    public abstract String getTitle();

    public abstract UberView<BaseProcessInstanceListPresenter> getView();
}
