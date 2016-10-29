/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.console.ng.cm.client.navbar;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import org.gwtbootstrap3.client.ui.Label;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.console.ng.cm.client.resources.i18n.ShowcaseConstants;

/**
 * The Logo banner for the application
 */
@Dependent
public class LogoWidgetView extends Composite implements LogoWidgetPresenter.View {

    private SimplePanel container = GWT.create(SimplePanel.class);

    @Inject
    private TranslationService translationService;

    @PostConstruct
    public void init() {
        final RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, "banner/banner.html");
        rb.setCallback(new RequestCallback() {
            @Override
            public void onResponseReceived(final Request request, final Response response) {
                final HTMLPanel html = new HTMLPanel(response.getText());
                container.setWidget(html);
            }

            @Override
            public void onError(final Request request, final Throwable exception) {
                container.setWidget(new Label(translationService.format(ShowcaseConstants.LOGO_BANNER_ERROR)));
            }
        });
        try {
            rb.send();
        } catch (RequestException re) {
            container.setWidget(new Label(translationService.format(ShowcaseConstants.LOGO_BANNER_ERROR)));
        }

        initWidget(container);
    }

}