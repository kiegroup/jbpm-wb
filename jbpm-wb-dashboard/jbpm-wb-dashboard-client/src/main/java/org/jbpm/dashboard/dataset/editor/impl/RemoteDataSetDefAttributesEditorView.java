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

package org.jbpm.dashboard.dataset.editor.impl;

import javax.enterprise.context.Dependent;

import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.dashbuilder.common.client.editor.list.DropDownEditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * <p>The KIE Server/Remote Data Set attributes editor view.</p>
 *
 */
@Dependent
public class RemoteDataSetDefAttributesEditorView extends Composite implements RemoteDataSetDefAttributesEditor.View {

    interface Binder extends UiBinder<Widget, RemoteDataSetDefAttributesEditorView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    RemoteDataSetDefAttributesEditor presenter;

    @UiField(provided = true)
    DropDownEditor.View queryTarget;
    
    @UiField(provided = true)
    DropDownEditor.View serverTemplateId;

    @UiField(provided = true)
    ValueBoxEditor.View dataSource;

    @UiField
    FlowPanel dbSQLPanel;

    @UiField(provided = true)
    ValueBoxEditor.View dbSQL;

    @Override
    public void init(final RemoteDataSetDefAttributesEditor presenter) {
        this.presenter = presenter;
    }
    
    @Override
    public void initWidgets(final DropDownEditor.View queryTarget, final DropDownEditor.View serverTemplateId, final ValueBoxEditor.View dataSource,
                            final ValueBoxEditor.View dbSQL) {
        this.queryTarget = queryTarget;
        this.serverTemplateId = serverTemplateId;
        this.dataSource = dataSource;
        this.dbSQL = dbSQL;
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

}
