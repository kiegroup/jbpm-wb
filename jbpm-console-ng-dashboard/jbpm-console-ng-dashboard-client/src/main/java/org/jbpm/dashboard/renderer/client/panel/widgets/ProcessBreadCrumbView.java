/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.ListItem;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class ProcessBreadCrumbView extends Composite
        implements ProcessBreadCrumb.View {

    @Inject
    @DataField
    protected Anchor rootAnchor;

    @Inject
    @DataField
    protected ListItem processItem;

    ProcessBreadCrumb presenter;

    @Override
    public void init(final ProcessBreadCrumb presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setRootTitle(String text) {
        rootAnchor.setTextContent(text);
    }

    @Override
    public void setProcess(String name) {
        processItem.setText(name);
    }

    @EventHandler("rootAnchor")
    public void onRootClick(ClickEvent e) {
        presenter.gotoRoot();
    }
}
