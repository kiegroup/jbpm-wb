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
package org.jbpm.workbench.common.client.util;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;

public abstract class AbstractLogItemView<T> extends AbstractView<T> {

    @Inject
    @DataField("list-group-item")
    private Div listGroupItem;

    @Inject
    @DataField("logIcon")
    protected Span logIcon;

    @Inject
    @DataField("logTime")
    protected Span logTime;

    @Inject
    @DataField("logTypeDesc")
    protected Span logTypeDesc;

    @Inject
    @DataField("logInfo")
    protected Span logInfo;

    @PostConstruct
    public void init() {
        tooltip(logIcon);
    }

    @Override
    public HTMLElement getElement() {
        return listGroupItem;
    }

    protected void setLogTime(final Date modelLogTime) {
        tooltip(logTime);
        logTime.setAttribute("data-original-title", DateUtils.getDateTimeStr(modelLogTime));
        logTime.setTextContent(DateUtils.getPrettyTime(modelLogTime));
    }
}
