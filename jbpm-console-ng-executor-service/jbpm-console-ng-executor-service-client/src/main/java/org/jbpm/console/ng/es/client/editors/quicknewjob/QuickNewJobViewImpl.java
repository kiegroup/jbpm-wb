package org.jbpm.console.ng.es.client.editors.quicknewjob;

import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.es.client.i18n.Constants;
import org.jbpm.console.ng.es.client.util.ResizableHeader;
import org.jbpm.console.ng.es.model.RequestParameterSummary;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.workbench.widgets.events.BeforeClosePlaceEvent;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.shared.mvp.PlaceRequest;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.ListDataProvider;

@Dependent
@Templated(value = "QuickNewJobViewImpl.html")
public class QuickNewJobViewImpl extends Composite 
		implements QuickNewJobPresenter.QuickNewJobView {

    @Inject
    @DataField
    public TextBox jobNameText;
    @Inject
    @DataField
    public DateBox jobDueDate;
    @Inject
    @DataField
    public TextBox jobTypeText;
    @Inject
    @DataField
    public IntegerBox dataTriesNumber;
    @Inject
    @DataField
    public Button newParametersButton;
    @Inject
    @DataField
    public Button createButton;
    @Inject
    @DataField
    public DataGrid<RequestParameterSummary> myParametersGrid;
    @Inject
    Event<NotificationEvent> notificationEvents;
    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;
    private PlaceRequest place;
    private ListDataProvider<RequestParameterSummary> dataProvider = new ListDataProvider<RequestParameterSummary>();
	private QuickNewJobPresenter presenter;
    private Constants constants = GWT.create(Constants.class);
	
	public void init(QuickNewJobPresenter p) {
		this.presenter = p;
		
        myParametersGrid.setHeight("200px");

        //      Set the message to display when the table is empty.
        myParametersGrid.setEmptyTableWidget(new Label(constants.No_Parameters_added_yet()));

        initGridColumns();
	}

	@OnStart
    public void onStart( final PlaceRequest place ) {
        this.place = place;
    }

	private void initGridColumns() {
		Column<RequestParameterSummary, String> paramKeyColumn = new Column<RequestParameterSummary, String>(new EditTextCell()) {
        	public String getValue(RequestParameterSummary rowObject) {
        		return rowObject.getKey();
        	}
        };
        myParametersGrid.addColumn(paramKeyColumn, new ResizableHeader<RequestParameterSummary>("Key", myParametersGrid, paramKeyColumn));

        Column<RequestParameterSummary, String> paramValueColumn = new Column<RequestParameterSummary, String>(new EditTextCell()) {
        	public String getValue(RequestParameterSummary rowObject) {
        		return rowObject.getValue();
        	}
        };
        myParametersGrid.addColumn(paramValueColumn, new ResizableHeader<RequestParameterSummary>("Value", myParametersGrid, paramValueColumn));
        
        // actions (icons)
        List<HasCell<RequestParameterSummary, ?>> cells = new LinkedList<HasCell<RequestParameterSummary, ?>>();

        cells.add(new ActionHasCell("Remove", new Delegate<RequestParameterSummary>() {
            @Override
            public void execute(RequestParameterSummary parameter) {
            	presenter.removeParameter(parameter);
            }
        }));

        CompositeCell<RequestParameterSummary> cell = new CompositeCell<RequestParameterSummary>(cells);
        Column<RequestParameterSummary, RequestParameterSummary> actionsColumn = 
        	new Column<RequestParameterSummary, RequestParameterSummary>(cell) {
        		public RequestParameterSummary getValue(RequestParameterSummary object) {
                	return object;
        		}
        };
        myParametersGrid.addColumn(actionsColumn, "Actions");
        myParametersGrid.setColumnWidth(actionsColumn, "70px");
        
        dataProvider.addDataDisplay(myParametersGrid);
	}
	
    @EventHandler("newParametersButton")
    public void newParametersButton(ClickEvent e) {
    	presenter.addNewParameter();
    }
    
    @EventHandler("createButton")
    public void createButton(ClickEvent e) {
    	presenter.createJob(jobNameText.getText(), jobDueDate.getValue(), jobTypeText.getText(), 
    			dataTriesNumber.getValue(), myParametersGrid.getVisibleItems());
    }

	private class ActionHasCell implements HasCell<RequestParameterSummary, RequestParameterSummary> {
        private ActionCell<RequestParameterSummary> cell;

        public ActionHasCell(String text, Delegate<RequestParameterSummary> delegate) {
            cell = new ActionCell<RequestParameterSummary>(text, delegate);
        }

        @Override
        public Cell<RequestParameterSummary> getCell() {
            return cell;
        }

        @Override
        public FieldUpdater<RequestParameterSummary, RequestParameterSummary> getFieldUpdater() {
            return null;
        }

        @Override
        public RequestParameterSummary getValue(RequestParameterSummary object) {
            return object;
        }
	}

	public void makeRowEditable(RequestParameterSummary parameter) {
		myParametersGrid.getSelectionModel().setSelected(parameter, true);
	}
	
	public void removeRow(RequestParameterSummary parameter) {
		 dataProvider.getList().remove(parameter);
	}
	
	public void addRow(RequestParameterSummary parameter) {
		dataProvider.getList().add(parameter);
	}
	
	public void displayNotification(String notification) {
		notificationEvents.fire(new NotificationEvent(notification));
	}
	
	public void close() {
		closePlaceEvent.fire(new BeforeClosePlaceEvent(this.place));
	}

	public Focusable getJobNameText() {
		return jobNameText;
	}
}
