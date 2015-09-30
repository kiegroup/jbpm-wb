package org.jbpm.console.ng.pr.client.editors.instance.details.multi.basic;

import javax.enterprise.context.Dependent;

import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.jbpm.console.ng.pr.client.editors.instance.details.multi.BaseProcessInstanceDetailsMultiViewImpl;

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
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    protected AnchorListItem initSpecificOptionsButton() {
        return null;
    }

    @Override
    protected void initSpecificTabs() {
    }

}
