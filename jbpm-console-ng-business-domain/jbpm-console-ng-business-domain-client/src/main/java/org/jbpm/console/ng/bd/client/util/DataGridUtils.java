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

package org.jbpm.console.ng.bd.client.util;



import com.github.gwtbootstrap.client.ui.DataGrid;
import com.google.gwt.dom.client.Document;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.view.client.Range;
import org.jbpm.console.ng.bd.model.KModuleDeploymentUnitSummary;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import java.util.HashMap;
import java.util.Map;


public class DataGridUtils {

    public static final int pageSize = 10;

    public static final int UPPER_CASE_CHAR_SIZE_IN_PIXELS = 11;

    public static final int LOWER_CASE_CHAR_SIZE_IN_PIXELS = 9;

    public static final int DOT_CHAR_SIZE_IN_PIXELS = 2;

    private static final String DEPLOYMENTS_SIZE_CALCULATOR =  "CONSOLE_DEPLOYMENTS_SIZE_CALCULATOR";

    //Hidden DIV used to calculate character widths
    private static Element charSizeDiv = DOM.createDiv();

    private static Map<Character, Integer> charSizeCache = new HashMap<Character, Integer>();

    private static int getCurrentRowCount(DataGrid<KModuleDeploymentUnitSummary> myTaskListGrid) {
        int rowCount = 0;
        for (int i = 0; i < DataGridUtils.pageSize; i++) {
            try {
                rowCount = i + 1;
                myTaskListGrid.getRowElement(i);
            } catch (Exception e) {
                rowCount = i;
                break;
            }
        }
        return rowCount;
    }

    public static void setTooltip(DataGrid<KModuleDeploymentUnitSummary> kmoduleListGrid, String idCurrentRow, int column,
            String kModuleId) {
        for (int i = 0; i < getCurrentRowCount(kmoduleListGrid); i++) {
            String kModuleFinalId = "";
            if(kModuleId.length() > 25){
                kModuleFinalId = idCurrentRow.substring(0, 25) + "...";
            }else{
                kModuleFinalId = idCurrentRow;
            }
            if (kmoduleListGrid.getRowElement(i).getCells().getItem(0).getInnerText().equals(kModuleFinalId)) {
                kmoduleListGrid.getRowElement(i).getCells().getItem(column).setTitle(kModuleId);
                break;
            }
        }
    }

    public static String trimToColumnWidthUsingFixedCharsWidth(AbstractCellTable table, Column column, String value) {
        if (value != null && value.length() > 0) {
            int columnWidth = getColumnWith(table, column);
            if (columnWidth < 0) {
                columnWidth = getDistributedColumnWidth(table, column);
            }
            if (columnWidth < 0) return "";
            int charWidth = weightedCharacterSize(value);
            int textWidth = charWidth * value.length();
            if (columnWidth < textWidth) {
                int visibleChars = columnWidth / charWidth;
                visibleChars = visibleChars > value.length() ? value.length() : visibleChars;
                value = value.substring(0, visibleChars) + "...";
            }
        }
        return value;
    }

    public static String trimToColumnWidth(AbstractCellTable table, Column column, String value) {
        if (value == null || value.length() == 0) return "";

        int columnWidth = getColumnWith(table, column);
        if (columnWidth < 0) {
            columnWidth = getDistributedColumnWidth(table, column);
        }
        if (columnWidth < 0) return "";

        int padding = calculateWidth("...");
        padding = padding == 0 ? 12 : (padding + 12);
        int charCount = 0;
        int charSize;
        while (columnWidth > 0 && (charCount < value.length())) {
            charSize = getCharWidth(value.charAt(charCount), true);
            charSize = charSize >= 0 ? charSize : 0;
            if ((columnWidth - charSize - padding) > 0) {
                charCount++;
                columnWidth = columnWidth - charSize;
            } else {
                break;
            }
        }
        String paddingStr = charCount < value.length() ? "..." : "";

        return charCount > 0 ? ((value.substring(0, charCount)) + paddingStr) : "";
    }

