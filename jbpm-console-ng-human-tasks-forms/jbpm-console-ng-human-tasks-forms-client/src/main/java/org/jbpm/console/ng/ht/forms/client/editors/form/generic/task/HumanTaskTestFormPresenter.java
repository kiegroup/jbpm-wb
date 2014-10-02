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
package org.jbpm.console.ng.ht.forms.client.editors.form.generic.task;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.console.ng.ht.forms.client.i18n.Constants;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "HumanTaskTest Form")
public class HumanTaskTestFormPresenter {

  private Constants constants = GWT.create(Constants.class);

  public interface HumanTaskTestFormView extends UberView<HumanTaskTestFormPresenter> {

    void displayNotification(String text);

    TextBox getInputTextBox();

    TextBox getOutputTextBox();

    TextBox getTaskIdTextBox();

    void setReadOnly(boolean readOnly);

  }

  @Inject
  private HumanTaskTestFormView view;

  @Inject
  private User identity;

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
  public UberView<HumanTaskTestFormPresenter> getView() {
    return view;
  }

  public HumanTaskTestFormPresenter() {
  }

  @PostConstruct
  public void init() {
  }

  @OnOpen
  public void onOpen() {
    String taskId = place.getParameter("taskId", "");
    String inputs = place.getParameter("inputs", "");
    String outputs = place.getParameter("outputs", "");

    view.getInputTextBox().setText(inputs);
    view.getTaskIdTextBox().setText(taskId);
    view.getOutputTextBox().setText(outputs);
    view.getOutputTextBox().setName("out_variable");
  }
}
