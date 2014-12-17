package org.jbpm.console.ng.ht.forms.client.display.views;

import com.github.gwtbootstrap.client.ui.ModalFooter;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.github.gwtbootstrap.client.ui.event.HiddenEvent;
import com.github.gwtbootstrap.client.ui.event.HiddenHandler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import org.jbpm.console.ng.ht.forms.client.display.providers.StartProcessFormDisplayProviderImpl;
import org.jbpm.console.ng.ht.forms.display.GenericFormDisplayer;
import org.jbpm.console.ng.ht.forms.display.view.FormContentResizeListener;
import org.jbpm.console.ng.ht.forms.display.view.FormDisplayerView;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.mvp.Command;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class PopupFormDisplayerView extends BaseModal implements FormDisplayerView {
    @Inject
    private StartProcessFormDisplayProviderImpl widgetPresenter;

    private Command onCloseCommand;

    private Command childCloseCommand;

    private FormContentResizeListener formContentResizeListener;

    private boolean initialized = false;

    private FlowPanel body = new FlowPanel();

    private ModalFooter footer = new ModalFooter();

    private GenericFormDisplayer currentDisplayer;

    @PostConstruct
    protected void init() {
        onCloseCommand = new Command() {
            @Override
            public void execute() {
                closePopup();
            }
        };

        formContentResizeListener = new FormContentResizeListener () {
            @Override
            public void resize(int width, int height) {
                if (width > 0) setWidth(width + 20);
                centerVertically(getElement());
            }
        };
        add(body);
        add(footer);
        this.addHiddenHandler(new HiddenHandler() {
            @Override
            public void onHidden(HiddenEvent hiddenEvent) {
                if (initialized) closePopup();
            }
        });
    }

    @Override
    public void display(GenericFormDisplayer displayer) {
        setBackdrop(BackdropType.NORMAL);
        setKeyboard(true);
        setAnimation(true);
        setDynamicSafe(true);
        currentDisplayer = displayer;
        body.clear();
        footer.clear();
        body.add(displayer.getContainer());
        if (displayer.getOpener() == null) footer.add(displayer.getFooter());
        centerVertically(getElement());
        initialized = true;
        show();
    }

    public void closePopup() {
        hide();
        if (childCloseCommand != null) childCloseCommand.execute();
        setWidth("");
        initialized = false;
    }

    private native void centerVertically(Element e) /*-{
        $wnd.jQuery(e).css("margin-top", (-1 * $wnd.jQuery(e).outerHeight() / 2) + "px");
    }-*/;

    @Override
    public Command getOnCloseCommand() {
        return onCloseCommand;
    }

    @Override
    public void setOnCloseCommand(Command onCloseCommand) {
        this.childCloseCommand = onCloseCommand;
    }

    @Override
    public FormContentResizeListener getResizeListener() {
        return formContentResizeListener;
    }

    @Override
    public void setResizeListener(FormContentResizeListener resizeListener) {
        formContentResizeListener = resizeListener;
    }

    @Override
    public GenericFormDisplayer getCurrentDisplayer() {
        return currentDisplayer;
    }
}
