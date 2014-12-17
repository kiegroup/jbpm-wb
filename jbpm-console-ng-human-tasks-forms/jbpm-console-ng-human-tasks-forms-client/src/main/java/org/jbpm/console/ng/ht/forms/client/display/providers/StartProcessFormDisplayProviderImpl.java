package org.jbpm.console.ng.ht.forms.client.display.providers;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jbpm.console.ng.ht.forms.display.process.api.ProcessDisplayerConfig;
import org.jbpm.console.ng.ht.forms.display.process.api.StartProcessFormDisplayProvider;
import org.jbpm.console.ng.ht.forms.display.view.FormContentResizeListener;
import org.jbpm.console.ng.ht.forms.client.editors.taskform.generic.GenericFormDisplayView;
import org.jbpm.console.ng.ht.forms.client.i18n.Constants;
import org.jbpm.console.ng.ht.forms.display.process.api.StartProcessFormDisplayer;
import org.jbpm.console.ng.ht.forms.display.view.FormDisplayerView;
import org.jbpm.console.ng.ht.forms.service.FormServiceEntryPoint;
import org.jbpm.console.ng.pr.model.ProcessDefinitionKey;
import org.uberfire.mvp.Command;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.*;

@Dependent
public class StartProcessFormDisplayProviderImpl implements StartProcessFormDisplayProvider {

    protected Constants constants = GWT.create(Constants.class);

    @Inject
    protected SyncBeanManager iocManager;

    @Inject
    private GenericFormDisplayView view;

    @Inject
    private Caller<FormServiceEntryPoint> formServices;

    private String currentProcessId;

    private String currentDeploymentId;

    protected String opener;

    private Command onClose;

    private Command onRefresh;

    private List<StartProcessFormDisplayer> processDisplayers = new ArrayList<StartProcessFormDisplayer>();

    private FormContentResizeListener resizeListener;

    @PostConstruct
    public void init() {
        processDisplayers.clear();
        final Collection<IOCBeanDef<StartProcessFormDisplayer>> processDisplayersBeans = iocManager.lookupBeans(StartProcessFormDisplayer.class);
        if (processDisplayersBeans != null) {
            for (final IOCBeanDef displayerDef : processDisplayersBeans) {
                processDisplayers.add((StartProcessFormDisplayer) displayerDef.getInstance());
            }
        }
        Collections.sort(processDisplayers, new Comparator<StartProcessFormDisplayer>() {

            @Override
            public int compare(StartProcessFormDisplayer o1, StartProcessFormDisplayer o2) {
                if (o1.getPriority() < o2.getPriority()) {
                    return -1;
                } else if (o1.getPriority() > o2.getPriority()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }

    @Override
    public void setup(final ProcessDisplayerConfig config, final FormDisplayerView view) {
        display(config, view);
    }

    protected void display(final ProcessDisplayerConfig config, final FormDisplayerView view) {
        if (processDisplayers != null) {
            formServices.call(new RemoteCallback<String>() {
                @Override
                public void callback(String form) {

                    for (final StartProcessFormDisplayer d : processDisplayers) {
                        if (d.supportsContent(form)) {
                            config.setFormContent(form);
                            d.init(config, view.getOnCloseCommand(), new Command() {
                                @Override public void execute() {
                                    display(config, view);
                                }
                            } , view.getResizeListener());
                            view.display(d);
                            return;
                        }
                    }
                }
            }).getFormDisplayProcess(config.getKey().getDeploymentId(), config.getKey().getProcessId());
        }
    }

    public IsWidget getView() {
        return view;
    }
}
