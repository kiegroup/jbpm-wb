/*
 * Copyright 2014 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.gc.client.experimental.details;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import org.jbpm.console.ng.gc.client.experimental.details.base.DetailsTabbedPanel;
import org.uberfire.client.mvp.UberView;

/**
 * @param <T> presenter extending AbstractTabbedDetailsPresenter
 * @author salaboy
 */
public abstract class AbstractTabbedDetailsView<T extends AbstractTabbedDetailsPresenter>
        extends Composite implements RequiresResize {

    protected DetailsTabbedPanel tabPanel;

    protected T presenter;

    public interface TabbedDetailsView<T> extends UberView<T> {

        DetailsTabbedPanel getTabPanel();
    }

    public void init( final T presenter ) {
        this.presenter = presenter;
        tabPanel = new DetailsTabbedPanel();
        initWidget( tabPanel );
        initTabs();
    }

    @Override
    public void onResize() {

    }

    public abstract void initTabs();

    public DetailsTabbedPanel getTabPanel() {
        return tabPanel;
    }
}
