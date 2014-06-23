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

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.resources.ProcessRuntimeImages;
import org.jbpm.console.ng.pr.model.ProcessSummary;
import org.jbpm.console.ng.pr.model.events.NewProcessInstanceEvent;
import org.jbpm.console.ng.pr.model.events.ProcessDefSelectionEvent;
import org.jbpm.console.ng.pr.model.events.ProcessInstanceSelectionEvent;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Dependent
@Templated(value = "ProcessDefinitionListViewImpl.html")
public class ProcessDefinitionListViewImpl extends AbstractListView<ProcessSummary, ProcessDefinitionListPresenter>
        implements ProcessDefinitionListPresenter.ProcessDefinitionListView {

  private Constants constants = GWT.create(Constants.class);
  private ProcessRuntimeImages images = GWT.create(ProcessRuntimeImages.class);

  @Inject
  private Event<ProcessDefSelectionEvent> processDefSelected;

  @Inject
  private Event<ProcessInstanceSelectionEvent> processInstanceSelected;

  private ProcessSummary selectedItem;

  @Override
  public void init(final ProcessDefinitionListPresenter presenter) {
    super.init(presenter);

    // Add a selection model so we can select cells.
    final SingleSelectionModel<ProcessSummary> selectionModel = new SingleSelectionModel<ProcessSummary>();
    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
      @Override
      public void onSelectionChange(SelectionChangeEvent event) {
        ProcessSummary process = selectionModel.getSelectedObject();
        
        listGrid.paint(listGrid.getKeyboardSelectedRow());
        
        PlaceStatus instanceDetailsStatus = placeManager.getStatus(new DefaultPlaceRequest("Process Instance Details"));
        PlaceStatus status = placeManager.getStatus(new DefaultPlaceRequest("Process Definition Details"));
        if (instanceDetailsStatus == PlaceStatus.OPEN) {
          placeManager.closePlace("Process Instance Details");
        }
        if (status == PlaceStatus.CLOSE) {
          placeManager.goTo("Process Definition Details");
          processDefSelected.fire(new ProcessDefSelectionEvent(process.getProcessDefId(), process.getDeploymentId()));
        } else if (status == PlaceStatus.OPEN ) {
          processDefSelected.fire(new ProcessDefSelectionEvent(process.getProcessDefId(), process.getDeploymentId()));
        }
        
      }
    });

    listGrid.setSelectionModel(selectionModel);
    listGrid.setEmptyTableCaption(constants.No_Process_Definitions_Found());
  }
  
  @Override
  public void initColumns() {
    initProcessNameColumn();
    initVersionColumn();
    initActionsColumn();

  }

  private void initProcessNameColumn() {
    // Process Name String.
    Column<ProcessSummary, String> processNameColumn = new Column<ProcessSummary, String>(new TextCell()) {
      @Override
      public String getValue(ProcessSummary object) {
        return object.getName();
      }
    };
    processNameColumn.setSortable(true);
    listGrid.addColumn(processNameColumn, constants.Name());
  }

  private void initVersionColumn() {
    Column<ProcessSummary, String> versionColumn = new Column<ProcessSummary, String>(new TextCell()) {
      @Override
      public String getValue(ProcessSummary object) {
        return object.getVersion();
      }
    };
    versionColumn.setSortable(true);
    listGrid.addColumn(versionColumn, constants.Version());
  }

  private void initActionsColumn() {
    // actions (icons)
    List<HasCell<ProcessSummary, ?>> cells = new LinkedList<HasCell<ProcessSummary, ?>>();

    cells.add(new StartActionHasCell("Start process", new Delegate<ProcessSummary>() {
      @Override
      public void execute(ProcessSummary process) {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Form Display Popup");
        placeRequestImpl.addParameter("processId", process.getProcessDefId());
        placeRequestImpl.addParameter("domainId", process.getDeploymentId());
        placeRequestImpl.addParameter("processName", process.getName());
        placeManager.goTo(placeRequestImpl);
      }
    }));

    cells.add(new DetailsActionHasCell("Details", new Delegate<ProcessSummary>() {
      @Override
      public void execute(ProcessSummary process) {
        listGrid.paint(listGrid.getKeyboardSelectedRow());
        PlaceStatus status = placeManager.getStatus(new DefaultPlaceRequest("Process Definition Details"));
        PlaceStatus instanceDetailsStatus = placeManager.getStatus(new DefaultPlaceRequest("Process Instance Details"));
        if (instanceDetailsStatus == PlaceStatus.OPEN) {
          placeManager.closePlace("Process Instance Details");
        }
        if (status == PlaceStatus.CLOSE || selectedItem != process) {
          placeManager.goTo("Process Definition Details");
          processDefSelected.fire(new ProcessDefSelectionEvent(process.getProcessDefId(), process.getDeploymentId()));
        } else if (status == PlaceStatus.OPEN && selectedItem == process) {
          placeManager.closePlace(new DefaultPlaceRequest("Process Definition Details"));
        }
        selectedItem = process;
      }
    }));

    CompositeCell<ProcessSummary> cell = new CompositeCell<ProcessSummary>(cells);
    Column<ProcessSummary, ProcessSummary> actionsColumn = new Column<ProcessSummary, ProcessSummary>(cell) {
      @Override
      public ProcessSummary getValue(ProcessSummary object) {
        return object;
      }
    };
    listGrid.addColumn(actionsColumn, constants.Actions());
  }
  
  public void refreshNewProcessInstance(@Observes NewProcessInstanceEvent newProcessInstance) {
    PlaceStatus definitionDetailsStatus = placeManager.getStatus(new DefaultPlaceRequest("Process Definition Details"));
    if (definitionDetailsStatus == PlaceStatus.OPEN) {
      placeManager.closePlace("Process Definition Details");
    }
    placeManager.goTo("Process Instance Details");
    processInstanceSelected.fire(new ProcessInstanceSelectionEvent(newProcessInstance.getDeploymentId(),
            newProcessInstance.getNewProcessInstanceId(),
            newProcessInstance.getNewProcessDefId()));

  }

  /*
   * Custom Action Columns for the Process Definition List
   */
  private class StartActionHasCell implements HasCell<ProcessSummary, ProcessSummary> {

    private ActionCell<ProcessSummary> cell;

    public StartActionHasCell(String text,
            Delegate<ProcessSummary> delegate) {
      cell = new ActionCell<ProcessSummary>(text, delegate) {
        @Override
        public void render(Cell.Context context,
                ProcessSummary value,
                SafeHtmlBuilder sb) {

          AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.startGridIcon());
          SafeHtmlBuilder mysb = new SafeHtmlBuilder();
          mysb.appendHtmlConstant("<span title='" + constants.Start() + "' style='margin-right:5px;'>");
          mysb.append(imageProto.getSafeHtml());
          mysb.appendHtmlConstant("</span>");
          sb.append(mysb.toSafeHtml());
        }
      };
    }

    @Override
    public Cell<ProcessSummary> getCell() {
      return cell;
    }

    @Override
    public FieldUpdater<ProcessSummary, ProcessSummary> getFieldUpdater() {
      return null;
    }

    @Override
    public ProcessSummary getValue(ProcessSummary object) {
      return object;
    }
  }

  private class DetailsActionHasCell implements HasCell<ProcessSummary, ProcessSummary> {

    private ActionCell<ProcessSummary> cell;

    public DetailsActionHasCell(String text,
            Delegate<ProcessSummary> delegate) {
      cell = new ActionCell<ProcessSummary>(text, delegate) {
        @Override
        public void render(Cell.Context context,
                ProcessSummary value,
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
    public Cell<ProcessSummary> getCell() {
      return cell;
    }

    @Override
    public FieldUpdater<ProcessSummary, ProcessSummary> getFieldUpdater() {
      return null;
    }

    @Override
    public ProcessSummary getValue(ProcessSummary object) {
      return object;
    }
  }

}
