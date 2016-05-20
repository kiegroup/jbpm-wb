/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.bd.client.editors.deployment.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.jbpm.console.ng.bd.client.i18n.Constants;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

public class NewDeploymentUnitMenuBuilder implements MenuFactory.CustomMenuBuilder {

    public interface SupportsNewDeploymentUnit {

        void onNewDeploymentUnit();

    }

    private SupportsNewDeploymentUnit supportsNewDeploymentUnit;

    protected Button menuNewButton = GWT.create(Button.class);

    public NewDeploymentUnitMenuBuilder(final SupportsNewDeploymentUnit supportsNewDeploymentUnit) {
        this.supportsNewDeploymentUnit = supportsNewDeploymentUnit;
        setupMenuButton();
    }

    @Override
    public void push(MenuFactory.CustomMenuBuilder element) {
    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<IsWidget>() {
            @Override
            public IsWidget build() {
                return menuNewButton;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void setEnabled(boolean enabled) {

            }
        };
    }

    public void setupMenuButton() {
        menuNewButton.setSize(ButtonSize.SMALL);
        menuNewButton.setText(Constants.INSTANCE.New_Deployment_Unit());
        menuNewButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                supportsNewDeploymentUnit.onNewDeploymentUnit();
            }
        });
    }

}