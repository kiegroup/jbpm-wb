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
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.dataset.*;
import org.dashbuilder.dataset.client.DataSetClientServiceError;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.DataSetMetadataCallback;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.GroupFunction;
import org.dashbuilder.displayer.*;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerHelper;
import org.dashbuilder.displayer.client.DisplayerLocator;
import org.dashbuilder.displayer.client.prototypes.DisplayerPrototypes;
import org.dashbuilder.displayer.client.resources.i18n.CommonConstants;
import org.dashbuilder.displayer.client.widgets.*;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class TableDisplayerEditor implements IsWidget,
        DisplayerTypeSelector.Listener,
        DataSetLookupEditor.Listener,
        DisplayerSettingsEditor.Listener {

    public interface Listener {
        void onClose(TableDisplayerEditor editor);
        void onSave(TableDisplayerEditor editor);
    }

    public interface View extends IsWidget {
        void init(TableSettings settings, TableDisplayerEditor presenter);
        void gotoDataSetConf();
        void updateDataSetLookup(DataSetLookupConstraints constraints, DataSetMetadata metadata);
        void error(String msg, Throwable e);
        void error(DataSetClientServiceError error);
        void close();
    }

    protected View view = null;
    protected Listener listener = null;
    protected TableSettings displayerSettings = null;
    protected boolean brandNewDisplayer = true;

    public TableDisplayerEditor() {
        SyncBeanManager beanManager = IOC.getBeanManager();
        /*IOCBeanDef iocBeanDef = beanManager.lookupBean(DisplayerSettingsEditor.class);
        DisplayerSettingsEditor settingsEditor = (DisplayerSettingsEditor) iocBeanDef.getInstance();

        iocBeanDef = beanManager.lookupBean(DisplayerTypeSelector.class);
        DisplayerTypeSelector typeSelector = (DisplayerTypeSelector) iocBeanDef.getInstance();
        */
        IOCBeanDef iocBeanDef = beanManager.lookupBean(DataSetLookupEditor.class);
        DataSetLookupEditor lookupEditor = (DataSetLookupEditor) iocBeanDef.getInstance();

        //this.view = new TableDisplayerEditorView( typeSelector, lookupEditor, settingsEditor);
        this.view = new TableDisplayerEditorView(  lookupEditor);
    }

    public Widget asWidget() {
        return view.asWidget();
    }

    public void initTableDisplayerEditor(TableSettings settings, Listener editorListener) {
        this.listener = editorListener;

        if (settings != null) {
            brandNewDisplayer = false;
            displayerSettings = settings;
            view.init(displayerSettings, this);
        } else {
            brandNewDisplayer = true;
            displayerSettings = new TableSettings().cloneFrom( DisplayerPrototypes.get().getProto( DisplayerType.TABLE ) );
            displayerSettings.setTitle("- " + CommonConstants.INSTANCE.displayer_editor_new() + " -");
            view.init(displayerSettings, this);

        }
    }

    public boolean isBrandNewDisplayer() {
        return brandNewDisplayer;
    }

    public View getView() {
        return view;
    }

    public DisplayerSettings getDisplayerSettings() {
        return displayerSettings;
    }

    public void save() {
        view.close();

        // Clear settings before return
        Displayer displayer = DisplayerHelper.lookupDisplayer( displayerSettings );
        DisplayerConstraints displayerConstraints = displayer.getDisplayerConstraints();
        displayerConstraints.removeUnsupportedAttributes(displayerSettings);

        if (listener != null) {
            listener.onSave(this);
        }
    }

    public void close() {
        view.close();
        if (listener != null) {
            listener.onClose(this);
        }
    }

    public void fetchDataSetLookup() {
        try {
            String uuid = displayerSettings.getDataSetLookup().getDataSetUUID();
            DataSetClientServices.get().fetchMetadata(uuid, new DataSetMetadataCallback() {

                public void callback(DataSetMetadata metadata) {
                    Displayer displayer = DisplayerLocator.get().lookupDisplayer(displayerSettings);
                    DataSetLookupConstraints constraints = displayer.getDisplayerConstraints().getDataSetLookupConstraints();
                    view.updateDataSetLookup(constraints, metadata);
                }
                public void notFound() {
                    // Very unlikely since this data set has been selected from a list provided by the backend.
                    view.error(CommonConstants.INSTANCE.displayer_editor_dataset_notfound(), null);
                }

                @Override
                public boolean onError(DataSetClientServiceError error) {
                    view.error(error);
                    return false;
                }
            });
        } catch (Exception e) {
            view.error(CommonConstants.INSTANCE.displayer_editor_datasetmetadata_fetcherror(), e);
        }
    }

    // Widget listeners callback notifications

    @Override
    public void displayerSettingsChanged(DisplayerSettings settings) {
        displayerSettings = new TableSettings().cloneFrom( settings);
        view.init(displayerSettings, this);
    }

    @Override
    public void displayerTypeChanged(DisplayerType type, DisplayerSubType displayerSubType) {
    }

    @Override
    public void dataSetChanged(final String uuid) {
        try {
            DataSetClientServices.get().fetchMetadata(uuid, new DataSetMetadataCallback() {
                public void callback(DataSetMetadata metadata) {

                    // Create a dataSetLookup instance for the target data set that fits the displayer constraints
                    Displayer displayer = DisplayerLocator.get().lookupDisplayer(displayerSettings);
                    DataSetLookupConstraints constraints = displayer.getDisplayerConstraints().getDataSetLookupConstraints();
                    DataSetLookup lookup = constraints.newDataSetLookup(metadata);
                    if (lookup == null) view.error(CommonConstants.INSTANCE.displayer_editor_dataset_nolookuprequest(), null);

                    // Make the view to show the new lookup instance
                    displayerSettings.setDataSet(null);
                    displayerSettings.setDataSetLookup(lookup);

                    removeStaleSettings();
                    view.updateDataSetLookup(constraints, metadata);
                }
                public void notFound() {
                    // Very unlikely since this data set has been selected from a list provided by the backend.
                    view.error(CommonConstants.INSTANCE.displayer_editor_dataset_notfound(), null);
                }

                @Override
                public boolean onError(DataSetClientServiceError error) {
                    view.error(error);
                    return false;
                }
            });
        } catch (Exception e) {
            view.error(CommonConstants.INSTANCE.displayer_editor_datasetmetadata_fetcherror(), e);
        }
    }

    @Override
    public void groupChanged(DataSetGroup groupOp) {
        removeStaleSettings();
        view.init(displayerSettings, this);
    }

    @Override
    public void columnChanged(GroupFunction groupFunction) {
        removeStaleSettings();
        view.init(displayerSettings, this);
    }

    @Override
    public void filterChanged(DataSetFilter filterOp) {
        view.init(displayerSettings, this);
    }

    public void removeStaleSettings() {
        List<String> columnIds = getExistingDataColumnIds();

        // Remove the settings for non existing columns
        Iterator<ColumnSettings> it = displayerSettings.getColumnSettingsList().iterator();
        while (it.hasNext()) {
            ColumnSettings columnSettings = it.next();
            if (!columnIds.contains(columnSettings.getColumnId())) {
                it.remove();
            }
        }
        // Reset table sort column
        if (!columnIds.contains(displayerSettings.getTableDefaultSortColumnId())) {
            displayerSettings.setTableDefaultSortColumnId(null);
        }
    }

    public List<String> getExistingDataColumnIds() {
        DataSet dataSet = displayerSettings.getDataSet();
        DataSetLookup dataSetLookup = displayerSettings.getDataSetLookup();

        List<String> columnIds = new ArrayList<String>();
        if (dataSet != null) {
            for (DataColumn dataColumn : dataSet.getColumns()) {
                columnIds.add(dataColumn.getId());
            }
        }
        else if (dataSetLookup != null) {
            int idx = dataSetLookup.getLastGroupOpIndex(0);
            if (idx != -1) {
                DataSetGroup groupOp = dataSetLookup.getOperation(idx);
                for (GroupFunction groupFunction : groupOp.getGroupFunctions()) {
                    columnIds.add(groupFunction.getColumnId());
                }
            }
        }
        return columnIds;
    }
    public TableSettings getTableSettings() {
        return displayerSettings;
    }

    public void setTableName(String tableName){
        this.displayerSettings.setTableName( tableName );
    }

    public void setTableDesc(String tableDesc){
        this.displayerSettings.setTableDescription( tableDesc );
    }

    public void init(TableSettings tableSettings, final Listener listener) {

        this.displayerSettings = tableSettings;
        initTableDisplayerEditor( tableSettings, new TableDisplayerEditor.Listener() {
            @Override
            public void onClose( TableDisplayerEditor editor ) {
                listener.onClose( TableDisplayerEditor.this );
            }

            @Override
            public void onSave( TableDisplayerEditor editor ) {
                listener.onSave( TableDisplayerEditor.this );
            }
        } );
    }

}

/*
        extends DisplayerEditor {

    public interface Listener {
        void onClose( TableDisplayerEditor editor );
        void onSave( TableDisplayerEditor editor );
    }

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
*/
