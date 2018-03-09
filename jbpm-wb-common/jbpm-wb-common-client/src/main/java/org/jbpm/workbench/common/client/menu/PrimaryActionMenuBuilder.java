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
    boolean isOn = true;

    public PrimaryActionMenuBuilder(final String label,
                                    final Command command) {
        initPrimaryActionButton(label,
                                "",
                                "");
        button.setClickHandler(() -> command.execute());
    }

    public PrimaryActionMenuBuilder(final String onLabel,
                                    final String onIcon,
                                    final String onTitle,
                                    final String offLabel,
                                    final String offIcon,
                                    final String offTitle,
                                    final Command command) {

        isOn = true;
        initPrimaryActionLink(onLabel,
                              onIcon,
                              onTitle);
        button.setClickHandler(() -> {
            if (isOn) {
                setupButton(offLabel,
                            offIcon,
                            offTitle);
            } else {
                setupButton(onLabel,
                            onIcon,
                            onTitle);
            }
            command.execute();
            isOn = !isOn;
        });
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

    private void initPrimaryActionButton(String label,
                                         String icon,
                                         String title) {
        button = (Button) document.createElement("button");
        button.setType(Button.ButtonType.BUTTON);
        button.setButtonStyleType(Button.ButtonStyleType.PRIMARY);
        setupButton(label,
                    icon,
                    title);
    }

    private void initPrimaryActionLink(String label,
                                       String icon,
                                       String title) {
        button = (Button) document.createElement("button");
        button.setButtonStyleType(Button.ButtonStyleType.LINK);
        setupButton(label,
                    icon,
                    title);
    }

    private void setupButton(String label,
                             String icon,
                             String title) {
        while (button.hasChildNodes()) {
            button.removeChild(button.lastChild);
        }

        if (label != null && !label.isEmpty()) {
            button.setText(label);
        }
        if (icon != null && !icon.isEmpty()) {
            button.addIcon("fa",
                           icon);
        }
        if (title != null && !title.isEmpty()) {
            button.title = title;
        }
    }

    public void setVisible(boolean visible) {
        if (visible) {
            button.show();
        } else {
            button.hide();
        }
    }
}