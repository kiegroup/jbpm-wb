/**
 * Copyright (C) 2015 JBoss Inc
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
package org.jbpm.console.ng.gc.client.displayer;

import com.google.gwt.core.client.GWT;
import org.dashbuilder.displayer.client.widgets.DataSetLookupEditor;
import org.dashbuilder.displayer.client.widgets.DisplayerEditor;
import org.dashbuilder.displayer.client.widgets.DisplayerSettingsEditor;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;


public class TableDisplayerEditor extends DisplayerEditor {

    public interface Listener {
        void onClose( TableDisplayerEditor editor );
        void onSave( TableDisplayerEditor editor );
    }

    public interface View extends DisplayerEditor.View {
        void init( TableSettings settings, TableDisplayerEditor presenter );
    }

    private View view = null;
    private TableSettings tableSettings;

    public TableDisplayerEditor() {
        SyncBeanManager beanManager = IOC.getBeanManager();
        IOCBeanDef iocBeanDef = beanManager.lookupBean(DisplayerSettingsEditor.class);
        DisplayerSettingsEditor settingsEditor = (DisplayerSettingsEditor) iocBeanDef.getInstance();

        iocBeanDef = beanManager.lookupBean(DataSetLookupEditor.class);
        DataSetLookupEditor lookupEditor = (DataSetLookupEditor) iocBeanDef.getInstance();

        super.view = new TableDisplayerEditorView(lookupEditor, settingsEditor);
    }

    public TableSettings getTableSettings() {
        return tableSettings;
    }

    public void setTableName(String tableName){
        this.tableSettings.setTableName( tableName );
    }

    public void setTableDesc(String tableDesc){
        this.tableSettings.setTableDescription( tableDesc );
    }

    public void init(TableSettings tableSettings, final Listener listener) {
        this.tableSettings = tableSettings;
        super.init(tableSettings, new DisplayerEditor.Listener() {
            @Override public void onClose(DisplayerEditor editor) {
                listener.onClose(TableDisplayerEditor.this);
            }
            @Override public void onSave(DisplayerEditor editor) {
                listener.onSave(TableDisplayerEditor.this);
            }
        });
    }
}
