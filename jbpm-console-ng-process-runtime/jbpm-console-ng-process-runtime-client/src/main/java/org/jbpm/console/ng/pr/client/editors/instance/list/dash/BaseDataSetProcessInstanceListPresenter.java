package org.jbpm.console.ng.pr.client.editors.instance.list.dash;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.list.base.DataSetQueryHelper;
import org.jbpm.console.ng.gc.client.list.base.AbstractScreenListPresenter;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.events.NewProcessInstanceEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesUpdateEvent;
import org.jbpm.console.ng.pr.service.ProcessInstanceService;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.paging.PageResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.Range;


public abstract class BaseDataSetProcessInstanceListPresenter extends AbstractScreenListPresenter<ProcessInstanceSummary> {

    public interface BaseDataSetProcessInstanceListView extends ListView<ProcessInstanceSummary, BaseDataSetProcessInstanceListPresenter> {

    }

    @Inject
    private Caller<ProcessInstanceService> processInstanceService;

    @Inject
    protected Caller<KieSessionEntryPoint> kieSessionServices;

    @Inject
    DataSetQueryHelper dataSetQueryHelper;

    @Inject
    private ErrorPopupPresenter errorPopup;

    protected Constants constants = GWT.create(Constants.class);

    public BaseDataSetProcessInstanceListPresenter() {
     super();
    }

    public void filterGrid(FilterSettings tableSettings) {
      dataSetQueryHelper.setCurrentTableSetting( tableSettings);
      refreshGrid();
    }

    @Override
    protected ListView getListView() {
      return getSpecificView();
    }

    @Override
    public void getData(final Range visibleRange) {
      try {
        FilterSettings currentTableSettings = dataSetQueryHelper.getCurrentTableSettings();
        if(currentTableSettings!=null) {
          currentTableSettings.setTablePageSize( getSpecificView().getListGrid().getPageSize() );
          ColumnSortList columnSortList = getSpecificView().getListGrid().getColumnSortList();
          //GWT.log( "processInstances getData "+columnSortList.size() +"currentTableSettings table name "+ currentTableSettings.getTableName() );
          if(columnSortList!=null &&  columnSortList.size()>0) {
            dataSetQueryHelper.setLastOrderedColumn( ( columnSortList.size() > 0 ) ? columnSortList.get( 0 ).getColumn().getDataStoreName() : "" );
            dataSetQueryHelper.setLastSortOrder( ( columnSortList.size() > 0 ) && columnSortList.get( 0 ).isAscending() ? SortOrder.ASCENDING : SortOrder.DESCENDING );
          }else {
            dataSetQueryHelper.setLastOrderedColumn( BaseDataSetProcessInstanceListViewImpl.COLUMN_START );
            dataSetQueryHelper.setLastSortOrder( SortOrder.ASCENDING );
          }
          dataSetQueryHelper.setDataSetHandler(   currentTableSettings );
          dataSetQueryHelper.lookupDataSet( visibleRange.getStart(), new DataSetReadyCallback() {
            @Override
            public void callback( DataSet dataSet ) {
              if ( dataSet != null) {
                List<ProcessInstanceSummary> myProcessInstancesFromDataSet = new ArrayList<ProcessInstanceSummary>();

                for ( int i = 0; i < dataSet.getRowCount(); i++ ) {
                  myProcessInstancesFromDataSet.add( new ProcessInstanceSummary(
                                  dataSetQueryHelper.getColumnLongValue( dataSet, BaseDataSetProcessInstanceListViewImpl.COLUMN_PROCESSINSTANCEID, i ),
                                  dataSetQueryHelper.getColumnStringValue( dataSet, BaseDataSetProcessInstanceListViewImpl.COLUMN_PROCESSID, i ),
                                  dataSetQueryHelper.getColumnStringValue( dataSet, BaseDataSetProcessInstanceListViewImpl.COLUMN_EXTERNALID, i ),
                                  dataSetQueryHelper.getColumnStringValue( dataSet, BaseDataSetProcessInstanceListViewImpl.COLUMN_PROCESSNAME, i ),
                                  dataSetQueryHelper.getColumnStringValue( dataSet, BaseDataSetProcessInstanceListViewImpl.COLUMN_PROCESSVERSION, i ),
                                  dataSetQueryHelper.getColumnIntValue( dataSet, BaseDataSetProcessInstanceListViewImpl.COLUMN_STATUS, i ),
                                  dataSetQueryHelper.getColumnDateValue( dataSet, BaseDataSetProcessInstanceListViewImpl.COLUMN_START, i ),
                                  dataSetQueryHelper.getColumnStringValue( dataSet, BaseDataSetProcessInstanceListViewImpl.COLUMN_IDENTITY, i ),
                                  dataSetQueryHelper.getColumnStringValue( dataSet, BaseDataSetProcessInstanceListViewImpl.COLUMN_PROCESSINSTANCEDESCRIPTION, i ),
                                  dataSetQueryHelper.getColumnStringValue( dataSet, BaseDataSetProcessInstanceListViewImpl.COLUMN_CORRELATIONKEY, i ),
                                  dataSetQueryHelper.getColumnLongValue( dataSet, BaseDataSetProcessInstanceListViewImpl.COLUMN_PARENTPROCESSINSTANCEID, i )));


                }
                PageResponse<ProcessInstanceSummary> processInstanceSummaryPageResponse = new PageResponse<ProcessInstanceSummary>();
                processInstanceSummaryPageResponse.setPageRowList( myProcessInstancesFromDataSet );
                processInstanceSummaryPageResponse.setStartRowIndex( visibleRange.getStart() );
                processInstanceSummaryPageResponse.setTotalRowSize( dataSet.getRowCountNonTrimmed() );
                processInstanceSummaryPageResponse.setTotalRowSizeExact( true );
                if ( visibleRange.getStart() + dataSet.getRowCount() == dataSet.getRowCountNonTrimmed() ) {
                  processInstanceSummaryPageResponse.setLastPage( true );
                } else {
                  processInstanceSummaryPageResponse.setLastPage( false );
                }
                BaseDataSetProcessInstanceListPresenter.this.updateDataOnCallback( processInstanceSummaryPageResponse );
              }
              getSpecificView().hideBusyIndicator();
            }

            @Override
            public void notFound() {
                getSpecificView().hideBusyIndicator();
              errorPopup.showMessage( "Not found DataSet with UUID [  jbpmProcessInstances ] " );
              GWT.log( "DataSet with UUID [  jbpmProcessInstances ] not found." );
            }

            @Override
            public boolean onError( final ClientRuntimeError error ) {
              getSpecificView().hideBusyIndicator();
              errorPopup.showMessage( "DataSet with UUID [  jbpmProcessInstances ] error: " + error.getThrowable() );
              GWT.log( "DataSet with UUID [  jbpmProcessInstances ] error: ", error.getThrowable() );
              return false;
            }
          } );
        }else {
            getSpecificView().hideBusyIndicator();
        }
      } catch (Exception e) {
        GWT.log("Error looking up dataset with UUID [ jbpmProcessInstances ]");
      }

    }

