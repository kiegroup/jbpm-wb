package org.jbpm.console.ng.client.editors.tasks.indentity;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.client.util.ResizableHeader;
import org.jbpm.console.ng.shared.model.IdentitySummary;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.security.Identity;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

@Dependent
@Templated(value = "IdentityViewImpl.html")
public class IdentityViewImpl extends Composite implements
        IdentityPresenter.InboxView {
    
    @Inject
    private Identity identity;
    @Inject
    private PlaceManager placeManager;
    
    private IdentityPresenter presenter;
    
    @Inject
    @DataField
    public TextBox filterUserText;
    
    @Inject
    @DataField
    public Button filterUserButton;
    
    @Inject
    @DataField
    public DataGrid<IdentitySummary> identityListGrid;

    @Inject
    @DataField
    public SimplePager pager;
    
    @Inject
    private Event<NotificationEvent> notification;
    private ListHandler<IdentitySummary> sortHandler;

    @Override
    public void init(IdentityPresenter presenter) {
        this.presenter = presenter;


        identityListGrid.setWidth("100%");
        identityListGrid.setHeight("200px");

        // Set the message to display when the table is empty.
        identityListGrid.setEmptyTableWidget(new Label("No User/Groups Available"));
        
        // Attach a column sort handler to the ListDataProvider to sort the list.
        sortHandler =
                new ListHandler<IdentitySummary>(presenter.getDataProvider().getList());
        identityListGrid.addColumnSortHandler(sortHandler);
        
        pager.setDisplay(identityListGrid);
        pager.setPageSize(6);
        
        Column<IdentitySummary, String> identityIdColumn =
                new Column<IdentitySummary, String>(new TextCell()) {
                    @Override
                    public String getValue(IdentitySummary object) {
                        return object.getId();
                    }
                };
        identityListGrid.addColumn(identityIdColumn,
                new ResizableHeader("Entity id", identityListGrid, identityIdColumn));
        
        Column<IdentitySummary, String> identityTypeColumn =
                new Column<IdentitySummary, String>(new TextCell()) {
                    @Override
                    public String getValue(IdentitySummary object) {
                        return object.getType();
                    }
                };
                
        identityListGrid.addColumn(identityTypeColumn,
                new ResizableHeader("Entity type", identityListGrid, identityTypeColumn));
        presenter.addDataDisplay(identityListGrid);
        
    }
    
    @EventHandler("filterUserButton")
    public void refreshButton(ClickEvent e) {
        if (filterUserText.getText() != null && filterUserText.getText().length() > 0) {
            presenter.getEntityById(filterUserText.getText());
        } else {
            presenter.refreshIdentityList();
        }
        
    }

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
        
    }

    @Override
    public TextBox getUserText() {
        return this.filterUserText;
    }

    @Override
    public DataGrid<IdentitySummary> getDataGrid() {
        return this.identityListGrid;
    }

}
