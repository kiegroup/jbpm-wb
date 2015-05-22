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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.displayer.client.widgets.DataSetLookupEditor;
import org.dashbuilder.displayer.client.widgets.DisplayerEditorView;
import org.dashbuilder.displayer.client.widgets.DisplayerSettingsEditor;

import javax.enterprise.context.Dependent;

@Dependent
public class TableDisplayerEditorView  extends DisplayerEditorView
        implements TableDisplayerEditor.View {

    interface Binder extends UiBinder<Widget, TableDisplayerEditorView> {}
    private static Binder uiBinder = GWT.create(Binder.class);

    protected TableDisplayerEditor presenter;
    protected TableSettings settings;

    public TableDisplayerEditorView() {

//        initWidget( uiBinder.createAndBindUi( this ) );
    }

   /* public TableDisplayerEditorView(boolean initWidget) {
        if(initWidget) {
            initWidget( uiBinder.createAndBindUi( this ) );
        }
    }*/
    public TableDisplayerEditorView(DataSetLookupEditor lookupEditor, DisplayerSettingsEditor settingsEditor) {
        this();
        //GWT.log( "TableDisplayerEditorView. TableDisplayerEditorView(DataSetLookupEditor lookupEditor, DisplayerSettingsEditor settingsEditor)" );
        this.lookupEditor = lookupEditor;
        this.settingsEditor = settingsEditor;
    }

    @Override
    public void init(TableSettings settings, TableDisplayerEditor presenter) {
        //GWT.log("TableDisplayerEditorView.init ");

        super.init(settings, presenter);
        this.settings = settings;
        this.presenter = presenter;
        disableTypeSelection();
        gotoDataSetConf();
    }
    public void gotoTypeSelection() {
        optionsPanel.selectTab(0);

       // typeSelector.init(presenter);
       // typeSelector.select(settings.getRenderer(), settings.getType(), settings.getSubtype());
        leftPanel.clear();
        leftPanel.add(typeSelector);

        dataTablePanel.setVisible(false);
        showDisplayer();
    }
}

