package org.jbpm.console.ng.ht.forms.client.display.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jbpm.console.ng.ht.forms.display.GenericFormDisplayer;
import org.jbpm.console.ng.ht.forms.display.view.FormContentResizeListener;
import org.jbpm.console.ng.ht.forms.display.view.FormDisplayerView;
import org.uberfire.mvp.Command;

public class EmbeddedFormDisplayView implements FormDisplayerView {

    @Inject
    private VerticalPanel formContainer;

    private GenericFormDisplayer currentDisplayer;

    private Command onCloseCommand;

    private FormContentResizeListener resizeListener;

    @PostConstruct
    public void init() {
        formContainer.setWidth("100%");
    }


    @Override
    public void display(GenericFormDisplayer displayer) {
        currentDisplayer = displayer;
        formContainer.clear();
        formContainer.add(displayer.getContainer());
        if (displayer.getOpener() == null) formContainer.add(displayer.getFooter());
    }

    public Widget getView() {
        return formContainer;
    }

    @Override
    public Command getOnCloseCommand() {
        return onCloseCommand;
    }

    @Override
    public void setOnCloseCommand(Command onCloseCommand) {
        this.onCloseCommand = onCloseCommand;
    }

    @Override
    public FormContentResizeListener getResizeListener() {
        return resizeListener;
    }

    @Override
    public void setResizeListener(FormContentResizeListener resizeListener) {
        this.resizeListener = resizeListener;
    }

    @Override
    public GenericFormDisplayer getCurrentDisplayer() {
        return currentDisplayer;
    }
}
