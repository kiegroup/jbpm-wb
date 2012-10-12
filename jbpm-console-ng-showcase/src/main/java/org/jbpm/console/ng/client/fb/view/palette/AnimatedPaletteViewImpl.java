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
package org.jbpm.console.ng.client.fb.view.palette;

import com.allen_sauer.gwt.dnd.client.DragHandlerAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.DecoratedStackPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jbpm.console.ng.client.fb.command.DisposeDropController;
import org.jbpm.form.builder.ng.model.client.CommonGlobals;
import org.jbpm.form.builder.ng.model.client.menu.FBMenuItem;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController;

public class AnimatedPaletteViewImpl extends ScrollPanel implements PaletteView {

    private PickupDragController dragController;
    private Map<String, List<FBMenuItem>> items = new HashMap<String, List<FBMenuItem>>();
    private Map<String, FBPalettePanel> displays = new HashMap<String, FBPalettePanel>();
    private StackPanel panel = new StackPanel() {
        @Override
        public void showStack(int index) {
            super.showStack(index);
            FBPalettePanel panel = (FBPalettePanel) getWidget(index);
            for (Widget widget : panel) {
                dragController.makeDraggable(widget);
            }
        }
    ;

    };
    
    public AnimatedPaletteViewImpl() {
//        LayoutPanel layoutPanel = new LayoutPanel(new BoxLayout(BoxLayout.Orientation.VERTICAL));
//        layoutPanel.setLayoutData(new BoxLayoutData(BoxLayoutData.FillStyle.BOTH));
//        layoutPanel.setAnimationEnabled(true);
        DecoratedStackPanel stackPanel = new DecoratedStackPanel();
        stackPanel.setWidth("200px");
        panel.setStylePrimaryName("fbStackPanel");
        //        layoutPanel.add(panel);
        //        add(layoutPanel);
        stackPanel.add(panel);
        add(stackPanel);
        this.dragController = CommonGlobals.getInstance().getDragController();
        startDropController(this.dragController);
        

    }

    public void startDropController(PickupDragController dragController) {
        this.dragController = dragController;
        this.dragController.registerDropController(new DisposeDropController(this));
        this.dragController.setBehaviorMultipleSelection(false);
        this.dragController.setConstrainWidgetToBoundaryPanel(false);
        this.dragController.addDragHandler(new DragHandlerAdapter());
    }

    public void addItem(String group, FBMenuItem item) {
        if (items.get(group) == null) {
            items.put(group, new ArrayList<FBMenuItem>());
            FBPalettePanel listDisplay = new FBPalettePanel(dragController);
            panel.add(listDisplay, group);
            displays.put(group, listDisplay);
        }
        this.displays.get(group).add(item);
        this.items.get(group).add(item);
    }

    public void removeItem(String group, FBMenuItem item) {
        List<FBMenuItem> groupItems = items.get(group);
        if (groupItems != null) {
            groupItems.remove(item);
            FBPalettePanel display = displays.get(group);
            display.fullRemove(item);
            if (groupItems.isEmpty()) {
                panel.remove(display);
                panel.showStack(0);
            }
        }
    }
    
    public void removeAllItems(){
        items.clear();
    }
}
