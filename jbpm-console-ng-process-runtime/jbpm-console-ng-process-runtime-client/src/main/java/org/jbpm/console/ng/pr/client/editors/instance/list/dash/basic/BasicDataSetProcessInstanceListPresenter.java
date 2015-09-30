package org.jbpm.console.ng.pr.client.editors.instance.list.dash.basic;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.console.ng.pr.client.editors.instance.list.dash.BaseDataSetProcessInstanceListPresenter;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;

@Dependent
@WorkbenchScreen(identifier = "Basic DataSet Process Instance List")
public class BasicDataSetProcessInstanceListPresenter extends BaseDataSetProcessInstanceListPresenter {
    
    public interface BasicDataSetProcessInstanceListView extends BaseDataSetProcessInstanceListView {

    }
    
    @Inject 
    private BasicDataSetProcessInstanceListView view;
    
    @Override
    protected BaseDataSetProcessInstanceListView getSpecificView() {
        return view;
    }
    
    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Process_Instances();
    }

    @WorkbenchPartView
    public UberView<BaseDataSetProcessInstanceListPresenter> getView() {
        return view;
    }

}