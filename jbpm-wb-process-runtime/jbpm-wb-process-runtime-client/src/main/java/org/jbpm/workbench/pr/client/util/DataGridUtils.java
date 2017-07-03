/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.workbench.pr.client.util;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;

public class DataGridUtils {

    public static int CHAR_SIZE_IN_PIXELS = 10;

    public static String trimToColumnWidth(AbstractCellTable table,
                                           Column column,
                                           String value) {
        if (value != null && value.length() > 0) {
            int columnWidth = getColumnWith(table,
                                            column);
            if (columnWidth < 0) {
                columnWidth = getDistributedColumnWidth(table,
                                                        column);
            }
            if (columnWidth < 0) {
                return "";
            }
            int textWidth = CHAR_SIZE_IN_PIXELS * value.length();
            if (columnWidth < textWidth) {
                int visibleChars = columnWidth / CHAR_SIZE_IN_PIXELS;
                visibleChars = visibleChars > value.length() ? value.length() : visibleChars;
                value = value.substring(0,
                                        visibleChars) + "...";
            }
        }
        return value;
    }

    public static int getDistributedColumnWidth(AbstractCellTable table,
                                                Column col) {
        int width = getColumnWith(table,
                                  col);
        if (width <= 0) {
            width = table.getOffsetWidth();
            int columnWidth = 0;
            int columns = table.getColumnCount();
            for (int i = 0; i < table.getColumnCount(); i++) {
                columnWidth = getColumnWith(table,
                                            i);
                if (columnWidth > 0) {
                    columns--;
                    width = width - columnWidth;
                }
            }
            width = width >= 0 ? width / (columns != 0 ? columns : 1) : -1;
        }
        return width;
    }

    public static int getColumnWith(AbstractCellTable table,
                                    int col) {
        return getColumnWith(table,
                             table.getColumn(col));
    }

    public static int getColumnWith(AbstractCellTable table,
                                    Column col) {
        String columnWidth = table.getColumnWidth(col);
        return columnWidth != null && !"null".equals(columnWidth) ? Integer.parseInt(columnWidth.substring(0,
                                                                                                           columnWidth.length() - 2)) : -1;
    }

    public static SafeHtml createDivStart(String title) {
        return createDivStart(title,
                              "");
    }

    public static SafeHtml createDivStart(String title,
                                          String defaultValue) {
        if (title == null || "".equals(title)) {
            title = defaultValue;
        }
        return SafeHtmlUtils.fromTrustedString("<div title=\"" + title.trim() + "\">");
    }

    public static SafeHtml createDivEnd() {
        return SafeHtmlUtils.fromTrustedString("</div>");
    }
}
