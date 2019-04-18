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

package org.jbpm.workbench.common.client.filters.active;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jbpm.workbench.common.client.filters.basic.ClearAllBasicFilterEvent;
import org.jbpm.workbench.common.client.resources.i18n.Constants;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class ActiveFiltersImpl implements ActiveFilters {

    @Inject
    ActiveFiltersView view;

    @Inject
    Event<ClearAllActiveFiltersEvent> clearAllActiveFiltersEvent;

    @Inject
    Event<ActiveFilterItemAddedEvent> activeFilterItemAddedEvent;

    @Inject
    Event<NotificationEvent> notification;

    private BiConsumer<String, Consumer<String>> filterNameCallback;

    @PostConstruct
    public void init() {
        view.setSaveFilterCallback(name -> {
            if (filterNameCallback != null) {
                filterNameCallback.accept(name,
                                          error -> {
                                              if (error == null) {
                                                  view.closeSaveFilter();
                                                  displayNotification(Constants.INSTANCE.SavedFilterCorrectlyWithName(name));
                                              } else {
                                                  view.setSaveFilterErrorMessage(error);
                                              }
                                          });
            }
        });
        view.setRemoveAllFilterCallback(() -> {
            view.removeAllActiveFilters(true);
            clearAllActiveFiltersEvent.fire(new ClearAllActiveFiltersEvent());
        });
    }

    @Override
    public void setSaveFilterCallback(final BiConsumer<String, Consumer<String>> filterNameCallback) {
        this.filterNameCallback = filterNameCallback;
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public <T extends Object> void addActiveFilter(final ActiveFilterItem<T> filter) {
        view.addActiveFilter(filter);
        activeFilterItemAddedEvent.fire(new ActiveFilterItemAddedEvent(filter));
    }

    @Override
    public <T> void removeActiveFilter(final ActiveFilterItem<T> filter) {
        view.removeActiveFilter(filter);
    }

    @Override
    public void removeAllActiveFilters() {
        view.removeAllActiveFilters(false);
        clearAllActiveFiltersEvent.fire(new ClearAllActiveFiltersEvent());
    }

    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text,
                                                NotificationEvent.NotificationType.SUCCESS));
    }

    public void clearAllActiveFilters (@Observes ClearAllBasicFilterEvent event){
            view.removeAllActiveFilters(true);
    }
}
