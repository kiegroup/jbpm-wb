package org.jbpm.console.ng.bd.client.editors.deployment.descriptor;

import java.util.ArrayList;
import java.util.Arrays;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonCell;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jbpm.console.ng.bd.client.i18n.Constants;
import org.jbpm.console.ng.bd.dd.model.DeploymentDescriptorModel;
import org.jbpm.console.ng.bd.dd.model.ItemObjectModel;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

public class DeploymentDescriptorViewImpl extends KieEditorViewImpl implements DeploymentDescriptorView {

    interface DeploymentDescriptorBinder
            extends
            UiBinder<Widget, DeploymentDescriptorViewImpl> {

    }

    private static DeploymentDescriptorBinder uiBinder = GWT.create(DeploymentDescriptorBinder.class);

    @Inject
    private PlaceManager placeManager;

    @Inject
    private DDParametersPopup ddParametersPopup;

    @UiField
    ControlGroup persistenceUnitGroup;

    @UiField
    TextBox persistenceUnitTextBox;

    @UiField
    HelpInline persistenceUnitHelpInline;

    @UiField
    ControlGroup auditPersistenceUnitGroup;

    @UiField
    TextBox auditPersistenceUnitTextBox;

    @UiField
    HelpInline auditPersistenceUnitHelpInline;

    @UiField
    ControlGroup persistenceModeGroup;

    @UiField
    HelpInline persistenceModeHelpInline;

    @UiField
    ListBox persistenceModeDropdown;

    @UiField
    ControlGroup auditModeGroup;

    @UiField
    HelpInline auditModeHelpInline;

    @UiField
    ListBox auditModeDropdown;

    @UiField
    ControlGroup runtimeStrategyGroup;

    @UiField
    HelpInline runtimeStrategyHelpInline;

    @UiField
    ListBox runtimeStrategyDropdown;

    // tables for items
    @UiField
    ControlGroup marshalStrategyGroup;

    @UiField
    Button addMarshalStrategyButton;

    @UiField
    HelpInline marshalStrategyHelpInline;

    @UiField(provided = true)
    CellTable<ItemObjectModel> marshalStrategyTable = new CellTable<ItemObjectModel>();

    private ListDataProvider<ItemObjectModel> marshalStrategyDataProvider = new ListDataProvider<ItemObjectModel>();

    // event listeners
    @UiField
    ControlGroup eventListenersGroup;

    @UiField
    Button addEventListenersButton;

    @UiField
    HelpInline eventListenersHelpInline;

    @UiField(provided = true)
    CellTable<ItemObjectModel> eventListenersTable = new CellTable<ItemObjectModel>();

    private ListDataProvider<ItemObjectModel> eventListenersDataProvider = new ListDataProvider<ItemObjectModel>();

    // globals
    @UiField
    ControlGroup globalsGroup;

    @UiField
    Button addGlobalsButton;

    @UiField
    HelpInline globalsHelpInline;

    @UiField(provided = true)
    CellTable<ItemObjectModel> globalsTable = new CellTable<ItemObjectModel>();

    private ListDataProvider<ItemObjectModel> globalsDataProvider = new ListDataProvider<ItemObjectModel>();


    // work item handlers
    @UiField
    ControlGroup workItemHandlersGroup;

    @UiField
    Button addWorkItemHandlersButton;

    @UiField
    HelpInline workItemHandlersHelpInline;

    @UiField(provided = true)
    CellTable<ItemObjectModel> workItemHandlersTable = new CellTable<ItemObjectModel>();

    private ListDataProvider<ItemObjectModel> workItemHandlersDataProvider = new ListDataProvider<ItemObjectModel>();

    // task event listeners
    @UiField
    ControlGroup taskEventListenersGroup;

    @UiField
    Button addTaskEventListenersButton;

    @UiField
    HelpInline taskEventListenersHelpInline;

    @UiField(provided = true)
    CellTable<ItemObjectModel> taskEventListenersTable = new CellTable<ItemObjectModel>();

    private ListDataProvider<ItemObjectModel> taskEventListenersDataProvider = new ListDataProvider<ItemObjectModel>();

    // environment entries
    @UiField
    ControlGroup environmentEntriesGroup;

    @UiField
    Button addEnvironmentEntriesButton;

    @UiField
    HelpInline environmentEntriesHelpInline;

    @UiField(provided = true)
    CellTable<ItemObjectModel> environmentEntriesTable = new CellTable<ItemObjectModel>();

    private ListDataProvider<ItemObjectModel> environmentEntriesDataProvider = new ListDataProvider<ItemObjectModel>();

    // configuration
    @UiField
    ControlGroup configurationGroup;

    @UiField
    Button addConfigurationButton;

    @UiField
    HelpInline configurationHelpInline;

    @UiField(provided = true)
    CellTable<ItemObjectModel> configurationTable = new CellTable<ItemObjectModel>();

    private ListDataProvider<ItemObjectModel> configurationDataProvider = new ListDataProvider<ItemObjectModel>();

    // required roles
    @UiField
    ControlGroup requiredRolesGroup;

    @UiField
    Button addRequiredRolesButton;

    @UiField
    HelpInline requiredRolesHelpInline;

    @UiField(provided = true)
    CellTable<String> requiredRolesTable = new CellTable<String>();

    private ListDataProvider<String> requiredRolesDataProvider = new ListDataProvider<String>();

    // remoteable classes
    @UiField
    ControlGroup remoteableClassesGroup;

    @UiField
    Button addRemoteableClassesButton;

    @UiField
    HelpInline remoteableClassesHelpInline;

    @UiField(provided = true)
    CellTable<String> remoteableClassesTable = new CellTable<String>();

