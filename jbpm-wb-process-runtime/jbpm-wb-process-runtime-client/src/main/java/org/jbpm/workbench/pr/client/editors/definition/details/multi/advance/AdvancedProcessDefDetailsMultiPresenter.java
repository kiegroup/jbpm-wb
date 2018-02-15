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
import org.jbpm.workbench.common.client.menu.RefreshMenuBuilder;
import org.jbpm.workbench.pr.client.editors.definition.details.advance.AdvancedViewProcessDefDetailsPresenter;
import org.jbpm.workbench.pr.client.editors.definition.details.multi.BaseProcessDefDetailsMultiPresenter;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.jbpm.workbench.common.client.PerspectiveIds.DETAILS_SCREEN_PREFERRED_WIDTH;
import static org.jbpm.workbench.common.client.PerspectiveIds.PROCESS_DEFINITION_DETAILS_SCREEN;

@Dependent
@WorkbenchScreen(identifier = PROCESS_DEFINITION_DETAILS_SCREEN, preferredWidth = DETAILS_SCREEN_PREFERRED_WIDTH)
public class AdvancedProcessDefDetailsMultiPresenter extends BaseProcessDefDetailsMultiPresenter<AdvancedProcessDefDetailsMultiPresenter.AdvancedProcessDefDetailsMultiView> {

    @Inject
    private AdvancedViewProcessDefDetailsPresenter detailPresenter;

    @DefaultPosition
    public Position getPosition() {
        return CompassPosition.EAST;
    }

    @WorkbenchPartView
    public UberView<AdvancedProcessDefDetailsMultiPresenter> getView() {
        return view;
    }

    @WorkbenchMenu
    public Menus buildMenu() {
        return MenuFactory
                .newTopLevelCustomMenu(new RefreshMenuBuilder(this)).endMenu()
                .newTopLevelCustomMenu(primaryActionMenuBuilder).endMenu()
                .newTopLevelMenu(Constants.INSTANCE.Options())
                    .menus()
                        .menu(Constants.INSTANCE.View_Process_Instances())
                            .respondsWith(() -> viewProcessInstances())
                        .endMenu()
                        .menu(Constants.INSTANCE.View_Process_Model())
                            .respondsWith(() -> goToProcessDefModelPopup())
                        .endMenu()
                    .endMenus()
                .endMenu()
                .build();
    }

    public IsWidget getTabView() {
        return detailPresenter.getWidget();
    }

    public interface AdvancedProcessDefDetailsMultiView extends
                                                        UberView<AdvancedProcessDefDetailsMultiPresenter>,
                                                        BaseProcessDefDetailsMultiPresenter.BaseProcessDefDetailsMultiView {

    }
}
