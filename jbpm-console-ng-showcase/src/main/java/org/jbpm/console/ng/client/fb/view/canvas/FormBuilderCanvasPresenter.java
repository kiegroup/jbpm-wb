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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.shared.fb.FormServiceEntryPoint;
import org.jbpm.form.builder.ng.model.client.CommonGlobals;
import org.jbpm.form.builder.ng.model.client.messages.I18NConstants;
import org.jbpm.form.builder.ng.model.shared.api.FormRepresentation;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController;

@Dependent
@WorkbenchScreen(identifier = "Form Builder - Canvas")
public class FormBuilderCanvasPresenter {

    @Inject
    private FormBuilderView view;
    @Inject
    private Caller<FormServiceEntryPoint> formServices;

    @Inject WorkbenchPickupDragController dndController;
    
    public interface FormBuilderView
            extends
            UberView<FormBuilderCanvasPresenter> {

        

        ScrollPanel getLayoutView();
        
        AbsolutePanel getPanel();
    }

    @PostConstruct
    public void init() {
        CommonGlobals.getInstance().registerI18n((I18NConstants) GWT.create(I18NConstants.class));
        
        
    }
   
    
    
    public void decodeForm(String jsonForm) {
        
        formServices.call(new RemoteCallback<FormRepresentation>() {
                @Override
                public void callback(FormRepresentation formRep) {
                    ((CanvasViewImpl)view.getLayoutView()).getFormDisplay().populate(formRep);
                }
        }).loadForm(jsonForm);
    
    }
    
    @UiHandler(value="saveButton")
    public void saveForm(FormRepresentation formRep){
        
        formServices.call(new RemoteCallback<String>() {
                @Override
                public void callback(String content) {
                    System.out.println("XXXXX  RETURN List Menu Items"+content);
                }
        }).saveForm(formRep);
    
    }
    
    
    
    @WorkbenchPartTitle
    public String getTitle() {
        return "Form Builder - Canvas";
    }

    @WorkbenchPartView
    public UberView<FormBuilderCanvasPresenter> getView() {
        return view;
    }
}
