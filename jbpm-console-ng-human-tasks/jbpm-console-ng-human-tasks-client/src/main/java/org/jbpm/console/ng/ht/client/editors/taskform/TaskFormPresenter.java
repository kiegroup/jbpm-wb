package org.jbpm.console.ng.ht.client.editors.taskform;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.console.ng.ht.forms.display.view.FormDisplayerView;

@Dependent
public class TaskFormPresenter {
    public interface TaskFormView extends IsWidget {
        FormDisplayerView getDisplayerView();
    }

    @Inject
    TaskFormView view;

    public TaskFormView getTaskFormView() {
        return view;
    }

    public IsWidget getView() {
        return view;
    }
}
