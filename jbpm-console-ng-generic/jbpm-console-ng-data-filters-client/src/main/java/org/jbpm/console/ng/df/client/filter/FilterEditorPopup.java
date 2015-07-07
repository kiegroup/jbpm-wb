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
package org.jbpm.console.ng.df.client.filter;

import com.github.gwtbootstrap.client.ui.*;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.DataSetOpType;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.DataSetMetadataCallback;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.df.client.i18n.FiltersConstants;
import org.jbpm.console.ng.df.client.popup.filter.DataSetFilterEditor;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;

import javax.enterprise.context.Dependent;
import java.util.List;

@Dependent
public class FilterEditorPopup extends BaseModal implements DataSetFilterEditor.Listener {

    interface Binder extends UiBinder<Widget, FilterEditorPopup> {
    }

    private static Binder uiBinder = GWT.create( Binder.class );

    public interface Listener {
        void onClose( FilterEditorPopup editor );

        void onSave( FilterEditorPopup editor );
    }

    @UiField
    public TabPanel tabPanel;

    @UiField
    public Tab basictab;

    @UiField
    public Tab filtertab;

    @UiField
    public ControlGroup tableNameControlGroup;

    @UiField
    public TextBox tableNameText;

    @UiField
    HelpInline tableNameHelpInline;

    @UiField
    public ControlGroup tableDescControlGroup;

    @UiField
    public TextBox tableDescText;

    @UiField
    HelpInline tableDescHelpInline;

    @UiField
    public HelpBlock errorMessages;

    @UiField
    public ControlGroup errorMessagesGroup;


    @UiField
    DataSetFilterEditor filterEditor;

    protected FilterSettings tableDisplayerSettings = null;

    Listener editorListener;
    DataSetMetadata metadata;
    DataSetLookup dataSetLookup;


    public FilterEditorPopup() {

        add( uiBinder.createAndBindUi( this ) );
        tableNameText.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent changeEvent ) {
                validateForm();
            }
        } );
        tableDescText.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent changeEvent ) {
                validateForm();
            }
        } );
        final GenericModalFooter footer = new GenericModalFooter();
        footer.addButton(FiltersConstants.INSTANCE.ok(),
                new Command() {
                    @Override
                    public void execute() {
                        ok();
                    }
                }, IconType.PLUS_SIGN,
                ButtonType.PRIMARY );
        footer.addButton(FiltersConstants.INSTANCE.cancel(),
                new Command() {
                    @Override
                    public void execute() {
                        cancel();
                    }
                }, IconType.PLUS_SIGN,
                ButtonType.PRIMARY );

        add( footer );
        setMaxHeigth( "550px" );
        setWidth( 400 );
    }

    public void show( FilterSettings settings, FilterEditorPopup.Listener editorListener ) {
        clean();
        tabPanel.selectTab( 0 );
        basictab.setActive( true );
        filtertab.setActive( false );

        this.editorListener = editorListener;
        tableDisplayerSettings = settings;
        if ( settings.getDataSet() == null && settings.getDataSetLookup() != null ) {
            fetchDataSetLookup();
        }

        super.show();
    }

    void cancel() {
        hide();
        editorListener.onClose( this );
    }

    void ok() {
        if ( validateForm() ) {
            hide();
            this.tableDisplayerSettings.setTableName( tableNameText.getValue() );
            this.tableDisplayerSettings.setTableDescription( tableDescText.getValue() );
            editorListener.onSave( this );

        }
    }

    private void clean() {
        tableNameText.setValue( "" );
        tableDescText.setValue( "" );
        clearErrorMessages();
    }

    private boolean validateForm() {
        boolean valid = true;
        clearErrorMessages();

        if ( tableNameText.getText() != null && tableNameText.getText().trim().length() == 0 ) {
            tableNameHelpInline.setText(FiltersConstants.INSTANCE.Name_must_be_defined() );
            tableNameControlGroup.setType( ControlGroupType.ERROR );
            valid = false;
        }
        if ( tableDescText.getText() != null && tableDescText.getText().trim().length() == 0 ) {
            tableDescHelpInline.setText(FiltersConstants.INSTANCE.Description_must_be_defined() );
            tableDescControlGroup.setType( ControlGroupType.ERROR );
            valid = false;
        }
        if(!valid) {
            errorMessages.setText(FiltersConstants.INSTANCE.Required_fields_must_be_defined() );
            errorMessagesGroup.setType( ControlGroupType.ERROR );
        }

        return valid;
    }

    private void clearErrorMessages() {
        errorMessages.setText( "" );
        tableNameHelpInline.setText( "" );
        tableDescHelpInline.setText( "" );
        tableNameControlGroup.setType( ControlGroupType.NONE );
        tableDescControlGroup.setType( ControlGroupType.NONE );
    }

    public void fetchDataSetLookup() {
        try {
            String uuid = tableDisplayerSettings.getDataSetLookup().getDataSetUUID();
            DataSetClientServices.get().fetchMetadata(uuid, new DataSetMetadataCallback() {

                public void callback( DataSetMetadata metadata ) {
                    updateDataSetLookup( null, metadata );
                }

                public void notFound() {
                    // Very unlikely since this data set has been selected from a list provided by the backend.
                    error(FiltersConstants.INSTANCE.displayer_editor_dataset_notfound(), null );
                }

                @Override
                public boolean onError( final ClientRuntimeError error ) {
                    error( error );
                    return false;
                }
            } );
        } catch ( Exception e ) {
            error(FiltersConstants.INSTANCE.displayer_editor_datasetmetadata_fetcherror(), e );
        }
    }

    public void error( String message, Throwable e ) {
        String cause = e != null ? e.getMessage() : null;

        if ( e != null ) GWT.log( message, e );
        else GWT.log( message );
    }

    public void error( final ClientRuntimeError error ) {
        String message = error.getThrowable() != null ? error.getThrowable().getMessage() : error.getMessage().toString();
        Throwable e = error.getThrowable();
        if ( e.getCause() != null ) e = e.getCause();
        error( message, e );
    }

    public void updateDataSetLookup( DataSetLookupConstraints constraints, DataSetMetadata metadata ) {

        this.dataSetLookup = tableDisplayerSettings.getDataSetLookup();
        this.metadata = metadata;

        DataSetClientServices.get().getRemoteSharedDataSetDefs(new RemoteCallback<List<DataSetDef>>() {
            public void callback( List<DataSetDef> dataSetDefs ) {
                updateFilterControls();
            }
        } );

    }

    private void updateFilterControls() {
        filterEditor.init( metadata,
                dataSetLookup.getFirstFilterOp(),
                this );
    }

    public void filterChanged( DataSetFilter filterOp ) {
        changeDataSetFilter( filterOp );
    }

    public void changeDataSetFilter( DataSetFilter filterOp ) {
        tableDisplayerSettings.getDataSetLookup().removeOperations( DataSetOpType.FILTER );
        if ( filterOp != null ) {
            tableDisplayerSettings.getDataSetLookup().addOperation( 0, filterOp );
        }


    }

    public FilterSettings getTableDisplayerSettings() {
        return tableDisplayerSettings;
    }

    public void setTableDisplayerSettings( FilterSettings tableDisplayerSettings ) {
        this.tableDisplayerSettings = tableDisplayerSettings;
    }
}

