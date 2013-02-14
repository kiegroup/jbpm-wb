package org.jbpm.console.ng.ht.client.editors.taskcomments;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.ht.model.CommentSummary;
import org.jbpm.console.ng.ht.model.TaskSummary;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.base.UnorderedList;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;

@Dependent
@Templated(value = "TaskCommentsPopupViewImpl.html")
public class TaskCommentsPopupViewImpl extends Composite implements TaskCommentsPopupPresenter.TaskCommentsPopupView {

    private TaskCommentsPopupPresenter presenter;

    @Inject
    @DataField
    public Label taskIdText;

    @Inject
    @DataField
    public Label taskNameText;

    @Inject
    @DataField
    public TextArea newTaskCommentTextArea;

    @Inject
    @DataField
    public Button addCommentButton;

    @Inject
    @DataField
    public UnorderedList navBarUL;

    @Inject
    @DataField
    public DataGrid<CommentSummary> commentsListGrid;

    @Inject
    @DataField
    public SimplePager pager;

    @Inject
    @DataField
    public FlowPanel listContainer;

    @Override
    public Label getTaskIdText() {
        return taskIdText;
    }

    @Override
    public Label getTaskNameText() {
        return taskNameText;
    }

    @Override
    public UnorderedList getNavBarUL() {
        return navBarUL;
    }

    @Override
    public TextArea getNewTaskCommentTextArea() {
        return newTaskCommentTextArea;
    }

    @Override
    public Button addCommentButton() {
        return addCommentButton;
    }

    @Override
    public DataGrid<CommentSummary> getDataGrid() {
        return commentsListGrid;
    }

    @Override
    public void init(TaskCommentsPopupPresenter presenter) {
        this.presenter = presenter;
        listContainer.add(commentsListGrid);
        listContainer.add(pager);
        commentsListGrid.setHeight("100px");
        commentsListGrid.setEmptyTableWidget(new Label("No comments for this task."));
        // sortHandler = new ListHandler<CommentSummary>(presenter.getDataProvider().getList());
        // commentsListGrid.addColumnSortHandler(sortHandler);
        initTableColumns();
        presenter.addDataDisplay(commentsListGrid);
        // Create a Pager to control the table.
        pager.setDisplay(commentsListGrid);
        pager.setPageSize(6);
    }

    @EventHandler("addCommentButton")
    public void updateTaskButton(ClickEvent e) {
        presenter.addTaskComment(Long.parseLong(taskIdText.getText()), newTaskCommentTextArea.getText(), new Date());
    }

    private void initTableColumns() {
        // addedBy
        Column<CommentSummary, String> addedByColumn = new Column<CommentSummary, String>(new TextCell()) {
            @Override
            public String getValue(CommentSummary c) {
                return new HTML(c.getAddedBy() + " on " + c.getAddedAt()).getHTML();
            }
        };
        addedByColumn.setSortable(false);
        commentsListGrid.addColumn(addedByColumn);
        
        // comment text
        Column<CommentSummary, String> commentTextColumn = new Column<CommentSummary, String>(new TextCell()) {
            @Override
            public String getValue(CommentSummary object) {
                return object.getText();
            }
        };
        addedByColumn.setSortable(false);
        commentsListGrid.addColumn(commentTextColumn);
    }
}
