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

import com.github.gwtbootstrap.client.ui.*;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.jbpm.console.ng.gc.client.i18n.TableDisplayerConstants;
import org.uberfire.ext.services.shared.preferences.MultiGridPreferencesStore;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class TableDisplayerEditorPopup extends BaseModal {

    interface Binder extends UiBinder<Widget, TableDisplayerEditorPopup> {}
    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    public FlowPanel editorPanel;

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

    //@Inject
    TableDisplayerEditor editor;

    public TableDisplayerEditorPopup() {
        this( new TableDisplayerEditor() );
    }


    public TableDisplayerEditorPopup(TableDisplayerEditor editor) {
        this.editor = editor;
        add( uiBinder.createAndBindUi( this ) );
        editorPanel.add(editor.asWidget());
        final GenericModalFooter footer = new GenericModalFooter();
        footer.addButton( TableDisplayerConstants.INSTANCE.ok(),
                new Command() {
                    @Override
                    public void execute() {
                        ok();
                    }
                }, IconType.PLUS_SIGN,
                ButtonType.PRIMARY );
        footer.addButton( TableDisplayerConstants.INSTANCE.cancel(),
                new Command() {
                    @Override
                    public void execute() {
                        cancel();
                    }
                }, IconType.PLUS_SIGN,
                ButtonType.PRIMARY );

        add( footer );
        setMaxHeigth("550px");
        setWidth(950);
    }

    public void show(TableSettings settings, TableDisplayerEditor.Listener editorListener) {
        clean();
        editor= new TableDisplayerEditor();
        editorPanel.clear();
        editorPanel.add(editor.asWidget());
        editor.init( settings, editorListener );
        super.show();
    }

    void cancel() {
        hide();
        editor.close();
    }

    void ok() {
        if(validateForm()) {
            hide();
            editor.setTableName( tableNameText.getValue() );
            editor.setTableDesc( tableDescText.getValue() );
            editor.save();
        }else {
            errorMessages.setText( TableDisplayerConstants.INSTANCE.Required_fields_must_be_defined() );
            errorMessagesGroup.setType( ControlGroupType.ERROR );
        }
    }
    private void clean(){
        clearErrorMessages();
    }
    private boolean validateForm() {
        boolean valid = true;
        clearErrorMessages();

        if ( tableNameText.getText() != null && tableNameText.getText().trim().length() == 0 ) {
            tableNameHelpInline.setText( TableDisplayerConstants.INSTANCE.Name_must_be_defined() );
            tableNameControlGroup.setType( ControlGroupType.ERROR );
            valid = false;
        } else {
            tableNameControlGroup.setType( ControlGroupType.SUCCESS );
        }
        if ( tableDescText.getText() != null && tableDescText.getText().trim().length() == 0 ) {
            tableDescHelpInline.setText( TableDisplayerConstants.INSTANCE.Description_must_be_defined() );
            tableDescControlGroup.setType( ControlGroupType.ERROR );
            valid = false;
        } else {
            tableDescControlGroup.setType( ControlGroupType.SUCCESS );
        }
        return valid;
    }
    private void clearErrorMessages(){
        errorMessages.setText( "" );
        tableNameHelpInline.setText( "" );
        tableDescHelpInline.setText( "" );
        tableDescControlGroup.setType( ControlGroupType.NONE );
        tableDescControlGroup.setType( ControlGroupType.NONE );
    }

}

