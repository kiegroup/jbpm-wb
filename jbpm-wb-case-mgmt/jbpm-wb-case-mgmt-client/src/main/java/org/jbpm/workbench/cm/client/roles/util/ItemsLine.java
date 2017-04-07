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

package org.jbpm.workbench.cm.client.roles.util;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.shared.api.annotations.DataField;

import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.cm.client.events.CaseRoleAssignmentListOpenEvent;
import org.jbpm.workbench.cm.client.roles.CaseRolesPresenter;
import org.jbpm.workbench.cm.client.util.AbstractView;

import static org.jboss.errai.common.client.dom.DOMUtil.*;
import static org.jboss.errai.common.client.dom.Window.getDocument;

@Dependent
@Templated
public class ItemsLine extends AbstractView<CaseRolesPresenter> {
    public static int MORE_LINK_SIZE = 60;

    @Inject
    @DataField("items-line")
    Div itemsLine;

    @Inject
    @DataField("visible-items")
    UnorderedList visibleItems;

    @Inject
    @DataField("hidden-items")
    UnorderedList hiddenItems;

    @Inject
    @DataField("div-unassigned")
    Div unassignedDiv;

    @Inject
    @DataField("custom-popover")
    Div customPopover;

    @Inject
    @DataField("more-items-link")
    Div moreItemsLinkDiv;

    String itemsLineId;

    boolean popoverOpen = false;

    private Event<CaseRoleAssignmentListOpenEvent> roleAssignmentListOpenEvent;

    @Inject
    public void setRoleAssignmentListOpenEvent(final Event<CaseRoleAssignmentListOpenEvent> roleAssignmentListOpenEvent) {
        this.roleAssignmentListOpenEvent = roleAssignmentListOpenEvent;
    }

    public void initWithSingleItem(String itemsLineId, String item) {
        this.itemsLineId = itemsLineId;
        final HTMLElement itemText = createElement("span");
        itemText.setTextContent(item);
        final HTMLElement li = createElement("li");
        li.appendChild(itemText);
        getVisibleUnorderedList().appendChild(li);
    }

    public void initWithItemsLine(int maxWidth, String itemsLineId, List<CaseRolesPresenter.CaseAssignmentItem> items) {
        this.itemsLineId = itemsLineId;

        if (items.size() == 0) {
            displayUnassignedItem();
        } else {
            boolean overflow = false;

            for (CaseRolesPresenter.CaseAssignmentItem item : items) {
                if(!overflow){
                    addItem(item, getVisibleUnorderedList());
                    if (getVisibleUnorderedList().getBoundingClientRect().getWidth().intValue() + MORE_LINK_SIZE > maxWidth) {
                        getVisibleUnorderedList().removeChild(getVisibleUnorderedList().getLastChild());

                        final HTMLElement li = createElement("li");
                        li.appendChild(getMoreItemsLinkDiv());
                        getVisibleUnorderedList().appendChild(li);

                        overflow = true;
                        addItem(item, getHiddenUnorderedList());
                        removeCSSClass(getMoreItemsLinkDiv(), "hidden");
                    }
                } else {
                    addItem(item, getHiddenUnorderedList());
                }
            }
        }
    }

    public void displayUnassignedItem() {
        removeCSSClass(unassignedDiv, "hidden");
    }

    public void showAllItems() {
        removeCSSClass(customPopover, "hidden");
        popoverOpen = true;
    }

    public void hideAllItems() {
        addCSSClass(customPopover, "hidden");
        popoverOpen = false;
    }

    public void addItem(final CaseRolesPresenter.CaseAssignmentItem item, UnorderedList itemList) {
        final HTMLElement closeIcon = createElement("span");
        addCSSClass(closeIcon, "pficon");
        addCSSClass(closeIcon, "pficon-close");
        addCSSClass(closeIcon, "kie-remove-role-item");

        final HTMLElement itemText = createElement("span");
        itemText.setTextContent(item.label());

        final HTMLElement a = createElement("a");
        a.appendChild(closeIcon);
        a.setOnclick(e -> item.execute());

        itemText.appendChild(a);
        final HTMLElement li = createElement("li");
        li.appendChild(itemText);
        itemList.appendChild(li);
    }

    protected HTMLElement createElement(String type){
        return getDocument().createElement(type);
    }

    protected UnorderedList getVisibleUnorderedList(){
        return visibleItems ;
    }

    protected UnorderedList getHiddenUnorderedList(){
        return hiddenItems;
    }

    public Div getMoreItemsLinkDiv() {
        return moreItemsLinkDiv;
    }

    public HTMLElement getElement() {
        return itemsLine;
    }

    @EventHandler("more-items")
    @SuppressWarnings("unsued")
    public void onMoreItems(@ForEvent("click") final org.jboss.errai.common.client.dom.Event event) {
        roleAssignmentListOpenEvent.fire(new CaseRoleAssignmentListOpenEvent(itemsLineId));
        if (!popoverOpen) {
            showAllItems();
        } else {
            hideAllItems();
        }
    }

}