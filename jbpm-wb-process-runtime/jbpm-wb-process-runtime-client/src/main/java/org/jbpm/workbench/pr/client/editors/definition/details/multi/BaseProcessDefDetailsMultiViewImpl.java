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

package org.jbpm.workbench.pr.client.editors.definition.details.multi;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.uberfire.client.views.pfly.widgets.Button;

public abstract class BaseProcessDefDetailsMultiViewImpl extends Composite
        implements BaseProcessDefDetailsMultiPresenter.BaseProcessDefDetailsMultiView {

    private Button newInstanceButton;

    public BaseProcessDefDetailsMultiViewImpl() {
        newInstanceButton = (Button) DomGlobal.document.createElement("button");
        newInstanceButton.setType(Button.ButtonType.BUTTON);
        newInstanceButton.setButtonStyleType(Button.ButtonStyleType.PRIMARY);
        newInstanceButton.setText(Constants.INSTANCE.New_Instance());
        newInstanceButton.setClickHandler(() -> createNewProcessInstance());
    }

    @Override
    public HTMLElement getNewInstanceButton() {
        return newInstanceButton;
    }

    protected abstract IsWidget getTabView();

    protected abstract void closeDetails();

    protected abstract void createNewProcessInstance();

    @Override
    public void setNewInstanceButtonVisible(boolean visible) {
        if(visible){
            newInstanceButton.show();
        } else {
            newInstanceButton.hide();
        }
    }
}