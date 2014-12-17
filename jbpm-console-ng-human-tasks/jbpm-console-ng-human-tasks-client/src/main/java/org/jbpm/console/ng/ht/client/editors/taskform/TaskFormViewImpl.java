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
package org.jbpm.console.ng.ht.client.editors.taskform;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jbpm.console.ng.gc.client.experimental.details.AbstractTabbedDetailsView;
import org.jbpm.console.ng.ht.client.editors.taskadmin.TaskAdminPresenter;
import org.jbpm.console.ng.ht.client.editors.taskassignments.TaskAssignmentsPresenter;
import org.jbpm.console.ng.ht.client.editors.taskcomments.TaskCommentsPresenter;
import org.jbpm.console.ng.ht.client.editors.taskdetails.TaskDetailsPresenter;
import org.jbpm.console.ng.ht.client.editors.taskdetailsmulti.TaskDetailsMultiPresenter;
import org.jbpm.console.ng.ht.client.editors.taskprocesscontext.TaskProcessContextPresenter;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.jbpm.console.ng.ht.forms.client.display.views.EmbeddedFormDisplayView;
import org.jbpm.console.ng.ht.forms.display.view.FormDisplayerView;

import static com.github.gwtbootstrap.client.ui.resources.ButtonSize.*;

@Dependent
public class TaskFormViewImpl extends Composite implements TaskFormPresenter.TaskFormView {

    interface Binder
            extends
            UiBinder<Widget, TaskFormViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    FlowPanel content;

    @Inject
    private EmbeddedFormDisplayView view;

    @PostConstruct
    public void init() {
        initWidget(uiBinder.createAndBindUi(this));
        content.add(view.getView());
    }

    @Override
    public FormDisplayerView getDisplayerView() {
        return view;
    }
}
