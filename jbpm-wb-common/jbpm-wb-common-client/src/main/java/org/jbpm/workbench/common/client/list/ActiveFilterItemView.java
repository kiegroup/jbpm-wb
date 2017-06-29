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

package org.jbpm.workbench.common.client.list;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.TakesValue;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.*;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.client.views.pfly.widgets.PopoverOptions;

@Templated
@Dependent
public class ActiveFilterItemView implements TakesValue<ActiveFilterItem>,
                                             IsElement {

    @Inject
    @DataField
    @Bound
    Span labelKey;

    @Inject
    @DataField
    @Bound
    Span labelValue;

    @Inject
    @DataField
    ListItem li;

    @Inject
    private JQueryProducer.JQuery<Popover> jQueryPopover;
    @Inject
    @AutoBound
    private DataBinder<ActiveFilterItem> dataBinder;

    @Inject
    private javax.enterprise.event.Event<ActiveFilterItemRemoved> event;

    @Override
    public ActiveFilterItem getValue() {
        return dataBinder.getModel();
    }

    @Override
    public void setValue(final ActiveFilterItem value) {
        dataBinder.setModel(value);
        if (value.getHint() != null) {
            final PopoverOptions options = new PopoverOptions();
            options.setContent(value.getHint());
            options.setHtml(true);
            options.setPlacement("top");
            options.setTrigger("hover click");
            jQueryPopover.wrap(li).popover(options);
        }
    }

    @Override
    public HTMLElement getElement() {
        return li;
    }

    @EventHandler("remove")
    public void onRemove(@ForEvent("click") Event e) {
        event.fire(new ActiveFilterItemRemoved(getValue()));
    }

    @PreDestroy
    public void destroy() {
        if (getValue().getHint() != null) {
            jQueryPopover.wrap(li).destroy();
        }
    }
}