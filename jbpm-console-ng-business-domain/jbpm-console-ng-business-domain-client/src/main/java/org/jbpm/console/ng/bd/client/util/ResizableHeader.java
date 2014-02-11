/*
 * Copyright 2012 JBoss by Red Hat.
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

import org.jbpm.console.ng.gc.client.util.DataGridUtils;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;

public class ResizableHeader<T> extends Header<String> {

    private Column<T, ?> column = null;
    private AbstractCellTable<T> cellTable;
    private String title = "";
    private static final int width = 20;
    private int minWidth = 0;
    private int maxWidth = Integer.MAX_VALUE;

    public ResizableHeader(String title, AbstractCellTable<T> cellTable, Column<T, ?> column) {
        this(title, 0, Integer.MAX_VALUE, cellTable, column);
    }

    public ResizableHeader(String title, int minWidth, AbstractCellTable<T> cellTable, Column<T, ?> column) {
        this(title, minWidth, Integer.MAX_VALUE, cellTable, column);
    }

    public ResizableHeader(String title, int minWidth, int maxWidth, AbstractCellTable<T> cellTable, Column<T, ?> column) {
        super(new HeaderCell());
        this.title = title;
        this.cellTable = cellTable;
        this.column = column;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
    }

    @Override
    public String getValue() {
        return title;
    }

    @Override
    public void onBrowserEvent(Context context, Element target, NativeEvent event) {
        String eventType = event.getType();
        if (eventType.equals("mousemove")) {
            new ColumnResizeHelper<T>(cellTable, column, target, minWidth, maxWidth);
        } else {
            return;
        }
    }

    private void setCursor(Element element, Cursor cursor) {
        element.getStyle().setCursor(cursor);
    }

    class ColumnResizeHelper<E> implements NativePreviewHandler {

        private HandlerRegistration handler;
        private AbstractCellTable<E> table;
        private Column<E, ?> col;
        private Element el;
        private boolean mousedown;
        private Element measuringElement;

        private int initialColWith;
        private int newColWidth;
        private int minWidth;
        private int maxWidth;

        public ColumnResizeHelper(AbstractCellTable<E> table, Column<E, ?> col, Element el, int minWidth, int maxWidth) {
            this.el = el;
            this.table = table;
            this.col = col;
            this.initialColWith = DataGridUtils.getColumnWith(table, col);
            this.newColWidth = initialColWith;
            handler = Event.addNativePreviewHandler(this);
            this.minWidth = minWidth;
            this.maxWidth = maxWidth;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onPreviewNativeEvent(NativePreviewEvent event) {
            NativeEvent nativeEvent = event.getNativeEvent();
            nativeEvent.preventDefault();
            nativeEvent.stopPropagation();

            String eventType = nativeEvent.getType();
            int clientX = nativeEvent.getClientX();
            if (eventType.equals("mousemove") && mousedown) {
                int absoluteLeft = el.getAbsoluteLeft();
                int newWidth = clientX - absoluteLeft;
                newColWidth = getBestSuitedWidth(minWidth, maxWidth, newWidth);
                table.setColumnWidth(col, newColWidth + "px");
                return;
            }

            if (eventType.equals("mousemove") || eventType.equals("mousedown")) {
                Element eventTargetEl = nativeEvent.getEventTarget().cast();
                int absoluteLeft = eventTargetEl.getAbsoluteLeft();
                int offsetWidth = eventTargetEl.getOffsetWidth();
                if (clientX > absoluteLeft + offsetWidth - width) {
                    if (eventType.equals("mousedown")) {
                        mousedown = true;
                    } else {
                        setCursor(el, Cursor.COL_RESIZE);
                    }
                } else {
                    removeHandler();
                    return;
                }
            } else if (eventType.equals("mouseup")) {
                mousedown = false;
            } else if (eventType.equals("mouseout") && !mousedown) {
                removeHandler();
                return;
            }

            if (eventType.equals("dblclick")) {
                // Get column
                nativeEvent.preventDefault();
                nativeEvent.stopPropagation();
                int max = 0;
                startMeasuring();
                for (E t : table.getVisibleItems()) {
                    Object value = col.getValue(t);
                    SafeHtmlBuilder sb = new SafeHtmlBuilder();
                    Cell<Object> cell = (Cell<Object>) col.getCell();
                    cell.render(null, value, sb);
                    max = Math.max(measureText(sb.toSafeHtml().asString()), max);
                }
                finishMeasuring();
                max = max + width;
                newColWidth = getBestSuitedWidth(minWidth, maxWidth, max);
                table.setColumnWidth(col, newColWidth + "px");
                removeHandler();
            }
        }

        private void removeHandler() {
            if (initialColWith != newColWidth) {
                DataGridUtils.redrawVisibleRange(table);
            }
            handler.removeHandler();
            setCursor(el, Cursor.DEFAULT);
        }

        private void startMeasuring() {
            Document document = Document.get();
            measuringElement = document.createElement("div");
            measuringElement.getStyle().setPosition(Position.ABSOLUTE);
            measuringElement.getStyle().setLeft(-1000, Unit.PX);
            measuringElement.getStyle().setTop(-1000, Unit.PX);
            document.getBody().appendChild(measuringElement);
        }

        private int measureText(String text) {
            measuringElement.setInnerHTML(text);
            return measuringElement.getOffsetWidth();
        }

        private int getBestSuitedWidth(int minWidth, int maxWidth, int newWidth) {
            if (newWidth < minWidth) return minWidth;
            if (newWidth > maxWidth) return maxWidth;
            return newWidth;
        }

        private void finishMeasuring() {
            Document.get().getBody().removeChild(measuringElement);
        }

    }

    static class HeaderCell extends AbstractCell<String> {
        public HeaderCell() {
            super("click", "mousedown", "mousemove", "dblclick");
        }

        @Override
        public void render(Context context, String value, SafeHtmlBuilder sb) {
            sb.append(SafeHtmlUtils.fromString(value));
        }
    }
}