    public void newInstanceCreated(@Observes NewProcessInstanceEvent pi) {
      refreshGrid();
    }

    public void newInstanceCreated(@Observes ProcessInstancesUpdateEvent pis) {
      refreshGrid();
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
      this.place = place;
    }

    @OnFocus
    public void onFocus() {
      refreshGrid();
    }

    @OnOpen
    public void onOpen() {
      refreshGrid();
    }
    
    public void abortProcessInstance(long processInstanceId) {
        kieSessionServices.call(new RemoteCallback<Void>() {
          @Override
          public void callback(Void v) {
            refreshGrid(  );
          }
        }, new ErrorCallback<Message>() {
          @Override
          public boolean error(Message message, Throwable throwable) {
            ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
            return true;
          }
        }).abortProcessInstance(processInstanceId);
      }

      public void abortProcessInstance(List<Long> processInstanceIds) {
        kieSessionServices.call(new RemoteCallback<Void>() {
          @Override
          public void callback(Void v) {
            refreshGrid();
          }
        }, new ErrorCallback<Message>() {
          @Override
          public boolean error(Message message, Throwable throwable) {
            ErrorPopup.showMessage("Unexpected error encountered : " + throwable.getMessage());
            return true;
          }
        }).abortProcessInstances(processInstanceIds);
      }
      
      public void bulkAbort(List<ProcessInstanceSummary> processInstances) {
          if (processInstances != null) {
            if (Window.confirm("Are you sure that you want to abort the selected process instances?")) {
              List<Long> ids = new ArrayList<Long>();
              for (ProcessInstanceSummary selected : processInstances) {
                if (selected.getState() != ProcessInstance.STATE_ACTIVE) {
                    getSpecificView().displayNotification(constants.Aborting_Process_Instance_Not_Allowed() + "(id=" + selected.getId()
                          + ")");
                  continue;
                }
                ids.add(selected.getProcessInstanceId());

                getSpecificView().displayNotification(constants.Aborting_Process_Instance() + "(id=" + selected.getId() + ")");
              }
              abortProcessInstance(ids);

            }
          }
        }
      
      protected abstract BaseDataSetProcessInstanceListView getSpecificView();

      public abstract String getTitle();

      public abstract UberView<BaseDataSetProcessInstanceListPresenter> getView();
}
