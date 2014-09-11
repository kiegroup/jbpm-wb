/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.dm.client.document.list;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jbpm.console.ng.dm.client.i18n.Constants;
import org.jbpm.console.ng.dm.client.resources.DocumentsImages;
import org.jbpm.console.ng.dm.model.CMSContentSummary;
import org.jbpm.console.ng.dm.model.events.DocumentRemoveSearchEvent;
import org.jbpm.console.ng.dm.model.events.DocumentsHomeSearchEvent;
import org.jbpm.console.ng.dm.model.events.DocumentsListSearchEvent;
import org.jbpm.console.ng.dm.model.events.DocumentsParentSearchEvent;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView;
import org.kie.uberfire.shared.preferences.GridGlobalPreferences;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonGroup;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;

@Dependent
public class DocumentListViewImpl extends AbstractListView<CMSContentSummary, DocumentListPresenter> implements
        DocumentListPresenter.DocumentListView {

    interface Binder extends UiBinder<Widget, DocumentListViewImpl> {

    }

    private Constants constants = GWT.create(Constants.class);

    private DocumentsImages images = GWT.create(DocumentsImages.class);

    private static Binder uiBinder = GWT.create(Binder.class);

    private Column actionsColumn;

    private ButtonGroup filtersButtonGroup;

    public Button parentLink;

    public Anchor pathLink;

    public Button homeLink;

    public Button newLink;

    @Inject
    private Event<DocumentsListSearchEvent> selectDocEvent;

    @Inject
    private Event<DocumentsParentSearchEvent> parentDocEvent;

    @Inject
    private Event<DocumentsHomeSearchEvent> homeDocEvent;

    @Inject
    private Event<DocumentRemoveSearchEvent> removeDocEvent;

    @Inject
    private Event<NotificationEvent> notification;

    @Override
    public void init(final DocumentListPresenter presenter) {

        List<String> bannedColumns = new ArrayList<String>();
        bannedColumns.add(constants.DocumentID());
        bannedColumns.add(constants.DocumentName());
        bannedColumns.add(constants.Actions());
        List<String> initColumns = new ArrayList<String>();
        initColumns.add(constants.DocumentID());
        initColumns.add(constants.DocumentName());
        initColumns.add(constants.Actions());

        super.init(presenter, new GridGlobalPreferences("DocumentListGrid", initColumns, bannedColumns));

        initFiltersBar();
        initPathLink();

        selectionModel = new NoSelectionModel<CMSContentSummary>();
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

                selectedItem = selectionModel.getLastSelectedObject();

            }
        });

        noActionColumnManager = DefaultSelectionEventManager
                .createCustomManager(new DefaultSelectionEventManager.EventTranslator<CMSContentSummary>() {

                    @Override
                    public boolean clearCurrentSelection(CellPreviewEvent<CMSContentSummary> event) {
                        return false;
                    }

                    @Override
                    public DefaultSelectionEventManager.SelectAction translateSelectionEvent(
                            CellPreviewEvent<CMSContentSummary> event) {
                        NativeEvent nativeEvent = event.getNativeEvent();
                        if (BrowserEvents.CLICK.equals(nativeEvent.getType())) {
                            // Ignore if the event didn't occur in the correct
                            // column.
                            if (listGrid.getColumnIndex(actionsColumn) == event.getColumn()) {
                                return DefaultSelectionEventManager.SelectAction.IGNORE;
                            }
                        }
                        return DefaultSelectionEventManager.SelectAction.DEFAULT;
                    }
                });
        listGrid.setSelectionModel(selectionModel, noActionColumnManager);

        Button configRepoButton = new Button();
        configRepoButton.setIcon(IconType.COG);
        configRepoButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                PlaceStatus instanceDetailsStatus = placeManager.getStatus(new DefaultPlaceRequest("CMIS Configuration"));
                if (instanceDetailsStatus == PlaceStatus.OPEN) {
                    placeManager.closePlace("CMIS Configuration");
                }
                placeManager.goTo("CMIS Configuration");
            }
        });

        listGrid.getRightToolbar().add(configRepoButton);
        listGrid.setEmptyTableCaption(constants.No_Documents_Available());
        listGrid.setRowStyles(selectedStyles);
    }

    @Override
    public void initColumns() {
        initIdColumn();
        initNameColumn();
        initActionsColumn();
    }

    private void initIdColumn() {
        Column<CMSContentSummary, String> idColumn = new Column<CMSContentSummary, String>(new TextCell()) {
            @Override
            public String getValue(CMSContentSummary object) {
                return object.getId();
            }
        };

        idColumn.setSortable(true);

        listGrid.addColumn(idColumn, constants.DocumentID());

    }

    private void initNameColumn() {
        Column<CMSContentSummary, String> processNameColumn = new Column<CMSContentSummary, String>(new TextCell()) {
            @Override
            public String getValue(CMSContentSummary object) {
                return object.getName();
            }
        };
        processNameColumn.setSortable(true);

        listGrid.addColumn(processNameColumn, constants.DocumentName());
    }

    private void initActionsColumn() {
        List<HasCell<CMSContentSummary, ?>> cells = new LinkedList<HasCell<CMSContentSummary, ?>>();

        cells.add(new RemoveHasCell("Remove", new Delegate<CMSContentSummary>() {
            @Override
            public void execute(CMSContentSummary process) {
                removeDocEvent.fire(new DocumentRemoveSearchEvent(process));
            }
        }));

        cells.add(new GoHasCell("Go", new Delegate<CMSContentSummary>() {
            @Override
            public void execute(CMSContentSummary process) {
                selectDocEvent.fire(new DocumentsListSearchEvent(process));
                pathLink.setText(process.getPath());
            }
        }));

        CompositeCell<CMSContentSummary> cell = new CompositeCell<CMSContentSummary>(cells);
        actionsColumn = new Column<CMSContentSummary, CMSContentSummary>(cell) {
            @Override
            public CMSContentSummary getValue(CMSContentSummary object) {
                return object;
            }
        };
        listGrid.addColumn(actionsColumn, constants.Actions());

    }

    private void initFiltersBar() {
        HorizontalPanel filtersBar = new HorizontalPanel();

        parentLink = new Button();
        parentLink.setIcon(IconType.BACKWARD);
        parentLink.setSize(ButtonSize.SMALL);
        parentLink.setText(constants.Parent());
        parentLink.setEnabled(true);
        parentLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (BrowserEvents.CLICK.equalsIgnoreCase(event.getNativeEvent().getType())) {
                    parentDocEvent.fire(new DocumentsParentSearchEvent());
                    pathLink.setText(presenter.currentCMSContentSummary.getParent().getPath());
                }
            }
        });

        homeLink = new Button();
        homeLink.setIcon(IconType.HOME);
        homeLink.setSize(ButtonSize.SMALL);
        homeLink.setText(constants.Home());
        homeLink.setEnabled(true);
        homeLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (BrowserEvents.CLICK.equalsIgnoreCase(event.getNativeEvent().getType())) {
                    homeDocEvent.fire(new DocumentsHomeSearchEvent());
                    pathLink.setText("/");
                }
            }
        });

        newLink = new Button();
        newLink.setIcon(IconType.PLUS);
        newLink.setSize(ButtonSize.SMALL);
        newLink.setText(constants.New());
        newLink.setEnabled(true);
        newLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                DefaultPlaceRequest req = new DefaultPlaceRequest("New Document");
                String folder = (presenter.currentCMSContentSummary == null) ? "/" : presenter.currentCMSContentSummary
                        .getPath();
                req.addParameter("folder", folder);
                placeManager.goTo(req);
            }
        });

        filtersButtonGroup = new ButtonGroup(parentLink, homeLink, newLink);

        filtersBar.add(filtersButtonGroup);
        listGrid.getCenterToolbar().add(filtersBar);
    }
    
    private void initPathLink(){
        FlowPanel container = new FlowPanel();
        Label pathLabel = new Label();
        pathLabel.setText("Path:");
        pathLink = new Anchor();
        pathLink.setText("/");
        container.add(pathLabel);
        container.add(pathLink);
        listGrid.getLeftToolbar().add(container);
    }

    @Override
    public void updatePathLink() {
        if (presenter.currentCMSContentSummary != null) {
            String path = presenter.currentCMSContentSummary.getPath();
            if (path != null && !path.equals("")) {
                pathLink.setText(path);
            } else {
                pathLink.setText("/");
            }
        } else {
            pathLink.setText("/");
        }
    }

    protected class RemoveHasCell implements HasCell<CMSContentSummary, CMSContentSummary> {
        private ActionCell<CMSContentSummary> cell;

        public RemoveHasCell(String text, ActionCell.Delegate<CMSContentSummary> delegate) {
            cell = new ActionCell<CMSContentSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context, CMSContentSummary value, SafeHtmlBuilder sb) {
                    ImageResource detailsIcon = images.removeIcon();
                    AbstractImagePrototype imageProto = AbstractImagePrototype.create(detailsIcon);
                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant("<span title='" + constants.Remove() + "' style='margin-right:5px;'>"); // TODO
                                                                                                                    // add
                                                                                                                    // constants
                    mysb.append(imageProto.getSafeHtml());
                    mysb.appendHtmlConstant("</span>");
                    sb.append(mysb.toSafeHtml());
                }
            };

        }

        @Override
        public Cell<CMSContentSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<CMSContentSummary, CMSContentSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public CMSContentSummary getValue(CMSContentSummary object) {
            return object;
        }
    }

    protected class GoHasCell implements HasCell<CMSContentSummary, CMSContentSummary> {
        private ActionCell<CMSContentSummary> cell;

        public GoHasCell(String text, ActionCell.Delegate<CMSContentSummary> delegate) {
            cell = new ActionCell<CMSContentSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context, CMSContentSummary value, SafeHtmlBuilder sb) {
                    ImageResource detailsIcon = images.goIcon();
                    AbstractImagePrototype imageProto = AbstractImagePrototype.create(detailsIcon);
                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant("<span title='" + constants.Go() + "' style='margin-right:5px;'>");
                    mysb.append(imageProto.getSafeHtml());
                    mysb.appendHtmlConstant("</span>");
                    sb.append(mysb.toSafeHtml());
                }
            };

        }

        @Override
        public Cell<CMSContentSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<CMSContentSummary, CMSContentSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public CMSContentSummary getValue(CMSContentSummary object) {
            return object;
        }
    }

}
