/**
 * Copyright (C) 2015 JBoss Inc
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.df.client.filter;

import java.util.List;
import javax.enterprise.context.Dependent;

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
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jbpm.console.ng.df.client.i18n.FiltersConstants;
import org.jbpm.console.ng.df.client.popup.filter.DataSetFilterEditor;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;

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
    HelpBlock tableNameHelpInline;

    @UiField
    public FormGroup tableDescControlGroup;

    @UiField
    public TextBox tableDescText;

    @UiField
    HelpBlock tableDescHelpInline;

    @UiField
    public HelpBlock errorMessages;

    @UiField
    public FormGroup errorMessagesGroup;

    @UiField
    DataSetFilterEditor filterEditor;

    protected FilterSettings tableDisplayerSettings = null;

    Listener editorListener;
    DataSetMetadata metadata;
    DataSetLookup dataSetLookup;

    public FilterEditorPopup() {
        setBody( uiBinder.createAndBindUi( FilterEditorPopup.this ) );

        basictab.setDataTargetWidget( basictabPane );
        filtertab.setDataTargetWidget( filtertabPane );

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
        footer.addButton( FiltersConstants.INSTANCE.ok(),
                          new Command() {
                              @Override
                              public void execute() {
                                  ok();
                              }
                          }, IconType.PLUS,
                          ButtonType.PRIMARY );
        footer.addButton( FiltersConstants.INSTANCE.cancel(),
                          new Command() {
                              @Override
                              public void execute() {
                                  cancel();
                              }
                          }, IconType.ERASER,
                          ButtonType.DEFAULT );

        add( footer );
        setWidth( 500 + "px" );
    }

    public void show( FilterSettings settings,
                      FilterEditorPopup.Listener editorListener ) {
        clean();
        basictab.setActive( true );
        basictabPane.setActive( true );
        filtertab.setActive( false );
        filtertabPane.setActive( false );
        basictab.showTab();

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
            tableNameHelpInline.setText( FiltersConstants.INSTANCE.Name_must_be_defined() );
            tableNameControlGroup.setValidationState( ValidationState.ERROR );
            valid = false;
        }
        if ( !valid ) {
            errorMessages.setText( FiltersConstants.INSTANCE.Required_fields_must_be_defined() );
            errorMessagesGroup.setValidationState( ValidationState.ERROR );
        }

        return valid;
    }

    private void clearErrorMessages() {
        errorMessages.setText( "" );
        tableNameHelpInline.setText( "" );
        tableDescHelpInline.setText( "" );
        tableNameControlGroup.setValidationState( ValidationState.NONE );
        tableDescControlGroup.setValidationState( ValidationState.NONE );
    }

    public void fetchDataSetLookup() {
        try {
            String uuid = tableDisplayerSettings.getDataSetLookup().getDataSetUUID();
            DataSetClientServices.get().fetchMetadata( uuid, new DataSetMetadataCallback() {

                public void callback( DataSetMetadata metadata ) {
                    updateDataSetLookup( null, metadata );
                }

                public void notFound() {
                    // Very unlikely since this data set has been selected from a list provided by the backend.
                    error( FiltersConstants.INSTANCE.displayer_editor_dataset_notfound(), null );
                }

                @Override
                public boolean onError( final ClientRuntimeError error ) {
                    error( error );
                    return false;
                }
            } );
        } catch ( Exception e ) {
            error( FiltersConstants.INSTANCE.displayer_editor_datasetmetadata_fetcherror(), e );
        }
    }

    public void error( String message,
                       Throwable e ) {
        String cause = e != null ? e.getMessage() : null;

        if ( e != null ) {
            GWT.log( message, e );
        } else {
            GWT.log( message );
        }
    }

    public void error( final ClientRuntimeError error ) {
        String message = error.getThrowable() != null ? error.getThrowable().getMessage() : error.getMessage().toString();
        Throwable e = error.getThrowable();
        if ( e.getCause() != null ) {
            e = e.getCause();
        }
        error( message, e );
    }

    public void updateDataSetLookup( DataSetLookupConstraints constraints,
                                     DataSetMetadata metadata ) {

        this.dataSetLookup = tableDisplayerSettings.getDataSetLookup();
        this.metadata = metadata;

        DataSetClientServices.get().getRemoteSharedDataSetDefs( new RemoteCallback<List<DataSetDef>>() {
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

