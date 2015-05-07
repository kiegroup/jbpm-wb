package org.jbpm.console.ng.pr.client.editors.instance.details.multi.basic;

import javax.enterprise.context.Dependent;

import org.jbpm.console.ng.gc.client.experimental.details.base.DetailsTabbedPanel;
import org.jbpm.console.ng.pr.client.editors.instance.details.multi.BaseProcessInstanceDetailsMultiViewImpl;

import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class BasicProcessInstanceDetailsMultiViewImpl extends BaseProcessInstanceDetailsMultiViewImpl
        implements BasicProcessInstanceDetailsMultiPresenter.BasicProcessInstanceDetailsMultiView {

    interface BasicProcessInstanceDetailsMultiViewBinder extends UiBinder<Widget, BasicProcessInstanceDetailsMultiViewImpl> {

    }

    private static BasicProcessInstanceDetailsMultiViewBinder uiBinder = GWT.create( BasicProcessInstanceDetailsMultiViewBinder.class );


    @Override
    protected void createAndBindUi() {
        uiBinder.createAndBindUi( this );
    }

    @Override
    protected void onResizeForSpecificPanel( String height ) {
    }

    @Override
    protected void initSpecificOptionsButton( DropdownButton dropdownButton ) {
    }

    @Override
    protected void refreshSpecificGrid() {
    }

    @Override
    protected void initSpecificTabs( DetailsTabbedPanel tabPanel ) {
    }
}
