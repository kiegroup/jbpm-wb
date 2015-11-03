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
package org.jbpm.console.ng.df.client.filter;


import javax.inject.Inject;

import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.json.Json;
import org.dashbuilder.json.JsonObject;
import org.dashbuilder.json.JsonString;
import org.dashbuilder.json.JsonValue;
import org.dashbuilder.displayer.json.DisplayerSettingsJSONMarshaller;


public class FilterSettingsJSONMarshaller {

    private static final String TABLE_KEY = "tableKey";
    private static final String TABLE_NAME = "tableName";
    private static final String TABLE_DESCR = "tableDescription";
    private static final String EDIT_ENABLED = "tableEditEnabled";

    @Inject
    protected DisplayerSettingsJSONMarshaller _displayerJsonMarshaller;

    public FilterSettingsJSONMarshaller() {}

    public FilterSettingsJSONMarshaller(DisplayerSettingsJSONMarshaller displayerSettingsJSONMarshaller){
        this._displayerJsonMarshaller =displayerSettingsJSONMarshaller;
    }
    public String toJsonString( FilterSettings settings ) {
        JsonObject json = _displayerJsonMarshaller.toJsonObject( settings );
        json.put( TABLE_KEY, settings.getKey() != null ? new JsonString( settings.getKey() ) : null );
        json.put( TABLE_NAME, settings.getTableName() != null ? new JsonString( settings.getTableName() ) : null );
        json.put( TABLE_DESCR, settings.getTableDescription() != null ? new JsonString( settings.getTableDescription() ) : null );
        json.put( EDIT_ENABLED, new JsonString( Boolean.toString( settings.isEditable() ) ) );
        return json.toString();
    }

    public FilterSettings fromJsonString( String jsonString ) {
        DisplayerSettings displayerSettings = _displayerJsonMarshaller.fromJsonString(jsonString);
        FilterSettings tableSettings = FilterSettings.cloneFrom(displayerSettings);

        JsonObject parseResult = Json.parse(jsonString);

        if ( parseResult != null ) {

            JsonValue value = parseResult.get( TABLE_NAME );
            tableSettings.setTableName( value != null && value.asString() != null ? value.asString() : null );

            value = parseResult.get( TABLE_KEY );
            tableSettings.setKey( value != null && value.asString() != null ? value.asString() : null  );

            value = parseResult.get( TABLE_DESCR );
            tableSettings.setTableDescription( value != null && value.asString() != null ? value.asString() : null  );

            value = parseResult.get( EDIT_ENABLED );
            tableSettings.setEditable( value.asBoolean() );
        }

        return tableSettings;
    }
}
