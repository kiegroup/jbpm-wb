package org.jbpm.console.ng.gc.client.experimental.customGrid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

public class ColumnConfigRowWidget extends Composite {

	interface ColumnConfigRowWidgetUIBinder
			extends UiBinder<Widget, ColumnConfigRowWidget> {
	}

	private static ColumnConfigRowWidgetUIBinder uiBinder = GWT.create( ColumnConfigRowWidgetUIBinder.class );

	@UiField
	HTMLPanel rowPanel;

	public ColumnConfigRowWidget( CheckBox checkBox, String columnLabel ) {
		initWidget( uiBinder.createAndBindUi( this ) );
		rowPanel.add( checkBox, "columnVisibleId" );
		rowPanel.add( new Label( columnLabel ), "columnLabelId" );
	}
}
