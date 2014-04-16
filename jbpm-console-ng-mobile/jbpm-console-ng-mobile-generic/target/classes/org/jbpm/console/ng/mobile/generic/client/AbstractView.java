/*
 * Copyright 2014 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.mobile.generic.client;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.tap.HasTapHandlers;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.dialog.Dialogs;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;

/**
 *
 * @author livthomas
 */
public abstract class AbstractView implements IsWidget {

    protected final LayoutPanel layoutPanel;

    protected final HeaderPanel headerPanel;

    protected final HeaderButton headerBackButton;

    protected final HTML title;

    public AbstractView() {
        layoutPanel = new LayoutPanel();
        headerPanel = new HeaderPanel();

        title = new HTML();
        headerPanel.setCenterWidget(title);

        headerBackButton = new HeaderButton();
        headerBackButton.setBackButton(true);
        headerBackButton.setText("Back");
        headerBackButton.setVisible(!MGWT.getOsDetection().isAndroid());
        headerPanel.setLeftWidget(headerBackButton);

        layoutPanel.add(headerPanel);
    }

    @Override
    public Widget asWidget() {
        return layoutPanel;
    }

    public void displayNotification(String title, String text) {
        Dialogs.alert(title, text, null);
    }

    public HasTapHandlers getBackButton() {
        return headerBackButton;
    }

}
