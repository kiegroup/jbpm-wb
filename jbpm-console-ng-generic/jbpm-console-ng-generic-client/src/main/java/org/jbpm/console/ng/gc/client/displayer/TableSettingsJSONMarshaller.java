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

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.json.DisplayerSettingsJSONMarshaller;

public class TableSettingsJSONMarshaller {

    private static final String TABLE_NAME = "tableName";
    private static final String TABLE_DESCR = "tableDescription";
    private static final String EDIT_ENABLED = "tableEditEnabled";

    protected DisplayerSettingsJSONMarshaller _displayerJsonMarshaller = new DisplayerSettingsJSONMarshaller();

    public String toJsonString(TableSettings settings) {
        JSONObject json = _displayerJsonMarshaller.toJsonObject(settings);
        json.put(TABLE_NAME, settings.getTableName() != null ? new JSONString(settings.getTableName()) : null);
        json.put(TABLE_DESCR, settings.getTableDescription() != null ? new JSONString(settings.getTableDescription()) : null);
        json.put(EDIT_ENABLED, new JSONString(Boolean.toString(settings.isEditable())));
        return json.toString();
    }

    public TableSettings fromJsonString(String jsonString) {
        DisplayerSettings displayerSettings = _displayerJsonMarshaller.fromJsonString(jsonString);
        TableSettings tableSettings = TableSettings.cloneFrom( displayerSettings );

        JSONObject parseResult = JSONParser.parseStrict(jsonString).isObject();
        if (parseResult != null) {

            JSONValue value = parseResult.get(TABLE_NAME);
            tableSettings.setTableName(value != null && value.isString() != null ? value.isString().stringValue() : null);

            value = parseResult.get(TABLE_DESCR);
            tableSettings.setTableDescription(value != null && value.isString() != null ? value.isString().stringValue() : null);

            value = parseResult.get(EDIT_ENABLED);
            tableSettings.setEditable(value != null && value.isBoolean() != null ? value.isBoolean().booleanValue() : true);
        }

        return tableSettings;
    }
}
