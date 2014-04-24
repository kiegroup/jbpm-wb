package org.jbpm.console.ng.gc.client.experimental.customGrid;

public class ColumnSettings {

	private boolean isVisible;
	private String columnLabel;

	// TODO adapt this for non-string headers
	public ColumnSettings( boolean visible, String columnLabel ) {
		isVisible = visible;
		this.columnLabel = columnLabel;
	}

	public String getColumnLabel() {
		return columnLabel;
	}

	public void setColumnLabel( String columnLabel ) {
		this.columnLabel = columnLabel;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible( boolean visible ) {
		isVisible = visible;
	}
}
