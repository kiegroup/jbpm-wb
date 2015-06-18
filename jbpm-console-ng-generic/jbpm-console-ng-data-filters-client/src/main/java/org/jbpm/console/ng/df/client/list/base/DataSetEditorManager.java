/*
 * Copyright 2014 JBoss by Red Hat.
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
package org.jbpm.console.ng.df.client.list.base;

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.security.shared.api.identity.User;

import org.jbpm.console.ng.df.client.filter.FilterEditorPopup;
import org.jbpm.console.ng.df.client.filter.FilterSettings;
import org.jbpm.console.ng.df.client.filter.FilterSettingsJSONMarshaller;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.tables.FilterPagedTable;
import org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.HashMap;

@Dependent
public class DataSetEditorManager extends Composite  {

    public static String FILTER_TABLE_SETTINGS = "tableSettings";

    @Inject
    public User identity;

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    protected PlaceManager placeManager;

    @Inject
    FilterEditorPopup tableDisplayerEditorPopup;

    @Inject
    FilterSettingsJSONMarshaller tableSettingsJSONMarshaller;

    public void showTableSettingsEditor(final FilterPagedTable filterPagedTable ,String popupTitle, final FilterSettings tableSettings,final Command drawCommand) {
        FilterSettings clone = tableSettings.cloneInstance();
        clone.setKey( tableSettings.getKey() );
        clone.setDataSet( tableSettings.getDataSet());
        tableDisplayerEditorPopup.setTitle( popupTitle );
        tableDisplayerEditorPopup.show(clone, new FilterEditorPopup.Listener() {

            public void onClose( FilterEditorPopup editor ) {
            }

            public void onSave( FilterEditorPopup editor ) {
                FilterSettings modifiedSettings = editor.getTableDisplayerSettings();
                HashMap<String, Object> tabSettingsValues = new HashMap<String, Object>();


                tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_NAME_PARAM, modifiedSettings.getTableName() );
                tabSettingsValues.put( NewTabFilterPopup.FILTER_TAB_DESC_PARAM, modifiedSettings.getTableDescription() );
                tabSettingsValues.put( FILTER_TABLE_SETTINGS, getTableSettingsToStr( modifiedSettings ) );

                filterPagedTable.saveNewTabSettings( modifiedSettings.getKey(),tabSettingsValues );
                drawCommand.execute();
            }
        } );
    }

    public String getTableSettingsToStr(FilterSettings tableSettings){
        return tableSettingsJSONMarshaller.toJsonString( tableSettings );
    }

    public FilterSettings getStrToTableSettings(String json){
        return tableSettingsJSONMarshaller.fromJsonString( json );
    }



}
