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

package org.jbpm.workbench.forms.client.display.views.display;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.forms.client.display.GenericFormDisplayer;

@Templated
public class EmbeddedFormDisplayerViewImpl extends Composite implements EmbeddedFormDisplayerView {

    @Inject
    @DataField
    private DivElement formContainerRow;

    @DataField
    private FlowPanel formContainer = GWT.create(FlowPanel.class);

    @Inject
    @DataField
    private DivElement formFooterRow;

    @DataField
    private FlowPanel formFooter = GWT.create(FlowPanel.class);

    @Inject
    @DataField
    private DivElement errorContainerRow;

    @Inject
    @DataField
    private SpanElement errorHeader;

    @Inject
    @DataField
    private SpanElement errorContainer;

    @PostConstruct
    public void initialize() {
        init();
    }

    @Override
    public void display(GenericFormDisplayer displayer) {
        init();

        formContainerRow.getStyle().setDisplay(Style.Display.BLOCK);
        formContainer.add(displayer.getContainer());

        formFooterRow.getStyle().setDisplay(Style.Display.BLOCK);
        formFooter.add(displayer.getFooter());
    }

    @Override
    public void showErrorMessage(String header,
                                 String errorMessage) {
        init();
        errorContainerRow.getStyle().setDisplay(Style.Display.BLOCK);
        errorHeader.setInnerText(header);
        errorContainer.setInnerText(errorMessage);
    }

    protected void init() {
        formContainerRow.getStyle().setDisplay(Style.Display.NONE);
        formContainer.clear();

        formFooterRow.getStyle().setDisplay(Style.Display.NONE);
        formFooter.clear();

        errorContainerRow.getStyle().setDisplay(Style.Display.NONE);
        errorContainer.removeAllChildren();
        errorHeader.removeAllChildren();
    }
}
