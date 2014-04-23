package org.jbpm.console.ng.gc.client.experimental.customGrid;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.Icon;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ColumnSelectionWidget extends Composite {

    interface ColumnSelectionWidgetUIBinder
            extends UiBinder<Widget, ColumnSelectionWidget> {
    };

    private static ColumnSelectionWidgetUIBinder uiBinder = GWT.create(ColumnSelectionWidgetUIBinder.class);

    @UiField
    Icon dynGridIcon;

    private String gridId;
    private DataGrid dataGrid;
    private ColumnConfigPopup selectorPopup;
    private boolean initialized = false;

    public ColumnSelectionWidget() {
        initWidget(uiBinder.createAndBindUi(this));

        dynGridIcon.getElement().getStyle().setPaddingLeft(4, Style.Unit.PX);
        dynGridIcon.getElement().getStyle().setCursor(Style.Cursor.POINTER);

        dynGridIcon.sinkEvents(Event.ONCLICK);
        dynGridIcon.addHandler(new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                iconClicked();
            }
        }, ClickEvent.getType());
    }

    //TODO add some kind of error message, to be shown in case the widget is not well configured (i.e. has been included but does not set the datagrid)
    public void setDataGrid( String gridId, DataGrid dataGrid ) {
        if (dataGrid == null) return;
        this.gridId = gridId;
        this.dataGrid = dataGrid;
        selectorPopup = new ColumnConfigPopup( dataGrid );
        initialized = true;
    }

    private void iconClicked() {
        // TODO initialize the popup each time?
        selectorPopup.init( gridId );
        selectorPopup.show();
    }
}
