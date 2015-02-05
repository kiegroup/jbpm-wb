package org.jbpm.console.ng.ht.forms.client.display.providers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jbpm.console.ng.ht.forms.display.ht.api.HumanTaskDisplayerConfig;
import org.jbpm.console.ng.ht.forms.display.ht.api.HumanTaskFormDisplayer;
import org.jbpm.console.ng.ht.forms.display.ht.api.HumanTaskFormDisplayProvider;
import org.jbpm.console.ng.ht.forms.display.view.FormDisplayerView;
import org.jbpm.console.ng.ht.forms.service.FormServiceEntryPoint;
import org.uberfire.mvp.Command;

@ApplicationScoped
public class HumanTaskFormDisplayProviderImpl implements HumanTaskFormDisplayProvider {

    @Inject
    private Caller<FormServiceEntryPoint> formServices;

    @Inject
    protected SyncBeanManager iocManager;

    private List<HumanTaskFormDisplayer> taskDisplayers = new ArrayList<HumanTaskFormDisplayer>();

    @PostConstruct
    public void init() {
        taskDisplayers.clear();

        final Collection<IOCBeanDef<HumanTaskFormDisplayer>> taskDisplayersBeans = iocManager.lookupBeans(HumanTaskFormDisplayer.class);
        if (taskDisplayersBeans != null) {
            for (final IOCBeanDef displayerDef : taskDisplayersBeans) {
                taskDisplayers.add((HumanTaskFormDisplayer) displayerDef.getInstance());
            }
        }
        Collections.sort( taskDisplayers, new Comparator<HumanTaskFormDisplayer>() {

            @Override
            public int compare( HumanTaskFormDisplayer o1, HumanTaskFormDisplayer o2 ) {
                if ( o1.getPriority() < o2.getPriority() ) {
                    return -1;
                } else if ( o1.getPriority() > o2.getPriority() ) {
                    return 1;
                } else {
                    return 0;
                }
            }
        } );
    }

    @Override public void setup(final HumanTaskDisplayerConfig config, final FormDisplayerView view) {
        display(config, view);
    }

    protected void display(final HumanTaskDisplayerConfig config, final FormDisplayerView view) {
        if (taskDisplayers != null) {
            formServices.call(new RemoteCallback<String>() {
                @Override
                public void callback(String form) {
                    for (final HumanTaskFormDisplayer d : taskDisplayers) {
                        if (d.supportsContent(form)) {
                            config.setFormContent(form);
                            d.init(config, view.getOnCloseCommand(), new Command() {
                                @Override
                                public void execute() {
                                    display(config, view);
                                }
                            }, view.getResizeListener());
                            view.display(d);
                            return;
                        }
                    }
                }
            }).getFormDisplayTask(config.getKey().getTaskId());
        }
    }

}
