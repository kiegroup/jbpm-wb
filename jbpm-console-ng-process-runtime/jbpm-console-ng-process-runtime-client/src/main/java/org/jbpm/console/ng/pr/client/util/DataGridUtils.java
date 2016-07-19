/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.pr.client.util;

import java.util.Date;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.view.client.Range;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.jbpm.console.ng.bd.model.ProcessInstanceSummary;
import org.jbpm.console.ng.bd.model.ProcessSummary;
import org.jbpm.console.ng.bd.model.ProcessVariableSummary;

public class DataGridUtils {

    // it is rgb because datagrid returns this info
    private static final String BG_ROW_SELECTED = "rgb(229, 241, 255)";

    public static String newProcessDefName = null;
    public static String newProcessDefVersion = null;

    public static Long newProcessInstanceId = null;
    public static String newProcessInstanceDefName = null;
    public static String newProcessInstanceDefVersion = null;
    public static Date newProcessInstanceStartDate = null;

    public static int pageSize = 5;

    public static int CHAR_SIZE_IN_PIXELS = 10;

    public static void paintRowSelected( DataGrid<ProcessSummary> myProcessDefListGrid,
                                         String nameProcessDef,
                                         String versionProcessDef ) {
        for ( int i = 0; i < myProcessDefListGrid.getRowCount(); i++ ) {
            boolean nameMatch = false;
            boolean versionMatch = false;
            for ( int j = 0; j < myProcessDefListGrid.getColumnCount(); j++ ) {
                if ( myProcessDefListGrid.getRowElement( i ).getCells().getItem( j ).getInnerText().equals( nameProcessDef ) ) {
                    nameMatch = true;
                }
                if ( myProcessDefListGrid.getRowElement( i ).getCells().getItem( j ).getInnerText().equals( versionProcessDef ) ) {
                    versionMatch = true;
                }

            }
            for ( int k = 0; k < myProcessDefListGrid.getColumnCount(); k++ ) {
                if ( nameMatch && versionMatch ) {
                    myProcessDefListGrid.getRowElement( i ).getCells().getItem( k ).getStyle().setBackgroundColor( BG_ROW_SELECTED );
                } else {
                    myProcessDefListGrid.getRowElement( i ).getCells().getItem( k ).getStyle().clearBackgroundColor();
                }
            }
        }

    }

    public static void paintInstanceRowSelected( DataGrid<ProcessInstanceSummary> myProcessInstanceListGrid,
                                                 Long id ) {
        for ( int i = 0; i < myProcessInstanceListGrid.getRowCount(); i++ ) {
            boolean idMatch = false;
            if ( myProcessInstanceListGrid.getRowElement( i ).getCells().getItem( 1 ).getInnerText().equals( String.valueOf( id ) ) ) {
                idMatch = true;
            }

            for ( int k = 0; k < myProcessInstanceListGrid.getColumnCount(); k++ ) {
                if ( idMatch ) {
                    myProcessInstanceListGrid.getRowElement( i ).getCells().getItem( k ).getStyle().setBackgroundColor( BG_ROW_SELECTED );
                } else {
                    myProcessInstanceListGrid.getRowElement( i ).getCells().getItem( k ).getStyle().clearBackgroundColor();
                }
            }
        }

    }

    public static String getProcessNameRowSelected( DataGrid<ProcessSummary> myProcessDefListGrid ) {
        String processDefName = null;
        for ( int i = 0; i < myProcessDefListGrid.getRowCount(); i++ ) {
            if ( myProcessDefListGrid.getRowElement( i ).getCells().getItem( 0 ).getStyle().getBackgroundColor().equals( BG_ROW_SELECTED ) ) {
                processDefName = myProcessDefListGrid.getRowElement( i ).getCells().getItem( 0 ).getInnerText();
                break;
            }
        }
        return processDefName;
    }

    public static String getProcessVersionRowSelected( DataGrid<ProcessSummary> myProcessDefListGrid ) {
        String processDefVersion = null;
        for ( int i = 0; i < myProcessDefListGrid.getRowCount(); i++ ) {
            if ( myProcessDefListGrid.getRowElement( i ).getCells().getItem( 1 ).getStyle().getBackgroundColor().equals( BG_ROW_SELECTED ) ) {
                processDefVersion = myProcessDefListGrid.getRowElement( i ).getCells().getItem( 1 ).getInnerText();
                break;
            }
        }
        return processDefVersion;
    }

    public static String getProcessInstanceNameRowSelected( DataGrid<ProcessInstanceSummary> myProcessInstanceListGrid ) {
        String processDefName = null;
        for ( int i = 0; i < myProcessInstanceListGrid.getRowCount(); i++ ) {
            if ( myProcessInstanceListGrid.getRowElement( i ).getCells().getItem( 1 ).getStyle().getBackgroundColor().equals( BG_ROW_SELECTED ) ) {
                processDefName = myProcessInstanceListGrid.getRowElement( i ).getCells().getItem( 1 ).getInnerText();
                break;
            }
        }
        return processDefName;
    }

