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

package org.jbpm.workbench.common.client.filters.saved;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Composite;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.jbpm.workbench.df.client.filter.SavedFilter;

@Dependent
@Templated
public class SavedFilterView extends Composite implements TakesValue<SavedFilter> {

    @Inject
    @DataField("name")
    @Bound
    HTMLButtonElement name;

    @Inject
    @DataField("remove")
    HTMLButtonElement remove;

    @Inject
    @DataField("set-as-default")
    HTMLButtonElement setDefaultBtn;

    @Inject
    @Named("span")
    @DataField("set-as-default-icon")
    HTMLElement setDefaultIcon;

    @Inject
    @AutoBound
    private DataBinder<SavedFilter> dataBinder;

    @Inject
    private Event<SavedFilterRemoveEvent> savedFilterRemoveEvent;

    @Inject
    private Event<SavedFilterAsDefaultActiveEvent> savedFilterAsDefaultEventEvent;

    @Inject
    private Event<SavedFilterSelectedEvent> savedFilterSelectedEvent;

    @PostConstruct
    public void init(){
        remove.title = Constants.INSTANCE.Remove();
    }

    @Override
    public SavedFilter getValue() {
        return dataBinder.getModel();
    }

    @Override
    public void setValue(final SavedFilter savedFilter) {
        showAsSelectedFilter(savedFilter.isDefaultFilter());
        dataBinder.setModel(savedFilter);
    }

    @EventHandler("remove")
    public void onRemove(@ForEvent("click") elemental2.dom.Event e) {
        savedFilterRemoveEvent.fire(new SavedFilterRemoveEvent(getValue()));
    }

    @EventHandler("set-as-default")
    public void onSetAsDefaultFilter(@ForEvent("click") elemental2.dom.Event e) {
        if (!getValue().isDefaultFilter()) {
            savedFilterAsDefaultEventEvent.fire(new SavedFilterAsDefaultActiveEvent(getValue()));
        }
    }

    @EventHandler("name")
    public void onFilterSelected(@ForEvent("click") elemental2.dom.Event e) {
        savedFilterSelectedEvent.fire(new SavedFilterSelectedEvent(getValue()));
    }

    public void showAsSelectedFilter(boolean selected) {
        if (selected) {
            setDefaultBtn.title = Constants.INSTANCE.DefaultFilter();
            setDefaultIcon.className = "fa fa-check-circle";
        } else {
            setDefaultBtn.title = Constants.INSTANCE.SetAsDefaultFilter();
            setDefaultIcon.className = "fa fa-circle";
        }
    }
}
