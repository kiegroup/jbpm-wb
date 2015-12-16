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

package org.jbpm.console.ng.es.client.editors.requestlist;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonGroup;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jbpm.console.ng.es.client.i18n.Constants;
import org.jbpm.console.ng.es.model.RequestSummary;
import org.jbpm.console.ng.es.model.events.RequestChangedEvent;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.SplitDropdownButton;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;


@Dependent
public class RequestListViewImpl extends AbstractListView<RequestSummary,RequestListPresenter> implements RequestListPresenter.RequestListView{

    private Constants constants = GWT.create( Constants.class );

    private Label filterLabel;
    
    private ButtonGroup filtersButtonGroup;

    private Button showAllFilterButton;

    private Button showQueuedFilterButton;

    private Button showRunningFilterButton;

    private Button showRetryingFilterButton;
    
    private Button showErrorFilterButton;
    
    private Button showCompletedFilterButton;
    
    private Button showCancelledFilterButton;
    
    @Inject
    private Event<NotificationEvent> notification;

    private List<RequestSummary> selectedRequestSummary = new ArrayList<RequestSummary>();

    @Override
    public void init(final RequestListPresenter presenter ) {
        List<String> bannedColumns = new ArrayList<String>();
        bannedColumns.add("r.id");
        bannedColumns.add("r.commandName");
        List<String> initColumns = new ArrayList<String>();
        initColumns.add("r.id");
        initColumns.add("r.commandName");
        initColumns.add("Actions");
        
        super.init(presenter, new GridGlobalPreferences("RequestListGrid", initColumns, bannedColumns));

        initFiltersBar();
        this.initActionsDropDown();
        listGrid.setEmptyTableCaption(constants.No_Jobs_Found());
        initSelectionModel();
        initNoActionColumnManager();
        
        listGrid.setSelectionModel(selectionModel, noActionColumnManager);
        listGrid.setRowStyles(selectedStyles);

    }

