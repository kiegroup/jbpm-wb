package org.jbpm.console.ng.pr.client.editors.definition.details.basic;

import javax.enterprise.context.Dependent;

import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.pr.client.editors.definition.details.BaseProcessDefDetailsViewImpl;
import org.jbpm.console.ng.pr.client.i18n.Constants;

import com.google.gwt.core.client.GWT;

@Dependent
@Templated(value = "BasicProcessDefDetailsViewImpl.html")
public class BasicProcessDefDetailsViewImpl extends
        BaseProcessDefDetailsViewImpl implements
        BasicProcessDefDetailsPresenter.BasicProcessDefDetailsView {

    private Constants constants = GWT.create(Constants.class);

    @Override
    public void init() {
        processIdLabel.setText(constants.Process_Definition_Id());
        processNameLabel.setText(constants.Process_Definition_Name());
        deploymentIdLabel.setText(constants.Deployment_Name());
    }

}
