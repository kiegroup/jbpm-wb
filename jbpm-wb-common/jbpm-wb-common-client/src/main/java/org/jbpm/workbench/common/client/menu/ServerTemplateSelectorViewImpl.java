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

import java.util.Iterator;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class ServerTemplateSelectorViewImpl extends Composite implements ServerTemplateSelectorMenuBuilder.ServerTemplateSelectorView {

    private Constants constants = Constants.INSTANCE;

    private DropDownMenu dropDownServerTemplates;

    private Button serverTemplateButton;

    private ButtonGroup serverTemplates;

    private ParameterizedCommand<String> changeCommand;

    @PostConstruct
    public void init() {
        serverTemplateButton = GWT.create(Button.class);
        serverTemplateButton.setText(constants.ServerTemplates());
        serverTemplateButton.setDataToggle(Toggle.DROPDOWN);
        serverTemplateButton.setSize(ButtonSize.SMALL);

        dropDownServerTemplates = GWT.create(DropDownMenu.class);
        dropDownServerTemplates.addStyleName(Styles.DROPDOWN_MENU + "-right");

        serverTemplates = GWT.create(ButtonGroup.class);
        serverTemplates.add(serverTemplateButton);
        serverTemplates.add(dropDownServerTemplates);

        initWidget(serverTemplates);
    }

    @Override
    public void selectServerTemplate(final String serverTemplateId) {
        for (Widget widget : dropDownServerTemplates) {
            if (widget instanceof AnchorListItem && ((AnchorListItem) widget).getText().equals(serverTemplateId)) {
                selectServerTemplate((AnchorListItem) widget);
                break;
            }
        }
    }

    @Override
    public void addServerTemplate(final String serverTemplateId) {
        final AnchorListItem serverTemplateNavLink = GWT.create(AnchorListItem.class);
        serverTemplateNavLink.setText(serverTemplateId);
        serverTemplateNavLink.setIcon(IconType.SERVER);
        serverTemplateNavLink.setIconFixedWidth(true);
        serverTemplateNavLink.addClickHandler(e -> selectServerTemplate(serverTemplateNavLink));
        dropDownServerTemplates.add(serverTemplateNavLink);
    }

    protected void selectServerTemplate(final AnchorListItem serverTemplateNavLink) {
        final boolean serverChanged = serverTemplateNavLink.getText().equals(serverTemplateButton.getText()) == false;
        unselectAllServerTemplateNavLinks();
        serverTemplateNavLink.setActive(true);
        if (serverChanged) {
            serverTemplateButton.setText(serverTemplateNavLink.getText());
            if (changeCommand != null) {
                changeCommand.execute(serverTemplateNavLink.getText());
            }
        }
    }

    @Override
    public void clearSelectedServerTemplate() {
        serverTemplateButton.setText(constants.ServerTemplates());
        if (changeCommand != null) {
            changeCommand.execute(null);
        }
    }

    @Override
    public String getSelectedServerTemplate() {
        final String serverTemplate = serverTemplateButton.getText();
        return serverTemplate.equals(constants.ServerTemplates()) ? null : serverTemplate;
    }

    @Override
    public void removeAllServerTemplates() {
        final Iterator<Widget> iterator = dropDownServerTemplates.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    @Override
    public void setServerTemplateChangeHandler(final ParameterizedCommand<String> command) {
        changeCommand = command;
    }

    private void unselectAllServerTemplateNavLinks() {
        for (Widget widget : dropDownServerTemplates) {
            if (widget instanceof AnchorListItem) {
                ((AnchorListItem) widget).setActive(false);
            }
        }
    }

}