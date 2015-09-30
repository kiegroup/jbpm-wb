package org.jbpm.console.ng.pr.client.editors.instance.list.dash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.sort.SortOrder;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.bd.service.KieSessionEntryPoint;
import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.list.base.DataSetQueryHelper;
import org.jbpm.console.ng.gc.client.list.base.AbstractScreenListPresenter;
import org.jbpm.console.ng.gc.client.list.base.RefreshSelectorMenuBuilder;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView.ListView;
import org.jbpm.console.ng.gc.client.list.base.events.SearchEvent;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.forms.client.editors.quicknewinstance.QuickNewProcessInstancePopup;
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
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.paging.PageResponse;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.Range;


public abstract class BaseDataSetProcessInstanceListPresenter extends AbstractScreenListPresenter<ProcessInstanceSummary> {

    public interface BaseDataSetProcessInstanceListView extends ListView<ProcessInstanceSummary, BaseDataSetProcessInstanceListPresenter> {
        public static String PROCESS_INSTANCES_LIST_PREFIX = "DS_ProcessInstancesGrid";
        public static final String PROCESS_INSTANCES_DATASET_ID = "jbpmProcessInstances";

        public static final String COLUMN_PROCESSINSTANCEID = "processInstanceId";
        public static final String COLUMN_PROCESSID = "processId";
        public static final String COLUMN_START = "start_date";
        public static final String COLUMN_END = "end_date";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_PARENTPROCESSINSTANCEID = "parentProcessInstanceId";
        public static final String COLUMN_OUTCOME = "outcome";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_IDENTITY = "user_identity";
        public static final String COLUMN_PROCESSVERSION = "processVersion";
        public static final String COLUMN_PROCESSNAME = "processName";
        public static final String COLUMN_CORRELATIONKEY = "correlationKey";
        public static final String COLUMN_EXTERNALID = "externalId";
        public static final String COLUMN_PROCESSINSTANCEDESCRIPTION = "processInstanceDescription";

        public static final String BASIC_VIEW_MODE = "Basic Process Instance Details Multi";
        public static final String ADVANCED_VIEW_MODE = "Advanced Process Instance Details Multi";
        
        public int getRefreshValue();

        public void restoreTabs();

        public void saveRefreshValue( int newValue );

        public void applyFilterOnPresenter( String key );
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
    
    @Inject
    private QuickNewProcessInstancePopup newProcessInstancePopup;
    
    private RefreshSelectorMenuBuilder refreshSelectorMenuBuilder = new RefreshSelectorMenuBuilder( this );

    public Button menuRefreshButton = new Button();
    public Button menuResetTabsButton = new Button();
    public BaseDataSetProcessInstanceListPresenter() {
     super();
    }

    public void filterGrid(FilterSettings tableSettings) {
      dataSetQueryHelper.setCurrentTableSettings( tableSettings );
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
            dataSetQueryHelper.setLastOrderedColumn( BaseDataSetProcessInstanceListView.COLUMN_START );
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
      
      public Menus getMenus() {
          return MenuFactory
                  .newTopLevelMenu( Constants.INSTANCE.New_Process_Instance() )
                  .respondsWith( new Command() {
                      @Override
                      public void execute() {
                          newProcessInstancePopup.show();
                      }
                  } )
                  .endMenu()
                  .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                      @Override
                      public void push( MenuFactory.CustomMenuBuilder element ) {
                      }

                      @Override
                      public MenuItem build() {
                          return new BaseMenuCustom<IsWidget>() {
                              @Override
                              public IsWidget build() {
                                  menuRefreshButton.addClickHandler( new ClickHandler() {
                                      @Override
                                      public void onClick( ClickEvent clickEvent ) {
                                          refreshGrid();
                                      }
                                  } );
                                  return menuRefreshButton;
                              }

                              @Override
                              public boolean isEnabled() {
                                  return true;
                              }

                              @Override
                              public void setEnabled( boolean enabled ) {

                              }

                              @Override
                              public String getSignatureId() {
                                  return "org.jbpm.console.ng.pr.client.editors.instance.list.ProcessInstanceListPresenter#menuRefreshButton";
                              }

                          };
                      }
                  } ).endMenu()
                  .newTopLevelCustomMenu( refreshSelectorMenuBuilder ).endMenu()
                  .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                      @Override
                      public void push( MenuFactory.CustomMenuBuilder element ) {
                      }

                      @Override
                      public MenuItem build() {
                          return new BaseMenuCustom<IsWidget>() {
                              @Override
                              public IsWidget build() {
                                  menuResetTabsButton.addClickHandler( new ClickHandler() {
                                      @Override
                                      public void onClick( ClickEvent clickEvent ) {
                                          getSpecificView().restoreTabs();
                                      }
                                  } );
                                  return menuResetTabsButton;
                              }

                              @Override
                              public boolean isEnabled() {
                                  return true;
                              }

                              @Override
                              public void setEnabled( boolean enabled ) {

                              }

                              @Override
                              public String getSignatureId() {
                                  return "org.jbpm.console.ng.pr.client.editors.instance.list.ProcessInstanceList#menuResetTabsButton";
                              }

                          };
                      }
                  } ).endMenu()
                  .build();

      }

      @PostConstruct
      public void setupButtons() {
          menuRefreshButton.setIcon( IconType.REFRESH );
          menuRefreshButton.setSize( ButtonSize.SMALL );
          menuRefreshButton.setTitle( Constants.INSTANCE.Refresh() );

          menuResetTabsButton.setIcon( IconType.TH_LIST );
          menuResetTabsButton.setSize( ButtonSize.SMALL );
          menuResetTabsButton.setTitle( Constants.INSTANCE.RestoreDefaultFilters() );
      }
      @Override
      public void onGridPreferencesStoreLoaded() {
          refreshSelectorMenuBuilder.loadOptions( getSpecificView().getRefreshValue() );
      }

      @Override
      protected void updateRefreshInterval( boolean enableAutoRefresh, int newInterval ) {
          super.updateRefreshInterval( enableAutoRefresh, newInterval );
          getSpecificView().saveRefreshValue( newInterval );
      }

      @Override
      protected void onSearchEvent( @Observes SearchEvent searchEvent ) {
          textSearchStr = searchEvent.getFilter();
          if ( textSearchStr != null && textSearchStr.trim().length() > 0 ) {
              Map<String, Object> params = new HashMap<String, Object>();
              params.put( "textSearch", textSearchStr );
              dataSetQueryHelper.getCurrentTableSettings().getKey();

              getSpecificView().applyFilterOnPresenter( dataSetQueryHelper.getCurrentTableSettings().getKey() );
          }
      }
}