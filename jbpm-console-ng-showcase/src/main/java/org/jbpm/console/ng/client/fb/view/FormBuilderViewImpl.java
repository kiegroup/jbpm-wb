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
package org.jbpm.console.ng.client.fb.view;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jbpm.console.ng.client.fb.view.canvas.CanvasViewImpl;
import org.jbpm.console.ng.client.fb.view.palette.AnimatedPaletteViewImpl;
import org.jbpm.console.ng.client.fb.view.palette.PalettePresenter;
import org.jbpm.console.ng.client.fb.view.palette.PaletteView;
import org.jbpm.console.ng.shared.fb.events.PaletteItemAddedEvent;
import org.jbpm.form.builder.ng.model.client.menu.FBMenuItem;
import org.jbpm.form.builder.ng.model.common.reflect.ReflectionHelper;
import org.jbpm.form.builder.ng.model.shared.menu.MenuItemDescription;
import org.uberfire.client.mvp.PlaceManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jbpm.console.ng.shared.fb.events.FormLoadedEvent;
import org.jbpm.form.builder.ng.model.client.FormBuilderException;
import org.jbpm.form.builder.ng.model.client.form.FBForm;
import org.jbpm.form.builder.ng.model.shared.api.FormRepresentation;

/**
 * Main view. Uses UIBinder to define the correct position of components
 */
@Dependent
public class FormBuilderViewImpl extends AbsolutePanel
        implements
        FormBuilderPresenter.FormBuilderView {

    interface FormBuilderViewImplBinder
            extends
            UiBinder<Widget, FormBuilderViewImpl> {
    }
    private static FormBuilderViewImplBinder uiBinder = GWT.create(FormBuilderViewImplBinder.class);
    @Inject
    private PlaceManager placeManager;
    private FormBuilderPresenter presenter;
    public @UiField(provided = true)
    ScrollPanel menuView;
    public @UiField(provided = true)
    ScrollPanel layoutView;
    @UiField
    public Button saveButton;

    @UiField
    public Button clearButton;
    
    @Override
    public void init(final FormBuilderPresenter presenter) {
        this.presenter = presenter;
        init();
    }

    protected final void init() {
        menuView = new AnimatedPaletteViewImpl();
        layoutView = new CanvasViewImpl();
        menuView.setAlwaysShowScrollBars(true);
        menuView.setSize("235px",
                "100%");
        layoutView.setSize("700px",
                "700px");
        layoutView.setAlwaysShowScrollBars(true);
        add(uiBinder.createAndBindUi(this));

        ((PaletteView) menuView).removeAllItems();

    }

    public void addItem(@Observes PaletteItemAddedEvent event) {
        try {
            String group = event.getGroupName();
            MenuItemDescription menuItemDescription = event.getMenuItemDescription();
            Object newInstance = ReflectionHelper.newInstance(menuItemDescription.getClassName());
            FBMenuItem item = (FBMenuItem) newInstance;

            ((PaletteView) menuView).addItem(group,
                    item);
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(PalettePresenter.class.getName()).log(Level.SEVERE,
                    null,
                    ex);
        }
    }

    @UiHandler("saveButton")
    public void saveButton(ClickEvent e) {
        
            FBForm formDisplay = ((CanvasViewImpl) layoutView).getFormDisplay();
            FormRepresentation createRepresentation = formDisplay.createRepresentation();
            presenter.saveForm(createRepresentation);
        
    }

    @UiHandler("clearButton")
    public void clearButton(ClickEvent e) {

        ((CanvasViewImpl) layoutView).getFormDisplay().clear();

    }

    public ScrollPanel getMenuView() {
        return menuView;
    }

    public ScrollPanel getLayoutView() {
        return layoutView;
    }

    public AbsolutePanel getPanel() {
        return this;
    }

    public void loadForm(@Observes FormLoadedEvent event)  {
        presenter.decodeForm(event.getJsonForm());
    }
}