    public static int weightedCharacterSize(String text) {
        if (text == null || "".equals(text)) return 1;

        int size = 0;

        for (char c : text.toCharArray()) {
            if (Character.isLowerCase(c)) {
                size += LOWER_CASE_CHAR_SIZE_IN_PIXELS;
            } else if ('.' == c) {
                size += DOT_CHAR_SIZE_IN_PIXELS;
            } else {
                size += UPPER_CASE_CHAR_SIZE_IN_PIXELS;
            }
        }
        return (size / text.length());
    }

    public static int fixedCharacterSize(char c) {
        if (Character.isLowerCase(c)) {
            return LOWER_CASE_CHAR_SIZE_IN_PIXELS;
        } else if ('.' == c) {
            return DOT_CHAR_SIZE_IN_PIXELS;
        } else {
            return UPPER_CASE_CHAR_SIZE_IN_PIXELS;
        }
    }

    public static void redrawVisibleRange(AbstractCellTable table) {
        if (table != null) {
            Range range = table.getVisibleRange();
            if (range != null && range.getLength() > 0) {
                int offset = range.getStart();
                int count = 0;

                for ( ; (count < table.getVisibleItemCount()) && (offset < (range.getStart() + range.getLength() )) ; ) {
                    table.redrawRow(offset);
                    count++;
                    offset++;
                }
            }
        }
    }

    public static int getDistributedColumnWidth(AbstractCellTable table, Column col) {
        int width = getColumnWith(table, col);
        if (width <= 0) {
            width = table.getOffsetWidth();
            int columnWidth = 0;
            int columns = table.getColumnCount();
            for (int i = 0; i < table.getColumnCount() ; i++) {
                columnWidth = getColumnWith(table, i);
                if (columnWidth > 0) {
                    columns--;
                    width = width - columnWidth;
                }
            }
            width = width >= 0 ? width / (columns != 0 ? columns : 1) : -1;
        }
        return width;
    }

    public static int getColumnWith(AbstractCellTable table, int col) {
        return getColumnWith(table, table.getColumn(col));
    }

    public static int getColumnWith(AbstractCellTable table, Column col) {
        String columnWidth = table.getColumnWidth(col);
        return columnWidth != null && !"null".equals(columnWidth) ? Integer.parseInt(columnWidth.substring(0, columnWidth.length() - 2)) : -1;
    }

    public static SafeHtml createDivStart(String title) {
        return createDivStart(title, "");
    }

    public static SafeHtml createDivStart(String title, String defaultValue) {
        if (title == null || "".equals(title)) title = defaultValue;
        return SafeHtmlUtils.fromTrustedString("<div title=\"" + title.trim() + "\">");
    }

    public static SafeHtml createDivEnd() {
        return SafeHtmlUtils.fromTrustedString("</div>");
    }

    public static com.google.gwt.dom.client.Element getCharacterMeasuringElement(String name) {
        Document document = Document.get();

        com.google.gwt.dom.client.Element measuringElement = document.getElementById(name);
        if (measuringElement == null) {

            measuringElement = document.createElement("div");
            measuringElement.setId(name);
            measuringElement.getStyle().setPosition(Position.ABSOLUTE);
            measuringElement.getStyle().setLeft(-1000, Unit.PX);
            measuringElement.getStyle().setTop(-1000, Unit.PX);
            document.getBody().appendChild(measuringElement);
        }
        return  measuringElement;
    }

    public static int calculateWidth( final String value ) {

        if (value == null || value.length() == 0) return 0;
        com.google.gwt.dom.client.Element m = getCharacterMeasuringElement(DEPLOYMENTS_SIZE_CALCULATOR);
        if (m != null) {
            SafeHtmlBuilder sb = new SafeHtmlBuilder();
            sb.append(SafeHtmlUtils.fromTrustedString(value));
            m.setInnerHTML(sb.toSafeHtml().asString());
            int a = m.getClientWidth();
            int b = m.getOffsetWidth();
            return a;
        }
        return 0;
    }

    public static int getCharWidth( final Character c, boolean useCache) {
        Integer width;

        if (c == null) return 0;
        if (useCache) {
            width = charSizeCache.get(c);
            if (width != null) return width;
        }

        width = calculateWidth(String.valueOf(c));
        if (width <= 0) {
            width = fixedCharacterSize(c);
        }
        charSizeCache.put(c, width);
        return width;
    }

}
