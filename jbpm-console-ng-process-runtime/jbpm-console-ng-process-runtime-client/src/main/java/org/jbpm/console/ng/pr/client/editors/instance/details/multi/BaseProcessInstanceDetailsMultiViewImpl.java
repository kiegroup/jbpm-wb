package org.jbpm.console.ng.pr.client.editors.instance.details.multi;

import static com.github.gwtbootstrap.client.ui.resources.ButtonSize.MINI;

import javax.inject.Inject;

import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsView;
import org.jbpm.console.ng.gc.client.experimental.details.base.DetailsTabbedPanel;
import org.jbpm.console.ng.pr.client.editors.documents.list.ProcessDocumentListPresenter;
import org.jbpm.console.ng.pr.client.editors.instance.details.ProcessInstanceDetailsPresenter;
import org.jbpm.console.ng.pr.client.i18n.Constants;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;

public abstract class BaseProcessInstanceDetailsMultiViewImpl extends AbstractTabbedDetailsView<BaseProcessInstanceDetailsMultiPresenter>
        implements BaseProcessInstanceDetailsMultiPresenter.BaseProcessInstanceDetailsMultiView, RequiresResize {

    @Inject
    private ProcessInstanceDetailsPresenter detailsPresenter;

    @Inject
    private ProcessDocumentListPresenter documentListPresenter;

    private ScrollPanel detailsScrollPanel = new ScrollPanel();
    private ScrollPanel documentScrollPanel = new ScrollPanel();

    @Override
    public void init( BaseProcessInstanceDetailsMultiPresenter presenter ) {
        super.init( presenter );
        createAndBindUi();
    }

    protected abstract void createAndBindUi();

    @Override
    public void onResize() {
        super.onResize();
        Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                tabPanel.setHeight( BaseProcessInstanceDetailsMultiViewImpl.this.getParent().getOffsetHeight() - 30 + "px" );
                detailsScrollPanel.setHeight( BaseProcessInstanceDetailsMultiViewImpl.this.getParent().getOffsetHeight() - 30 + "px" );
                documentScrollPanel.setHeight( BaseProcessInstanceDetailsMultiViewImpl.this.getParent().getOffsetHeight() - 30 + "px" );
                onResizeForSpecificPanel( BaseProcessInstanceDetailsMultiViewImpl.this.getParent().getOffsetHeight() - 30 + "px" );
            }
        } );
    }

    protected abstract void onResizeForSpecificPanel( String height );

    @Override
    public IsWidget getOptionsButton() {
        DropdownButton dropdownButton = new DropdownButton( Constants.INSTANCE.Options() );
        dropdownButton.setSize( MINI );
        dropdownButton.setRightDropdown( true );
        dropdownButton.add( new NavLink( Constants.INSTANCE.Abort() ) {

            {
                addClickHandler( new ClickHandler() {

                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.abortProcessInstance();
                    }
                } );
            }
        } );
        dropdownButton.add( new NavLink( Constants.INSTANCE.View_Process_Model() ) {

            {
                addClickHandler( new ClickHandler() {

                    @Override
                    public void onClick( ClickEvent event ) {
                        presenter.goToProcessInstanceModelPopup();
                    }
                } );
            }
        } );
        initSpecificOptionsButton( dropdownButton );
        return dropdownButton;
    }

    protected abstract void initSpecificOptionsButton( DropdownButton dropdownButton );

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
    public IsWidget getCloseButton() {
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
    public void initTabs() {
        tabPanel.addTab( "Instance Details", Constants.INSTANCE.Process_Instance_Details() );
        tabPanel.addTab( "Documents", "Documents" );
        tabPanel.addSelectionHandler( new SelectionHandler<Integer>() {

            @Override
            public void onSelection( SelectionEvent<Integer> event ) {
                if ( event.getSelectedItem() == 2 ) {
                    refreshSpecificGrid();
                } else if ( event.getSelectedItem() == 1 ) {
                    documentListPresenter.refreshGrid();
                }
            }
        } );

        detailsScrollPanel.add( detailsPresenter.getWidget() );
        documentScrollPanel.add( documentListPresenter.getWidget().asWidget() );

        ((HTMLPanel) tabPanel.getWidget( 0 )).add( detailsScrollPanel );
        ((HTMLPanel) tabPanel.getWidget( 1 )).add( documentScrollPanel );

        initSpecificTabs( tabPanel );
    }

    protected abstract void refreshSpecificGrid();

    protected abstract void initSpecificTabs( DetailsTabbedPanel tabPanel );
}
