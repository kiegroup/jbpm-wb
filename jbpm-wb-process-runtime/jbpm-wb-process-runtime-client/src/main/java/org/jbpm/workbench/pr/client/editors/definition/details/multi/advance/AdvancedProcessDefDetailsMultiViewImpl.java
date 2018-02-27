/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.client.editors.definition.details.multi.advance;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.jbpm.workbench.pr.client.editors.definition.details.multi.BaseProcessDefDetailsMultiViewImpl;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;

@Dependent
public class AdvancedProcessDefDetailsMultiViewImpl extends BaseProcessDefDetailsMultiViewImpl
        implements AdvancedProcessDefDetailsMultiPresenter.AdvancedProcessDefDetailsMultiView {

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    NavTabs navTabs;

    @UiField
    TabContent tabContent;

    private AdvancedProcessDefDetailsMultiPresenter presenter;

    private TabPane definitionDetailsPane;

    private TabListItem definitionDetailsTab;

    private TabListItem diagramTab;

    private TabPane diagramPane;

    @Override
    public void init(final AdvancedProcessDefDetailsMultiPresenter presenter) {
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));
        initTabs();
    }

    protected void initTabs() {
        definitionDetailsPane = new TabPane() {{
            add(getTabView());
            setActive(true);
        }};
        definitionDetailsTab = new TabListItem(Constants.INSTANCE.Definition_Details()) {{
            setDataTargetWidget(definitionDetailsPane);
            addStyleName("uf-dropdown-tab-list-item");
            setActive(true);
        }};
        navTabs.add(definitionDetailsTab);
        tabContent.add(definitionDetailsPane);

        {
            diagramPane = GWT.create(TabPane.class);
            diagramPane.add(presenter.getProcessDiagramView());

            diagramTab = GWT.create(TabListItem.class);
            diagramTab.setText(Constants.INSTANCE.Diagram());
            diagramTab.setDataTargetWidget(diagramPane);
            diagramTab.addStyleName("uf-dropdown-tab-list-item");

            tabContent.add(diagramPane);
            navTabs.add(diagramTab);
        }
    }

    @Override
    protected IsWidget getTabView() {
        return presenter.getTabView();
    }

    @Override
    protected void closeDetails() {
        presenter.closeDetails();
    }

    interface Binder extends
                     UiBinder<Widget, AdvancedProcessDefDetailsMultiViewImpl> {

    }
}
