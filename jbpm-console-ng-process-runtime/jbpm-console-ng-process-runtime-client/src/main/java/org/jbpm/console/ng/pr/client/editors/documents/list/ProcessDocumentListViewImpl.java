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

import com.google.gwt.cell.client.*;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import org.jbpm.console.ng.gc.client.experimental.grid.base.ExtendedPagedTable;
import org.jbpm.console.ng.gc.client.list.base.AbstractListView;
import org.jbpm.console.ng.pr.client.i18n.Constants;

import org.jbpm.console.ng.pr.model.DocumentSummary;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.ext.widgets.common.client.tables.ColumnMeta;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Dependent
public class ProcessDocumentListViewImpl extends AbstractListView<DocumentSummary, ProcessDocumentListPresenter>
        implements ProcessDocumentListPresenter.ProcessDocumentListView {

    public static final String COL_ID_DOCID ="documentId";
    public static final String COL_ID_LASTMOD ="lastModified";
    public static final String COL_ID_DOCSIZE ="docSize";
    public static final String COL_ID_PATH ="Path";
    public static final String COL_ID_ACTIONS ="Actions";

    interface Binder
            extends
            UiBinder<Widget, ProcessDocumentListViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    private Constants constants = GWT.create(Constants.class);

    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };

    private Column actionsColumn;

    @Override
    public void init(final ProcessDocumentListPresenter presenter) {
        List<String> bannedColumns = new ArrayList<String>();

        bannedColumns.add(COL_ID_DOCID);
        bannedColumns.add(COL_ID_ACTIONS);
        List<String> initColumns = new ArrayList<String>();
        initColumns.add(COL_ID_DOCID);
        initColumns.add(COL_ID_LASTMOD);
        initColumns.add(COL_ID_DOCSIZE);
        initColumns.add(COL_ID_ACTIONS);

        super.init(presenter, new GridGlobalPreferences("DocumentGrid", initColumns, bannedColumns));

        listGrid.setEmptyTableCaption(constants.No_Documents_Available());

        selectionModel = new NoSelectionModel<DocumentSummary>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {


                if (selectedRow == -1) {
                    listGrid.setRowStyles(selectedStyles);
                    selectedRow = listGrid.getKeyboardSelectedRow();
                    listGrid.redraw();

                } else if (listGrid.getKeyboardSelectedRow() != selectedRow) {
                    listGrid.setRowStyles(selectedStyles);
                    selectedRow = listGrid.getKeyboardSelectedRow();
                    listGrid.redraw();
                }

                selectedItem = selectionModel.getLastSelectedObject();

            }
        });

        noActionColumnManager = DefaultSelectionEventManager
                .createCustomManager(new DefaultSelectionEventManager.EventTranslator<DocumentSummary>() {

                    @Override
                    public boolean clearCurrentSelection(CellPreviewEvent<DocumentSummary> event) {
                        return false;
                    }

                    @Override
                    public DefaultSelectionEventManager.SelectAction translateSelectionEvent(CellPreviewEvent<DocumentSummary> event) {
                        NativeEvent nativeEvent = event.getNativeEvent();
                        if (BrowserEvents.CLICK.equals(nativeEvent.getType())) {
                            // Ignore if the event didn't occur in the correct column.
                            if (listGrid.getColumnIndex(actionsColumn) == event.getColumn()) {
                                return DefaultSelectionEventManager.SelectAction.IGNORE;
                            }

                        }

                        return DefaultSelectionEventManager.SelectAction.DEFAULT;
                    }

                });

        listGrid.setSelectionModel(selectionModel, noActionColumnManager);
        listGrid.setRowStyles(selectedStyles);
    }

    @Override
    public void initColumns(ExtendedPagedTable extendedPagedTable) {
        Column documentId = initDocumentIdColumn();
        Column lastModifiedColumn = initDocumentLastModifiedColumn();
        Column sizeColumn = initDocumentSizeColumn();
        actionsColumn = initActionsColumn();

        List<ColumnMeta<DocumentSummary>> columnMetas = new ArrayList<ColumnMeta<DocumentSummary>>();
        columnMetas.add(new ColumnMeta<DocumentSummary>(documentId, constants.Name()));
        columnMetas.add(new ColumnMeta<DocumentSummary>(lastModifiedColumn, constants.Last_Modification()));
        columnMetas.add(new ColumnMeta<DocumentSummary>(sizeColumn, constants.Size()));
        columnMetas.add(new ColumnMeta<DocumentSummary>(actionsColumn, constants.Actions()));
        extendedPagedTable.addColumns(columnMetas);
    }

    private Column initDocumentIdColumn() {
        // Id
        Column<DocumentSummary, String> documentId = new Column<DocumentSummary, String>(new TextCell()) {

            @Override
            public String getValue(DocumentSummary object) {
                return object.getDocumentId();
            }
        };
        documentId.setSortable(true);
        documentId.setDataStoreName(COL_ID_DOCID);

        return documentId;
    }

    private Column initDocumentLastModifiedColumn() {
        // Value.
        Column<DocumentSummary, String> lastModifiedColumn = new Column<DocumentSummary, String>(new TextCell()) {

            @Override
            public String getValue(DocumentSummary object) {
                return object.getDocumentLastModified().toString();
            }
        };
        lastModifiedColumn.setSortable(true);
         lastModifiedColumn.setDataStoreName(COL_ID_LASTMOD);
        return lastModifiedColumn;
    }

    private Column initDocumentSizeColumn() {
        // Value.
        Column<DocumentSummary, String> sizeColumn = new Column<DocumentSummary, String>(new TextCell()) {

            @Override
            public String getValue(DocumentSummary object) {
                return readableFileSize(object.getDocumentSize());
            }
        };
        sizeColumn.setSortable(true);
        sizeColumn.setDataStoreName(COL_ID_DOCSIZE);
        return sizeColumn;
    }

    public String readableFileSize(long size) {
        if(size <= 0) return "0";
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return NumberFormat.getDecimalFormat().format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public Column initDocumentLinkColumn() {

        // Type.
        Column<DocumentSummary, String> pathColumn = new Column<DocumentSummary, String>(new TextCell()) {

            @Override
            public String getValue(DocumentSummary object) {
                return String.valueOf(object.getDocumentSize());
            }
        };
        pathColumn.setSortable(true);
        pathColumn.setDataStoreName(COL_ID_PATH);
        return pathColumn;
    }


    private Column initActionsColumn() {

        List<HasCell<DocumentSummary, ?>> cells = new LinkedList<HasCell<DocumentSummary, ?>>();


        cells.add(new AccessDocumentActionHasCell("Access Document", new Delegate<DocumentSummary>() {
            @Override
            public void execute(DocumentSummary document) {
                if (document != null) {
                    GWT.log("Accessing document: " + document.getDocumentLink());
                }
            }
        }));

        CompositeCell<DocumentSummary> cell = new CompositeCell<DocumentSummary>(cells);
        Column<DocumentSummary, DocumentSummary> actionsColumn = new Column<DocumentSummary, DocumentSummary>(cell) {
            @Override
            public DocumentSummary getValue(DocumentSummary object) {
                return object;
            }
        };
        actionsColumn.setDataStoreName(COL_ID_ACTIONS);
        return actionsColumn;
    }

    public void formClosed(@Observes BeforeClosePlaceEvent closed) {
        if ("Edit Variable Popup".equals(closed.getPlace().getIdentifier())) {
            presenter.refreshGrid();
        }
    }


    private class AccessDocumentActionHasCell implements HasCell<DocumentSummary, DocumentSummary> {

        private ActionCell<DocumentSummary> cell;

        public AccessDocumentActionHasCell(String text,
                                           Delegate<DocumentSummary> delegate) {
            cell = new ActionCell<DocumentSummary>(text, delegate) {
                @Override
                public void render(Cell.Context context,
                                   DocumentSummary value,
                                   SafeHtmlBuilder sb) {
                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant("<a href='" + value.getDocumentLink() + "' class='btn btn-mini' style='margin-right:5px; title='" + constants.download() + "' target='_blank'>");
                    mysb.appendHtmlConstant(constants.download());
                    mysb.appendHtmlConstant("</a>");
                    sb.append(mysb.toSafeHtml());
                }
            };
        }

        @Override
        public Cell<DocumentSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<DocumentSummary, DocumentSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public DocumentSummary getValue(DocumentSummary object) {
            return object;
        }

    }

}
