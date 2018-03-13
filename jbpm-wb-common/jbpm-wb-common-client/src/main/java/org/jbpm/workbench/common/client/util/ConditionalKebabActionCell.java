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

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLLIElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.common.model.GenericSummary;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.KebabMenu;
import org.uberfire.client.views.pfly.widgets.KebabMenuItem;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;

@Dependent
public class ConditionalKebabActionCell<T extends GenericSummary> extends AbstractCell<T> {

    private List<ConditionalAction<T>> actions;

    @Inject
    private HTMLDocument document;

    @Inject
    private ManagedInstance<KebabMenu> kebabMenus;

    @Inject
    private ManagedInstance<KebabMenuItem> kebabMenuItems;

    @Inject
    private ManagedInstance<Button> buttons;

    public ConditionalKebabActionCell() {
        super(CLICK,
              KEYDOWN);
    }

    public void setActions(final List<ConditionalAction<T>> actions) {
        this.actions = actions;
    }

    private String getHTMLContent(final Element element) {
        final HTMLDivElement div = (HTMLDivElement) document.createElement("div");
        div.appendChild(element);
        return div.innerHTML;
    }

    @Override
    public void render(final Cell.Context context,
                       final T value,
                       final SafeHtmlBuilder sb) {

        final List<ConditionalAction<T>> availableActions = actions.stream().filter(a -> a.getPredicate().test(value)).collect(Collectors.toList());
        if (availableActions.isEmpty()) {
            return;
        }
        if (availableActions.size() > 1) {
            final KebabMenu menu = kebabMenus.get();
            menu.setItemsAlignment(KebabMenu.ItemsAlignment.RIGHT);
            menu.setDropPosition(KebabMenu.DropPosition.UP);
            menu.getElement().id = Document.get().createUniqueId();
            final List<ConditionalAction<T>> actionMenus = availableActions.stream().filter(action -> action.isNavigation() == false).collect(Collectors.toList());
            actionMenus.forEach(action -> {
                addKebabMenuItem(menu,
                                 value,
                                 action.getText(),
                                 action.getCallback());
            });
            final List<ConditionalAction<T>> navigationMenus = availableActions.stream().filter(action -> action.isNavigation()).collect(Collectors.toList());
            if (actionMenus.isEmpty() == false && navigationMenus.isEmpty() == false) {
                menu.addSeparator();
            }
            navigationMenus.forEach(action -> {
                addKebabMenuItem(menu,
                                 value,
                                 action.getText(),
                                 action.getCallback());
            });
            sb.appendHtmlConstant(getHTMLContent(menu.getElement()));

            fixOpenPosition(menu.getElement().id);
        } else {
            final Button button = buttons.get();
            button.setType(Button.ButtonType.BUTTON);
            button.setButtonStyleType(Button.ButtonStyleType.DEFAULT);
            button.setText(availableActions.get(0).getText());
            button.getElement().id = Document.get().createUniqueId();
            setCallback(button.getElement().id,
                        value,
                        availableActions.get(0).getCallback());
            sb.appendHtmlConstant(getHTMLContent(button.getElement()));
        }
    }

    private void addKebabMenuItem(final KebabMenu menu,
                                  final T value,
                                  final String label,
                                  final Consumer<T> callback) {
        final KebabMenuItem menuItem = kebabMenuItems.get();
        menuItem.setText(label);
        menuItem.getElement().id = Document.get().createUniqueId();
        setCallback(menuItem.getElement().id,
                    value,
                    callback);
        menu.addKebabItem((HTMLLIElement) menuItem.getElement());
    }

    /* Needs to manualy set the positon in order to dispaly the kebab outside the table overflow hidden area. */
    public void fixOpenPosition(final String id) {
        Scheduler.get().scheduleDeferred(() -> {
            fixKebabMenuOpenPosition(id);
        });
    }

    public void setCallback(final String id,
                            final T value,
                            final Consumer<T> callback) {
        Scheduler.get().scheduleDeferred(() -> {
            final Element elementById = document.getElementById(id);
            if (elementById != null) {
                elementById.addEventListener("click",
                                             e -> callback.accept(value));
            }
        });
    }

    private native void fixKebabMenuOpenPosition(final String id) /*-{
        $wnd.jQuery("#" + id).on("show.bs.dropdown", function () {
            var btnDropDown = $wnd.jQuery(this).find(".dropdown-toggle");
            var listHolder = $wnd.jQuery(this).find(".dropdown-menu");
            listHolder.css({
                "top": btnDropDown.offset().top - listHolder.outerHeight(true) + "px",
                "left": btnDropDown.offset().left - 130 + "px",
                "position": "fixed",
                "width": listHolder.width() + "px",
                "height": listHolder.outerHeight() + "px"
            });
        });
    }-*/;
}