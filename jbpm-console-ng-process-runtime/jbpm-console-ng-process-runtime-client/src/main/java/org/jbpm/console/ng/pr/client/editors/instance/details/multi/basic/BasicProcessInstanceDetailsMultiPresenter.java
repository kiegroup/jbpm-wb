package org.jbpm.console.ng.pr.client.editors.instance.details.multi.basic;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.console.ng.pr.client.editors.instance.details.multi.BaseProcessInstanceDetailsMultiPresenter;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchScreen(identifier = "Basic Process Instance Details Multi", preferredWidth = 500)
public class BasicProcessInstanceDetailsMultiPresenter extends BaseProcessInstanceDetailsMultiPresenter {

    public interface BasicProcessInstanceDetailsMultiView
            extends BaseProcessInstanceDetailsMultiPresenter.BaseProcessInstanceDetailsMultiView {

    }

    @Inject
    private BasicProcessInstanceDetailsMultiView view;

    @WorkbenchMenu
    public Menus buildSpecificMenu() {
        return buildMenu();
    }

    @Override
    protected BaseProcessInstanceDetailsMultiView getSpecificView() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Process_Instances();
    }

    @WorkbenchPartView
    public UberView<BaseProcessInstanceDetailsMultiPresenter> getView() {
        return view;
    }

    @DefaultPosition
    public Position getPosition() {
        return super.getPosition();
    }
}
