/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.console.ng.ht.client.editors.taskcomments;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.gc.client.util.DateUtils;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.model.CommentSummary;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "TaskCommentsViewImpl.html")
public class TaskCommentsViewImpl extends Composite implements TaskCommentsPresenter.TaskCommentsView {

    protected static final String COL_ADDEDBY = "addedBy";
    protected static final String COL_ADDEDAT = "addedAt";
    protected static final String COL_COMMENT = "comment";
    protected static final String COL_ID_ACTIONS = "Actions";

    private Constants constants = GWT.create( Constants.class );

    private TaskCommentsPresenter presenter;

    @Inject
    @DataField
    public TextArea newTaskCommentTextArea;

    @Inject
    @DataField
    public FormLabel newTaskCommentLabel = GWT.create(FormLabel.class);

    @Inject
    @DataField
    public Button addCommentButton = GWT.create(Button.class);

    @DataField
    public PagedTable<CommentSummary> commentsListGrid = new PagedTable<CommentSummary>(COMMENTS_PER_PAGE);

    @Inject
    private Event<NotificationEvent> notification;

    private ListHandler<CommentSummary> sortHandler;

    private static final int COMMENTS_PER_PAGE = 10;

    @Override
    public void clearCommentInput() {
        newTaskCommentTextArea.setText( "" );
    }

    @Override
    public void redrawDataGrid() {
        commentsListGrid.refresh();
        commentsListGrid.redraw();
    }

    @Override
    public void init( TaskCommentsPresenter presenter ) {
        this.presenter = presenter;
        List<String> bannedColumns = new ArrayList<String>();
        bannedColumns.add( COL_COMMENT);
        bannedColumns.add( COL_ID_ACTIONS );

        List<String> initColumns = new ArrayList<String>();
        initColumns.add( COL_ADDEDBY );
        initColumns.add( COL_COMMENT );
        initColumns.add( COL_ADDEDAT );
        initColumns.add(COL_ID_ACTIONS);

        commentsListGrid.setGridPreferencesStore( new GridPreferencesStore( new GridGlobalPreferences( "CommentsGrid", initColumns, bannedColumns ) ));
        commentsListGrid.setEmptyTableCaption( constants.No_Comments_For_This_Task() );
        // Attach a column sort handler to the ListDataProvider to sort the list.
        sortHandler = new ListHandler<CommentSummary>( presenter.getDataProvider().getList() );
        commentsListGrid.addColumnSortHandler( sortHandler );
        initTableColumns();
        presenter.addDataDisplay( commentsListGrid );

        addCommentButton.setText( constants.Add_Comment() );
        newTaskCommentLabel.setText( constants.Comment() );
    }

    @EventHandler("addCommentButton")
    public void addCommentButton( ClickEvent e ) {
        presenter.addTaskComment( newTaskCommentTextArea.getText() );
    }

    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    private void initTableColumns() {
        // addedBy
        Column<CommentSummary, String> addedByColumn = new Column<CommentSummary, String>( new TextCell() ) {
            @Override
            public String getValue( CommentSummary c ) {
                return c.getAddedBy();
            }
        };
        addedByColumn.setSortable( false );
        addedByColumn.setDataStoreName(COL_ADDEDBY);
        commentsListGrid.addColumn( addedByColumn, constants.Added_By() );


        // date
        Column<CommentSummary, String> addedAtColumn = new Column<CommentSummary, String>( new TextCell() ) {
            @Override
            public String getValue( CommentSummary c ) {
                return DateUtils.getDateTimeStr(c.getAddedAt());
            }
        };
        addedAtColumn.setSortable( true );
        addedAtColumn.setDataStoreName(COL_ADDEDAT);
        addedAtColumn.setDefaultSortAscending( true );
        commentsListGrid.addColumn( addedAtColumn, constants.At() );
        sortHandler.setComparator( addedAtColumn, new Comparator<CommentSummary>() {
            @Override
            public int compare( CommentSummary o1,
                                CommentSummary o2 ) {
                return o1.getAddedAt().compareTo( o2.getAddedAt() );
            }
        } );

        // comment text
        Column<CommentSummary, String> commentTextColumn = new Column<CommentSummary, String>( new TextCell() ) {
            @Override
            public String getValue( CommentSummary object ) {
                return object.getText();
            }
        };
        commentTextColumn.setSortable( false );
        commentTextColumn.setDataStoreName(COL_COMMENT);
        commentsListGrid.addColumn(commentTextColumn, constants.Comment());

        List<HasCell<CommentSummary, ?>> cells = new LinkedList<HasCell<CommentSummary, ?>>();

        cells.add( new DeleteCommentActionHasCell( constants.Delete(), new Delegate<CommentSummary>() {
            @Override
            public void execute( CommentSummary comment ) {
                presenter.removeTaskComment( comment.getId() );
            }
        } ) );

        CompositeCell<CommentSummary> cell = new CompositeCell<CommentSummary>( cells );
        Column<CommentSummary, CommentSummary> actionsColumn = new Column<CommentSummary, CommentSummary>(
                cell ) {
            @Override
            public CommentSummary getValue( CommentSummary object ) {
                return object;
            }
        };
        actionsColumn.setSortable(false);
        actionsColumn.setDataStoreName(COL_ID_ACTIONS);
        commentsListGrid.addColumn(actionsColumn, "");
    }

    private class DeleteCommentActionHasCell implements HasCell<CommentSummary, CommentSummary> {

        private ActionCell<CommentSummary> cell;

        public DeleteCommentActionHasCell( String text,
                                           Delegate<CommentSummary> delegate ) {
            cell = new ActionCell<CommentSummary>( text, delegate ) {
                @Override
                public void render( Cell.Context context,
                                    CommentSummary value,
                                    SafeHtmlBuilder sb ) {

                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
                    mysb.appendHtmlConstant( new SimplePanel( new Button( constants.Delete() ) {{
                        setSize( ButtonSize.SMALL );
                        setType( ButtonType.DANGER );
                    }} ).getElement().getInnerHTML() );
                    sb.append( mysb.toSafeHtml() );
                }
            };
        }

        @Override
        public Cell<CommentSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<CommentSummary, CommentSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public CommentSummary getValue( CommentSummary object ) {
            return object;
        }
    }
}
