package org.jbpm.console.ng.gc.client.experimental.customGrid;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.github.gwtbootstrap.client.ui.DataGrid;

import java.util.HashMap;
import java.util.Map;

public class GridColumnsHelper {

    // TODO temporal 'storage' for grid configurations
    private static Map<String, GridColumnsConfig> gridColumnsConfigs = new HashMap<String, GridColumnsConfig>(10);

    private Map<Integer, CachedColumn> cachedColumns = new HashMap<Integer, CachedColumn>(10);

    private DataGrid dataGrid;

    private ColumnIndexHelper indexManager;

    public GridColumnsHelper( DataGrid dataGrid ) {
        this.dataGrid = dataGrid;
        for (int i = 0; i < dataGrid.getColumnCount(); i++) {
            Column<?, ?> column = dataGrid.getColumn(i);
            cachedColumns.put(
                                i,
                                new CachedColumn( column,
                                                  dataGrid.getHeader(i),
                                                  dataGrid.getFooter(i),
                                                  dataGrid.getColumnWidth(column) )
                             );
            indexManager = new ColumnIndexHelper( cachedColumns.size() );
        }
    }

    public void saveGridColumnsConfig( GridColumnsConfig gridColumnsConfig ) {
        if (gridColumnsConfig != null) gridColumnsConfigs.put( gridColumnsConfig.getGridId(), gridColumnsConfig );
        // TODO persist, attach to user preferences, ...
    }

    public GridColumnsConfig getColumnsConfigForGrid( String gridId ) {
        GridColumnsConfig gcc = gridColumnsConfigs.get( gridId );
        if ( gcc == null ) gridColumnsConfigs.put( gridId, gcc = initializeGridColumnsConfig( gridId, dataGrid ) );
        return gcc;
    }

    /**
     * Notifies the helper that a column is about to be removed
     * @param selectedColumnIndex The selector's index
     * @return The column that is about to be removed's true index within the data-grid
     */
    public int notifyColumnRemoved( int selectedColumnIndex ) {
        return indexManager.indexDropped( selectedColumnIndex );
    }

    /**
     * Notifies the helper that a column is about to be added
     * @param selectedColumnIndex The selector's index
     * @return The index of the column before which the column that is about to be inserted should be placed.
     */
    public int notifyColumnAdded( int selectedColumnIndex ) {
        return indexManager.indexAdded( selectedColumnIndex );
    }

    public String getColumnWidth( int cacheIndex ) {
        CachedColumn cachedColumn = cachedColumns.get( cacheIndex );
        return cachedColumn != null ? cachedColumn.getColumnWidth() : "";
    }

    public Header<?> getColumnHeader( int cacheIndex ) {
        CachedColumn cachedColumn = cachedColumns.get( cacheIndex );
        return cachedColumn != null ? cachedColumn.getColumnHeader() : null;
    }

    public Header<?> getColumnFooter( int cacheIndex ) {
        CachedColumn cachedColumn = cachedColumns.get( cacheIndex );
        return cachedColumn != null ? cachedColumn.getColumnFooter() : null;
    }

    public Column<?, ?> getColumn( int cacheIndex ) {
        CachedColumn cachedColumn = cachedColumns.get( cacheIndex );
        return cachedColumn != null ? cachedColumn.getColumn() : null;
    }

    private GridColumnsConfig initializeGridColumnsConfig( String gridId, DataGrid dataGrid ) {

        GridColumnsConfig gridColumnsConfig = new GridColumnsConfig( gridId );
        for (int i = 0; i < dataGrid.getColumnCount(); i++) {
            gridColumnsConfig.put(i,
                    new ColumnSettings(true,
                            // TODO adapt this for non-string headers
                            (String) dataGrid.getHeader(i).getValue())
            );
        }
        return gridColumnsConfig;
    }

    private class CachedColumn {

        private Column<?, ?> column;

        private Header<?> columnHeader;

        private Header<?> columnFooter;

        private String columnWidth;

        private CachedColumn( Column<?, ?> column, Header<?> columnHeader, Header<?> columnFooter, String columnWidth ) {
            this.column = column;
            this.columnHeader = columnHeader;
            this.columnFooter = columnFooter;
            this.columnWidth = columnWidth;
        }

        public Column<?, ?> getColumn() {
            return column;
        }

        public Header<?> getColumnFooter() {
            return columnFooter;
        }

        public Header<?> getColumnHeader() {
            return columnHeader;
        }

        public String getColumnWidth() {
            return columnWidth;
        }
    }

    //TODO implement some kind of adequate test for this
    private class ColumnIndexHelper {
        private int[] selectorIndexes;
        private int[] gridIndexes;
        private ColumnIndexHelper( int maxIndex ) {
            selectorIndexes = new int[maxIndex];
            gridIndexes = new int[maxIndex];
            for (int i = 0; i < maxIndex; i++) {
                selectorIndexes[i] = i;
                gridIndexes[i] = i;
            }
        }
        private int indexDropped( int selectedColumnIndex ) {
            int current = gridIndexes[selectedColumnIndex];
            int counter = current;
            if ( current == -1 ) throw new RuntimeException( "Internal error: index to be dropped (" + selectedColumnIndex + ") was not set (" + current + "). Something went wrong." );
            for ( int i = selectedColumnIndex; i < selectorIndexes.length; i++ ) {
                if ( i == selectedColumnIndex ) gridIndexes[i] = -1;
                else if (gridIndexes[i] != -1) {
                    gridIndexes[i] = counter++;
                }
            }
            return current;
        }
        // Returns the CURRENT data-grid index BEFORE which the new column is to be inserted
        private int indexAdded( int selectedColumnIndex ) {
            int current = gridIndexes[selectedColumnIndex];
            if (current != -1) throw new RuntimeException("Internal error: index to be added (" + selectedColumnIndex + ") was internally still set (" + current + "). Something went wrong.");
            int nextValidIndex = 0;
            for (int i = 0; i < selectedColumnIndex; i++) {
                if (gridIndexes[i] != -1) nextValidIndex = gridIndexes[i] + 1;
            }
            for (int i = selectedColumnIndex; i < selectorIndexes.length; i++) {
                if ( i == selectedColumnIndex ) gridIndexes[i] = nextValidIndex;
                else if (gridIndexes[i] != -1) gridIndexes[i] = gridIndexes[i] + 1;
            }
            return nextValidIndex;
        }
    }
}
