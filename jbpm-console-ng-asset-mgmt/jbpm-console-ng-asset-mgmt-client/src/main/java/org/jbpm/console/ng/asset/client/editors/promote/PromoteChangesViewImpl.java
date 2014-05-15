/*
 * Copyright 2012 JBoss Inc
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
package org.jbpm.console.ng.asset.client.editors.promote;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.asset.client.i18n.Constants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "PromoteChangesViewImpl.html")
public class PromoteChangesViewImpl extends Composite implements PromoteChangesPresenter.PromoteChangesView {

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    private PromoteChangesPresenter presenter;

    @Inject
    @DataField
    public Label accordionLabel;
    
    @Inject
    @DataField
    public Label chooseRepositoryLabel;
    
//    @Inject
//    @DataField
//    public ListBox chooseRepositoryBox;
    
    @Inject
    @DataField
    public TextBox chooseRepositoryBox;
    
    @Inject
    @DataField
    public Label chooseSourceBranchLabel;
    
//    @Inject
//    @DataField
//    public ListBox chooseSourceBranchBox;
    
    @Inject
    @DataField
    public TextBox chooseSourceBranchBox;
    
      @Inject
    @DataField
    public Label chooseDestBranchLabel;
    
//    @Inject
//    @DataField
//    public ListBox chooseSourceBranchBox;
    
    @Inject
    @DataField
    public TextBox chooseDestBranchBox;
  
    @Inject
    @DataField
    public Button promoteButton;

   

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create(Constants.class);

    @Override
    public void init(PromoteChangesPresenter presenter) {
        this.presenter = presenter;
        accordionLabel.setText(constants.Promote_Assets());
        chooseRepositoryLabel.setText(constants.Choose_Repository());
        chooseSourceBranchLabel.setText(constants.Choose_Source_Branch());
        chooseDestBranchLabel.setText(constants.Choose_Destination_Branch());
        promoteButton.setText(constants.Promote_Assets());
    }

    

    @EventHandler("promoteButton")
    public void promoteButton(ClickEvent e) {
        presenter.promoteChanges(chooseRepositoryBox.getText(), chooseSourceBranchBox.getText(), chooseDestBranchBox.getText());
       
    }

   

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }

//    public ListBox getChooseRepositoryBox() {
//        return chooseRepositoryBox;
//    }

   
    @Override
    public TextBox getChooseRepositoryBox() {
        return chooseRepositoryBox;
    }

   

}
