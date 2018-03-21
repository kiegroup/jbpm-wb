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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.dashbuilder.common.client.editor.list.DropDownEditor;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.jboss.errai.common.client.api.Caller;
import org.jbpm.dashboard.renderer.client.panel.i18n.DashboardConstants;
import org.jbpm.workbench.ks.integration.RemoteDataSetDef;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateList;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.uberfire.client.mvp.UberView;

import com.google.common.collect.FluentIterable;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * <p>KIE Server/Remote Data Set specific attributes editor presenter.</p>
 */
@Dependent
public class RemoteDataSetDefAttributesEditor implements IsWidget, org.jbpm.dashboard.dataset.editor.RemoteDataSetDefAttributesEditor {

    public interface View extends UberView<RemoteDataSetDefAttributesEditor> {
        /**
         * <p>Specify the views to use for each sub-editor before calling <code>initWidget</code>.</p>
         */
        void initWidgets(DropDownEditor.View queryTarget, DropDownEditor.View serverTemplateId, ValueBoxEditor.View dbSourceName,
                          ValueBoxEditor.View dbSQL);

    }

    DropDownEditor queryTarget;
    DropDownEditor serverTemplateId;
    ValueBoxEditor<String> dataSource;
    
    ValueBoxEditor<String> dbSQL;
    public View view;
    
    private Caller<SpecManagementService> specManagementService;

    @Inject
    public RemoteDataSetDefAttributesEditor(final DropDownEditor queryTarget,
                                            final DropDownEditor serverTemplateId,
                                         final ValueBoxEditor<String> dataSource,
                                         final ValueBoxEditor<String> dbSQL,
                                         final View view,
                                         final Caller<SpecManagementService> specManagementService) {
        this.queryTarget = queryTarget;
        this.serverTemplateId = serverTemplateId;
        this.dataSource = dataSource;
        this.dbSQL = dbSQL;
        this.view = view;
        
        this.specManagementService = specManagementService;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.initWidgets(queryTarget.view, serverTemplateId.view, dataSource.view, dbSQL.view);

        queryTarget.setSelectHint(DashboardConstants.INSTANCE.remote_query_target_hint());
        List<DropDownEditor.Entry> entries = Stream.of("CUSTOM",
                                                       "PROCESS",
                                                        "TASK",
                                                        "BA_TASK",
                                                        "PO_TASK",
                                                        "JOBS",
                                                        "FILTERED_PROCESS",
                                                        "FILTERED_BA_TASK",
                                                        "FILTERED_PO_TASK")
                .map(s -> queryTarget.newEntry(s, s)).collect(Collectors.toList());
        queryTarget.setEntries(entries);

        queryTarget.addHelpContent(DashboardConstants.INSTANCE.query_target(),
                                   DashboardConstants.INSTANCE.query_target_description(),
                Placement.RIGHT); //bottom placement would interfere with the dropdown
        
        serverTemplateId.setSelectHint(DashboardConstants.INSTANCE.remote_server_template_hint());
        
        specManagementService.call((ServerTemplateList serverTemplates) -> {
            onServerTemplateLoad(serverTemplates);
        }).listServerTemplates();
        
        serverTemplateId.addHelpContent(DashboardConstants.INSTANCE.server_template(),
                                   DashboardConstants.INSTANCE.server_template_description(),
                Placement.RIGHT); //bottom placement would interfere with the dropdown
        
        dataSource.addHelpContent(DashboardConstants.INSTANCE.sql_datasource(),
                                    DashboardConstants.INSTANCE.sql_datasource_description(),
                Placement.BOTTOM);
               
        dbSQL.addHelpContent(DashboardConstants.INSTANCE.sql_source(),
                             DashboardConstants.INSTANCE.sql_source_description(),
                Placement.BOTTOM);
    }
    
    private DropDownEditor.Entry toDropDownEntry(ServerTemplate st) {
        return serverTemplateId.newEntry(st.getId(), st.getId());
    }

    private void onServerTemplateLoad(ServerTemplateList list) {
        List<DropDownEditor.Entry> entries = FluentIterable.from(list.getServerTemplates()).transform(this::toDropDownEntry).toList();
        serverTemplateId.setEntries(entries);
    }


    /*************************************************************
     ** GWT EDITOR CONTRACT METHODS **
     *************************************************************/

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
    
    @Override
    public DropDownEditor queryTarget() {
        return queryTarget;
    }
    
    @Override
    public DropDownEditor serverTemplateId() {
        return serverTemplateId;
    }

    @Override
    public ValueBoxEditor<String> dataSource() {
        return dataSource;
    }

    @Override
    public ValueBoxEditor<String> dbSQL() {
        return dbSQL;
    }

    @Override
    public void flush() {

    }

    @Override
    public void onPropertyChange(final String... paths) {

    }

    @Override
    public void setDelegate(final EditorDelegate<RemoteDataSetDef> delegate) {
        // No delegation required.
    }

    @Override
    public void setValue(RemoteDataSetDef value) {

    }

   
}
