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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.views.pfly.widgets.Select;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class ManageSelectorViewImpl implements ManageSelectorImpl.ManageSelectorView {

    @Inject
    @DataField("container")
    HTMLDivElement container;

    @Inject
    @DataField("selector")
    private Select selector;

    @Override
    public void addOption(String label,
                          String value,
                          boolean selected) {
        selector.addOption(label,
                           value,
                           selected);
    }

    @Override
    public void removeAllOptions() {
        selector.removeAllOptions();
    }

    @Override
    public void setOptionChangeCommand(Command changeCommand) {
        selector.getElement().addEventListener("change",
                                               event -> changeCommand.execute(),
                                               false);
    }

    @Override
    public void refresh() {
        selector.refresh();
    }

    @Override
    public String getSelectedOption() {
        return selector.getValue();
    }

    @Override
    public HTMLElement getElement() {
        return container;
    }
}