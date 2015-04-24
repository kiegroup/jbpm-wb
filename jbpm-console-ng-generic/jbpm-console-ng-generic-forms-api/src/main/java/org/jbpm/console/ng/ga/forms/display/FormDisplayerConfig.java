package org.jbpm.console.ng.ga.forms.display;

import org.jbpm.console.ng.ga.service.ItemKey;
import org.uberfire.mvp.Command;

public interface FormDisplayerConfig<T extends ItemKey> {
    T getKey();
    String getFormContent();
    String getFormOpener();
}
