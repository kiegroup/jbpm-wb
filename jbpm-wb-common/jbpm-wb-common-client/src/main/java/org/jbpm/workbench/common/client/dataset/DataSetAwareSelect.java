/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.common.client.dataset;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.jbpm.workbench.df.client.events.DataSetReadyEvent;
import org.jbpm.workbench.df.client.filter.FilterSettings;
import org.jbpm.workbench.ks.integration.ConsoleDataSetLookup;
import org.kie.workbench.common.workbench.client.error.DefaultWorkbenchErrorCallback;
import org.uberfire.client.views.pfly.widgets.ErrorPopup;
import org.uberfire.client.views.pfly.widgets.Select;

import static com.google.common.base.Strings.isNullOrEmpty;

@Dependent
public class DataSetAwareSelect {

    @Inject
    protected DataSetClientServices dataSetClientServices;

    @Inject
    protected ErrorPopup errorPopup;

    @Inject
    protected Select select;

    private String valueColumnId;
    private String textColumnId;
    private DataSetLookup dataSetLookup;

    @PostConstruct
    public void init(){
        select.disable();
    }

    public void setDataSetLookup(DataSetLookup dataSetLookup) {
        this.dataSetLookup = dataSetLookup;
    }

    public void setTextColumnId(String textColumnId) {
        this.textColumnId = textColumnId;
    }

    public void setValueColumnId(String valueColumnId) {
        this.valueColumnId = valueColumnId;
    }

    public void onDataSetReady(@Observes DataSetReadyEvent event) {
        final FilterSettings filterSettings = event.getFilterSettings();

        if (filterSettings.getServerTemplateId() == null || filterSettings.getServerTemplateId().isEmpty()) {
            removeOptions();
            return;
        }

        this.select.getElement().getParentElement().getElementsByTagName("button").item(0).addEventListener("click", e -> {
            if (dataSetLookup.getDataSetUUID().equals(event.getDataSetUUID())) {
                try {
                    dataSetClientServices.lookupDataSet(ConsoleDataSetLookup.fromInstance(dataSetLookup,
                                                                                          filterSettings.getServerTemplateId()),
                                                        new DataSetReadyCallback() {
                                                            @Override
                                                            public void callback(final DataSet dataSet) {
                                                                select.refresh(s -> {
                                                                    s.removeAllOptions();
                                                                    for (int i = 0; i < dataSet.getRowCount(); i++) {
                                                                        final String text = (String) dataSet.getValueAt(i,
                                                                                                                        textColumnId);
                                                                        final String value = (String) dataSet.getValueAt(i,
                                                                                                                         valueColumnId);
                                                                        if (isNullOrEmpty(text) == false && isNullOrEmpty(value) == false) {
                                                                            s.addOption(text,
                                                                                        value);
                                                                        }
                                                                    }
                                                                    if (s.getOptions().getLength() > 0) {
                                                                        s.enable();
                                                                    } else {
                                                                        s.disable();
                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void notFound() {
                                                                removeOptions();
                                                                errorPopup.showError(Constants.INSTANCE.DataSetNotFound(dataSetLookup.getDataSetUUID()));
                                                            }

                                                            @Override
                                                            public boolean onError(ClientRuntimeError error) {
                                                                removeOptions();
                                                                errorPopup.showError(Constants.INSTANCE.DataSetError(dataSetLookup.getDataSetUUID(),
                                                                                                                     error.getMessage()));
                                                                return false;
                                                            }
                                                        });
                } catch (Exception ex) {
                    new DefaultWorkbenchErrorCallback().error(ex);
                }
            }
        }, false);
        this.select.enable();
    }

    protected void removeOptions() {
        select.refresh(s -> {
            s.removeAllOptions();
            s.disable();
        });
    }

    public Select getSelect() {
        return select;
    }
}
