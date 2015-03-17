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
package org.jbpm.console.ng.pr.client.editors.definition.details.multi;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsView;
import org.jbpm.console.ng.pr.client.editors.definition.details.ProcessDefDetailsPresenter;
import org.jbpm.console.ng.pr.client.i18n.Constants;

import static com.github.gwtbootstrap.client.ui.resources.ButtonSize.*;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.ScrollPanel;

@Dependent
public class ProcessDefDetailsMultiViewImpl extends AbstractTabbedDetailsView<ProcessDefDetailsMultiPresenter>
        implements ProcessDefDetailsMultiPresenter.ProcessDefDetailsMultiView {

    interface Binder
            extends
            UiBinder<Widget, ProcessDefDetailsMultiViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @Inject
    private ProcessDefDetailsPresenter detailsPresenter;
    private ScrollPanel spDetails = new ScrollPanel();

    @Override
    public void init( final ProcessDefDetailsMultiPresenter presenter ) {
        super.init( presenter );
        uiBinder.createAndBindUi( this );
        spDetails.add(detailsPresenter.getWidget());
        ( (HTMLPanel) tabPanel.getWidget( 0 ) ).add(  spDetails );
    }

    @Override
    public void initTabs() {
        tabPanel.addTab( "Definition Details", Constants.INSTANCE.Definition_Details() );
    }

    @Override
    public Button getCloseButton() {
        return new Button() {
            {
                setIcon( IconType.REMOVE );
                setTitle( Constants.INSTANCE.Close() );
                setSize( MINI );
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
                setSize( MINI );
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
    public IsWidget getOptionsButton() {
        return new DropdownButton( Constants.INSTANCE.Options() ) {{
            setSize( MINI );
            setRightDropdown( true );
            add( new NavLink( Constants.INSTANCE.View_Process_Instances() ) {{
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.viewProcessInstances();
                    }
                } );
            }} );

            add( new NavLink( Constants.INSTANCE.View_Process_Model() ) {{
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.goToProcessDefModelPopup();
                    }
                } );
            }} );
        }};
    }

    @Override
    public IsWidget getNewInstanceButton() {
        return new Button() {{
            setSize( MINI );
            setIcon( IconType.PLAY );
            setText( Constants.INSTANCE.New_Instance() );
            addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    presenter.createNewProcessInstance();
                }
            } );
        }};
    }

    @Override
    public void onResize() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                tabPanel.setHeight(ProcessDefDetailsMultiViewImpl.this.getParent().getOffsetHeight() - 30 + "px");
                spDetails.setHeight(ProcessDefDetailsMultiViewImpl.this.getParent().getOffsetHeight() - 30 + "px");
            }
        });
    }
}
