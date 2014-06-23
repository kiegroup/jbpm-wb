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
package org.jbpm.console.ng.pr.client.editors.instance.list;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonGroup;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.SplitDropdownButton;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.resources.ProcessRuntimeImages;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceSelectionEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesUpdateEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstancesWithDetailsRequestEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Dependent
@Templated(value = "ProcessInstanceListViewImpl.html")
public class ProcessInstanceListViewImpl extends AbstractListView<ProcessInstanceSummary, ProcessInstanceListPresenter>
        implements ProcessInstanceListPresenter.ProcessInstanceListView {

  private Constants constants = GWT.create(Constants.class);
  private ProcessRuntimeImages images = GWT.create(ProcessRuntimeImages.class);

  private Label filterLabel;

  private ButtonGroup filtersButtonGroup;

  private Button activeFilterButton;

  private Button completedFilterButton;

  private Button abortedFilterButton;

  private Button relatedToMeFilterButton;

  private ProcessInstanceSummary selectedItem;

  private Set<ProcessInstanceSummary> selectedProcessInstances;

  @Inject
  private Event<ProcessInstanceSelectionEvent> processInstanceSelected;

  final MultiSelectionModel<ProcessInstanceSummary> selectionModel = new MultiSelectionModel<ProcessInstanceSummary>();

  @Override
  public void init(final ProcessInstanceListPresenter presenter) {
    super.init(presenter);

    initBulkActionsDropDown();
    initFiltersBar();

    listGrid.setEmptyTableCaption(constants.No_Process_Instances_Found());

    // Add a selection model so we can select cells.
    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
      @Override
      public void onSelectionChange(SelectionChangeEvent event) {

        selectedProcessInstances = selectionModel.getSelectedSet();
        listGrid.paint(listGrid.getKeyboardSelectedRow());

      }
    });

    listGrid.setSelectionModel(selectionModel,
            DefaultSelectionEventManager.<ProcessInstanceSummary>createCheckboxManager());

  }

  @Override
  public void initColumns() {
    initCellPreview();
    initChecksColumn();
    initProcessInstanceIdColumn();
    initProcessNameColumn();
    initInitiatorColumn();
    initProcessVersionColumn();
    initProcessStateColumn();
    initStartDateColumn();
    initActionsColumn();

  }

  private void initCellPreview() {
    listGrid.addCellPreviewHandler(new CellPreviewEvent.Handler<ProcessInstanceSummary>() {

      @Override
      public void onCellPreview(final CellPreviewEvent<ProcessInstanceSummary> event) {

        ProcessInstanceSummary processInstance = null;
        if (BrowserEvents.CLICK.equalsIgnoreCase(event.getNativeEvent().getType())) {
          int column = event.getColumn();
          int columnCount = listGrid.getColumnCount();
          if (column != columnCount - 1) {

            processInstance = event.getValue();


            PlaceStatus status = placeManager.getStatus(new DefaultPlaceRequest("Process Instance Details"));
            if (status == PlaceStatus.CLOSE) {
              placeManager.goTo("Process Instance Details");
              processInstanceSelected.fire(new ProcessInstanceSelectionEvent(processInstance.getDeploymentId(),
                      processInstance.getProcessInstanceId(), processInstance.getProcessId()));
            } else if (status == PlaceStatus.OPEN) {
              processInstanceSelected.fire(new ProcessInstanceSelectionEvent(processInstance.getDeploymentId(),
                      processInstance.getProcessInstanceId(), processInstance.getProcessId()));
            }

          }
        }

        if (BrowserEvents.FOCUS.equalsIgnoreCase(event.getNativeEvent().getType())) {
          listGrid.paint(listGrid.getKeyboardSelectedRow());
        }

      }
    });

  }

  private void initBulkActionsDropDown() {
    SplitDropdownButton bulkActions = new SplitDropdownButton();
    bulkActions.setText(constants.Bulk_Actions());
    NavLink bulkAbortNavLink = new NavLink(constants.Bulk_Abort());
    bulkAbortNavLink.setIcon(IconType.REMOVE_SIGN);
    bulkAbortNavLink.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        presenter.bulkAbort(selectedProcessInstances);
      }
    });

    NavLink bulkSignalNavLink = new NavLink(constants.Bulk_Signal());
    bulkSignalNavLink.setIcon(IconType.BELL);
    bulkSignalNavLink.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        presenter.bulkSignal(selectedProcessInstances);
      }
    });

    bulkActions.add(bulkAbortNavLink);
    bulkActions.add(bulkSignalNavLink);
    listGrid.getToolbar().add(bulkActions);
  }

  private void initFiltersBar() {
    HorizontalPanel filtersBar = new HorizontalPanel();
    filterLabel = new Label();
    filterLabel.setStyleName("");
    filterLabel.setText(constants.Showing());

    activeFilterButton = new Button();
    activeFilterButton.setIcon(IconType.FILTER);
    activeFilterButton.setSize(ButtonSize.SMALL);
    activeFilterButton.setText(constants.Active());
    activeFilterButton.setEnabled(false);
    activeFilterButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        activeFilterButton.setEnabled(false);
        completedFilterButton.setEnabled(true);
        abortedFilterButton.setEnabled(true);
        relatedToMeFilterButton.setEnabled(true);
        presenter.refreshActiveProcessList();
      }
    });

    completedFilterButton = new Button();
    completedFilterButton.setIcon(IconType.FILTER);
    completedFilterButton.setSize(ButtonSize.SMALL);
    completedFilterButton.setText(constants.Completed());
    completedFilterButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        activeFilterButton.setEnabled(true);
        completedFilterButton.setEnabled(false);
        abortedFilterButton.setEnabled(true);
        relatedToMeFilterButton.setEnabled(true);
        presenter.refreshCompletedProcessList();
      }
    });

    abortedFilterButton = new Button();
    abortedFilterButton.setIcon(IconType.FILTER);
    abortedFilterButton.setSize(ButtonSize.SMALL);
    abortedFilterButton.setText(constants.Aborted());
    abortedFilterButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        activeFilterButton.setEnabled(true);
        completedFilterButton.setEnabled(true);
        abortedFilterButton.setEnabled(false);
        relatedToMeFilterButton.setEnabled(true);
        presenter.refreshAbortedProcessList();
      }
    });

    relatedToMeFilterButton = new Button();
    relatedToMeFilterButton.setIcon(IconType.FILTER);
    relatedToMeFilterButton.setSize(ButtonSize.SMALL);
    relatedToMeFilterButton.setText(constants.Related_To_Me());
    relatedToMeFilterButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        activeFilterButton.setEnabled(true);
        completedFilterButton.setEnabled(true);
        abortedFilterButton.setEnabled(true);
        relatedToMeFilterButton.setEnabled(false);
        presenter.refreshRelatedToMeProcessList(identity.getName());
      }
    });

    filtersBar.add(filterLabel);
    filtersButtonGroup = new ButtonGroup(activeFilterButton, completedFilterButton,
            abortedFilterButton, relatedToMeFilterButton);

    filtersBar.add(filtersButtonGroup);
    listGrid.getToolbar().add(filtersBar);
  }

  private void initProcessInstanceIdColumn() {
    // Process Instance Id.
    Column<ProcessInstanceSummary, String> processInstanceIdColumn = new Column<ProcessInstanceSummary, String>(new TextCell()) {
      @Override
      public String getValue(ProcessInstanceSummary object) {
        return String.valueOf(object.getId());
      }
    };
    processInstanceIdColumn.setSortable(true);

    listGrid.addColumn(processInstanceIdColumn, constants.Id());
  }

  private void initProcessNameColumn() {
    // Process Name.
    Column<ProcessInstanceSummary, String> processNameColumn = new Column<ProcessInstanceSummary, String>(new TextCell()) {
      @Override
      public String getValue(ProcessInstanceSummary object) {
        return object.getProcessName();
      }
    };
    processNameColumn.setSortable(true);

    listGrid.addColumn(processNameColumn, constants.Name());
  }

  private void initInitiatorColumn() {
    Column<ProcessInstanceSummary, String> processInitiatorColumn = new Column<ProcessInstanceSummary, String>(
            new TextCell()) {
              @Override
              public String getValue(ProcessInstanceSummary object) {
                return object.getInitiator();
              }
            };
    processInitiatorColumn.setSortable(true);

    listGrid.addColumn(processInitiatorColumn, constants.Initiator());
  }

  private void initProcessVersionColumn() {
    // Process Version.
    Column<ProcessInstanceSummary, String> processVersionColumn = new Column<ProcessInstanceSummary, String>(new TextCell()) {
      @Override
      public String getValue(ProcessInstanceSummary object) {
        return object.getProcessVersion();
      }
    };
    processVersionColumn.setSortable(true);

    listGrid.addColumn(processVersionColumn, constants.Version());

  }

  private void initProcessStateColumn() {
    // Process State
    Column<ProcessInstanceSummary, String> processStateColumn = new Column<ProcessInstanceSummary, String>(new TextCell()) {
      @Override
      public String getValue(ProcessInstanceSummary object) {
        String statusStr = constants.Unknown();
        switch (object.getState()) {
          case ProcessInstance.STATE_ACTIVE:
            statusStr = constants.Active();
            break;
          case ProcessInstance.STATE_ABORTED:
            statusStr = constants.Aborted();
            break;
          case ProcessInstance.STATE_COMPLETED:
            statusStr = constants.Completed();
            break;
          case ProcessInstance.STATE_PENDING:
            statusStr = constants.Pending();
            break;
          case ProcessInstance.STATE_SUSPENDED:
            statusStr = constants.Suspended();
            break;

          default:
            break;
        }

        return statusStr;
      }
    };
    processStateColumn.setSortable(true);

    listGrid.addColumn(processStateColumn, constants.State());

  }

  private void initStartDateColumn() {
    // start time
    Column<ProcessInstanceSummary, String> startTimeColumn = new Column<ProcessInstanceSummary, String>(new TextCell()) {
      @Override
      public String getValue(ProcessInstanceSummary object) {
        Date startTime = object.getStartTime();
        if (startTime != null) {
          DateTimeFormat format = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");
          return format.format(startTime);
        }
        return "";
      }
    };
    startTimeColumn.setSortable(true);

    listGrid.addColumn(startTimeColumn, constants.Start_Date());

  }

  private void initActionsColumn() {
    List<HasCell<ProcessInstanceSummary, ?>> cells = new LinkedList<HasCell<ProcessInstanceSummary, ?>>();

    cells.add(new DetailsActionHasCell("Details", new Delegate<ProcessInstanceSummary>() {
      @Override
      public void execute(ProcessInstanceSummary processInstance) {

        PlaceStatus status = placeManager.getStatus(new DefaultPlaceRequest("Process Instance Details"));

        listGrid.paint(listGrid.getKeyboardSelectedRow());

        if (status == PlaceStatus.CLOSE || selectedItem != processInstance) {
          placeManager.goTo("Process Instance Details");
          processInstanceSelected.fire(new ProcessInstanceSelectionEvent(processInstance.getDeploymentId(),
                  processInstance.getProcessInstanceId(), processInstance.getProcessId()));
        } else if (status == PlaceStatus.OPEN && selectedItem == processInstance) {
          placeManager.closePlace(new DefaultPlaceRequest("Process Instance Details"));
        }
        selectedItem = processInstance;
      }
    }));

    cells.add(new SignalActionHasCell("Singal", new Delegate<ProcessInstanceSummary>() {
      @Override
      public void execute(ProcessInstanceSummary processInstance) {

        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Signal Process Popup");
        placeRequestImpl.addParameter("processInstanceId", Long.toString(processInstance.getProcessInstanceId()));

        placeManager.goTo(placeRequestImpl);
      }
    }));

    cells.add(new AbortActionHasCell("Abort", new Delegate<ProcessInstanceSummary>() {
      @Override
      public void execute(ProcessInstanceSummary processInstance) {
        if (Window.confirm("Are you sure that you want to abort the process instance?")) {
          presenter.abortProcessInstance(processInstance.getProcessInstanceId());
        }
      }
    }));

    CompositeCell<ProcessInstanceSummary> cell = new CompositeCell<ProcessInstanceSummary>(cells);
    Column<ProcessInstanceSummary, ProcessInstanceSummary> actionsColumn = new Column<ProcessInstanceSummary, ProcessInstanceSummary>(
            cell) {
              @Override
              public ProcessInstanceSummary getValue(ProcessInstanceSummary object) {
                return object;
              }
            };
    listGrid.addColumn(actionsColumn, constants.Actions());

  }

  private void initChecksColumn() {
    // Checkbox column. This table will uses a checkbox column for selection.
    // Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable
    // mouse selection.
    Column<ProcessInstanceSummary, Boolean> checkColumn = new Column<ProcessInstanceSummary, Boolean>(new CheckboxCell(
            true, false)) {
              @Override
              public Boolean getValue(ProcessInstanceSummary object) {
                // Get the value from the selection model.
                return selectionModel.isSelected(object);
              }
            };
    listGrid.addColumn(checkColumn, "");

  }

  public void refreshProcessInstanceListOnUpdates(@Observes ProcessInstancesUpdateEvent event) {
//        if(showAllLink.getStyleName().equals("active")){
//            presenter.refreshActiveProcessList();
//        }else if(showRelatedToMeLink.getStyleName().equals("active")){
//            presenter.refreshRelatedToMeProcessList();
//        }else if(showCompletedLink.getStyleName().equals("active")){
//            presenter.refreshCompletedProcessList();
//        }else if(showAbortedLink.getStyleName().equals("active")){
//            presenter.refreshAbortedProcessList();
//        }
  }

  public void onProcessInstanceSelectionEvent(@Observes ProcessInstancesWithDetailsRequestEvent event) {
    placeManager.goTo("Process Instance Details");
    processInstanceSelected.fire(new ProcessInstanceSelectionEvent(event.getDeploymentId(), event.getProcessInstanceId(), event.getProcessDefId()));
  }

