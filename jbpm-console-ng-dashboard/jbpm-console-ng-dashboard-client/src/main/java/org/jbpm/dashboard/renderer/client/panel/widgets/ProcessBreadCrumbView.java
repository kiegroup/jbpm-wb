/**
 * Copyright (C) 2015 JBoss Inc
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
package org.jbpm.dashboard.renderer.client.panel.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class ProcessBreadCrumbView extends Composite
        implements ProcessBreadCrumb.View {

    interface Binder extends UiBinder<Widget,ProcessBreadCrumbView> {}
    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    protected Panel rootPanel;

    @UiField
    protected Label processLabel;

    @UiField
    protected Anchor rootLink;

    public ProcessBreadCrumbView() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void init(final ProcessBreadCrumb presenter) {
        rootLink.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent clickEvent) {
                presenter.gotoRoot();
            }
        });
    }

    @Override
    public void setProcess(String name) {
        processLabel.setText(name);
    }
}
