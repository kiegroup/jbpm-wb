package org.jbpm.console.ng.pr.client.editors.instance.list.basic;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.console.ng.pr.client.editors.instance.list.BaseProcessInstanceListPresenter;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;

@Dependent
@WorkbenchScreen(identifier = "Basic Process Instance List")
public class BasicProcessInstanceListPresenter extends BaseProcessInstanceListPresenter {

    public interface BasicProcessInstanceListView extends BaseProcessInstanceListView {

    }

    @Inject 
    private BasicProcessInstanceListView view;
    
    @Override
    protected BaseProcessInstanceListView getSpecificView() {
        return view;
    }
    
    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Process_Instances();
    }

    @WorkbenchPartView
    public UberView<BaseProcessInstanceListPresenter> getView() {
        return view;
    }

}
