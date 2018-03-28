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

import java.util.ArrayList;
import java.util.List;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

public class PrimaryActionMenuBuilder implements MenuFactory.CustomMenuBuilder {

    private final List<EnabledStateChangeListener> changeListeners = new ArrayList<>();
    private Button button;

    public PrimaryActionMenuBuilder() {
    }

    public PrimaryActionMenuBuilder(final String label,
                                    final Command command) {
        button = IOC.getBeanManager().lookupBean(Button.class).newInstance();
        button.setType(Button.ButtonType.BUTTON);
        button.setButtonStyleType(Button.ButtonStyleType.PRIMARY);
        setupButton(label,
                    "",
                    "");
        button.setClickHandler(() -> command.execute());
    }

    public PrimaryActionMenuBuilder(final String label,
                                    final String icon,
                                    final String title,
                                    final Command command,
                                    boolean notifyChangeListeners) {

        button = IOC.getBeanManager().lookupBean(Button.class).newInstance();
        button.setButtonStyleType(Button.ButtonStyleType.LINK);
        setupButton(label,
                    icon,
                    title);
        button.setClickHandler(() -> {
            command.execute();
            if (notifyChangeListeners) {
                notifyListeners(true);
            }
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
                return button.getElement();
            }

            @Override
            public boolean isEnabled() {
                return super.isEnabled();
            }

            @Override
            public void setEnabled(final boolean enabled) {
                super.setEnabled(enabled);
                notifyListeners(enabled);
            }

            public void addEnabledStateChangeListener(final EnabledStateChangeListener listener) {
                addChangeListener(listener);
            }
        };
    }

    private void setupButton(String label,
                             String icon,
                             String title) {
        if (label != null && !label.isEmpty()) {
            button.setText(label);
        }
        if (icon != null && !icon.isEmpty()) {
            button.addIcon("fa",
                           icon);
        }
        if (title != null && !title.isEmpty()) {
            button.getElement().title = title;
        }
    }

    public void setVisible(boolean visible) {
        if (visible) {
            button.show();
        } else {
            button.hide();
        }
    }

    public void addChangeListener(final EnabledStateChangeListener listener) {
        changeListeners.add(listener);
    }

    private void notifyListeners(final boolean enabled) {
        for (final EnabledStateChangeListener listener : changeListeners) {
            listener.enabledStateChanged(enabled);
        }
    }
}