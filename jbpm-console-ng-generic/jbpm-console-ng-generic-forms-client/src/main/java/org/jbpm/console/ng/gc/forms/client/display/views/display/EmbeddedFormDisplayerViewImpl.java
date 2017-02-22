/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.gc.forms.client.display.views.display;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.ga.forms.display.GenericFormDisplayer;

@Templated
public class EmbeddedFormDisplayerViewImpl extends Composite implements EmbeddedFormDisplayerView {

    @DataField
    private DivElement formContainerRow = Document.get().createDivElement();

    @Inject
    @DataField
    private SimplePanel formContainer;

    @DataField
    private DivElement formFooterRow = Document.get().createDivElement();

    @Inject
    @DataField
    private FlowPanel formFooter;

    @DataField
    private DivElement errorContainerRow = Document.get().createDivElement();

    @DataField
    private SpanElement errorHeader = Document.get().createSpanElement();

    @DataField
    private SpanElement errorContainer = Document.get().createSpanElement();

    @PostConstruct
    public void initialize() {
        init();
    }

    @Override
    public void display(GenericFormDisplayer displayer) {
        init();

        formContainerRow.getStyle().setDisplay(Style.Display.BLOCK);
        formContainer.add(displayer.getContainer());

        if (displayer.getOpener() == null) {
            formFooterRow.getStyle().setDisplay(Style.Display.BLOCK);
            formFooter.add(displayer.getFooter());
        }
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
