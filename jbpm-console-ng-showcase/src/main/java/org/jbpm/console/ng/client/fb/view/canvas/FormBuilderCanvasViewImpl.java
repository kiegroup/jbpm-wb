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
package org.jbpm.console.ng.client.fb.view.canvas;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jbpm.console.ng.shared.fb.events.FormLoadedEvent;
import org.jbpm.console.ng.shared.fb.events.PaletteItemUpdatedEvent;
import org.jbpm.form.builder.ng.model.client.form.EditionContext;
import org.jbpm.form.builder.ng.model.client.form.FBCompositeItem;
import org.jbpm.form.builder.ng.model.client.form.FBForm;
import org.jbpm.form.builder.ng.model.client.form.FBFormItem;
import org.jbpm.form.builder.ng.model.shared.api.FormRepresentation;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Main view. Uses UIBinder to define the correct position of components
 */
@Dependent
public class FormBuilderCanvasViewImpl extends AbsolutePanel
        implements
        FormBuilderCanvasPresenter.FormBuilderView {

    interface FormBuilderViewImplBinder
            extends
            UiBinder<Widget, FormBuilderCanvasViewImpl> {
    }
    private static FormBuilderViewImplBinder uiBinder = GWT.create(FormBuilderViewImplBinder.class);
    @Inject
    private PlaceManager placeManager;
    private FormBuilderCanvasPresenter presenter;
    public @UiField(provided = true)
    ScrollPanel layoutView;
    @UiField
    public Button saveButton;
    @UiField
    public Button clearButton;
    
    @Inject WorkbenchPickupDragController dndController;

    @Override
    public void init(final FormBuilderCanvasPresenter presenter) {
        this.presenter = presenter;
        layoutView = new CanvasViewImpl();

        layoutView.setSize("700px",
                "700px");
        layoutView.setAlwaysShowScrollBars(true);
        
        add(uiBinder.createAndBindUi(this));
        
        
    }

    public void updateItem(@Observes PaletteItemUpdatedEvent event) {
    	FBForm form = ((CanvasViewImpl) layoutView).getFormDisplay();
    	for (FBFormItem item : form.getItems()) {
    		updateItem(item, event.getContext());
    	}
    }
    
    protected void updateItem(FBFormItem item, EditionContext context) {
    	if (item instanceof FBCompositeItem) {
    		FBCompositeItem compItem = (FBCompositeItem) item;
    		for (FBFormItem subItem : compItem.getItems()) {
    			updateItem(subItem, context);
    		}
    	}
    	item.save(context);
    }

    public ScrollPanel getLayoutView() {
        return layoutView;
    }

    public AbsolutePanel getPanel() {
        return this;
    }

    public void loadForm(@Observes FormLoadedEvent event) {
        presenter.decodeForm(event.getJsonForm());
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
}