    private ListDataProvider<String> remoteableClassesDataProvider = new ListDataProvider<String>();


    public DeploymentDescriptorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
        setup();
    }

    private void setup() {
        persistenceModeDropdown.addItem("NONE", "NONE");
        persistenceModeDropdown.addItem("JPA", "JPA");

        auditModeDropdown.addItem("NONE", "NONE");
        auditModeDropdown.addItem("JPA", "JPA");
        auditModeDropdown.addItem("JMS", "JMS");

        runtimeStrategyDropdown.addItem("SINGLETON", "SINGLETON");
        runtimeStrategyDropdown.addItem("PER_REQUEST", "PER_REQUEST");
        runtimeStrategyDropdown.addItem("PER_PROCESS_INSTANCE", "PER_PROCESS_INSTANCE");

        configureMarshalingTable();

        configureEventListenersTable();

        configureGlobalsTable();

        configureWorkItemHandlersTable();

        configureTaskEventListenersTable();

        configureEnvironmentEntriesTable();

        configureConfigurationTable();

        configureRequiredRolesTable();

        configureRemoteableClassesTable();
    }

    @Override
    public void setContent(DeploymentDescriptorModel deploymentDescriptorModel) {
        persistenceUnitTextBox.setText(deploymentDescriptorModel.getPersistenceUnitName());
        auditPersistenceUnitTextBox.setText(deploymentDescriptorModel.getAuditPersistenceUnitName());
        persistenceModeDropdown.setSelectedValue(deploymentDescriptorModel.getPersistenceMode());
        auditModeDropdown.setSelectedValue(deploymentDescriptorModel.getAuditMode());
        runtimeStrategyDropdown.setSelectedValue(deploymentDescriptorModel.getRuntimeStrategy());

        if (deploymentDescriptorModel.getMarshallingStrategies() != null) {
            marshalStrategyDataProvider.setList(deploymentDescriptorModel.getMarshallingStrategies());
        }

        if (deploymentDescriptorModel.getEventListeners() != null) {
            eventListenersDataProvider.setList(deploymentDescriptorModel.getEventListeners());
        }

        if (deploymentDescriptorModel.getGlobals() != null) {
            globalsDataProvider.setList(deploymentDescriptorModel.getGlobals());
        }

        if (deploymentDescriptorModel.getWorkItemHandlers() != null) {
            workItemHandlersDataProvider.setList(deploymentDescriptorModel.getWorkItemHandlers());
        }

        if (deploymentDescriptorModel.getTaskEventListeners() != null) {
            taskEventListenersDataProvider.setList(deploymentDescriptorModel.getTaskEventListeners());
        }

        if (deploymentDescriptorModel.getEnvironmentEntries() != null) {
            environmentEntriesDataProvider.setList(deploymentDescriptorModel.getEnvironmentEntries());
        }

        if (deploymentDescriptorModel.getConfiguration() != null) {
            configurationDataProvider.setList(deploymentDescriptorModel.getConfiguration());
        }

        if (deploymentDescriptorModel.getRequiredRoles() != null) {
            requiredRolesDataProvider.setList(deploymentDescriptorModel.getRequiredRoles());
        }

        if (deploymentDescriptorModel.getRemotableClasses() != null) {
            remoteableClassesDataProvider.setList(deploymentDescriptorModel.getRemotableClasses());
        }
    }

    @Override
    public void updateContent(DeploymentDescriptorModel deploymentDescriptorModel) {
        deploymentDescriptorModel.setAuditPersistenceUnitName(auditPersistenceUnitTextBox.getText());
        deploymentDescriptorModel.setPersistenceUnitName(persistenceUnitTextBox.getText());

        int pmSelected = persistenceModeDropdown.getSelectedIndex();
        deploymentDescriptorModel.setPersistenceMode(persistenceModeDropdown.getItemText(pmSelected));

        int amSelected = auditModeDropdown.getSelectedIndex();
        deploymentDescriptorModel.setAuditMode(auditModeDropdown.getItemText(amSelected));

        int rsSelected = runtimeStrategyDropdown.getSelectedIndex();
        deploymentDescriptorModel.setRuntimeStrategy(runtimeStrategyDropdown.getItemText(rsSelected));

        deploymentDescriptorModel.setMarshallingStrategies(marshalStrategyDataProvider.getList());

        deploymentDescriptorModel.setEventListeners(eventListenersDataProvider.getList());

        deploymentDescriptorModel.setGlobals(globalsDataProvider.getList());

        deploymentDescriptorModel.setWorkItemHandlers(workItemHandlersDataProvider.getList());

        deploymentDescriptorModel.setTaskEventListeners(taskEventListenersDataProvider.getList());

        deploymentDescriptorModel.setEnvironmentEntries(environmentEntriesDataProvider.getList());

        deploymentDescriptorModel.setConfiguration(configurationDataProvider.getList());

        deploymentDescriptorModel.setRequiredRoles(requiredRolesDataProvider.getList());

        deploymentDescriptorModel.setRemotableClasses(remoteableClassesDataProvider.getList());
    }

    @Override
    public boolean confirmClose() {
        return Window.confirm(CommonConstants.INSTANCE.DiscardUnsavedData());
    }



    private Column<ItemObjectModel, String> setUpResolverColumn() {
        ArrayList<String> options = new ArrayList<String>();
        options.add( "----" );
        options.add( ItemObjectModel.REFLECTION_RESOLVER );
        options.add( ItemObjectModel.MVEL_RESOLVER );

        Column<ItemObjectModel, String> column = new Column<ItemObjectModel, String>( new SelectionCell( options ) ) {
            @Override
            public String getValue( ItemObjectModel input ) {
                if ( input.getResolver() == null ) {
                    return "----";
                } else {
                    return input.getResolver();
                }
            }
        };

        column.setFieldUpdater( new FieldUpdater<ItemObjectModel, String>() {
            @Override
            public void update( int index,
                    ItemObjectModel model,
                    String value ) {
               model.setResolver(value);
            }
        } );

        return column;
    }


    @UiHandler("addMarshalStrategyButton")
    public void onClickAddMarshalStrategyButton( final ClickEvent event ) {
        marshalStrategyDataProvider.getList().add(new ItemObjectModel("", "enter value", "enter resolver type", null));
    }

    private void configureMarshalingTable() {
        //Setup table
        marshalStrategyTable.setStriped( true );
        marshalStrategyTable.setCondensed(true);
        marshalStrategyTable.setBordered(true);
        marshalStrategyTable.setEmptyTableWidget( new Label( Constants.INSTANCE.NoDataDefined() ) );

        //Columns
        final Column<ItemObjectModel, String> valueColumn = new Column<ItemObjectModel, String>(new EditTextCell()) {

            @Override
            public String getValue( final ItemObjectModel item ) {
                return item.getValue();
            }
        };
        valueColumn.setFieldUpdater(new FieldUpdater<ItemObjectModel, String>() {
            @Override
            public void update(int index, ItemObjectModel object, String value) {
                object.setValue(value);
            }
        });


        final Column<ItemObjectModel, String> resolverColumn = setUpResolverColumn();

        final ButtonCell parametersMSButton = new ButtonCell( ButtonSize.SMALL );
        parametersMSButton.setType(ButtonType.PRIMARY);
        parametersMSButton.setIcon(IconType.ADJUST);
        final Column<ItemObjectModel, String> parametersColumn = new Column<ItemObjectModel, String>( parametersMSButton ) {
            @Override
            public String getValue( final ItemObjectModel item ) {
                if (item.getParameters() != null) {
                    return Constants.INSTANCE.DeploymentDescriptorParameters() +"("+item.getParameters().size()+")";
                } else {
                    return Constants.INSTANCE.DeploymentDescriptorParameters() +"(0)";
                }
            }
        };
        parametersColumn.setFieldUpdater( new FieldUpdater<ItemObjectModel, String>() {
            public void update( final int index,
                    final ItemObjectModel item,
                    final String value ) {
                if (ItemObjectModel.REFLECTION_RESOLVER.equals(item.getResolver())) {
                ddParametersPopup.setContent(new Command() {

                    @Override
                        public void execute() {
                            item.setParameters(ddParametersPopup.getContent());
                            marshalStrategyDataProvider.refresh();
                        }
                    }, item);
                    ddParametersPopup.show();
                } else {
                    Window.alert(Constants.INSTANCE.NoParamResolver());
                }
            }
        } );

        final ButtonCell deleteMSButton = new ButtonCell( ButtonSize.SMALL );
        deleteMSButton.setType(ButtonType.DANGER);
        deleteMSButton.setIcon(IconType.MINUS_SIGN);
        final Column<ItemObjectModel, String> deleteGlobalColumn = new Column<ItemObjectModel, String>( deleteMSButton ) {
            @Override
            public String getValue( final ItemObjectModel item ) {
                return Constants.INSTANCE.Remove();
            }
        };
        deleteGlobalColumn.setFieldUpdater( new FieldUpdater<ItemObjectModel, String>() {
            public void update( final int index,
                    final ItemObjectModel item,
                    final String value ) {

                if ( Window.confirm( Constants.INSTANCE.PromptForRemoval() ) ) {
                    marshalStrategyDataProvider.getList().remove( index );
                }
            }
        } );

        marshalStrategyTable.addColumn( valueColumn,
                new TextHeader( Constants.INSTANCE.Value() ) );
        marshalStrategyTable.addColumn( resolverColumn,
                new TextHeader( Constants.INSTANCE.Resolver() ) );
        marshalStrategyTable.addColumn( parametersColumn,
                Constants.INSTANCE.DeploymentDescriptorParameters() );
        marshalStrategyTable.addColumn( deleteGlobalColumn,
                Constants.INSTANCE.Remove() );

        //Link data
        marshalStrategyDataProvider.addDataDisplay( marshalStrategyTable );
    }


    @UiHandler("addEventListenersButton")
    public void onClickAddEventListenersButton( final ClickEvent event ) {
        eventListenersDataProvider.getList().add(new ItemObjectModel("", "enter value", "enter resolver type", null));
    }

    private void configureEventListenersTable() {
        //Setup table
        eventListenersTable.setStriped( true );
        eventListenersTable.setCondensed(true);
        eventListenersTable.setBordered(true);
        eventListenersTable.setEmptyTableWidget( new Label( Constants.INSTANCE.NoDataDefined() ) );

        //Columns
        final Column<ItemObjectModel, String> valueColumn = new Column<ItemObjectModel, String>(new EditTextCell()) {

            @Override
            public String getValue( final ItemObjectModel item ) {
                return item.getValue();
            }
        };
        valueColumn.setFieldUpdater(new FieldUpdater<ItemObjectModel, String>() {
            @Override
            public void update(int index, ItemObjectModel object, String value) {
                object.setValue(value);
            }
        });


        final Column<ItemObjectModel, String> resolverColumn = setUpResolverColumn();

        final ButtonCell parametersMSButton = new ButtonCell( ButtonSize.SMALL );
        parametersMSButton.setType(ButtonType.PRIMARY);
        parametersMSButton.setIcon(IconType.ADJUST);
        final Column<ItemObjectModel, String> parametersColumn = new Column<ItemObjectModel, String>( parametersMSButton ) {
            @Override
            public String getValue( final ItemObjectModel item ) {
                if (item.getParameters() != null) {
                    return Constants.INSTANCE.DeploymentDescriptorParameters() +"("+item.getParameters().size()+")";
                } else {
                    return Constants.INSTANCE.DeploymentDescriptorParameters() +"(0)";
                }
            }
        };
        parametersColumn.setFieldUpdater( new FieldUpdater<ItemObjectModel, String>() {
            public void update( final int index,
                    final ItemObjectModel item,
                    final String value ) {
                if (ItemObjectModel.REFLECTION_RESOLVER.equals(item.getResolver())) {
                    ddParametersPopup.setContent(new Command() {

                        @Override
                        public void execute() {
                            item.setParameters(ddParametersPopup.getContent());
                            eventListenersDataProvider.refresh();
                        }
                    }, item);
                    ddParametersPopup.show();
                } else {
                    Window.alert(Constants.INSTANCE.NoParamResolver());
                }
            }
        } );

        final ButtonCell deleteMSButton = new ButtonCell( ButtonSize.SMALL );
        deleteMSButton.setType(ButtonType.DANGER);
        deleteMSButton.setIcon(IconType.MINUS_SIGN);
        final Column<ItemObjectModel, String> deleteGlobalColumn = new Column<ItemObjectModel, String>( deleteMSButton ) {
            @Override
            public String getValue( final ItemObjectModel item ) {
                return Constants.INSTANCE.Remove();
            }
        };
        deleteGlobalColumn.setFieldUpdater( new FieldUpdater<ItemObjectModel, String>() {
            public void update( final int index,
                    final ItemObjectModel item,
                    final String value ) {

                if ( Window.confirm( Constants.INSTANCE.PromptForRemoval() ) ) {
                    eventListenersDataProvider.getList().remove( index );
                }
            }
        } );

        eventListenersTable.addColumn( valueColumn,
                new TextHeader( Constants.INSTANCE.Value() ) );
        eventListenersTable.addColumn( resolverColumn,
                new TextHeader( Constants.INSTANCE.Resolver() ) );
        eventListenersTable.addColumn( parametersColumn,
                Constants.INSTANCE.DeploymentDescriptorParameters() );
        eventListenersTable.addColumn( deleteGlobalColumn,
                Constants.INSTANCE.Remove() );

        //Link data
        eventListenersDataProvider.addDataDisplay( eventListenersTable );

    }

    @UiHandler("addGlobalsButton")
    public void onClickAddGlobalsButton( final ClickEvent event ) {
        globalsDataProvider.getList().add(new ItemObjectModel("", "enter value", "enter resolver type", null));
    }

    private void configureGlobalsTable() {
        //Setup table
        globalsTable.setStriped( true );
        globalsTable.setCondensed(true);
        globalsTable.setBordered(true);
        globalsTable.setEmptyTableWidget( new Label( Constants.INSTANCE.NoDataDefined() ) );

        //Columns

        final Column<ItemObjectModel, String> nameColumn = new Column<ItemObjectModel, String>(new EditTextCell()) {

            @Override
            public String getValue( final ItemObjectModel item ) {
                return item.getName();
            }
        };
        nameColumn.setFieldUpdater(new FieldUpdater<ItemObjectModel, String>() {
            @Override
            public void update(int index, ItemObjectModel object, String value) {
                object.setName(value);
            }
        });

        final Column<ItemObjectModel, String> valueColumn = new Column<ItemObjectModel, String>(new EditTextCell()) {

            @Override
            public String getValue( final ItemObjectModel item ) {
                return item.getValue();
            }
        };
        valueColumn.setFieldUpdater(new FieldUpdater<ItemObjectModel, String>() {
            @Override
            public void update(int index, ItemObjectModel object, String value) {
                object.setValue(value);
            }
        });


        final Column<ItemObjectModel, String> resolverColumn = setUpResolverColumn();

        final ButtonCell parametersMSButton = new ButtonCell( ButtonSize.SMALL );
        parametersMSButton.setType(ButtonType.PRIMARY);
        parametersMSButton.setIcon(IconType.ADJUST);
        final Column<ItemObjectModel, String> parametersColumn = new Column<ItemObjectModel, String>( parametersMSButton ) {
            @Override
            public String getValue( final ItemObjectModel item ) {
                if (item.getParameters() != null) {
                    return Constants.INSTANCE.DeploymentDescriptorParameters() +"("+item.getParameters().size()+")";
                } else {
                    return Constants.INSTANCE.DeploymentDescriptorParameters() +"(0)";
                }
            }
        };
        parametersColumn.setFieldUpdater( new FieldUpdater<ItemObjectModel, String>() {
            public void update( final int index,
                    final ItemObjectModel item,
                    final String value ) {
                if (ItemObjectModel.REFLECTION_RESOLVER.equals(item.getResolver())) {
                    ddParametersPopup.setContent(new Command() {

                        @Override
                        public void execute() {
                            item.setParameters(ddParametersPopup.getContent());
                            globalsDataProvider.refresh();
                        }
                    }, item);
                    ddParametersPopup.show();
                } else {
                    Window.alert(Constants.INSTANCE.NoParamResolver());
                }
            }
        } );

        final ButtonCell deleteMSButton = new ButtonCell( ButtonSize.SMALL );
        deleteMSButton.setType(ButtonType.DANGER);
        deleteMSButton.setIcon(IconType.MINUS_SIGN);
        final Column<ItemObjectModel, String> deleteGlobalColumn = new Column<ItemObjectModel, String>( deleteMSButton ) {
            @Override
            public String getValue( final ItemObjectModel item ) {
                return Constants.INSTANCE.Remove();
            }
        };
        deleteGlobalColumn.setFieldUpdater( new FieldUpdater<ItemObjectModel, String>() {
            public void update( final int index,
                    final ItemObjectModel item,
                    final String value ) {

                if ( Window.confirm( Constants.INSTANCE.PromptForRemoval() ) ) {
                    globalsDataProvider.getList().remove( index );
                }
            }
        } );

        globalsTable.addColumn( nameColumn,
                new TextHeader( Constants.INSTANCE.Value() ) );
        globalsTable.addColumn( valueColumn,
                new TextHeader( Constants.INSTANCE.Value() ) );
        globalsTable.addColumn( resolverColumn,
                new TextHeader( Constants.INSTANCE.Resolver() ) );
        globalsTable.addColumn( parametersColumn,
                Constants.INSTANCE.DeploymentDescriptorParameters() );
        globalsTable.addColumn( deleteGlobalColumn,
                Constants.INSTANCE.Remove() );

        //Link data
        globalsDataProvider.addDataDisplay( globalsTable );
    }

    @UiHandler("addWorkItemHandlersButton")
    public void onClickAddWorkItemHandlersButton( final ClickEvent event ) {
        workItemHandlersDataProvider.getList().add(new ItemObjectModel("", "enter value", "enter resolver type", null));
    }

    private void configureWorkItemHandlersTable() {
        //Setup table
        workItemHandlersTable.setStriped( true );
        workItemHandlersTable.setCondensed(true);
        workItemHandlersTable.setBordered(true);
        workItemHandlersTable.setEmptyTableWidget( new Label( Constants.INSTANCE.NoDataDefined() ) );

        //Columns

        final Column<ItemObjectModel, String> nameColumn = new Column<ItemObjectModel, String>(new EditTextCell()) {

            @Override
            public String getValue( final ItemObjectModel item ) {
                return item.getName();
            }
        };
        nameColumn.setFieldUpdater(new FieldUpdater<ItemObjectModel, String>() {
            @Override
            public void update(int index, ItemObjectModel object, String value) {
                object.setName(value);
            }
        });

        final Column<ItemObjectModel, String> valueColumn = new Column<ItemObjectModel, String>(new EditTextCell()) {

            @Override
            public String getValue( final ItemObjectModel item ) {
                return item.getValue();
            }
        };
        valueColumn.setFieldUpdater(new FieldUpdater<ItemObjectModel, String>() {
            @Override
            public void update(int index, ItemObjectModel object, String value) {
                object.setValue(value);
            }
        });


        final Column<ItemObjectModel, String> resolverColumn = setUpResolverColumn();

        final ButtonCell parametersMSButton = new ButtonCell( ButtonSize.SMALL );
        parametersMSButton.setType(ButtonType.PRIMARY);
        parametersMSButton.setIcon(IconType.ADJUST);
        final Column<ItemObjectModel, String> parametersColumn = new Column<ItemObjectModel, String>( parametersMSButton ) {
            @Override
            public String getValue( final ItemObjectModel item ) {
                if (item.getParameters() != null) {
                    return Constants.INSTANCE.DeploymentDescriptorParameters() +"("+item.getParameters().size()+")";
                } else {
                    return Constants.INSTANCE.DeploymentDescriptorParameters() +"(0)";
                }
            }
        };
        parametersColumn.setFieldUpdater( new FieldUpdater<ItemObjectModel, String>() {
            public void update( final int index,
                    final ItemObjectModel item,
                    final String value ) {
                if (ItemObjectModel.REFLECTION_RESOLVER.equals(item.getResolver())) {
                    ddParametersPopup.setContent(new Command() {

                        @Override
                        public void execute() {
                            item.setParameters(ddParametersPopup.getContent());
                            workItemHandlersDataProvider.refresh();
                        }
                    }, item);
                    ddParametersPopup.show();
                } else {
                    Window.alert(Constants.INSTANCE.NoParamResolver());
                }
            }
        } );

        final ButtonCell deleteMSButton = new ButtonCell( ButtonSize.SMALL );
        deleteMSButton.setType(ButtonType.DANGER);
        deleteMSButton.setIcon(IconType.MINUS_SIGN);
        final Column<ItemObjectModel, String> deleteGlobalColumn = new Column<ItemObjectModel, String>( deleteMSButton ) {
            @Override
            public String getValue( final ItemObjectModel item ) {
                return Constants.INSTANCE.Remove();
            }
        };
        deleteGlobalColumn.setFieldUpdater( new FieldUpdater<ItemObjectModel, String>() {
            public void update( final int index,
                    final ItemObjectModel item,
                    final String value ) {

                if ( Window.confirm( Constants.INSTANCE.PromptForRemoval() ) ) {
                    workItemHandlersDataProvider.getList().remove( index );
                }
            }
        } );

        workItemHandlersTable.addColumn( nameColumn,
                new TextHeader( Constants.INSTANCE.Value() ) );
        workItemHandlersTable.addColumn( valueColumn,
                new TextHeader( Constants.INSTANCE.Value() ) );
        workItemHandlersTable.addColumn( resolverColumn,
                new TextHeader( Constants.INSTANCE.Resolver() ) );
        workItemHandlersTable.addColumn( parametersColumn,
                Constants.INSTANCE.DeploymentDescriptorParameters() );
        workItemHandlersTable.addColumn( deleteGlobalColumn,
                Constants.INSTANCE.Remove() );

        //Link data
        workItemHandlersDataProvider.addDataDisplay( workItemHandlersTable );
    }

    @UiHandler("addTaskEventListenersButton")
    public void onClickAddTaskEventListenersButton( final ClickEvent event ) {
        taskEventListenersDataProvider.getList().add(new ItemObjectModel("", "enter value", "enter resolver type", null));
    }

    private void configureTaskEventListenersTable() {
        //Setup table
        taskEventListenersTable.setStriped( true );
        taskEventListenersTable.setCondensed(true);
        taskEventListenersTable.setBordered(true);
        taskEventListenersTable.setEmptyTableWidget( new Label( Constants.INSTANCE.NoDataDefined() ) );

        //Columns
        final Column<ItemObjectModel, String> valueColumn = new Column<ItemObjectModel, String>(new EditTextCell()) {

            @Override
            public String getValue( final ItemObjectModel item ) {
                return item.getValue();
            }
        };
        valueColumn.setFieldUpdater(new FieldUpdater<ItemObjectModel, String>() {
            @Override
            public void update(int index, ItemObjectModel object, String value) {
                object.setValue(value);
            }
        });


        final Column<ItemObjectModel, String> resolverColumn = setUpResolverColumn();

        final ButtonCell parametersMSButton = new ButtonCell( ButtonSize.SMALL );
        parametersMSButton.setType(ButtonType.PRIMARY);
        parametersMSButton.setIcon(IconType.ADJUST);
        final Column<ItemObjectModel, String> parametersColumn = new Column<ItemObjectModel, String>( parametersMSButton ) {
            @Override
            public String getValue( final ItemObjectModel item ) {
                if (item.getParameters() != null) {
                    return Constants.INSTANCE.DeploymentDescriptorParameters() +"("+item.getParameters().size()+")";
                } else {
                    return Constants.INSTANCE.DeploymentDescriptorParameters() +"(0)";
                }
            }
        };
        parametersColumn.setFieldUpdater( new FieldUpdater<ItemObjectModel, String>() {
            public void update( final int index,
                    final ItemObjectModel item,
                    final String value ) {
                if (ItemObjectModel.REFLECTION_RESOLVER.equals(item.getResolver())) {
                    ddParametersPopup.setContent(new Command() {

                        @Override
                        public void execute() {
                            item.setParameters(ddParametersPopup.getContent());
                            taskEventListenersDataProvider.refresh();
                        }
                    }, item);
                    ddParametersPopup.show();
                } else {
                    Window.alert(Constants.INSTANCE.NoParamResolver());
                }
            }
        } );

        final ButtonCell deleteMSButton = new ButtonCell( ButtonSize.SMALL );
        deleteMSButton.setType(ButtonType.DANGER);
        deleteMSButton.setIcon(IconType.MINUS_SIGN);
        final Column<ItemObjectModel, String> deleteGlobalColumn = new Column<ItemObjectModel, String>( deleteMSButton ) {
            @Override
            public String getValue( final ItemObjectModel item ) {
                return Constants.INSTANCE.Remove();
            }
        };
        deleteGlobalColumn.setFieldUpdater( new FieldUpdater<ItemObjectModel, String>() {
            public void update( final int index,
                    final ItemObjectModel item,
                    final String value ) {

                if ( Window.confirm( Constants.INSTANCE.PromptForRemoval() ) ) {
                    taskEventListenersDataProvider.getList().remove( index );
                }
            }
        } );

        taskEventListenersTable.addColumn( valueColumn,
                new TextHeader( Constants.INSTANCE.Value() ) );
        taskEventListenersTable.addColumn( resolverColumn,
                new TextHeader( Constants.INSTANCE.Resolver() ) );
        taskEventListenersTable.addColumn( parametersColumn,
                Constants.INSTANCE.DeploymentDescriptorParameters() );
        taskEventListenersTable.addColumn( deleteGlobalColumn,
                Constants.INSTANCE.Remove() );

        //Link data
        taskEventListenersDataProvider.addDataDisplay( taskEventListenersTable );

    }

    @UiHandler("addEnvironmentEntriesButton")
    public void onClickAddEnvironmentEntriesButton( final ClickEvent event ) {
        environmentEntriesDataProvider.getList().add(new ItemObjectModel("", "enter value", "enter resolver type", null));
    }

    private void configureEnvironmentEntriesTable() {
        //Setup table
        environmentEntriesTable.setStriped( true );
        environmentEntriesTable.setCondensed(true);
        environmentEntriesTable.setBordered(true);
        environmentEntriesTable.setEmptyTableWidget( new Label( Constants.INSTANCE.NoDataDefined() ) );

        //Columns

        final Column<ItemObjectModel, String> nameColumn = new Column<ItemObjectModel, String>(new EditTextCell()) {

            @Override
            public String getValue( final ItemObjectModel item ) {
                return item.getName();
            }
        };
        nameColumn.setFieldUpdater(new FieldUpdater<ItemObjectModel, String>() {
            @Override
            public void update(int index, ItemObjectModel object, String value) {
                object.setName(value);
            }
        });

        final Column<ItemObjectModel, String> valueColumn = new Column<ItemObjectModel, String>(new EditTextCell()) {

            @Override
            public String getValue( final ItemObjectModel item ) {
                return item.getValue();
            }
        };
        valueColumn.setFieldUpdater(new FieldUpdater<ItemObjectModel, String>() {
            @Override
            public void update(int index, ItemObjectModel object, String value) {
                object.setValue(value);
            }
        });


        final Column<ItemObjectModel, String> resolverColumn = setUpResolverColumn();

        final ButtonCell parametersMSButton = new ButtonCell( ButtonSize.SMALL );
        parametersMSButton.setType(ButtonType.PRIMARY);
        parametersMSButton.setIcon(IconType.ADJUST);
        final Column<ItemObjectModel, String> parametersColumn = new Column<ItemObjectModel, String>( parametersMSButton ) {
            @Override
            public String getValue( final ItemObjectModel item ) {
                if (item.getParameters() != null) {
                    return Constants.INSTANCE.DeploymentDescriptorParameters() +"("+item.getParameters().size()+")";
                } else {
                    return Constants.INSTANCE.DeploymentDescriptorParameters() +"(0)";
                }
            }
        };
        parametersColumn.setFieldUpdater( new FieldUpdater<ItemObjectModel, String>() {
            public void update( final int index,
                    final ItemObjectModel item,
                    final String value ) {
                if (ItemObjectModel.REFLECTION_RESOLVER.equals(item.getResolver())) {
                    ddParametersPopup.setContent(new Command() {

                        @Override
                        public void execute() {
                            item.setParameters(ddParametersPopup.getContent());
                            environmentEntriesDataProvider.refresh();
                        }
                    }, item);
                    ddParametersPopup.show();
                } else {
                    Window.alert(Constants.INSTANCE.NoParamResolver());
                }
            }
        } );

        final ButtonCell deleteMSButton = new ButtonCell( ButtonSize.SMALL );
        deleteMSButton.setType(ButtonType.DANGER);
        deleteMSButton.setIcon(IconType.MINUS_SIGN);
        final Column<ItemObjectModel, String> deleteGlobalColumn = new Column<ItemObjectModel, String>( deleteMSButton ) {
            @Override
            public String getValue( final ItemObjectModel item ) {
                return Constants.INSTANCE.Remove();
            }
        };
        deleteGlobalColumn.setFieldUpdater( new FieldUpdater<ItemObjectModel, String>() {
            public void update( final int index,
                    final ItemObjectModel item,
                    final String value ) {

                if ( Window.confirm( Constants.INSTANCE.PromptForRemoval() ) ) {
                    environmentEntriesDataProvider.getList().remove( index );
                }
            }
        } );

        environmentEntriesTable.addColumn( nameColumn,
                new TextHeader( Constants.INSTANCE.Value() ) );
        environmentEntriesTable.addColumn( valueColumn,
                new TextHeader( Constants.INSTANCE.Value() ) );
        environmentEntriesTable.addColumn( resolverColumn,
                new TextHeader( Constants.INSTANCE.Resolver() ) );
        environmentEntriesTable.addColumn( parametersColumn,
                Constants.INSTANCE.DeploymentDescriptorParameters() );
        environmentEntriesTable.addColumn( deleteGlobalColumn,
                Constants.INSTANCE.Remove() );

        //Link data
        environmentEntriesDataProvider.addDataDisplay(environmentEntriesTable);
    }

    @UiHandler("addConfigurationButton")
    public void onClickAddConfigurationButton( final ClickEvent event ) {
        configurationDataProvider.getList().add(new ItemObjectModel("", "enter value", "enter resolver type", null));
    }

    private void configureConfigurationTable() {
        //Setup table
        configurationTable.setStriped(true);
        configurationTable.setCondensed(true);
        configurationTable.setBordered(true);
        configurationTable.setEmptyTableWidget( new Label( Constants.INSTANCE.NoDataDefined() ) );

        //Columns

        final Column<ItemObjectModel, String> nameColumn = new Column<ItemObjectModel, String>(new EditTextCell()) {

            @Override
            public String getValue( final ItemObjectModel item ) {
                return item.getName();
            }
        };
        nameColumn.setFieldUpdater(new FieldUpdater<ItemObjectModel, String>() {
            @Override
            public void update(int index, ItemObjectModel object, String value) {
                object.setName(value);
            }
        });

        final Column<ItemObjectModel, String> valueColumn = new Column<ItemObjectModel, String>(new EditTextCell()) {

            @Override
            public String getValue( final ItemObjectModel item ) {
                return item.getValue();
            }
        };
        valueColumn.setFieldUpdater(new FieldUpdater<ItemObjectModel, String>() {
            @Override
            public void update(int index, ItemObjectModel object, String value) {
                object.setValue(value);
            }
        });


        final Column<ItemObjectModel, String> resolverColumn = setUpResolverColumn();

        final ButtonCell parametersMSButton = new ButtonCell( ButtonSize.SMALL );
        parametersMSButton.setType(ButtonType.PRIMARY);
        parametersMSButton.setIcon(IconType.ADJUST);
        final Column<ItemObjectModel, String> parametersColumn = new Column<ItemObjectModel, String>( parametersMSButton ) {
            @Override
            public String getValue( final ItemObjectModel item ) {
                if (item.getParameters() != null) {
                    return Constants.INSTANCE.DeploymentDescriptorParameters() +"("+item.getParameters().size()+")";
                } else {
                    return Constants.INSTANCE.DeploymentDescriptorParameters() +"(0)";
                }
            }
        };
        parametersColumn.setFieldUpdater( new FieldUpdater<ItemObjectModel, String>() {
            public void update( final int index,
                    final ItemObjectModel item,
                    final String value ) {
                if (ItemObjectModel.REFLECTION_RESOLVER.equals(item.getResolver())) {
                    ddParametersPopup.setContent(new Command() {

                        @Override
                        public void execute() {
                            item.setParameters(ddParametersPopup.getContent());
                            configurationDataProvider.refresh();
                        }
                    }, item);
                    ddParametersPopup.show();
                } else {
                    Window.alert(Constants.INSTANCE.NoParamResolver());
                }
            }
        } );

        final ButtonCell deleteMSButton = new ButtonCell( ButtonSize.SMALL );
        deleteMSButton.setType(ButtonType.DANGER);
        deleteMSButton.setIcon(IconType.MINUS_SIGN);
        final Column<ItemObjectModel, String> deleteGlobalColumn = new Column<ItemObjectModel, String>( deleteMSButton ) {
            @Override
            public String getValue( final ItemObjectModel item ) {
                return Constants.INSTANCE.Remove();
            }
        };
        deleteGlobalColumn.setFieldUpdater( new FieldUpdater<ItemObjectModel, String>() {
            public void update( final int index,
                    final ItemObjectModel item,
                    final String value ) {

                if ( Window.confirm( Constants.INSTANCE.PromptForRemoval() ) ) {
                    configurationDataProvider.getList().remove( index );
                }
            }
        } );

        configurationTable.addColumn( nameColumn,
                new TextHeader( Constants.INSTANCE.Value() ) );
        configurationTable.addColumn( valueColumn,
                new TextHeader( Constants.INSTANCE.Value() ) );
        configurationTable.addColumn( resolverColumn,
                new TextHeader( Constants.INSTANCE.Resolver() ) );
        configurationTable.addColumn( parametersColumn,
                Constants.INSTANCE.DeploymentDescriptorParameters() );
        configurationTable.addColumn( deleteGlobalColumn,
                Constants.INSTANCE.Remove() );

        //Link data
        configurationDataProvider.addDataDisplay(configurationTable);
    }


    @UiHandler("addRequiredRolesButton")
    public void onClickAddRolesButton( final ClickEvent event ) {
        requiredRolesDataProvider.getList().add("");
    }

    private void configureRequiredRolesTable() {
        //Setup table
        requiredRolesTable.setStriped( true );
        requiredRolesTable.setCondensed(true);
        requiredRolesTable.setBordered(true);
        requiredRolesTable.setEmptyTableWidget( new Label( Constants.INSTANCE.NoDataDefined() ) );

        //Columns
        final Column<String, String> valueColumn = new Column<String, String>(new EditTextCell()) {

            @Override
            public String getValue( final String item ) {
                return item;
            }
        };
        valueColumn.setFieldUpdater(new FieldUpdater<String, String>() {
            @Override
            public void update(int index, String object, String value) {
                requiredRolesDataProvider.getList().remove(index);
                requiredRolesDataProvider.getList().add(index, value);
            }
        });


        final ButtonCell deleteMSButton = new ButtonCell( ButtonSize.SMALL );
        deleteMSButton.setType(ButtonType.DANGER);
        deleteMSButton.setIcon(IconType.MINUS_SIGN);
        final Column<String, String> deleteGlobalColumn = new Column<String, String>( deleteMSButton ) {
            @Override
            public String getValue( final String item ) {
                return Constants.INSTANCE.Remove();
            }
        };
        deleteGlobalColumn.setFieldUpdater( new FieldUpdater<String, String>() {
            public void update( final int index,
                    final String item,
                    final String value ) {

                if ( Window.confirm( Constants.INSTANCE.PromptForRemoval() ) ) {
                    requiredRolesDataProvider.getList().remove( index );
                }
            }
        } );

        requiredRolesTable.addColumn( valueColumn,
                new TextHeader( Constants.INSTANCE.Value() ) );
        requiredRolesTable.addColumn( deleteGlobalColumn,
                Constants.INSTANCE.Remove() );

        //Link data
        requiredRolesDataProvider.addDataDisplay( requiredRolesTable );

    }

    @UiHandler("addRemoteableClassesButton")
    public void onClickAddRemoteableClassesButton( final ClickEvent event ) {
        remoteableClassesDataProvider.getList().add("");
    }

    private void configureRemoteableClassesTable() {
        //Setup table
        remoteableClassesTable.setStriped( true );
        remoteableClassesTable.setCondensed(true);
        remoteableClassesTable.setBordered(true);
        remoteableClassesTable.setEmptyTableWidget( new Label( Constants.INSTANCE.NoDataDefined() ) );

        //Columns
        final Column<String, String> valueColumn = new Column<String, String>(new EditTextCell()) {

            @Override
            public String getValue( final String item ) {
                return item;
            }
        };
        valueColumn.setFieldUpdater(new FieldUpdater<String, String>() {
            @Override
            public void update(int index, String object, String value) {
                remoteableClassesDataProvider.getList().remove(index);
                remoteableClassesDataProvider.getList().add(index, value);
            }
        });


        final ButtonCell deleteMSButton = new ButtonCell( ButtonSize.SMALL );
        deleteMSButton.setType(ButtonType.DANGER);
        deleteMSButton.setIcon(IconType.MINUS_SIGN);
        final Column<String, String> deleteGlobalColumn = new Column<String, String>( deleteMSButton ) {
            @Override
            public String getValue( final String item ) {
                return Constants.INSTANCE.Remove();
            }
        };
        deleteGlobalColumn.setFieldUpdater( new FieldUpdater<String, String>() {
            public void update( final int index,
                    final String item,
                    final String value ) {

                if ( Window.confirm( Constants.INSTANCE.PromptForRemoval() ) ) {
                    remoteableClassesDataProvider.getList().remove( index );
                }
            }
        } );

        remoteableClassesTable.addColumn( valueColumn,
                new TextHeader( Constants.INSTANCE.Value() ) );
        remoteableClassesTable.addColumn( deleteGlobalColumn,
                Constants.INSTANCE.Remove() );

        //Link data
        remoteableClassesDataProvider.addDataDisplay( remoteableClassesTable );

    }
}