//    public void changeRowSelected(@Observes ProcessInstanceStyleEvent processInstanceStyleEvent) {
//        if (processInstanceStyleEvent.getProcessInstanceId() != null) {
//            DataGridUtils.paintInstanceRowSelected(processInstanceListGrid,
//                    processInstanceStyleEvent.getProcessInstanceId());
//            processInstanceListGrid.setFocus(true);
//        }
//    }
  private class DetailsActionHasCell implements HasCell<ProcessInstanceSummary, ProcessInstanceSummary> {

    private ActionCell<ProcessInstanceSummary> cell;

    public DetailsActionHasCell(String text,
            Delegate<ProcessInstanceSummary> delegate) {
      cell = new ActionCell<ProcessInstanceSummary>(text, delegate) {
        @Override
        public void render(Cell.Context context,
                ProcessInstanceSummary value,
                SafeHtmlBuilder sb) {
          AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.detailsGridIcon());
          SafeHtmlBuilder mysb = new SafeHtmlBuilder();
          mysb.appendHtmlConstant("<span title='" + constants.Details() + "' style='margin-right:5px;'>");
          mysb.append(imageProto.getSafeHtml());
          mysb.appendHtmlConstant("</span>");
          sb.append(mysb.toSafeHtml());

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
    public ProcessInstanceSummary getValue(ProcessInstanceSummary object) {
      return object;
    }
  }

  private class AbortActionHasCell implements HasCell<ProcessInstanceSummary, ProcessInstanceSummary> {

    private ActionCell<ProcessInstanceSummary> cell;

    public AbortActionHasCell(String text,
            Delegate<ProcessInstanceSummary> delegate) {
      cell = new ActionCell<ProcessInstanceSummary>(text, delegate) {
        @Override
        public void render(Cell.Context context,
                ProcessInstanceSummary value,
                SafeHtmlBuilder sb) {
          if (value.getState() == ProcessInstance.STATE_ACTIVE) {
            AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.abortGridIcon());
            SafeHtmlBuilder mysb = new SafeHtmlBuilder();
            mysb.appendHtmlConstant("<span title='" + constants.Abort() + "' style='margin-right:5px;'>");
            mysb.append(imageProto.getSafeHtml());
            mysb.appendHtmlConstant("</span>");
            sb.append(mysb.toSafeHtml());
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
    public ProcessInstanceSummary getValue(ProcessInstanceSummary object) {
      return object;
    }
  }

  private class SignalActionHasCell implements HasCell<ProcessInstanceSummary, ProcessInstanceSummary> {

    private ActionCell<ProcessInstanceSummary> cell;

    public SignalActionHasCell(String text,
            Delegate<ProcessInstanceSummary> delegate) {
      cell = new ActionCell<ProcessInstanceSummary>(text, delegate) {
        @Override
        public void render(Cell.Context context,
                ProcessInstanceSummary value,
                SafeHtmlBuilder sb) {
          if (value.getState() == ProcessInstance.STATE_ACTIVE) {
            AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.signalGridIcon());
            SafeHtmlBuilder mysb = new SafeHtmlBuilder();
            mysb.appendHtmlConstant("<span title='" + constants.Signal() + "' style='margin-right:5px;'>");
            mysb.append(imageProto.getSafeHtml());
            mysb.appendHtmlConstant("</span>");
            sb.append(mysb.toSafeHtml());
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
    public ProcessInstanceSummary getValue(ProcessInstanceSummary object) {
      return object;
    }
  }

  public void formClosed(@Observes BeforeClosePlaceEvent closed) {
    if ("Signal Process Popup".equals(closed.getPlace().getIdentifier())) {
      presenter.refreshActiveProcessList();
    }
  }

}
