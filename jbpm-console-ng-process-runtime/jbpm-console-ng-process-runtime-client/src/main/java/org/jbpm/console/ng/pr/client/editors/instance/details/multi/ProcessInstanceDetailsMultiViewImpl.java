/*
 * Copyright 2012 JBoss Inc
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
package org.jbpm.console.ng.pr.client.editors.instance.details.multi;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
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

@Dependent
public class ProcessInstanceDetailsMultiViewImpl extends Composite
        implements ProcessInstanceDetailsMultiPresenter.ProcessInstanceDetailsMultiView {

    interface Binder
            extends
            UiBinder<Widget, ProcessInstanceDetailsMultiViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField
    NavTabs navTabs;

    @UiField
    TabContent tabContent;

    private TabListItem instanceDetailsTab;
    private TabPane instanceDetailsPane;

    private TabListItem processVariablesTab;
    private TabPane processVariablesPane;

    private TabListItem documentTab;
    private TabPane documentPane;

    private TabListItem logsTab;
    private TabPane logsPane;

    private ProcessInstanceDetailsMultiPresenter presenter;

    @Override
    public void init( final ProcessInstanceDetailsMultiPresenter presenter ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.presenter = presenter;
        initTabs();
    }

    private void initTabs() {
        {
            instanceDetailsPane = new TabPane() {{
                add( presenter.getProcessIntanceView() );
            }};
            instanceDetailsTab = new TabListItem( Constants.INSTANCE.Process_Instance_Details() ) {{
                setDataTargetWidget( instanceDetailsPane );
                addStyleName( "uf-dropdown-tab-list-item" );
            }};
            tabContent.add( instanceDetailsPane );
            navTabs.add( instanceDetailsTab );
        }

        {
            processVariablesPane = new TabPane() {{
                add( presenter.getProcessVariablesView() );
            }};
            processVariablesTab = new TabListItem( Constants.INSTANCE.Process_Variables() ) {{
                setDataTargetWidget( processVariablesPane );
                addStyleName( "uf-dropdown-tab-list-item" );
            }};
            tabContent.add( processVariablesPane );
            navTabs.add( processVariablesTab );
            processVariablesTab.addShowHandler( new TabShowHandler() {
                @Override
                public void onShow( final TabShowEvent event ) {
                    presenter.variableListRefreshGrid();
                }
            } );

        }

        {
            documentPane = new TabPane() {{
                add( presenter.getDocumentView() );
            }};
            documentTab = new TabListItem( "Documents" ) {{
                setDataTargetWidget( documentPane );
                addStyleName( "uf-dropdown-tab-list-item" );
            }};
            tabContent.add( documentPane );
            navTabs.add( documentTab );
            documentTab.addShowHandler( new TabShowHandler() {
                @Override
                public void onShow( final TabShowEvent event ) {
                    presenter.documentListRefreshGrid();
                }
            } );
        }

        {
            logsPane = new TabPane() {{
                add( presenter.getLogsView() );
            }};
            logsTab = new TabListItem( Constants.INSTANCE.Logs() ) {{
                setDataTargetWidget( logsPane );
                addStyleName( "uf-dropdown-tab-list-item" );
            }};
            tabContent.add( logsPane );
            navTabs.add( logsTab );
        }
    }

    @Override
    public IsWidget getOptionsButton() {
        return new ButtonGroup() {{
            add( new Button( Constants.INSTANCE.Options() ) {{
                setSize( ButtonSize.SMALL );
                setDataToggle( Toggle.DROPDOWN );
            }} );
            add( new DropDownMenu() {{
                addStyleName( Styles.DROPDOWN_MENU + "-right" );
                add( new AnchorListItem( Constants.INSTANCE.Signal() ) {{
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( final ClickEvent clickEvent ) {
                            presenter.signalProcessInstance();
                        }
                    } );
                }} );
                add( new AnchorListItem( Constants.INSTANCE.Abort() ) {{
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( final ClickEvent clickEvent ) {
                            presenter.abortProcessInstance();
                        }
                    } );
                }} );
                add( new AnchorListItem( Constants.INSTANCE.View_Process_Model() ) {{
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( final ClickEvent clickEvent ) {
                            presenter.goToProcessInstanceModelPopup();
                        }
                    } );
                }} );
            }} );
        }};
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

}
