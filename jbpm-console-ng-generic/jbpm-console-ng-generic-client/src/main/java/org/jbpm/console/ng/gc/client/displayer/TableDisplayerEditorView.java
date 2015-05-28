package org.jbpm.console.ng.gc.client.displayer;

/**
 * Copyright (C) 2014 JBoss Inc
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

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.client.DataSetClientServiceError;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.*;
import org.dashbuilder.displayer.client.widgets.DataSetLookupEditor;
import org.dashbuilder.displayer.client.widgets.DisplayerEditorStatus;
import org.dashbuilder.displayer.client.widgets.DisplayerError;

import javax.enterprise.context.Dependent;

@Dependent
public class TableDisplayerEditorView extends Composite
        implements TableDisplayerEditor.View {

    interface Binder extends UiBinder<Widget, TableDisplayerEditorView> {}
    private static Binder uiBinder = GWT.create(Binder.class);

    public TableDisplayerEditorView() {
        initWidget(uiBinder.createAndBindUi(this));
        dataTablePanel.getElement().setAttribute("cellpadding", "5");

    }

    public TableDisplayerEditorView(DataSetLookupEditor lookupEditor) {

        this();
        this.lookupEditor = lookupEditor;
    }

    protected TableDisplayerEditor presenter;
    protected TableSettings settings;
    protected DataSetLookupEditor lookupEditor;

    protected Displayer displayer;
    protected DisplayerError errorWidget = new DisplayerError();

    DisplayerListener displayerListener = new AbstractDisplayerListener() {
        public void onError(Displayer displayer, DataSetClientServiceError error) {
            error(error);
        }
    };

    @UiField
    public Panel leftPanel;

    @UiField
    public Panel centerPanel;

    @UiField
    public Panel dataTablePanel;

    @UiField
    public CheckBox viewAsTable;

    public void init(TableSettings settings, TableDisplayerEditor presenter) {
        this.settings = settings;
        this.presenter = presenter;
        showDisplayer();
        gotoDataSetConf();
    }


    private void saveLastTab(int tab) {
        DisplayerEditorStatus.get().saveSelectedTab(settings.getUUID(), tab);
    }


    @Override
    public void gotoDataSetConf() {
        //optionsPanel.selectTab(1);
        //saveLastTab(1);

        if (settings.getDataSet() == null && settings.getDataSetLookup() != null) {
            // Fetch before initializing the editor
            presenter.fetchDataSetLookup();
        }
        else {
            // Just init the lookup editor
            lookupEditor.init(presenter);
        }

        leftPanel.clear();
        leftPanel.add(lookupEditor);

        if (DisplayerType.TABLE.equals(settings.getType())) {
            dataTablePanel.setVisible(false);
        } else {
            dataTablePanel.setVisible(true);
        }
        showDisplayer();
    }


    @Override
    public void updateDataSetLookup(DataSetLookupConstraints constraints, DataSetMetadata metadata) {
        DataSetLookup dataSetLookup = settings.getDataSetLookup();
        lookupEditor.init(presenter, dataSetLookup, constraints, metadata);

        showDisplayer();
    }


    @Override
    public void error(String message, Throwable e) {
        String cause = e != null ? e.getMessage() : null;

        centerPanel.clear();
        centerPanel.add(errorWidget);
        errorWidget.show(message, cause);

        if (e != null) GWT.log(message, e);
        else GWT.log(message);
    }

    @Override
    public void error(final DataSetClientServiceError error) {
        String message = error.getThrowable() != null ? error.getThrowable().getMessage() : error.getMessage().toString();
        Throwable e = error.getThrowable();
        if (e.getCause() != null) e = e.getCause();
        error(message, e);
    }

    @Override
    public void close() {
        if (displayer != null) {
            displayer.close();
        }
    }

    public void showDisplayer() {
        if (displayer != null) {
            displayer.close();
        }
        try {
            if (dataTablePanel.isVisible() && viewAsTable.getValue()) {
                DisplayerSettings tableSettings = settings.cloneInstance();
                tableSettings.setTitleVisible(false);
                tableSettings.setType(DisplayerType.TABLE);
                tableSettings.setTablePageSize(8);
                tableSettings.setTableWidth(-1);
                displayer = DisplayerLocator.get().lookupDisplayer(tableSettings);
                displayer.addListener(displayerListener);
                displayer.setRefreshOn(false);
                centerPanel.clear();
                centerPanel.add(displayer);
                DisplayerHelper.draw( displayer );
            } else {
                displayer = DisplayerLocator.get().lookupDisplayer(settings);
                displayer.addListener(displayerListener);
                displayer.setRefreshOn(false);
                centerPanel.clear();
                centerPanel.add(displayer);
                DisplayerHelper.draw(displayer);
            }
        } catch (Exception e) {
            error(e.getMessage(), null);
        }
    }

    @UiHandler(value = "viewAsTable")
    public void onRawTableChecked(ClickEvent clickEvent) {
        showDisplayer();
    }
}