    public void requestCreated( @Observes RequestChangedEvent event ) {
        presenter.refreshRequests(null);
    }

    
    @Override
    public void initColumns() {
        
        initJobIdColumn();
        initJobTypeColumn();
        initStatusColumn();
        initDueDateColumn();
        actionsColumn = initActionsColumn();
        actionsColumn.setDataStoreName("Actions");
        listGrid.addColumn(actionsColumn, constants.Actions());
    }
    private void initFiltersBar(){
        HorizontalPanel filtersBar = new HorizontalPanel();
        filterLabel = new Label();
        filterLabel.setStyleName("");
        filterLabel.setText(constants.Showing());
        
        showAllFilterButton = new Button();
        showAllFilterButton.setIcon(IconType.FILTER);
        showAllFilterButton.setSize(ButtonSize.SMALL);
        showAllFilterButton.setText(constants.All());
        showAllFilterButton.setEnabled(false);
        showAllFilterButton.addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
              showAllFilterButton.setEnabled(false);
              showQueuedFilterButton.setEnabled(true);
              showRunningFilterButton.setEnabled(true);
              showRetryingFilterButton.setEnabled(true);
              showErrorFilterButton.setEnabled(true);
              showCompletedFilterButton.setEnabled(true);
              showCancelledFilterButton.setEnabled(true);
            presenter.refreshRequests( null );
          }
        });
    
        showQueuedFilterButton = new Button();
        showQueuedFilterButton.setIcon(IconType.FILTER);
        showQueuedFilterButton.setSize(ButtonSize.SMALL);
        showQueuedFilterButton.setText(constants.Queued());
        showQueuedFilterButton.setEnabled(true);
        showQueuedFilterButton.addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
              showAllFilterButton.setEnabled(true);
              showQueuedFilterButton.setEnabled(false);
              showRunningFilterButton.setEnabled(true);
              showRetryingFilterButton.setEnabled(true);
              showErrorFilterButton.setEnabled(true);
              showCompletedFilterButton.setEnabled(true);
              showCancelledFilterButton.setEnabled(true);
              List<String> statuses = new ArrayList<String>();
              statuses.add( "QUEUED" );
              presenter.refreshRequests( statuses );
          }
        });
        
        showRunningFilterButton = new Button();
        showRunningFilterButton.setIcon(IconType.FILTER);
        showRunningFilterButton.setSize(ButtonSize.SMALL);
        showRunningFilterButton.setText(constants.Running());
        showRunningFilterButton.setEnabled(true);
        showRunningFilterButton.addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
              showAllFilterButton.setEnabled(true);
              showQueuedFilterButton.setEnabled(true);
              showRunningFilterButton.setEnabled(false);
              showRetryingFilterButton.setEnabled(true);
              showErrorFilterButton.setEnabled(true);
              showCompletedFilterButton.setEnabled(true);
              showCancelledFilterButton.setEnabled(true);
              List<String> statuses = new ArrayList<String>();
              statuses.add( "RUNNING" );
              presenter.refreshRequests( statuses );
          }
        });
        
        showRetryingFilterButton = new Button();
        showRetryingFilterButton.setIcon(IconType.FILTER);
        showRetryingFilterButton.setSize(ButtonSize.SMALL);
        showRetryingFilterButton.setText(constants.Retrying());
        showRetryingFilterButton.setEnabled(true);
        showRetryingFilterButton.addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
              showAllFilterButton.setEnabled(true);
              showQueuedFilterButton.setEnabled(true);
              showRunningFilterButton.setEnabled(true);
              showRetryingFilterButton.setEnabled(false);
              showErrorFilterButton.setEnabled(true);
              showCompletedFilterButton.setEnabled(true);
              showCancelledFilterButton.setEnabled(true);
              List<String> statuses = new ArrayList<String>();
              statuses.add( "RETRYING" );
              presenter.refreshRequests( statuses );
          }
        });
        
        showErrorFilterButton = new Button();
        showErrorFilterButton.setIcon(IconType.FILTER);
        showErrorFilterButton.setSize(ButtonSize.SMALL);
        showErrorFilterButton.setText(constants.Error());
        showErrorFilterButton.setEnabled(true);
        showErrorFilterButton.addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
              showAllFilterButton.setEnabled(true);
              showQueuedFilterButton.setEnabled(true);
              showRunningFilterButton.setEnabled(true);
              showRetryingFilterButton.setEnabled(true);
              showErrorFilterButton.setEnabled(false);
              showCompletedFilterButton.setEnabled(true);
              showCancelledFilterButton.setEnabled(true);
              List<String> statuses = new ArrayList<String>();
              statuses.add( "ERROR" );
              presenter.refreshRequests( statuses );
          }
        });
        
        showCompletedFilterButton = new Button();
        showCompletedFilterButton.setIcon(IconType.FILTER);
        showCompletedFilterButton.setSize(ButtonSize.SMALL);
        showCompletedFilterButton.setText(constants.Completed());
        showCompletedFilterButton.setEnabled(true);
        showCompletedFilterButton.addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
              showAllFilterButton.setEnabled(true);
              showQueuedFilterButton.setEnabled(true);
              showRunningFilterButton.setEnabled(true);
              showRetryingFilterButton.setEnabled(true);
              showErrorFilterButton.setEnabled(true);
              showCompletedFilterButton.setEnabled(false);
              showCancelledFilterButton.setEnabled(true);
              List<String> statuses = new ArrayList<String>();
              statuses.add( "DONE" );
              presenter.refreshRequests( statuses );
          }
        });
        
        showCancelledFilterButton = new Button();
        showCancelledFilterButton.setIcon(IconType.FILTER);
        showCancelledFilterButton.setSize(ButtonSize.SMALL);
        showCancelledFilterButton.setText(constants.Cancelled());
        showCancelledFilterButton.setEnabled(true);
        showCancelledFilterButton.addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
              showAllFilterButton.setEnabled(true);
              showQueuedFilterButton.setEnabled(true);
              showRunningFilterButton.setEnabled(true);
              showRetryingFilterButton.setEnabled(true);
              showErrorFilterButton.setEnabled(true);
              showCompletedFilterButton.setEnabled(true);
              showCancelledFilterButton.setEnabled(false);
              List<String> statuses = new ArrayList<String>();
              statuses.add( "CANCELLED" );
              presenter.refreshRequests( statuses );
          }
        });
        
        filtersBar.add(filterLabel);
        filtersButtonGroup = new ButtonGroup(showAllFilterButton, showQueuedFilterButton,
                                             showRunningFilterButton, showRetryingFilterButton ,showErrorFilterButton,showCompletedFilterButton,showCancelledFilterButton);

        filtersBar.add(filtersButtonGroup);
        listGrid.getCenterToolbar().add(filtersBar);
    }
    
    private void initSelectionModel(){
        selectionModel = new NoSelectionModel<RequestSummary>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
          @Override
          public void onSelectionChange(SelectionChangeEvent event) {

            boolean close = false;
            if (selectedRow == -1) {
              listGrid.setRowStyles(selectedStyles);
              selectedRow = listGrid.getKeyboardSelectedRow();
              listGrid.redraw();

            } else if (listGrid.getKeyboardSelectedRow() != selectedRow) {
              listGrid.setRowStyles(selectedStyles);
              selectedRow = listGrid.getKeyboardSelectedRow();
              listGrid.redraw();
            } else {
              close = true;
            }
          }
        });
    }

    private void initNoActionColumnManager(){
        noActionColumnManager = DefaultSelectionEventManager
                .createCustomManager(new DefaultSelectionEventManager.EventTranslator<RequestSummary>() {

                  @Override
                  public boolean clearCurrentSelection(CellPreviewEvent<RequestSummary> event) {
                    return false;
                  }

                  @Override
                  public DefaultSelectionEventManager.SelectAction translateSelectionEvent(CellPreviewEvent<RequestSummary> event) {
                    NativeEvent nativeEvent = event.getNativeEvent();
                    if (BrowserEvents.CLICK.equals(nativeEvent.getType())) {
                      // Ignore if the event didn't occur in the correct column.
                      if (listGrid.getColumnIndex(actionsColumn) == event.getColumn()) {
                        return DefaultSelectionEventManager.SelectAction.IGNORE;
                      }
                      //Extension for checkboxes
                      Element target = nativeEvent.getEventTarget().cast();
                      if ("input".equals(target.getTagName().toLowerCase())) {
                        final InputElement input = target.cast();
                        if ("checkbox".equals(input.getType().toLowerCase())) {
                          // Synchronize the checkbox with the current selection state.
                          if (!selectedRequestSummary.contains(event.getValue())) {
                              selectedRequestSummary.add(event.getValue());
                            input.setChecked(true);
                          } else {
                              selectedRequestSummary.remove(event.getValue());
                            input.setChecked(false);
                          }
                          return DefaultSelectionEventManager.SelectAction.IGNORE;
                        }
                      }
                    }

                    return DefaultSelectionEventManager.SelectAction.DEFAULT;
                  }

                });

    }
    
    private void initActionsDropDown() {
        SplitDropdownButton actions = new SplitDropdownButton();
        actions.setText(constants.Actions());
        NavLink newJobNavLink = new NavLink(constants.New_Job());
        newJobNavLink.setIcon(IconType.PLUS_SIGN);
        newJobNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
            	placeManager.goTo( new DefaultPlaceRequest( "Quick New Job" ) );
            }
        });

        NavLink settingsNavLink = new NavLink(constants.Settings());
        settingsNavLink.setIcon(IconType.COG);
        settingsNavLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
            	placeManager.goTo( new DefaultPlaceRequest( "Job Service Settings" ) );
            }
        });

        actions.add(newJobNavLink);
        actions.add(settingsNavLink);
        listGrid.getLeftToolbar().add(actions);
    }



    private void initJobIdColumn(){
        // Id
        Column<RequestSummary, Number> taskIdColumn = new Column<RequestSummary, Number>( new NumberCell() ) {
            @Override
            public Number getValue( RequestSummary object ) {
                return object.getJobId();
            }
        };
        taskIdColumn.setSortable( true );
        listGrid.addColumn(taskIdColumn, constants.Id());
        taskIdColumn.setDataStoreName( "r.id" );
    }
    
    private void initJobTypeColumn(){
        // Name
        Column<RequestSummary, String> jobTypeColumn = new Column<RequestSummary, String>( new TextCell() ) {
            @Override
            public String getValue( RequestSummary object ) {
                return object.getCommandName();
            }
        };
        jobTypeColumn.setSortable( true );
        listGrid.addColumn( jobTypeColumn, constants.Type());
        jobTypeColumn.setDataStoreName( "r.commandName" );
    }
    
    private void initStatusColumn(){
        // Status
        Column<RequestSummary, String> jobStatusColumn = new Column<RequestSummary, String>( new TextCell() ) {
            @Override
            public String getValue( RequestSummary object ) {
                return object.getStatus();
            }
        };
        jobStatusColumn.setSortable( true );
        listGrid.addColumn(jobStatusColumn, constants.Status());
        jobStatusColumn.setDataStoreName( "r.status" );
    }
    
    private void initDueDateColumn(){
        // Time
        Column<RequestSummary, String> taskNameColumn = new Column<RequestSummary, String>( new TextCell() ) {
            @Override
            public String getValue( RequestSummary object ) {
                return object.getTime().toString();
            }
        };
        taskNameColumn.setSortable( true );
        listGrid.addColumn(taskNameColumn, constants.Due_On());
        taskNameColumn.setDataStoreName( "r.time" );
    }

    private Column<RequestSummary, RequestSummary> initActionsColumn(){
        List<HasCell<RequestSummary, ?>> cells = new LinkedList<HasCell<RequestSummary, ?>>();
        List<String> allStatuses = new ArrayList<String>();
        allStatuses.add("QUEUED");
        allStatuses.add("DONE");
        allStatuses.add("CANCELLED");
        allStatuses.add("ERROR");
        allStatuses.add("RETRYING");
        allStatuses.add("RUNNING");
        cells.add( new ActionHasCell( "Details", allStatuses, new Delegate<RequestSummary>() {
            @Override
            public void execute( RequestSummary job ) {
                DefaultPlaceRequest request = new DefaultPlaceRequest( "Job Request Details" );
                request.addParameter( "requestId", String.valueOf( job.getJobId() ) );
                placeManager.goTo( request );
            }
        } ) );

        List<String> activeStatuses = new ArrayList<String>();
        activeStatuses.add("QUEUED");
        activeStatuses.add("RETRYING");
        activeStatuses.add("RUNNING");
        cells.add( new ActionHasCell( "Cancel", activeStatuses, new Delegate<RequestSummary>() {
            @Override
            public void execute( RequestSummary job ) {
                if ( Window.confirm( "Are you sure you want to cancel this Job?" ) ) {
                    presenter.cancelRequest( job.getJobId() );
                }
            }
        } ) );

        List<String> requeueStatuses = new ArrayList<String>();
        requeueStatuses.add("ERROR");
        requeueStatuses.add("RUNNING");
        cells.add( new ActionHasCell( "Requeue", requeueStatuses, new Delegate<RequestSummary>() {
            @Override
            public void execute( RequestSummary job ) {
                if ( Window.confirm( "Are you sure you want to requeue this Job?" ) ) {
                    presenter.requeueRequest(job.getJobId());
                }
            }
        } ) );

        CompositeCell<RequestSummary> cell = new CompositeCell<RequestSummary>( cells );
        Column<RequestSummary, RequestSummary> actionsColumn = new Column<RequestSummary, RequestSummary>( cell ) {
            @Override
            public RequestSummary getValue( RequestSummary object ) {
                return object;
            }
        };
        return actionsColumn;
    }

//    @Override
//    public void displayNotification( String text ) {
//        notification.fire( new NotificationEvent( text ) );
//    }

    private class ActionHasCell implements HasCell<RequestSummary, RequestSummary> {

        private final List<String> avaliableStatuses;
        private ActionCell<RequestSummary> cell;

        public ActionHasCell( String text, List<String> avaliableStatusesList,
                              Delegate<RequestSummary> delegate ) {
            this.avaliableStatuses = avaliableStatusesList;
            cell = new ActionCell<RequestSummary>( text, delegate ){
                @Override
                public void render(Context context, RequestSummary value, SafeHtmlBuilder sb) {
                    if (avaliableStatuses.contains(value.getStatus())){
                        super.render(context, value, sb);
                    }
                }
            };
        }

        @Override
        public Cell<RequestSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<RequestSummary, RequestSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public RequestSummary getValue( RequestSummary object ) {
            return object;
        }
    }

}
