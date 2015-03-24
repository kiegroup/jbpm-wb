package org.jbpm.console.ng.pr.client.editors.definition.details.multi.advance;

import static com.github.gwtbootstrap.client.ui.resources.ButtonSize.MINI;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.console.ng.pr.client.editors.definition.details.BaseProcessDefDetailsPresenter;
import org.jbpm.console.ng.pr.client.editors.definition.details.advance.AdvancedViewProcessDefDetailsPresenter;
import org.jbpm.console.ng.pr.client.editors.definition.details.multi.BaseProcessDefDetailsMultiViewImpl;
import org.jbpm.console.ng.pr.client.i18n.Constants;
import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class AdvancedProcessDefDetailsMultiViewImpl extends
        BaseProcessDefDetailsMultiViewImpl
        implements
        AdvancedProcessDefDetailsMultiPresenter.AdvancedProcessDefDetailsMultiView {

    interface Binder extends
            UiBinder<Widget, AdvancedProcessDefDetailsMultiPresenter.AdvancedProcessDefDetailsMultiView> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @Inject
    AdvancedViewProcessDefDetailsPresenter detailPresenter;

    @Override
    public IsWidget getOptionsButton() {
        return new DropdownButton( Constants.INSTANCE.Options() ) {

            {
                setSize( MINI );
                setRightDropdown( true );
                add( new NavLink( Constants.INSTANCE.View_Process_Instances() ) {

                    {
                        addClickHandler( new ClickHandler() {

                            @Override
                            public void onClick( ClickEvent event ) {
                                presenter.viewProcessInstances();
                            }
                        } );
                    }
                } );

                add( new NavLink( Constants.INSTANCE.View_Process_Model() ) {

                    {
                        addClickHandler( new ClickHandler() {

                            @Override
                            public void onClick( ClickEvent event ) {
                                presenter.goToProcessDefModelPopup();
                            }
                        } );
                    }
                } );
            }
        };
    }

    @Override
    protected BaseProcessDefDetailsPresenter getSpecificProcessDefDetailPresenter() {
        return detailPresenter;
    }

    @Override
    protected void createAndBindUi() {
        uiBinder.createAndBindUi( this );

    }

    @Override
    protected int getSpecificOffsetHeight() {
        return AdvancedProcessDefDetailsMultiViewImpl.this.getParent()
                .getOffsetHeight();
    }
}
