/*
 * Copyright 2013 JBoss by Red Hat.
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
package org.jbpm.console.ng.pr.client.editors.diagram;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.pr.client.i18n.Constants;

@Dependent
@Templated(value = "ProcessDiagramPopupViewImpl.html")
public class ProcessDiagramPopupViewImpl extends Composite implements ProcessDiagramPopupPresenter.PopupView, RequiresResize {

  private Constants constants = GWT.create(Constants.class);

  private ProcessDiagramPopupPresenter presenter;

  @Inject
  @DataField
  public FlowPanel container;

  @Override
  public void init(ProcessDiagramPopupPresenter presenter) {
    this.presenter = presenter;
    container.setSize("1000px", "600px");
    
  }

  @Override
  public FlowPanel getContainer() {
    return container;
  }

  @Override
  public void onResize() {
    Widget parent = container.getParent();
    final int width = parent.getOffsetWidth();
    final int height = parent.getOffsetHeight();
    container.setWidth(width + "px");
    container.setHeight(height + "px");
  }

}
