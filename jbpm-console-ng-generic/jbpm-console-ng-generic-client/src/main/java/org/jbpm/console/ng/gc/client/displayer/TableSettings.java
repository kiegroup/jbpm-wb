/*
 * Copyright 2015 JBoss by Red Hat.
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
package org.jbpm.console.ng.gc.client.displayer;

import org.dashbuilder.displayer.ColumnSettings;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Custom settings class holding the configuration of any jBPM table displayer
 */
public class TableSettings extends DisplayerSettings {

    protected String key;

    protected String tableName;
    protected String tableDescription;
    protected boolean editable;

    public TableSettings() {
        super(DisplayerType.TABLE);
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableDescription() {
        return tableDescription;
    }

    public void setTableDescription(String tableDescription) {
        this.tableDescription = tableDescription;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getKey() {
        return key;
    }

    public void setKey( String key ) {
        this.key = key;
    }

    public boolean equals(Object obj) {
        try {
            TableSettings other = (TableSettings ) obj;
            if (tableName == null || other.tableName == null) return false;
            if (!tableName.equals(other.tableName)) return false;
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }


    public static TableSettings cloneFrom(DisplayerSettings settings) {
        TableSettings tableSettings = new TableSettings();
        tableSettings.setType(DisplayerType.TABLE);
        tableSettings.setUUID(settings.getUUID());
        tableSettings.setDataSet(settings.getDataSet());
        tableSettings.setDataSetLookup(settings.getDataSetLookup());
        tableSettings.setColumnSettingsList(settings.getColumnSettingsList());
        tableSettings.getSettingsFlatMap().putAll(settings.getSettingsFlatMap());
        return tableSettings;
    }

    public TableSettings cloneInstance() {
        TableSettings clone = new TableSettings();
        clone.UUID = UUID;
        clone.tableName = tableName;
        clone.tableDescription = tableDescription;
        clone.settings = new HashMap(settings);
        clone.columnSettingsList = new ArrayList();
        for (ColumnSettings columnSettings : columnSettingsList) {
            clone.columnSettingsList.add(columnSettings.cloneInstance());
        }
        if (dataSet != null) clone.dataSet = dataSet.cloneInstance();
        if (dataSetLookup != null) clone.dataSetLookup = dataSetLookup.cloneInstance();
        return clone;
    }
}
