/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import elemental2.dom.HTMLElement;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import static elemental2.dom.DomGlobal.document;

public class PrimaryActionMenuBuilder implements MenuFactory.CustomMenuBuilder {

    private Button button;
    private String label;
    private Command command;

    public PrimaryActionMenuBuilder(final String label,
                                    final Command command) {
        this.label = label;
        this.command = command;
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
                return button;
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

    private void setupMenuButton() {
        button = (Button) document.createElement("button");
        button.setType(Button.ButtonType.BUTTON);
        button.setButtonStyleType(Button.ButtonStyleType.PRIMARY);
        button.setText(label);
        button.setClickHandler(() -> command.execute());
    }

    public void setVisible(boolean visible) {
        if(visible){
            button.show();
        } else {
            button.hide();
        }
    }
}