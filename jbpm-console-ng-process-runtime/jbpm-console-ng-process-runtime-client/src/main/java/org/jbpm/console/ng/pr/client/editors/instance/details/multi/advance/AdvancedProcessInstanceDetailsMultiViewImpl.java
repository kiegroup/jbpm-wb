package org.jbpm.console.ng.pr.client.editors.instance.details.multi.advance;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

import org.gwtbootstrap3.client.shared.event.TabShowEvent;
import org.gwtbootstrap3.client.shared.event.TabShowHandler;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import org.jbpm.console.ng.pr.client.editors.instance.details.multi.BaseProcessInstanceDetailsMultiViewImpl;

@Dependent
public class AdvancedProcessInstanceDetailsMultiViewImpl extends BaseProcessInstanceDetailsMultiViewImpl
        implements AdvancedProcessInstanceDetailsMultiPresenter.AdvancedProcessInstanceDetailsMultiView {

    interface AdvancedProcessInstanceDetailsMultiViewBinder extends UiBinder<Widget, AdvancedProcessInstanceDetailsMultiViewImpl> {

    }

    private TabListItem processVariablesTab;
    private TabPane processVariablesPane;

    private TabListItem logsTab;
    private TabPane logsPane;
    private static AdvancedProcessInstanceDetailsMultiViewBinder uiBinder = GWT.create( AdvancedProcessInstanceDetailsMultiViewBinder.class );

    @Override
    protected void createAndBindUi() {
        initWidget( uiBinder.createAndBindUi( this ) );

    }

    @Override
    protected AnchorListItem initSpecificOptionsButton() {
        AnchorListItem signalItem = new AnchorListItem(Constants.INSTANCE.Signal());
        signalItem.addClickHandler( new ClickHandler(){

            @Override
            public void onClick( ClickEvent event ) {
                ((AdvancedProcessInstanceDetailsMultiPresenter)presenter).signalProcessInstance();
                
            }
            } );
        return  signalItem;
    }

    @Override
    protected void initSpecificTabs() {
        {
            processVariablesPane = new TabPane() {

                {
                    add( presenter.getProcessVariablesView() );
                }
            };
            processVariablesTab = new TabListItem( Constants.INSTANCE.Process_Variables() ) {

                {
                    setDataTargetWidget( processVariablesPane );
                    addStyleName( "uf-dropdown-tab-list-item" );
                }
            };
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
            logsPane = new TabPane() {

                {
                    add( presenter.getLogsView() );
                }
            };
            logsTab = new TabListItem( Constants.INSTANCE.Logs() ) {

                {
                    setDataTargetWidget( logsPane );
                    addStyleName( "uf-dropdown-tab-list-item" );
                }
            };
            tabContent.add( logsPane );
            navTabs.add( logsTab );
        }

    }

}
