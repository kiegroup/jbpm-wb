/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.console.ng.pr.forms.client.display.views;

import com.github.gwtbootstrap.client.ui.ModalFooter;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.github.gwtbootstrap.client.ui.event.HiddenEvent;
import com.github.gwtbootstrap.client.ui.event.HiddenHandler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import org.jbpm.console.ng.ga.forms.display.GenericFormDisplayer;
import org.jbpm.console.ng.ga.forms.display.view.FormContentResizeListener;
import org.jbpm.console.ng.ga.forms.display.view.FormDisplayerView;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.mvp.Command;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jbpm.console.ng.pr.forms.display.process.api.StartProcessFormDisplayProvider;

@Dependent
public class PopupFormDisplayerView extends BaseModal implements FormDisplayerView {
    @Inject
    private StartProcessFormDisplayProvider widgetPresenter;

    private Command onCloseCommand;

    private Command childCloseCommand;

    private FormContentResizeListener formContentResizeListener;

    private boolean initialized = false;

    private FlowPanel body = new FlowPanel();

    private ModalFooter footer = new ModalFooter();

    private GenericFormDisplayer currentDisplayer;

    private int initialWidth = -1;

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
                if (initialWidth == -1 && getOffsetWidth() > 0) initialWidth = getOffsetWidth();
                if (width > getOffsetWidth()) setWidth(width + 20);
                else if (initialWidth != -1) setWidth(initialWidth);
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
