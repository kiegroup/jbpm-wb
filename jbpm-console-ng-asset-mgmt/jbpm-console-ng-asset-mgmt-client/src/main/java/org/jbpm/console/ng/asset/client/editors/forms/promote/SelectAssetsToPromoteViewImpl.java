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
package org.jbpm.console.ng.asset.client.editors.forms.promote;

import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.asset.client.i18n.Constants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.security.Identity;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "SelectAssetsToPromoteViewImpl.html")
public class SelectAssetsToPromoteViewImpl extends Composite implements SelectAssetsToPromotePresenter.SelectAssetsToPromoteView {

    @Inject
    private Identity identity;

    @Inject
    private PlaceManager placeManager;

    private SelectAssetsToPromotePresenter presenter;

    
    @Inject
    @DataField
    public Label chooseRepositoryLabel;
    
//    @Inject
//    @DataField
//    public ListBox chooseRepositoryBox;
    
    @Inject
    @DataField
    public TextBox taskIdBox;
    
    @Inject
    @DataField
    public TextBox chooseRepositoryBox;
 

    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create(Constants.class);

    @Override
    public void init(SelectAssetsToPromotePresenter presenter) {
        this.presenter = presenter;
        chooseRepositoryLabel.setText(constants.Choose_Repository());
       
    }

    public TextBox getTaskIdBox() {
        return taskIdBox;
    }

   
    
    
//
//    @EventHandler("promoteButton")
//    public void promoteButton(ClickEvent e) {
//       
//       
//    }

   

    @Override
    public void displayNotification(String text) {
        notification.fire(new NotificationEvent(text));
    }


   
    @Override
    public TextBox getChooseRepositoryBox() {
        return chooseRepositoryBox;
    }

   

}
