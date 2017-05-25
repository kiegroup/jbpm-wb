/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.console.ng.wi.client.editors.deployment.descriptor;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.jbpm.console.ng.wi.client.i18n.Constants;
import org.jbpm.console.ng.wi.dd.model.ItemObjectModel;
import org.jbpm.console.ng.wi.dd.model.Parameter;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

import static org.jbpm.console.ng.wi.client.editors.deployment.descriptor.DeploymentDescriptorViewImpl.PAGE_SIZE_UNLIMITED;

public class DDParametersPopup extends BaseModal {

    interface DDParametersPopupBinder
            extends
            UiBinder<Widget, DDParametersPopup> {

    }

    private static DDParametersPopupBinder uiBinder = GWT.create( DDParametersPopupBinder.class );

    @UiField
    FormGroup parametersGroup;

    @UiField(provided=true)
    CellTable<Parameter> parametersTable = new CellTable<>(PAGE_SIZE_UNLIMITED);

    @UiField
    HelpBlock parametersHelpInline;

    @UiField
    Button addParameterButton;

    @UiField
    FormLabel parametersLabel;

    private Command callbackCommand;

    private ListDataProvider<Parameter> parametersDataProvider = new ListDataProvider<Parameter>();

    private final Command okCommand = new Command() {
        @Override
        public void execute() {
            if ( callbackCommand != null ) {
                callbackCommand.execute();
            }
            hide();
        }
    };

    private final Command cancelCommand = new Command() {
        @Override
        public void execute() {
            hide();
        }
    };

    private final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons( okCommand, cancelCommand );

    public DDParametersPopup() {
        setTitle( Constants.INSTANCE.DDParametersPopupTitle() );
        setBody( uiBinder.createAndBindUi( DDParametersPopup.this ) );
        configureParametersTable();
        add( footer );

    }

    public void setContent( final Command callbackCommand,
                            ItemObjectModel model ) {
        this.callbackCommand = callbackCommand;
        this.parametersLabel.setTitle( Constants.INSTANCE.DeploymentDescriptorParameters() + model.getName() );
        this.parametersDataProvider.getList().clear();
        if ( model.getParameters() != null ) {
            this.parametersDataProvider.setList( model.getParameters() );
        }
        this.parametersDataProvider.refresh();
    }

    public List<Parameter> getContent() {

        return new ArrayList<Parameter>( parametersDataProvider.getList() );
    }

    @UiHandler("addParameterButton")
    public void onClickAddParameterButton( final ClickEvent event ) {
        parametersDataProvider.getList().add( new Parameter( "java.lang.String", "" ) );
    }

    private void configureParametersTable() {
        //Setup table
        parametersTable.setStriped( true );
        parametersTable.setCondensed( true );
        parametersTable.setBordered( true );
        parametersTable.setEmptyTableWidget( new Label( Constants.INSTANCE.NoDataDefined() ) );

        //Columns
        final Column<Parameter, String> typeColumn = new Column<Parameter, String>( new EditTextCell() ) {

            @Override
            public String getValue( final Parameter item ) {
                return item.getType();
            }
        };
        typeColumn.setFieldUpdater( new FieldUpdater<Parameter, String>() {
            @Override
            public void update( int index,
                                Parameter object,
                                String value ) {
                if ( value.equals( "" ) ) {
                    return;
                }
                object.setType( value );
            }
        } );

        final Column<Parameter, String> valueColumn = new Column<Parameter, String>( new EditTextCell() ) {

            @Override
            public String getValue( final Parameter item ) {
                return item.getValue();
            }
        };
        valueColumn.setFieldUpdater( new FieldUpdater<Parameter, String>() {
            @Override
            public void update( int index,
                                Parameter object,
                                String value ) {
                if ( value.equals( "" ) ) {
                    return;
                }
                object.setValue( value );
            }
        } );

        final ButtonCell deleteMSButton = new ButtonCell( IconType.TRASH, ButtonType.DANGER, ButtonSize.SMALL );
        final Column<Parameter, String> deleteGlobalColumn = new Column<Parameter, String>( deleteMSButton ) {
            @Override
            public String getValue( final Parameter item ) {
                return Constants.INSTANCE.Remove();
            }
        };
        deleteGlobalColumn.setFieldUpdater( new FieldUpdater<Parameter, String>() {
            public void update( final int index,
                                final Parameter item,
                                final String value ) {

                if ( Window.confirm( Constants.INSTANCE.PromptForRemoval() ) ) {
                    parametersDataProvider.getList().remove( index );
                }
            }
        } );

        parametersTable.addColumn( typeColumn, new TextHeader( Constants.INSTANCE.Type() ) );
        parametersTable.addColumn( valueColumn, new TextHeader( Constants.INSTANCE.Value() ) );
        parametersTable.addColumn( deleteGlobalColumn, Constants.INSTANCE.Remove() );

        //Link data
        parametersDataProvider.addDataDisplay( parametersTable );
    }
}
