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

package org.jbpm.console.ng.gc.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jbpm.console.ng.gc.client.i18n.Constants;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

public class RestoreDefaultFiltersMenuBuilder implements MenuFactory.CustomMenuBuilder {

    public interface SupportsRestoreDefaultFilters {

        void onRestoreDefaultFilters();

    }

    private SupportsRestoreDefaultFilters supportsRestoreDefaultFilters;

    protected Button menuResetTabsButton = GWT.create(Button.class);

    public RestoreDefaultFiltersMenuBuilder(final SupportsRestoreDefaultFilters supportsRestoreDefaultFilters) {
        this.supportsRestoreDefaultFilters = supportsRestoreDefaultFilters;
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
                return menuResetTabsButton;
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

    protected void setupMenuButton() {
        menuResetTabsButton.setIcon(IconType.FILTER);
        menuResetTabsButton.setSize(ButtonSize.SMALL);
        menuResetTabsButton.setTitle(Constants.INSTANCE.RestoreDefaultFilters());
        menuResetTabsButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                supportsRestoreDefaultFilters.onRestoreDefaultFilters();
            }
        });
    }

}