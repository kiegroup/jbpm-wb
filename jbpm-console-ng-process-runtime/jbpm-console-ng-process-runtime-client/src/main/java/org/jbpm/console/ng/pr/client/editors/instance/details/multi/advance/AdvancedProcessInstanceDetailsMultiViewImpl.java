package org.jbpm.console.ng.pr.client.editors.instance.details.multi.advance;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.console.ng.gc.client.experimental.details.base.DetailsTabbedPanel;
import org.jbpm.console.ng.pr.client.editors.instance.details.multi.BaseProcessInstanceDetailsMultiViewImpl;
import org.jbpm.console.ng.pr.client.editors.instance.log.RuntimeLogPresenter;
import org.jbpm.console.ng.pr.client.editors.variables.list.ProcessVariableListPresenter;
import org.jbpm.console.ng.pr.client.i18n.Constants;

import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class AdvancedProcessInstanceDetailsMultiViewImpl extends BaseProcessInstanceDetailsMultiViewImpl
        implements AdvancedProcessInstanceDetailsMultiPresenter.AdvancedProcessInstanceDetailsMultiView {

    interface AdvancedProcessInstanceDetailsMultiViewBinder extends UiBinder<Widget, AdvancedProcessInstanceDetailsMultiViewImpl> {

    }

    private static AdvancedProcessInstanceDetailsMultiViewBinder uiBinder = GWT.create( AdvancedProcessInstanceDetailsMultiViewBinder.class );

    @Inject
    private ProcessVariableListPresenter variableListPresenter;

    @Inject
    private RuntimeLogPresenter runtimeLogPresenter;

    private ScrollPanel variablesScrollPanel = new ScrollPanel();
    private ScrollPanel runtimeScrollPanel = new ScrollPanel();

    @Override
    protected void createAndBindUi() {
        uiBinder.createAndBindUi( this );

    }

    @Override
    protected void onResizeForSpecificPanel( String height ) {
        variablesScrollPanel.setHeight( height );
        runtimeScrollPanel.setHeight( height );
    }

    @Override
    protected void initSpecificOptionsButton( DropdownButton dropdownButton ) {
        dropdownButton.add( new NavLink( Constants.INSTANCE.Signal() ) {

            {
                addClickHandler( new ClickHandler() {

                    @Override
                    public void onClick( ClickEvent event ) {
                        ((AdvancedProcessInstanceDetailsMultiPresenter) presenter).signalProcessInstance();
                    }
                } );
            }
        } );
    }

    @Override
    protected void refreshSpecificGrid() {
        variableListPresenter.refreshGrid();
    }

    @Override
    protected void initSpecificTabs( DetailsTabbedPanel tabPanel ) {
        tabPanel.addTab( "Process Variables", Constants.INSTANCE.Process_Variables() );

        tabPanel.addTab( "Process Logs", Constants.INSTANCE.Logs() );
        variablesScrollPanel.add( variableListPresenter.getWidget().asWidget() );

        runtimeScrollPanel.add( runtimeLogPresenter.getWidget().asWidget() );

        ((HTMLPanel) tabPanel.getWidget( 2 )).add( variablesScrollPanel );
        ((HTMLPanel) tabPanel.getWidget( 3 )).add( runtimeScrollPanel );
    }
}
