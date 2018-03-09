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

package org.jbpm.workbench.common.client.filters.active;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import elemental2.dom.Event;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Templated
@Dependent
public class SaveFilterPopoverView implements IsElement {

    @Inject
    @DataField("content")
    HTMLDivElement content;

    @Inject
    @DataField("input-group")
    HTMLDivElement inputGroup;

    @Inject
    @DataField("save")
    HTMLButtonElement save;

    @Inject
    @DataField("cancel")
    HTMLButtonElement cancel;

    @Inject
    @DataField("name")
    HTMLInputElement filterName;

    @Inject
    @DataField("error")
    @Named("span")
    HTMLElement error;

    private Command cancelCallback;

    private ParameterizedCommand<String> saveCallback;

    public void setCancelCallback(final Command cancelCallback) {
        this.cancelCallback = cancelCallback;
    }

    public void setSaveCallback(final ParameterizedCommand<String> saveCallback) {
        this.saveCallback = saveCallback;
    }

    @Override
    public HTMLElement getElement() {
        return content;
    }

    @EventHandler("save")
    public void onSave(@ForEvent("click") Event e) {
        if (saveCallback != null) {
            saveCallback.execute(filterName.value);
        }
    }

    @EventHandler("name")
    public void onKeyPressEvent(final KeyDownEvent e) {
        if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            e.preventDefault();
            onSave(null);
        }
    }

    public void onOpen() {
        filterName.value = "";
        error.textContent = "";
        inputGroup.classList.remove("has-error");
    }

    public void onShow() {
        filterName.focus();
    }

    public void setError(final String message) {
        error.textContent = message;
        inputGroup.classList.add("has-error");
    }

    @EventHandler("cancel")
    public void onCancel(@ForEvent("click") Event e) {
        if (cancelCallback != null) {
            cancelCallback.execute();
        }
    }
}
