package org.jbpm.console.ng.pr.client.editors.instance.details.multi;

import org.gwtbootstrap3.client.shared.event.TabShowEvent;
import org.gwtbootstrap3.client.shared.event.TabShowHandler;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.jbpm.console.ng.pr.client.i18n.Constants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;

public abstract class BaseProcessInstanceDetailsMultiViewImpl extends Composite implements BaseProcessInstanceDetailsMultiPresenter.BaseProcessInstanceDetailsMultiView {

    @UiField
    public NavTabs navTabs;

    @UiField
    public TabContent tabContent;

    public TabListItem instanceDetailsTab;
    public TabPane instanceDetailsPane;

    public TabListItem documentTab;
    public TabPane documentPane;

    public BaseProcessInstanceDetailsMultiPresenter presenter;

    @Override
    public void init( final BaseProcessInstanceDetailsMultiPresenter presenter ) {
        createAndBindUi();
        this.presenter = presenter;
        initTabs();
    }

    protected abstract void createAndBindUi();

    private void initTabs() {
        {
            instanceDetailsPane = new TabPane() {

                {
                    add( presenter.getProcessIntanceView() );
                }
            };
            instanceDetailsTab = new TabListItem( Constants.INSTANCE.Process_Instance_Details() ) {

                {
                    setDataTargetWidget( instanceDetailsPane );
                    addStyleName( "uf-dropdown-tab-list-item" );
                }
            };
            tabContent.add( instanceDetailsPane );
            navTabs.add( instanceDetailsTab );
        }

        //        {
        //            processVariablesPane = new TabPane() {{
        //                add( presenter.getProcessVariablesView() );
        //            }};
        //            processVariablesTab = new TabListItem( Constants.INSTANCE.Process_Variables() ) {{
        //                setDataTargetWidget( processVariablesPane );
        //                addStyleName( "uf-dropdown-tab-list-item" );
        //            }};
        //            tabContent.add( processVariablesPane );
        //            navTabs.add( processVariablesTab );
        //            processVariablesTab.addShowHandler( new TabShowHandler() {
        //                @Override
        //                public void onShow( final TabShowEvent event ) {
        //                    presenter.variableListRefreshGrid();
        //                }
        //            } );
        //
        //        }

        {
            documentPane = new TabPane() {

                {
                    add( presenter.getDocumentView() );
                }
            };
            documentTab = new TabListItem( "Documents" ) {

                {
                    setDataTargetWidget( documentPane );
                    addStyleName( "uf-dropdown-tab-list-item" );
                }
            };
            tabContent.add( documentPane );
            navTabs.add( documentTab );
            documentTab.addShowHandler( new TabShowHandler() {

                @Override
                public void onShow( final TabShowEvent event ) {
                    presenter.documentListRefreshGrid();
                }
            } );
        }
        initSpecificTabs();
    }

    @Override
    public IsWidget getOptionsButton() {
        return new ButtonGroup() {

            {
                addStyleName( Styles.PULL_RIGHT );
                add( new Button( Constants.INSTANCE.Options() ) {

                    {
                        setSize( ButtonSize.SMALL );
                        setDataToggle( Toggle.DROPDOWN );
                    }
                } );
                add( new DropDownMenu() {

                    {
                        if ( initSpecificOptionsButton() != null ) {
                            add( initSpecificOptionsButton() );
                        }
                        add( new AnchorListItem( Constants.INSTANCE.Abort() ) {

                            {
                                addClickHandler( new ClickHandler() {

                                    @Override
                                    public void onClick( final ClickEvent clickEvent ) {
                                        presenter.abortProcessInstance();
                                    }
                                } );
                            }
                        } );
                        add( new AnchorListItem( Constants.INSTANCE.View_Process_Model() ) {

                            {
                                addClickHandler( new ClickHandler() {

                                    @Override
                                    public void onClick( final ClickEvent clickEvent ) {
                                        presenter.goToProcessInstanceModelPopup();
                                    }
                                } );
                            }
                        } );
                    }
                } );
            }
        };
    }

    @Override
    public IsWidget getRefreshButton() {
        return new Button() {

            {
                setIcon( IconType.REFRESH );
                setTitle( Constants.INSTANCE.Refresh() );
                setSize( ButtonSize.SMALL );
                addClickHandler( new ClickHandler() {

                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.refresh();
                    }
                } );
            }
        };
    }

    @Override
    public IsWidget getCloseButton() {
        return new Button() {

            {
                setIcon( IconType.REMOVE );
                setTitle( Constants.INSTANCE.Close() );
                setSize( ButtonSize.SMALL );
                addClickHandler( new ClickHandler() {

                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.closeDetails();
                    }
                } );
            }
        };
    }

    @Override
    public void selectInstanceDetailsTab() {
        instanceDetailsTab.showTab();
    }

    protected abstract AnchorListItem initSpecificOptionsButton();
    
    protected abstract void initSpecificTabs( );
}
