package org.jbpm.console.ng.gc.client.experimental.customGrid;

import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.client.common.popups.footers.ModalFooterOKButton;

import java.util.Map;

public class ColumnConfigPopup extends Modal {

	interface ColumnConfigPopupUIBinder
			extends UiBinder<Widget, ColumnConfigPopup> {
	}

	private static ColumnConfigPopupUIBinder uiBinder = GWT.create( ColumnConfigPopupUIBinder.class );

	@UiField
	VerticalPanel columnPopupMainPanel;

	private GridColumnsHelper gridColumnsHelper;

	public ColumnConfigPopup( final GridColumnsHelper gridColumnsHelper ) {

		this.gridColumnsHelper = gridColumnsHelper;

		setTitle( "Configure grid columns" );
		setMaxHeigth( ( Window.getClientHeight() * 0.75 ) + "px" );
		setBackdrop( BackdropType.STATIC );
		setKeyboard( true );
		setAnimation( true );
		setDynamicSafe( true );

		add( uiBinder.createAndBindUi( this ) );

		add( new ModalFooterOKButton(
				new Command() {
					@Override
					public void execute() {
						gridColumnsHelper.saveGridColumnsConfig();
						hide();
					}
				}
		) );
	}

	public void setup( GridColumnsConfig gridColumnsConfig ) {
		// Initialize the popup when the widget's icon is clicked
		columnPopupMainPanel.clear();

		for ( final Map.Entry<Integer, ColumnSettings> entry : gridColumnsConfig.entrySet() ) {

			final ColumnSettings columnSettings = entry.getValue();

			final CheckBox checkBox = new com.google.gwt.user.client.ui.CheckBox();
			checkBox.setValue( columnSettings.isVisible() );
			checkBox.addClickHandler( new ClickHandler() {
				@Override
				public void onClick( ClickEvent event ) {
					gridColumnsHelper.applyGridColumnConfig( entry.getKey(), checkBox.getValue() );
				}
			} );
			columnPopupMainPanel.add( new ColumnConfigRowWidget( checkBox, columnSettings.getColumnLabel() ) );
		}
	}
}
