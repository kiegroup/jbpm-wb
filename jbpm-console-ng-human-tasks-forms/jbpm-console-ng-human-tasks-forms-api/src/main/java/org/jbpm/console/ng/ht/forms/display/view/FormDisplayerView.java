package org.jbpm.console.ng.ht.forms.display.view;

import org.jbpm.console.ng.ga.service.ItemKey;
import org.jbpm.console.ng.ht.forms.display.FormDisplayerConfig;
import org.jbpm.console.ng.ht.forms.display.GenericFormDisplayer;
import org.uberfire.mvp.Command;

public interface FormDisplayerView {
    void display(GenericFormDisplayer<? extends ItemKey> display);

    Command getOnCloseCommand();

    void setOnCloseCommand(Command onCloseCommand);

    FormContentResizeListener getResizeListener();

    void setResizeListener(FormContentResizeListener resizeListener);

    GenericFormDisplayer getCurrentDisplayer();
}
