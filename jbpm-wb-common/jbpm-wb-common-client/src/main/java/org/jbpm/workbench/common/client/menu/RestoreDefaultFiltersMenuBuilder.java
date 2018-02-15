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

package org.jbpm.workbench.common.client.menu;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

public class RestoreDefaultFiltersMenuBuilder implements MenuFactory.CustomMenuBuilder {

    protected Button menuResetTabsButton;
    private SupportsRestoreDefaultFilters supportsRestoreDefaultFilters;
    private HTMLDocument document;

    public RestoreDefaultFiltersMenuBuilder(final SupportsRestoreDefaultFilters supportsRestoreDefaultFilters) {
        this(DomGlobal.document,
             supportsRestoreDefaultFilters);
    }

    public RestoreDefaultFiltersMenuBuilder(final HTMLDocument document,
                                            final SupportsRestoreDefaultFilters supportsRestoreDefaultFilters) {
        this.document = document;
        this.supportsRestoreDefaultFilters = supportsRestoreDefaultFilters;
        setupMenuButton();
    }

    @Override
    public void push(MenuFactory.CustomMenuBuilder element) {
    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<HTMLElement>() {
            @Override
            public HTMLElement build() {
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
        menuResetTabsButton = (Button) document.createElement("button");
        menuResetTabsButton.setType(Button.ButtonType.BUTTON);
        menuResetTabsButton.setButtonStyleType(Button.ButtonStyleType.LINK);
        menuResetTabsButton.addIcon("fa",
                                    "fa-filter");
        menuResetTabsButton.title = Constants.INSTANCE.RestoreDefaultFilters();
        menuResetTabsButton.setClickHandler(getClickHandler());
    }

    protected Command getClickHandler() {
        return () -> supportsRestoreDefaultFilters.onRestoreDefaultFilters();
    }

    public interface SupportsRestoreDefaultFilters {

        void onRestoreDefaultFilters();
    }
}