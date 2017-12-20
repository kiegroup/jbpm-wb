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
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.workbench.common.client.PerspectiveIds;
import org.uberfire.ext.widgets.common.client.menu.RefreshMenuBuilder;
import org.jbpm.workbench.pr.client.editors.definition.details.advance.AdvancedViewProcessDefDetailsPresenter;
import org.jbpm.workbench.pr.client.editors.definition.details.multi.BaseProcessDefDetailsMultiPresenter;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@Dependent
@WorkbenchScreen(identifier = PerspectiveIds.PROCESS_DEFINITION_DETAILS_SCREEN)
public class AdvancedProcessDefDetailsMultiPresenter extends BaseProcessDefDetailsMultiPresenter<AdvancedProcessDefDetailsMultiPresenter.AdvancedProcessDefDetailsMultiView> {

    @Inject
    private AdvancedViewProcessDefDetailsPresenter detailPresenter;

    @WorkbenchPartView
    public UberView<AdvancedProcessDefDetailsMultiPresenter> getView() {
        return view;
    }

    @WorkbenchMenu
    public Menus buildMenu() {
        return MenuFactory
                .newTopLevelCustomMenu(newInstanceMenu).endMenu()

                .newTopLevelCustomMenu(new MenuFactory.CustomMenuBuilder() {

                    @Override
                    public void push(MenuFactory.CustomMenuBuilder element) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {

                            @Override
                            public IsWidget build() {
                                return view.getOptionsButton();
                            }
                        };
                    }
                }).endMenu()

                .newTopLevelCustomMenu(new RefreshMenuBuilder(this)).endMenu()
                .build();
    }

    public IsWidget getTabView() {
        return detailPresenter.getWidget();
    }

    public interface AdvancedProcessDefDetailsMultiView extends
                                                        UberView<AdvancedProcessDefDetailsMultiPresenter>,
                                                        BaseProcessDefDetailsMultiPresenter.BaseProcessDefDetailsMultiView {

        IsWidget getOptionsButton();
    }
}
