/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
