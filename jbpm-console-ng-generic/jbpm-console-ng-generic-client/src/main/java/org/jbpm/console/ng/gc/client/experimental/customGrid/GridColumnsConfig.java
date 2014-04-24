package org.jbpm.console.ng.gc.client.experimental.customGrid;

import java.util.HashMap;

public class GridColumnsConfig extends HashMap<Integer, ColumnSettings> {

	// Save some kind of grid identifier for future storage of specific grid column configurations.
	private String gridId;

	public GridColumnsConfig( String gridId ) {
		super();
		this.gridId = gridId;
	}

	public String getGridId() {
		return gridId;
	}
}
