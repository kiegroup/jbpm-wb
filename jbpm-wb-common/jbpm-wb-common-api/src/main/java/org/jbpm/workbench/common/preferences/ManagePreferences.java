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

package org.jbpm.workbench.common.preferences;

import java.util.Arrays;
import java.util.List;

import org.uberfire.preferences.shared.PropertyFormType;
import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.preferences.shared.bean.BasePreference;

@WorkbenchPreference(identifier = "ManagePreferences", bundleKey = "ManagePreferences.Label")
public class ManagePreferences implements BasePreference<ManagePreferences> {

    public static String COMPLETED_COLOR = "#C0C0C0";
    public static String COMPLETED_BORDER_COLOR = "#030303";
    public static String ACTIVE_BORDER_COLOR = "#FF0000";

    public static List<Integer> PAGINATION_OPTIONS = Arrays.asList(10, 20, 50, 100);
    public static Integer DEFAULT_PAGINATION_OPTION = 10;

    @Property(bundleKey = "ManagePreferences.ItemsPerPage", helpBundleKey = "ManagePreferences.ItemsPerPage.Help",
            formType = PropertyFormType.NATURAL_NUMBER, validators = ItemPerPageValidator.class)
    private Integer itemsPerPage;

    @Property(bundleKey = "ManagePreferences.showTaskCommentsAtWorkTab", helpBundleKey = "ManagePreferences.showTaskCommentsAtWorkTab.Help",
            formType = PropertyFormType.BOOLEAN)
    private boolean showTaskCommentsAtWorkTab;

    @Property(bundleKey = "ManagePreferences.ProcessInstanceDiagramCompletedNodeColor", helpBundleKey = "ManagePreferences.ProcessInstanceDiagramCompletedNodeColor.Help",
            formType = PropertyFormType.COLOR)
    private String processInstanceDiagramCompletedNodeColor;

    @Property(bundleKey = "ManagePreferences.ProcessInstanceDiagramCompletedNodeBorderColor",
            helpBundleKey = "ManagePreferences.ProcessInstanceDiagramCompletedNodeBorderColor.Help",
            formType = PropertyFormType.COLOR)
    private String processInstanceDiagramCompletedNodeBorderColor;

    @Property(bundleKey = "ManagePreferences.ProcessInstanceDiagramActiveNodeBorderColor", helpBundleKey = "ManagePreferences.ProcessInstanceDiagramActiveNodeBorderColor.Help",
            formType = PropertyFormType.COLOR)
    private String processInstanceDiagramActiveNodeBorderColor;

    @Override
    public ManagePreferences defaultValue(final ManagePreferences defaultValue) {
        defaultValue.itemsPerPage = DEFAULT_PAGINATION_OPTION;
        defaultValue.processInstanceDiagramCompletedNodeColor = COMPLETED_COLOR;
        defaultValue.processInstanceDiagramCompletedNodeBorderColor = COMPLETED_BORDER_COLOR;
        defaultValue.processInstanceDiagramActiveNodeBorderColor = ACTIVE_BORDER_COLOR;
        defaultValue.showTaskCommentsAtWorkTab = true;
        return defaultValue;
    }

    public Integer getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(Integer itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public String getProcessInstanceDiagramCompletedNodeColor() {
        return processInstanceDiagramCompletedNodeColor;
    }

    public void setProcessInstanceDiagramCompletedNodeColor(String processInstanceDiagramCompletedNodeColor) {
        this.processInstanceDiagramCompletedNodeColor = processInstanceDiagramCompletedNodeColor;
    }

    public String getProcessInstanceDiagramCompletedNodeBorderColor() {
        return processInstanceDiagramCompletedNodeBorderColor;
    }

    public void setProcessInstanceDiagramCompletedNodeBorderColor(String processInstanceDiagramCompletedNodeBorderColor) {
        this.processInstanceDiagramCompletedNodeBorderColor = processInstanceDiagramCompletedNodeBorderColor;
    }

    public String getProcessInstanceDiagramActiveNodeBorderColor() {
        return processInstanceDiagramActiveNodeBorderColor;
    }

    public void setProcessInstanceDiagramActiveNodeBorderColor(String processInstanceDiagramActiveNodeBorderColor) {
        this.processInstanceDiagramActiveNodeBorderColor = processInstanceDiagramActiveNodeBorderColor;
    }

    public boolean isShowTaskCommentsAtWorkTab() {
        return showTaskCommentsAtWorkTab;
    }

    public void setShowTaskCommentsAtWorkTab(boolean showTaskCommentsAtWorkTab) {
        this.showTaskCommentsAtWorkTab = showTaskCommentsAtWorkTab;
    }
}