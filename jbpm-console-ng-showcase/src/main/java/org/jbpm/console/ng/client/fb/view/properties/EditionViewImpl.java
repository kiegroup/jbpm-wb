/*
 * Copyright 2011 JBoss Inc 
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
package org.jbpm.console.ng.client.fb.view.properties;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Event;

import org.jbpm.console.ng.client.fb.command.DisposeDropController;
import org.jbpm.console.ng.shared.fb.events.PaletteItemUpdatedEvent;
import org.jbpm.form.builder.ng.model.client.CommonGlobals;
import org.jbpm.form.builder.ng.model.client.form.EditionContext;
import org.jbpm.form.builder.ng.model.client.form.FBFormItem;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Edition panel
 */
public class EditionViewImpl extends ScrollPanel implements EditionView {

    private SimplePanel panel = new SimplePanel();
    private final Event<PaletteItemUpdatedEvent> itemUpdatedManager;
    
    public EditionViewImpl(Event<PaletteItemUpdatedEvent> itemUpdatedManager) {
    	setSize("300px", "500px");//TODO resize strategy
        setAlwaysShowScrollBars(false);
        panel.setSize("300px", "500px");//TODO resize strategy
        add(panel);
        
        this.itemUpdatedManager = itemUpdatedManager;
        //PickupDragController dragController = CommonGlobals.getInstance().getDragController();
        //dragController.registerDropController(new DisposeDropController(this));
    }
    
    @Override
    public void selectTab() {
        /*Widget parent = getParent();
        while (!(parent instanceof TabLayoutPanel)) {
            parent = parent.getParent();
        }
        TabLayoutPanel tab = (TabLayoutPanel) parent;
        tab.selectTab(this);*/
    }
    
    @Override
    public void populate(final EditionContext context) {
        final Map<String, Object> map = context.getMap();
        final Grid grid = new Grid(map.size() + 2, 2);
        grid.setWidget(0, 0, new HTML("<strong>Name</strong>")); //TODO i18n
        grid.setWidget(0, 1, new HTML("<strong>Value</strong>")); //TODO i18n
        int index = 1;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            grid.setWidget(index, 0, new Label(entry.getKey()));
            TextBox textBox = new TextBox();
            textBox.setText(entry.getValue() == null ? "" : entry.getValue().toString());
            grid.setWidget(index, 1, textBox);
            index++;
        }
        Button saveButton = new Button("Save changes"); //TODO i18n
        saveButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
            	context.setMap(asPropertiesMap(grid));
            	itemUpdatedManager.fire(new PaletteItemUpdatedEvent(context));
            	//onSaveChanges(map, asPropertiesMap(grid), itemSelected);
            }
        });
        
        Button resetButton = new Button("Reset changes"); //TODO i18n
        resetButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
            	populate(context);
                //onResetChanges(itemSelected.cloneItem(), asPropertiesMap(grid));
            }
        });
        
        grid.setWidget(index, 0, saveButton);
        grid.setWidget(index, 1, resetButton);
        
        panel.clear();
        panel.add(grid);
        setVerticalScrollPosition(0);
    }
    
    public void onSaveChanges(Map<String, Object> oldProps, Map<String, Object> newProps, FBFormItem itemSelected) {
        /*Map<String, Object> dataSnapshot = new HashMap<String, Object>();
        dataSnapshot.put("oldItems", oldProps);
        dataSnapshot.put("newItems", newProps);
        dataSnapshot.put("itemSelected", itemSelected);
		bus.fireEvent(new UndoableEvent(dataSnapshot, new UndoableHandler() {
            @Override
            public void onEvent(UndoableEvent event) {  }
            @Override
            @SuppressWarnings("unchecked")
            public void undoAction(UndoableEvent event) {
                FBFormItem itemSelected = (FBFormItem) event.getData("itemSelected");
                itemSelected.saveValues((Map<String, Object>) event.getData("oldItems"));
            }
            @Override
            @SuppressWarnings("unchecked")
            public void doAction(UndoableEvent event) {
                FBFormItem itemSelected = (FBFormItem) event.getData("itemSelected");
                itemSelected.saveValues((Map<String, Object>) event.getData("newItems"));
            }
        }));*/
    }
    
    public void onResetChanges(FBFormItem fakeItem, Map<String, Object> newItems) {
        /*Map<String, Object> dataSnapshot = new HashMap<String, Object>();
        dataSnapshot.put("newItems", newItems);
        dataSnapshot.put("fakeItemSelected", fakeItem);
        bus.fireEvent(new UndoableEvent(dataSnapshot, new UndoableHandler() {
            @Override
            public void onEvent(UndoableEvent event) {  }
            @Override
            @SuppressWarnings("unchecked")
            public void undoAction(UndoableEvent event) {
                FBFormItem itemSelected = (FBFormItem) event.getData("fakeItemSelected");
                itemSelected.saveValues((Map<String, Object>) event.getData("newItems"));
                editView.populate(itemSelected);
            }
            @Override
            public void doAction(UndoableEvent event) {
                FBFormItem itemSelected = (FBFormItem) event.getData("fakeItemSelected");
                editView.populate(itemSelected);
            }
        }));*/
    }


    @Override
    public void clear() {
        panel.clear();
    }
    
    private Map<String, Object> asPropertiesMap(Grid grid) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (int row = 1; row < grid.getRowCount() - 1; row++) {
            map.put(
                ((Label) grid.getWidget(row, 0)).getText(), 
                ((HasValue<?>) grid.getWidget(row, 1)).getValue()
            );
        }
        return map;
    }
    
}
