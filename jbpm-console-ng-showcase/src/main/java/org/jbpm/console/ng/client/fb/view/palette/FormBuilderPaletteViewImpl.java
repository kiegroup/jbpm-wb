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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jbpm.console.ng.client.fb.view.palette.AnimatedPaletteViewImpl;
import org.jbpm.console.ng.client.fb.view.palette.PaletteView;
import org.jbpm.console.ng.shared.fb.events.PaletteItemAddedEvent;
import org.jbpm.form.builder.ng.model.client.menu.FBMenuItem;
import org.jbpm.form.builder.ng.model.common.reflect.ReflectionHelper;
import org.jbpm.form.builder.ng.model.shared.menu.MenuItemDescription;
import org.uberfire.client.mvp.PlaceManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.rg.ImageResourceGenerator;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jbpm.form.builder.ng.model.shared.menu.items.CustomMenuItem;

/**
 * Main view. Uses UIBinder to define the correct position of components
 */
@Dependent
public class FormBuilderPaletteViewImpl extends AbsolutePanel
        implements
        FormBuilderPalettePresenter.FormBuilderView {

    interface FormBuilderViewImplBinder
            extends
            UiBinder<Widget, FormBuilderPaletteViewImpl> {
    }
    private static FormBuilderViewImplBinder uiBinder = GWT.create(FormBuilderViewImplBinder.class);
    @Inject
    private PlaceManager placeManager;
    private FormBuilderPalettePresenter presenter;
    public @UiField(provided = true)
    ScrollPanel menuView;
    
    
    
    @Override
    public void init(final FormBuilderPalettePresenter presenter) {
        this.presenter = presenter;
        init();
    }

    protected final void init() {
        menuView = new AnimatedPaletteViewImpl();
        
        
        menuView.setSize("235px",
                "100%");
        
        add(uiBinder.createAndBindUi(this));

        ((PaletteView) menuView).removeAllItems();

    }

     public void addItem(@Observes PaletteItemAddedEvent event) {
        try {
            String group = event.getGroupName();
            MenuItemDescription menuItemDescription = event.getMenuItemDescription();
            Object newInstance = ReflectionHelper.newInstance(menuItemDescription.getClassName());
            if (newInstance instanceof CustomMenuItem) {
                
                   CustomMenuItem customItem = (CustomMenuItem) newInstance;
                   String optionName = menuItemDescription.getName();
                   
                   customItem.setRepresentation(menuItemDescription.getItemRepresentation());
                   customItem.setOptionName(optionName);
                   customItem.setGroupName(event.getGroupName());
                   if (menuItemDescription.getIconUrl() != null) {
                	   //TODO sacarle la barra inicial a los iconUrl del menuItems.json
                	   String baseUrl = GWT.getHostPageBaseURL().replace(GWT.getModuleName() + "/", "");
                	   customItem.setIconUrlAsString(baseUrl + menuItemDescription.getIconUrl());
                   }
                   customItem.repaint();
               }
            FBMenuItem item = (FBMenuItem) newInstance;

            ((PaletteView) menuView).addItem(group,
                    item);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(FormBuilderPaletteViewImpl.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }
    }

   
    public ScrollPanel getMenuView() {
        return menuView;
    }

   
    public AbsolutePanel getPanel() {
        return this;
    }

   
}
