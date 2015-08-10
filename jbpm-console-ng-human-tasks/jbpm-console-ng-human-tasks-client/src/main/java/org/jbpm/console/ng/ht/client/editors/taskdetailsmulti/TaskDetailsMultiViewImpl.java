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
package org.jbpm.console.ng.ht.client.editors.taskdetailsmulti;

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
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jbpm.console.ng.ht.client.i18n.Constants;

@Dependent
public class TaskDetailsMultiViewImpl extends Composite
        implements TaskDetailsMultiPresenter.TaskDetailsMultiView {

    interface Binder
            extends
            UiBinder<Widget, TaskDetailsMultiViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField
    NavTabs navTabs;

    @UiField
    TabContent tabContent;

    private TaskDetailsMultiPresenter presenter;

    private TabPane genericFormDisplayPane;
    private TabListItem genericFormDisplayTab;

    private TabPane taskDetailsPane;
    private TabListItem taskDetailsTab;

    private TabPane processContextPane;
    private TabListItem processContextTab;

    private TabPane taskAssignmentsPane;
    private TabListItem taskAssignmentsTab;

    private TabPane taskCommentsPane;
    private TabListItem taskCommentsTab;

    private TabPane taskAdminPane;
    private TabListItem taskAdminTab;

    @Override
    public void init( final TaskDetailsMultiPresenter presenter ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.presenter = presenter;
        initTabs();
    }

    private void initTabs() {
        {
            genericFormDisplayPane = new TabPane() {{
                add( presenter.getGenericFormView() );
            }};
            genericFormDisplayTab = new TabListItem( Constants.INSTANCE.Work() ) {{
                setDataTargetWidget( genericFormDisplayPane );
                addStyleName( "uf-dropdown-tab-list-item" );
            }};
            navTabs.add( genericFormDisplayTab );
            tabContent.add( genericFormDisplayPane );
        }

        {
            taskDetailsPane = new TabPane() {{
                add( presenter.getTaskDetailsView() );
            }};
            taskDetailsTab = new TabListItem( Constants.INSTANCE.Details() ) {{
                setDataTargetWidget( taskDetailsPane );
                addStyleName( "uf-dropdown-tab-list-item" );
            }};
            navTabs.add( taskDetailsTab );
            tabContent.add( taskDetailsPane );
            taskDetailsTab.addShowHandler( new TabShowHandler() {
                @Override
                public void onShow( final TabShowEvent event ) {
                    presenter.taskDetailsRefresh();
                }
            } );
        }

        {
            processContextPane = new TabPane() {{
                add( presenter.getProcessContextView() );
            }};
            processContextTab = new TabListItem( Constants.INSTANCE.Process_Context() ) {{
                setDataTargetWidget( processContextPane );
                addStyleName( "uf-dropdown-tab-list-item" );
            }};
            navTabs.add( processContextTab );
            tabContent.add( processContextPane );
            processContextTab.addShowHandler( new TabShowHandler() {
                @Override
                public void onShow( final TabShowEvent event ) {
                    presenter.taskProcessContextRefresh();
                }
            } );
        }

        {
            taskAssignmentsPane = new TabPane() {{
                add( presenter.getTaskAssignmentsView() );
            }};
            taskAssignmentsTab = new TabListItem( Constants.INSTANCE.Assignments() ) {{
                setDataTargetWidget( taskAssignmentsPane );
                addStyleName( "uf-dropdown-tab-list-item" );
            }};
            navTabs.add( taskAssignmentsTab );
            tabContent.add( taskAssignmentsPane );
            taskAssignmentsTab.addShowHandler( new TabShowHandler() {
                @Override
                public void onShow( final TabShowEvent event ) {
                    presenter.taskAssignmentsRefresh();
                }
            } );
        }

        {
            taskCommentsPane = new TabPane() {{
                add( presenter.getTaskCommentsView() );
            }};
            taskCommentsTab = new TabListItem( Constants.INSTANCE.Comments() ) {{
                setDataTargetWidget( taskCommentsPane );
                addStyleName( "uf-dropdown-tab-list-item" );
            }};
            navTabs.add( taskCommentsTab );
            tabContent.add( taskCommentsPane );
            taskCommentsTab.addShowHandler( new TabShowHandler() {
                @Override
                public void onShow( final TabShowEvent event ) {
                    presenter.taskCommentsRefresh();
                }
            } );
        }

        {
            taskAdminPane = new TabPane() {{
                add( presenter.getTaskAdminView() );
            }};
            taskAdminTab = new TabListItem( Constants.INSTANCE.Task_Admin() ) {{
                setDataTargetWidget( taskAdminPane );
                addStyleName( "uf-dropdown-tab-list-item" );
            }};
            navTabs.add( taskAdminTab );
            tabContent.add( taskAdminPane );
            taskAdminTab.addShowHandler( new TabShowHandler() {
                @Override
                public void onShow( final TabShowEvent event ) {
                    presenter.taskAdminRefresh();
                }
            } );
        }
    }

    @Override
    public void setAdminTabVisible( final boolean value ) {
        taskAdminTab.setVisible( value );
        taskAdminPane.setVisible( value );
    }

    @Override
    public void displayAllTabs() {
        for ( Widget active : navTabs ) {
            active.setVisible( true );
        }
        for ( Widget active : tabContent ) {
            active.setVisible( true );
        }
        ( (TabListItem) navTabs.getWidget( 0 ) ).showTab();
    }

    @Override
    public void displayOnlyLogTab() {
        for ( Widget active : navTabs ) {
            active.setVisible( false );
        }
        for ( Widget active : tabContent ) {
            active.setVisible( false );
        }
        taskDetailsPane.setVisible( true );
        taskDetailsTab.setVisible( true );
        taskDetailsTab.showTab();
    }

    @Override
    public IsWidget getCloseButton() {
        return new Button() {
            {
                setIcon( IconType.REMOVE );
                setTitle( Constants.INSTANCE.Close() );
                setSize( ButtonSize.EXTRA_SMALL );
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
    public IsWidget getRefreshButton() {
        return new Button() {
            {
                setIcon( IconType.REFRESH );
                setTitle( Constants.INSTANCE.Refresh() );
                setSize( ButtonSize.EXTRA_SMALL );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.refresh();
                    }
                } );
            }
        };
    }

}
