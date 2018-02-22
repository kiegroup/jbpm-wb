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
package org.jbpm.workbench.df.client.list;

import org.jbpm.workbench.df.client.filter.FilterEditorPopup;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.df.client.filter.FilterSettingsJSONMarshaller;
import org.uberfire.ext.widgets.common.client.tables.FilterPagedTable;
import org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup;
import org.uberfire.mvp.Command;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.function.BiConsumer;

@Dependent
public class DataSetEditorManager {

    public static String FILTER_TABLE_SETTINGS = "tableSettings";

    @Inject
    private FilterEditorPopup tableDisplayerEditorPopup;

    @Inject
    private FilterSettingsJSONMarshaller tableSettingsJSONMarshaller;

    public void showTableSettingsEditor(final FilterPagedTable filterPagedTable,
                                        final String popupTitle,
                                        final FilterSettings tableSettings,
                                        final Command drawCommand) {
        FilterSettings clone = tableSettings.cloneInstance();
        clone.setKey(tableSettings.getKey());
        clone.setDataSet(tableSettings.getDataSet());
        tableDisplayerEditorPopup.setTitle(popupTitle);
        tableDisplayerEditorPopup.show(clone,
                                       (FilterEditorPopup editor) -> {
                                           FilterSettings modifiedSettings = editor.getTableDisplayerSettings();
                                           HashMap<String, Object> tabSettingsValues = new HashMap<String, Object>();

                                           tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_NAME_PARAM,
                                                                 modifiedSettings.getTableName());
                                           tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_DESC_PARAM,
                                                                 modifiedSettings.getTableDescription());
                                           tabSettingsValues.put(FILTER_TABLE_SETTINGS,
                                                                 getTableSettingsToStr(modifiedSettings));

                                           filterPagedTable.saveNewTabSettings(modifiedSettings.getKey(),
                                                                               tabSettingsValues);
                                           drawCommand.execute();
                                       });
    }

    public void showTableSettingsEditor(final String popupTitle,
                                        final FilterSettings tableSettings,
                                        final BiConsumer<FilterSettings, HashMap<String, Object>> callback) {
        FilterSettings clone = tableSettings.cloneInstance();
        clone.setKey(tableSettings.getKey());
        clone.setDataSet(tableSettings.getDataSet());
        tableDisplayerEditorPopup.setTitle(popupTitle);
        tableDisplayerEditorPopup.show(clone,
                                       (FilterEditorPopup editor) -> {
                                           FilterSettings modifiedSettings = editor.getTableDisplayerSettings();
                                           HashMap<String, Object> tabSettingsValues = new HashMap<String, Object>();

                                           tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_NAME_PARAM,
                                                                 modifiedSettings.getTableName());
                                           tabSettingsValues.put(NewTabFilterPopup.FILTER_TAB_DESC_PARAM,
                                                                 modifiedSettings.getTableDescription());
                                           tabSettingsValues.put(FILTER_TABLE_SETTINGS,
                                                                 getTableSettingsToStr(modifiedSettings));

                                           callback.accept(modifiedSettings, tabSettingsValues);
                                       });
    }

    public String getTableSettingsToStr(FilterSettings tableSettings) {
        return tableSettingsJSONMarshaller.toJsonString(tableSettings);
    }

    public FilterSettings getStrToTableSettings(String json) {
        return tableSettingsJSONMarshaller.fromJsonString(json);
    }
}
