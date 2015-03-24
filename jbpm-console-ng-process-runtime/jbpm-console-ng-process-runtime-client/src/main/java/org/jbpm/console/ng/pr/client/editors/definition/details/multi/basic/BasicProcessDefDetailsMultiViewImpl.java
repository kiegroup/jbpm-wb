package org.jbpm.console.ng.pr.client.editors.definition.details.multi.basic;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jbpm.console.ng.pr.client.editors.definition.details.BaseProcessDefDetailsPresenter;
import org.jbpm.console.ng.pr.client.editors.definition.details.basic.BasicProcessDefDetailsPresenter;
import org.jbpm.console.ng.pr.client.editors.definition.details.multi.BaseProcessDefDetailsMultiViewImpl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

@Dependent
public class BasicProcessDefDetailsMultiViewImpl extends BaseProcessDefDetailsMultiViewImpl implements
        BasicProcessDefDetailsMultiPresenter.BasicProcessDefDetailsMultiView {

    interface Binder
            extends
            UiBinder<Widget, BasicProcessDefDetailsMultiPresenter.BasicProcessDefDetailsMultiView> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @Inject
    private BasicProcessDefDetailsPresenter detailsPresenter;

    @Override
    protected BaseProcessDefDetailsPresenter getSpecificProcessDefDetailPresenter() {
        return detailsPresenter;
    }

    @Override
    protected void createAndBindUi() {
        uiBinder.createAndBindUi( this );

    }

    @Override
    protected int getSpecificOffsetHeight() {
        return BasicProcessDefDetailsMultiViewImpl.this.getParent()
                .getOffsetHeight();
    }
}
