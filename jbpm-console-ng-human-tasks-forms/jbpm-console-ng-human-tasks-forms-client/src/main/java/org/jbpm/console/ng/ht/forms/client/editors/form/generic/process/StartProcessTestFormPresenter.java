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
package org.jbpm.console.ng.ht.forms.client.editors.form.generic.process;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.TextBox;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "test.test Form")
public class StartProcessTestFormPresenter {

  public interface StartProcessTestFormView extends UberView<StartProcessTestFormPresenter> {

    void displayNotification(String text);

    TextBox getOutputTextBox();

    TextBox getProcessIdTextBox();

  }

  @Inject
  private StartProcessTestFormView view;

  private PlaceRequest place;

  @Inject
  private PlaceManager placeManager;

  @OnStartup
  public void onStartup(final PlaceRequest place) {
    this.place = place;
    
  }

  @WorkbenchPartTitle
  public String getTitle() {
    return "Sample Form";
  }

  @WorkbenchPartView
  public UberView<StartProcessTestFormPresenter> getView() {
    return view;
  }

  public StartProcessTestFormPresenter() {
  }

  @PostConstruct
  public void init() {
  }

  @OnOpen
  public void onOpen() {
    String processId = place.getParameter("processId", "");
    String outputs = place.getParameter("outputs", "");
    view.getProcessIdTextBox().setText(processId);
    view.getOutputTextBox().setText(outputs);
    view.getOutputTextBox().setName("p_variable");
  }
  
}
