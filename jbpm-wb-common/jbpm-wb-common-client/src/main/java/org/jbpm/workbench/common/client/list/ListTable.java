/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.common.client.list;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.RequiresResize;
import org.jbpm.workbench.common.model.GenericSummary;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;

public class ListTable<T extends GenericSummary> extends ExtendedPagedTable<T> implements RequiresResize {

    public static final int ROW_HEIGHT_PX = 47;

    private int tableHeaderOffset = HEIGHT_OFFSET_PX;

    public ListTable(GridGlobalPreferences gridPreferences) {
        super(gridPreferences);
        this.addDataGridStyles("kie-datatable",
                               "kie-datatable");
        this.dataGrid.setStriped(false);
    }

    @Override
    protected void setTableHeight() {
        final NodeList<Element> byTagName = dataGrid.getElement().getFirstChildElement().getElementsByTagName("table");
        if (byTagName.getLength() > 0) {
            final Element element = byTagName.getItem(0);
            if (element.getOffsetHeight() > 0) {
                tableHeaderOffset = element.getOffsetHeight() + 1;
            }
        }
        int base = dataGrid.getRowCount() - dataGrid.getVisibleRange().getStart();
        int height = ((base <= 0 ? 1 : base) * ROW_HEIGHT_PX) + tableHeaderOffset;
        this.dataGrid.setHeight(height + "px");
    }

    @Override
    public void onResize() {
        Scheduler.get().scheduleDeferred(() -> setTableHeight());
    }
}
