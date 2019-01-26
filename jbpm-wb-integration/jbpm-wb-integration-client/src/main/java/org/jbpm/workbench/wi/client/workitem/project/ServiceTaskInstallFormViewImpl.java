/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.wi.client.workitem.project;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.gwtbootstrap3.client.shared.event.ModalHideEvent;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.FormType;
import org.jbpm.workbench.wi.client.i18n.Constants;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.FormStyleLayout;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class ServiceTaskInstallFormViewImpl
        extends BaseModal implements ServiceTaskInstallFormView {
    
    private String serviceTaskId;
    private String target;
    private String referenceLink;
    
    private FormStyleLayout form;
    
    private Presenter presenter;
    
    private List<TextBox> fields = new ArrayList<>();

    public ServiceTaskInstallFormViewImpl() {
        this.setTitle(Constants.INSTANCE.InstallServiceTaskParams()); 
    }

    private void doForm(List<String> parameters) {
        form = new FormStyleLayout();
        form.setType(FormType.HORIZONTAL);
        
        Anchor link = new Anchor();
        link.setText(referenceLink);
        link.setHref(referenceLink);
        link.setTarget("_blank");
        
        form.addAttribute("Reference Link", link);
                
        for (String param : parameters) {
            TextBox paramField = GWT.create(TextBox.class);
            form.addAttribute(param, paramField);
            fields.add(paramField);
        }
        
        this.setBody(form);
        Button installButton = new Button(Constants.INSTANCE.InstallServiceTask());
        installButton.setType(ButtonType.PRIMARY);
        installButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                List<String> parameters = fields.stream().map(textbox -> textbox.getValue()).collect(Collectors.toList());
                presenter.installWithParameters(serviceTaskId, target, parameters);
                hide();
            }
        });
        ModalFooter footer = new ModalFooter();
        Button cancelButton =  new Button(Constants.INSTANCE.Cancel());
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        footer.add(installButton);
        footer.add(cancelButton);

        this.add(footer);        
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;
        
        this.addHideHandler((ModalHideEvent evt) -> this.presenter.onCloseCommand().execute());
    }

    @Override
    public void showInstallBusy() {
        BusyPopup.showMessage(Constants.INSTANCE.Installing());
    }

    @Override
    public void hideInstallBusy() {
        BusyPopup.close();
    }

    @Override
    public void show(String serviceTaskId, String target, List<String> parameters, String referenceLink) {
        this.serviceTaskId = serviceTaskId;
        this.target = target;
        this.referenceLink = referenceLink;
        doForm(parameters);
        super.show();      
    }
}
