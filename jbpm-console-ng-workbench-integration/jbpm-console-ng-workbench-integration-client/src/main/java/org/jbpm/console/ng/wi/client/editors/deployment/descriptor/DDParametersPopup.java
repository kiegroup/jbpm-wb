package org.jbpm.console.ng.wi.client.editors.deployment.descriptor;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonCell;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;
import org.uberfire.client.workbench.widgets.common.Modal;
import com.google.gwt.user.client.Command;
import org.jbpm.console.ng.wi.client.i18n.Constants;
import org.jbpm.console.ng.wi.dd.model.ItemObjectModel;
import org.jbpm.console.ng.wi.dd.model.Parameter;

public class DDParametersPopup  extends Modal {

    interface DDParametersPopupBinder
            extends
            UiBinder<Widget, DDParametersPopup> {

    }

    private static DDParametersPopupBinder uiBinder = GWT.create(DDParametersPopupBinder.class);

    @UiField
    ControlGroup parametersGroup;

    @UiField
    CellTable<Parameter> parametersTable;

    @UiField
    HelpInline parametersHelpInline;

    @UiField
    Button addParameterButton;

    @UiField
    ControlLabel parametersLabel;

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

    private final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons( okCommand, cancelCommand);

    public DDParametersPopup() {
        setTitle( Constants.INSTANCE.DDParametersPopupTitle() );
        setBackdrop( BackdropType.STATIC );
        setKeyboard( true );
        setAnimation( true );
        setDynamicSafe( true );

        add( uiBinder.createAndBindUi( this ) );
        configureParametersTable();
        add( footer );

    }

    public void setContent( final Command callbackCommand, ItemObjectModel model) {
        this.callbackCommand = callbackCommand;
        this.parametersLabel.setTitle(Constants.INSTANCE.DeploymentDescriptorParameters() + model.getName());
        this.parametersDataProvider.getList().clear();
        if (model.getParameters() != null) {
            this.parametersDataProvider.setList(model.getParameters());
        }
        this.parametersDataProvider.refresh();
    }

    public List<Parameter> getContent() {

        return new ArrayList<Parameter>(parametersDataProvider.getList());
    }

    @UiHandler("addParameterButton")
    public void onClickAddParameterButton( final ClickEvent event ) {
        parametersDataProvider.getList().add(new Parameter("java.lang.String", ""));
    }

    private void configureParametersTable() {
        //Setup table
        parametersTable.setStriped( true );
        parametersTable.setCondensed(true);
        parametersTable.setBordered(true);
        parametersTable.setEmptyTableWidget( new Label( Constants.INSTANCE.NoDataDefined() ) );

        //Columns
        final Column<Parameter, String> typeColumn = new Column<Parameter, String>(new EditTextCell()) {

            @Override
            public String getValue( final Parameter item ) {
                return item.getType();
            }
        };
        typeColumn.setFieldUpdater(new FieldUpdater<Parameter, String>() {
            @Override
            public void update(int index, Parameter object, String value) {
                if (value.equals("")){
                    return;
                }
                object.setType(value);
            }
        });

        final Column<Parameter, String> valueColumn = new Column<Parameter, String>(new EditTextCell()) {

            @Override
            public String getValue( final Parameter item ) {
                return item.getValue();
            }
        };
        valueColumn.setFieldUpdater(new FieldUpdater<Parameter, String>() {
            @Override
            public void update(int index, Parameter object, String value) {
                if (value.equals("")){
                    return;
                }
                object.setValue(value);
            }
        });

        final ButtonCell deleteMSButton = new ButtonCell( ButtonSize.SMALL );
        deleteMSButton.setType(ButtonType.DANGER);
        deleteMSButton.setIcon(IconType.MINUS_SIGN);
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

                if ( Window.confirm(Constants.INSTANCE.PromptForRemoval()) ) {
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
