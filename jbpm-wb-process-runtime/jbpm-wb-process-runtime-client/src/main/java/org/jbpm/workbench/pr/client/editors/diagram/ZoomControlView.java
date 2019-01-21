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

package org.jbpm.workbench.pr.client.editors.diagram;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Templated
@Dependent
public class ZoomControlView implements IsElement {

    @Inject
    @DataField("buttons")
    private HTMLDivElement buttons;

    @Inject
    @DataField("scale-to-100")
    @SuppressWarnings("unused")
    private HTMLAnchorElement scaleTo100;

    @Inject
    @DataField("scale-to-150")
    @SuppressWarnings("unused")
    private HTMLAnchorElement scaleTo150;

    @Inject
    @DataField("scale-to-300")
    @SuppressWarnings("unused")
    private HTMLAnchorElement scaleTo300;

    @Inject
    @DataField("scale-to-50")
    @SuppressWarnings("unused")
    private HTMLAnchorElement scaleTo50;

    @Inject
    @DataField("scale-plus")
    @SuppressWarnings("unused")
    private HTMLAnchorElement scalePlus;

    @Inject
    @DataField("scale-minus")
    @SuppressWarnings("unused")
    private HTMLAnchorElement scaleMinus;

    @Inject
    @DataField("zoom")
    private Span zoom;

    private Command scaleTo100Command;
    private Command scaleTo150Command;
    private Command scaleTo300Command;
    private Command scaleTo50Command;
    private Command scalePlusCommand;
    private Command scaleMinusCommand;

    public void setScaleTo100Command(Command scaleTo100Command) {
        this.scaleTo100Command = scaleTo100Command;
    }

    public void setScaleMinusCommand(Command scaleMinusCommand) {
        this.scaleMinusCommand = scaleMinusCommand;
    }

    public void setScalePlusCommand(Command scalePlusCommand) {
        this.scalePlusCommand = scalePlusCommand;
    }

    public void setZoomText(String text) {
        zoom.setTextContent(text);
    }

    public void setScaleTo50Command(Command scaleTo50Command) {
        this.scaleTo50Command = scaleTo50Command;
    }

    public void setScaleTo150Command(Command scaleTo150Command) {
        this.scaleTo150Command = scaleTo150Command;
    }

    public void setScaleTo300Command(Command scaleTo300Command) {
        this.scaleTo300Command = scaleTo300Command;
    }

    @Override
    public HTMLElement getElement() {
        return buttons;
    }

    @EventHandler("scale-to-100")
    public void onScaleTo100(final @ForEvent("click") MouseEvent event) {
        if (scaleTo100Command != null) {
            scaleTo100Command.execute();
        }
    }

    @EventHandler("scale-to-150")
    public void onScaleTo150(final @ForEvent("click") MouseEvent event) {
        if (scaleTo150Command != null) {
            scaleTo150Command.execute();
        }
    }

    @EventHandler("scale-to-300")
    public void onScaleTo300(final @ForEvent("click") MouseEvent event) {
        if (scaleTo300Command != null) {
            scaleTo300Command.execute();
        }
    }

    @EventHandler("scale-to-50")
    public void onScaleTo50(final @ForEvent("click") MouseEvent event) {
        if (scaleTo50Command != null) {
            scaleTo50Command.execute();
        }
    }

    @EventHandler("scale-plus")
    public void onScalePlus(final @ForEvent("click") MouseEvent event) {
        if (scalePlusCommand != null) {
            scalePlusCommand.execute();
        }
    }

    @EventHandler("scale-minus")
    public void onScaleMinus(final @ForEvent("click") MouseEvent event) {
        if (scaleMinusCommand != null) {
            scaleMinusCommand.execute();
        }
    }
}
