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

package org.jbpm.console.ng.pr.client.editors.instance.details;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.resources.ProcessRuntimeImages;
import org.jbpm.console.ng.pr.client.util.ResizableHeader;
import org.jbpm.console.ng.pr.model.NodeInstanceSummary;
import org.jbpm.console.ng.pr.model.ProcessInstanceSummary;
import org.jbpm.console.ng.pr.model.VariableSummary;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.BeforeClosePlaceEvent;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.security.Identity;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

@Dependent
@Templated(value = "ProcessInstanceDetailsViewImpl.html")
public class ProcessInstanceDetailsViewImpl extends Composite implements
        ProcessInstanceDetailsPresenter.ProcessInstanceDetailsView {

    private ProcessInstanceDetailsPresenter presenter;

    @Inject
    @DataField
    public TextBox processIdText;

    @Inject
    @DataField
    public TextBox processNameText;

    @Inject
    @DataField
    public TextBox processPackageText;

    @Inject
    @DataField
    public FlowPanel listContainer;

    @Inject
    @DataField
    public TextBox processVersionText;

    @Inject
    @DataField
    public TextBox stateText;

    @Inject
    @DataField
    public ListBox currentActivitiesListBox;

    @Inject
    @DataField
    public TextArea logTextArea;

    @Inject
    @DataField
    public IconAnchor refreshIcon;

    @Inject
    @DataField
    public Label processInstanceDetailsLabel;

    @Inject
    @DataField
    public Label processIdLabel;

    @Inject
    @DataField
    public Label processNameLabel;

    @Inject
    @DataField
    public Label processPackageLabel;

    @Inject
    @DataField
    public Label processVersionLabel;

    @Inject
    @DataField
    public Label stateLabel;

    @Inject
    @DataField
    public Label currentActivitiesListLabel;

    @Inject
    @DataField
    public Label logTextLabel;

    @Inject
    @DataField
    public NavLink viewProcessDiagramButton;

    @Inject
    @DataField
    public DataGrid<VariableSummary> processDataGrid;

    @Inject
    @DataField
    public SimplePager pager;

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    private ColumnSortEvent.ListHandler<VariableSummary> sortHandler;

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create(Constants.class);
    private ProcessRuntimeImages images = GWT.create(ProcessRuntimeImages.class);
    private ProcessInstanceSummary processInstance;
    private Path processAssetPath;
    private String encodedProcessSource;
    private List<NodeInstanceSummary> activeNodes;
    private List<NodeInstanceSummary> completedNodes;

    @Override
    public void init(final ProcessInstanceDetailsPresenter presenter) {
        this.presenter = presenter;

        processIdText.setEnabled(false);
        processNameText.setEnabled(false);
        processPackageText.setEnabled(false);
        processVersionText.setEnabled(false);
        stateText.setEnabled(false);
        logTextArea.setEnabled(false);
        currentActivitiesListBox.setEnabled(false);

        viewProcessDiagramButton.setText(constants.View_Process_Model());
        listContainer.add(processDataGrid);
        listContainer.add(pager);
        processDataGrid.setHeight("200px");

        processNameLabel.setText(constants.Process_Definition_Name());
        processIdLabel.setText(constants.Process_Instance_ID());
        processPackageLabel.setText(constants.Process_Definition_Package());
        processVersionLabel.setText(constants.Process_Definition_Version());
        stateLabel.setText(constants.Process_Instance_State());
        currentActivitiesListLabel.setText(constants.Current_Activities());
        logTextLabel.setText(constants.Process_Instance_Log());

        processInstanceDetailsLabel.setText(constants.Process_Instance_Details());
        processInstanceDetailsLabel.setStyleName("");
        // Set the message to display when the table is empty.
        Label emptyTable = new Label(constants.No_Variables_Available());
        emptyTable.setStyleName("");
        processDataGrid.setEmptyTableWidget(emptyTable);

        // Attach a column sort handler to the ListDataProvider to sort the list.
        sortHandler = new ColumnSortEvent.ListHandler<VariableSummary>(presenter.getDataProvider().getList());

        processDataGrid.addColumnSortHandler(sortHandler);

        // Create a Pager to control the table.

        pager.setDisplay(processDataGrid);
        pager.setPageSize(4);

        refreshIcon.setTitle(constants.Refresh());
        refreshIcon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.refreshProcessInstanceData(processIdText.getText(), processNameText.getText());
                displayNotification(constants.Process_Instances_Details_Refreshed());
            }
        });

        initTableColumns();

        presenter.addDataDisplay(processDataGrid);
    }

    @EventHandler("viewProcessDiagramButton")
    public void viewProcessDiagramButton(ClickEvent e) {
        StringBuffer nodeParam = new StringBuffer();
        for (NodeInstanceSummary activeNode : activeNodes) {
            nodeParam.append(activeNode.getNodeUniqueName() + ",");
        }
        if (nodeParam.length() > 0) {
            nodeParam.deleteCharAt(nodeParam.length() - 1);
        }

        StringBuffer completedNodeParam = new StringBuffer();
        for (NodeInstanceSummary completedNode : completedNodes) {
            if (completedNode.isCompleted()) {
                // insert outgoing sequence flow and node as this is for on entry event
                completedNodeParam.append(completedNode.getNodeUniqueName() + ",");
                completedNodeParam.append(completedNode.getConnection() + ",");
            } else if (completedNode.getConnection() != null) {
                // insert only incoming sequence flow as node id was already inserted
                completedNodeParam.append(completedNode.getConnection() + ",");
            }

        }
        completedNodeParam.deleteCharAt(completedNodeParam.length() - 1);

        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Designer");
        placeRequestImpl.addParameter("activeNodes", nodeParam.toString());
        placeRequestImpl.addParameter("completedNodes", completedNodeParam.toString());
        placeRequestImpl.addParameter("readOnly", "true");
        if (encodedProcessSource != null) {
            placeRequestImpl.addParameter("encodedProcessSource", encodedProcessSource);
        }

        placeManager.goTo(processAssetPath, placeRequestImpl);
    }

    @Override
    public TextBox getProcessIdText() {
        return processIdText;
    }

    @Override
    public ListBox getCurrentActivitiesListBox() {
        return currentActivitiesListBox;
    }

    @Override
    public TextArea getLogTextArea() {
        return logTextArea;
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

    @Override
    public TextBox getProcessNameText() {
        return processNameText;
    }

    private void initTableColumns() {
        // Checkbox column. This table will uses a checkbox column for selection.
        // Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable
        // mouse selection.

        // Id
        Column<VariableSummary, String> variableId = new Column<VariableSummary, String>(new TextCell()) {
            @Override
            public String getValue(VariableSummary object) {
                return object.getVariableId();
            }
        };
        variableId.setSortable(true);

        processDataGrid.addColumn(variableId, new ResizableHeader(constants.Name(), processDataGrid, variableId));
        sortHandler.setComparator(variableId, new Comparator<VariableSummary>() {
            @Override
            public int compare(VariableSummary o1, VariableSummary o2) {
                return o1.getVariableId().compareTo(o2.getVariableId());
            }
        });

        // Value.
        Column<VariableSummary, String> valueColumn = new Column<VariableSummary, String>(new TextCell()) {
            @Override
            public String getValue(VariableSummary object) {
                return object.getNewValue();
            }
        };
        valueColumn.setSortable(true);

        processDataGrid.addColumn(valueColumn, new ResizableHeader(constants.Value(), processDataGrid, valueColumn));
        sortHandler.setComparator(valueColumn, new Comparator<VariableSummary>() {
            @Override
            public int compare(VariableSummary o1, VariableSummary o2) {
                return o1.getNewValue().compareTo(o2.getNewValue());
            }
        });

        // Type.
        Column<VariableSummary, String> typeColumn = new Column<VariableSummary, String>(new TextCell()) {
            @Override
            public String getValue(VariableSummary object) {
                return object.getType();
            }
        };
        typeColumn.setSortable(true);

        processDataGrid.addColumn(typeColumn, new ResizableHeader(constants.Type(), processDataGrid, typeColumn));
        sortHandler.setComparator(typeColumn, new Comparator<VariableSummary>() {
            @Override
            public int compare(VariableSummary o1, VariableSummary o2) {
                return o1.getType().compareTo(o2.getType());
            }
        });

        // Last Time Changed Date.
        Column<VariableSummary, String> dueDateColumn = new Column<VariableSummary, String>(new TextCell()) {
            @Override
            public String getValue(VariableSummary object) {

                return object.getTimestamp();

            }
        };
        dueDateColumn.setSortable(true);

        processDataGrid.addColumn(dueDateColumn, new ResizableHeader(constants.Last_Modification(), processDataGrid,
                dueDateColumn));
        sortHandler.setComparator(dueDateColumn, new Comparator<VariableSummary>() {
            @Override
            public int compare(VariableSummary o1, VariableSummary o2) {

                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
        });

        List<HasCell<VariableSummary, ?>> cells = new LinkedList<HasCell<VariableSummary, ?>>();

        cells.add(new EditVariableActionHasCell("Edit Variable", new Delegate<VariableSummary>() {
            @Override
            public void execute(VariableSummary variable) {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Edit Variable Popup");
                placeRequestImpl.addParameter("processInstanceId", Long.toString(variable.getProcessInstanceId()));
                placeRequestImpl.addParameter("variableId", variable.getVariableId());
                placeRequestImpl.addParameter("value", variable.getNewValue());

                placeManager.goTo(placeRequestImpl);
            }
        }));

        cells.add(new VariableHistoryActionHasCell("Variable History", new Delegate<VariableSummary>() {
            @Override
            public void execute(VariableSummary variable) {
                PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Variable History Popup");
                placeRequestImpl.addParameter("processInstanceId", Long.toString(variable.getProcessInstanceId()));
                placeRequestImpl.addParameter("variableId", variable.getVariableId());

                placeManager.goTo(placeRequestImpl);
            }
        }));

        CompositeCell<VariableSummary> cell = new CompositeCell<VariableSummary>(cells);
        processDataGrid.addColumn(new Column<VariableSummary, VariableSummary>(cell) {
            @Override
            public VariableSummary getValue(VariableSummary object) {
                return object;
            }
        }, constants.Actions());
    }

    private class EditVariableActionHasCell implements HasCell<VariableSummary, VariableSummary> {

        private ActionCell<VariableSummary> cell;

        public EditVariableActionHasCell(String text, Delegate<VariableSummary> delegate) {
            cell = new ActionCell<VariableSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context, VariableSummary value, SafeHtmlBuilder sb) {
                    if (processInstance.getState() == ProcessInstance.STATE_ACTIVE) {
                        AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.editGridIcon());
                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                        mysb.appendHtmlConstant("<span title='" + constants.Edit_Variable() + "'>");
                        mysb.append(imageProto.getSafeHtml());
                        mysb.appendHtmlConstant("</span>");
                        sb.append(mysb.toSafeHtml());
                    }
                }
            };
        }

        @Override
        public Cell<VariableSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<VariableSummary, VariableSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public VariableSummary getValue(VariableSummary object) {
            return object;
        }

    }

    private class VariableHistoryActionHasCell implements HasCell<VariableSummary, VariableSummary> {

        private ActionCell<VariableSummary> cell;

        public VariableHistoryActionHasCell(String text, Delegate<VariableSummary> delegate) {
            cell = new ActionCell<VariableSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context, VariableSummary value, SafeHtmlBuilder sb) {

                    AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.historyGridIcon());
                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant("<span title='" + constants.Variables_History() + "'>");
                    mysb.append(imageProto.getSafeHtml());
                    mysb.appendHtmlConstant("</span>");
                    sb.append(mysb.toSafeHtml());
                }
            };
        }

        @Override
        public Cell<VariableSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<VariableSummary, VariableSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public VariableSummary getValue(VariableSummary object) {
            return object;
        }

    }

    public void formClosed(@Observes BeforeClosePlaceEvent closed) {
        if ("Edit Variable Popup".equals(closed.getPlace().getIdentifier())) {
            presenter.loadVariables(processIdText.getText(), processNameText.getText());
        }
    }

    @Override
    public void setProcessInstance(ProcessInstanceSummary processInstance) {
        this.processInstance = processInstance;
    }

    @Override
    public TextBox getStateText() {
        return this.stateText;
    }

    @Override
    public TextBox getProcessPackageText() {
        return processPackageText;
    }

    @Override
    public TextBox getProcessVersionText() {
        return processVersionText;
    }

    @Override
    public void setProcessAssetPath(Path processAssetPath) {
        this.processAssetPath = processAssetPath;
    }

    @Override
    public void setCurrentActiveNodes(List<NodeInstanceSummary> activeNodes) {
        this.activeNodes = activeNodes;

    }

    @Override
    public void setCurrentCompletedNodes(List<NodeInstanceSummary> completedNodes) {
        this.completedNodes = completedNodes;
    }

    @Override
    public void setEncodedProcessSource(String encodedProcessSource) {
        this.encodedProcessSource = encodedProcessSource;
    }

}
