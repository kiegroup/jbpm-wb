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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jbpm.console.ng.shared.fb.events.PaletteItemUpdatedEvent;
import org.jbpm.form.builder.ng.model.client.bus.FormItemSelectionEvent;
import org.uberfire.client.mvp.PlaceManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Main view. Uses UIBinder to define the correct position of components
 */
@Dependent
public class FormBuilderPropertiesViewImpl extends AbsolutePanel
        implements
        FormBuilderPropertiesPresenter.FormBuilderView {

    interface FormBuilderViewImplBinder
            extends
            UiBinder<Widget, FormBuilderPropertiesViewImpl> {
    }
    private static FormBuilderViewImplBinder uiBinder = GWT.create(FormBuilderViewImplBinder.class);
    @Inject
    private PlaceManager placeManager;
    @Inject
    private Event<PaletteItemUpdatedEvent> itemUpdatedManager;
    private FormBuilderPropertiesPresenter presenter;
   
    @UiField(provided = true)
    public ScrollPanel propertiesView;
    
    
    
    @Override
    public void init(final FormBuilderPropertiesPresenter presenter) {
        this.presenter = presenter;
        init();
    }

    protected final void init() {
    	propertiesView = new EditionViewImpl(itemUpdatedManager);

    	add(uiBinder.createAndBindUi(this));
    }

    public void selectedItem(@Observes FormItemSelectionEvent event) {
        if (event.isSelected()) {
            ((EditionView) propertiesView).selectTab();
            ((EditionView) propertiesView).populate(event.getContext());
        }
    }

   
    public ScrollPanel getPropertiesView() {
        return propertiesView;
    }

   
    public AbsolutePanel getPanel() {
        return this;
    }

   
}
