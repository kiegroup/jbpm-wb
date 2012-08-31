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
package org.jbpm.console.ng.client.view.display;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jbpm.console.ng.shared.fb.events.FormRenderedEvent;
import org.uberfire.client.mvp.PlaceManager;

/**
 * Main view. Uses UIBinder to define the correct position of components
 */
@Dependent
public class FormDisplayViewImpl extends Composite implements FormDisplayPresenter.FormBuilderView{

    @Inject
    private UiBinder<Widget, FormDisplayViewImpl> uiBinder;

    @Inject
    private PlaceManager placeManager;
    
    private FormDisplayPresenter presenter;
    
    @UiField
    public ScrollPanel formView;
    
    @UiField
    public TextBox taskIdText;
    
    @UiField
    public Button renderButton;

    
    @UiHandler("renderButton")
    public void renderAction(ClickEvent e){
        formView.setSize("400px", "400px");
        presenter.renderForm(1);
    
    }

    public void renderForm(@Observes FormRenderedEvent formRendered){
        formView.add(new HTMLPanel(formRendered.getForm()));
        
    }

    @Override
    public void init(FormDisplayPresenter presenter) {
        System.out.println("Init is being called");
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));
        
    }
   
    
    

    
    
}
