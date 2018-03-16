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
package org.jbpm.workbench.df.client.filter;

import java.util.List;
import java.util.function.Consumer;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.DataSetOpType;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.DataSetMetadataCallback;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.displayer.client.widgets.filter.DataSetFilterEditor;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jbpm.workbench.df.client.i18n.FiltersConstants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;

@Dependent
public class FilterEditorPopup extends BaseModal {

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    public TabListItem basictab;

    @UiField
    public TabListItem filtertab;

    @UiField
    public TabPane basictabPane;

    @UiField
    public TabPane filtertabPane;

    @UiField
    public FormGroup tableNameControlGroup;

    @UiField
    public TextBox tableNameText;

    @UiField
    public FormGroup tableDescControlGroup;

    @UiField
    public TextBox tableDescText;

    @UiField
    public HelpBlock errorMessages;

    @UiField
    public FormGroup errorMessagesGroup;

    protected FilterSettings filterSettings;

    @UiField
    HelpBlock tableNameHelpInline;

    @UiField
    HelpBlock tableDescHelpInline;

    @UiField(provided = true)
    DataSetFilterEditor filterEditor;

    Consumer<FilterSettings> editorListener;

    DataSetMetadata metadata;

    DataSetLookup dataSetLookup;

    DataSetClientServices dataSetClientServices;

    @Inject
    public FilterEditorPopup(DataSetClientServices dataSetClientServices,
                             DataSetFilterEditor filterEditor) {
        this.filterEditor = filterEditor;
        this.dataSetClientServices = dataSetClientServices;
        setBody(uiBinder.createAndBindUi(FilterEditorPopup.this));

        basictab.setDataTargetWidget(basictabPane);
        filtertab.setDataTargetWidget(filtertabPane);

        tableNameText.addChangeHandler((Void) -> validateForm());

        tableDescText.addChangeHandler((Void) -> validateForm());

        final GenericModalFooter footer = new GenericModalFooter();
        footer.addButton(FiltersConstants.INSTANCE.ok(),
                         () -> ok(),
                         IconType.PLUS,
                         ButtonType.PRIMARY);
        footer.addButton(FiltersConstants.INSTANCE.cancel(),
                         () -> hide(),
                         IconType.ERASER,
                         ButtonType.DEFAULT);

        add(footer);
        setWidth(500 + "px");
    }

    public void show(final FilterSettings settings,
                     final Consumer<FilterSettings> editorListener) {
        clean();
        basictab.setActive(true);
        basictabPane.setActive(true);
        filtertab.setActive(false);
        filtertabPane.setActive(false);
        basictab.showTab();

        setEditorListener(editorListener);
        filterSettings = settings;
        if (settings.getDataSet() == null && settings.getDataSetLookup() != null) {
            fetchDataSetLookup();
        }

        super.show();
    }

    protected void ok() {
        if (validateForm()) {
            this.filterSettings.setTableName(tableNameText.getValue());
            this.filterSettings.setTableDescription(tableDescText.getValue());
            filterChanged(getDatasetFilter());
            editorListener.accept(this.filterSettings);
        }
    }

    private void clean() {
        tableNameText.setValue("");
        tableDescText.setValue("");
        clearErrorMessages();
    }

    protected boolean validateForm() {
        boolean valid = true;
        clearErrorMessages();

        if (tableNameText.getText() != null && tableNameText.getText().trim().isEmpty()) {
            setTableNameError(FiltersConstants.INSTANCE.Name_must_be_defined());
            valid = false;
        }
        if (!valid) {
            errorMessages.setText(FiltersConstants.INSTANCE.Required_fields_must_be_defined());
            errorMessagesGroup.setValidationState(ValidationState.ERROR);
        }

        if (valid && (getDatasetFilter() == null || getDatasetFilter().getColumnFilterList().isEmpty())) {
            errorMessages.setText(FiltersConstants.INSTANCE.PleaseDefineAtLeastColumnFilter());
            errorMessagesGroup.setValidationState(ValidationState.ERROR);
            valid = false;
        }

        return valid;
    }

    public void setTableNameError(final String errorMessage) {
        tableNameHelpInline.setText(errorMessage);
        tableNameControlGroup.setValidationState(ValidationState.ERROR);
    }

    private void clearErrorMessages() {
        errorMessages.setText("");
        tableNameHelpInline.setText("");
        tableDescHelpInline.setText("");
        tableNameControlGroup.setValidationState(ValidationState.NONE);
        tableDescControlGroup.setValidationState(ValidationState.NONE);
    }

    public void fetchDataSetLookup() {
        try {
            String uuid = filterSettings.getDataSetLookup().getDataSetUUID();
            dataSetClientServices.fetchMetadata(uuid,
                                                new DataSetMetadataCallback() {

                                                    public void callback(DataSetMetadata metadata) {
                                                        updateDataSetLookup(metadata);
                                                    }

                                                    public void notFound() {
                                                        // Very unlikely since this data set has been selected from a list provided by the backend.
                                                        error(FiltersConstants.INSTANCE.displayer_editor_dataset_notfound(),
                                                              null);
                                                    }

                                                    @Override
                                                    public boolean onError(final ClientRuntimeError error) {
                                                        error(error);
                                                        return false;
                                                    }
                                                });
        } catch (Exception e) {
            error(FiltersConstants.INSTANCE.displayer_editor_datasetmetadata_fetcherror(),
                  e);
        }
    }

    public void error(String message,
                      Throwable e) {
        if (e != null) {
            GWT.log(message,
                    e);
        } else {
            GWT.log(message);
        }
    }

    public void error(final ClientRuntimeError error) {
        String message = error.getThrowable() != null ? error.getThrowable().getMessage() : error.getMessage().toString();
        Throwable e = error.getThrowable();
        if (e.getCause() != null) {
            e = e.getCause();
        }
        error(message,
              e);
    }

    public void updateDataSetLookup(final DataSetMetadata metadata) {

        this.dataSetLookup = filterSettings.getDataSetLookup();
        this.metadata = metadata;

        dataSetClientServices.getPublicDataSetDefs((List<DataSetDef> dataset) -> updateFilterControls());
    }

    private void updateFilterControls() {
        filterEditor.init(dataSetLookup.getFirstFilterOp(),
                          metadata);
    }

    public void filterChanged(DataSetFilter filterOp) {
        changeDataSetFilter(filterOp);
    }

    public void changeDataSetFilter(DataSetFilter filterOp) {
        filterSettings.getDataSetLookup().removeOperations(DataSetOpType.FILTER);
        if (filterOp != null) {
            filterSettings.getDataSetLookup().addOperation(0,
                                                           filterOp);
        }
    }

    public FilterSettings getFilterSettings() {
        return filterSettings;
    }

    public void setFilterSettings(FilterSettings filterSettings) {
        this.filterSettings = filterSettings;
    }

    public DataSetFilter getDatasetFilter() {
        return filterEditor.getFilter();
    }

    protected void setEditorListener(final Consumer<FilterSettings> editorListener) {
        this.editorListener = editorListener;
    }

    interface Binder extends UiBinder<Widget, FilterEditorPopup> {

    }
}