    public static String getProcessInstanceVersionRowSelected( DataGrid<ProcessInstanceSummary> myProcessInstanceListGrid ) {
        String processDefVersion = null;
        for ( int i = 0; i < myProcessInstanceListGrid.getRowCount(); i++ ) {
            if ( myProcessInstanceListGrid.getRowElement( i ).getCells().getItem( 3 ).getStyle().getBackgroundColor().equals( BG_ROW_SELECTED ) ) {
                processDefVersion = myProcessInstanceListGrid.getRowElement( i ).getCells().getItem( 3 ).getInnerText();
                break;
            }
        }
        return processDefVersion;
    }

    public static String getProcessInstanceStartDateRowSelected( DataGrid<ProcessInstanceSummary> myProcessInstanceListGrid ) {
        String processStartDate = null;
        for ( int i = 0; i < myProcessInstanceListGrid.getRowCount(); i++ ) {
            if ( myProcessInstanceListGrid.getRowElement( i ).getCells().getItem( 5 ).getStyle().getBackgroundColor().equals( BG_ROW_SELECTED ) ) {
                processStartDate = myProcessInstanceListGrid.getRowElement( i ).getCells().getItem( 5 ).getInnerText();
                break;
            }
        }
        return processStartDate;
    }

    public static void setTooltip( DataGrid<ProcessVariableSummary> varListGrid,
                                   String idCurrentRow,
                                   int column,
                                   String newValue,
                                   String oldValue ) {
        for ( int i = 0; i < getCurrentRowCount( varListGrid ); i++ ) {
            if ( varListGrid.getRowElement( i ).getCells().getItem( 0 ).getInnerText().equals( newValue.substring( 0, 20 ) + "..." ) ) {
                varListGrid.getRowElement( i ).getCells().getItem( column ).setTitle( "New Value: " + newValue + ", Old Value: " + oldValue );
                break;
            }
        }
    }

    private static int getCurrentRowCount( DataGrid<ProcessVariableSummary> varListGrid ) {
        int rowCount = 0;
        for ( int i = 0; i < DataGridUtils.pageSize; i++ ) {
            try {
                rowCount = i + 1;
                varListGrid.getRowElement( i );
            } catch ( Exception e ) {
                rowCount = i;
                break;
            }
        }
        return rowCount;
    }

    public static String trimToColumnWidth( AbstractCellTable table,
                                            Column column,
                                            String value ) {
        if ( value != null && value.length() > 0 ) {
            int columnWidth = getColumnWith( table, column );
            if ( columnWidth < 0 ) {
                columnWidth = getDistributedColumnWidth( table, column );
            }
            if ( columnWidth < 0 ) {
                return "";
            }
            int textWidth = CHAR_SIZE_IN_PIXELS * value.length();
            if ( columnWidth < textWidth ) {
                int visibleChars = columnWidth / CHAR_SIZE_IN_PIXELS;
                visibleChars = visibleChars > value.length() ? value.length() : visibleChars;
                value = value.substring( 0, visibleChars ) + "...";
            }
        }
        return value;
    }

    public static void redrawVisibleRange( AbstractCellTable table ) {
        if ( table != null ) {
            Range range = table.getVisibleRange();
            if ( range != null && range.getLength() > 0 ) {
                int offset = range.getStart();
                int count = 0;

                while ( count < table.getVisibleItemCount() && offset < range.getStart() + range.getLength() ) {
                    table.redrawRow( offset );
                    count++;
                    offset++;
                }
            }
        }
    }

    public static int getDistributedColumnWidth( AbstractCellTable table,
                                                 Column col ) {
        int width = getColumnWith( table, col );
        if ( width <= 0 ) {
            width = table.getOffsetWidth();
            int columnWidth = 0;
            int columns = table.getColumnCount();
            for ( int i = 0; i < table.getColumnCount(); i++ ) {
                columnWidth = getColumnWith( table, i );
                if ( columnWidth > 0 ) {
                    columns--;
                    width = width - columnWidth;
                }
            }
            width = width >= 0 ? width / ( columns != 0 ? columns : 1 ) : -1;
        }
        return width;
    }

    public static int getColumnWith( AbstractCellTable table,
                                     int col ) {
        return getColumnWith( table, table.getColumn( col ) );
    }

    public static int getColumnWith( AbstractCellTable table,
                                     Column col ) {
        String columnWidth = table.getColumnWidth( col );
        return columnWidth != null && !"null".equals( columnWidth ) ? Integer.parseInt( columnWidth.substring( 0, columnWidth.length() - 2 ) ) : -1;
    }

    public static SafeHtml createDivStart( String title ) {
        return createDivStart( title, "" );
    }

    public static SafeHtml createDivStart( String title,
                                           String defaultValue ) {
        if ( title == null || "".equals( title ) ) {
            title = defaultValue;
        }
        return SafeHtmlUtils.fromTrustedString( "<div title=\"" + title.trim() + "\">" );
    }

    public static SafeHtml createDivEnd() {
        return SafeHtmlUtils.fromTrustedString( "</div>" );
    }
}
