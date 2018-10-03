/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.remoteableclasses;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.services.shared.kmodule.SingleValueItemObjectModel;
import org.kie.workbench.common.widgets.client.widget.ListItemPresenter;
import org.kie.workbench.common.widgets.client.widget.ListItemView;

@Dependent
public class RemoteableClassListItemPresenter extends ListItemPresenter<SingleValueItemObjectModel, DeploymentsRemoteableClassesPresenter, RemoteableClassListItemPresenter.View> {

    private SingleValueItemObjectModel remoteableClass;

    DeploymentsRemoteableClassesPresenter parentPresenter;

    @Inject
    public RemoteableClassListItemPresenter(final View view) {
        super(view);
    }

    @Override
    public RemoteableClassListItemPresenter setup(final SingleValueItemObjectModel remoteableClass,
                                                  final DeploymentsRemoteableClassesPresenter parentPresenter) {
        this.remoteableClass = remoteableClass;
        this.parentPresenter = parentPresenter;

        view.init(this);
        view.setClass(remoteableClass.getValue());

        return this;
    }

    @Override
    public void remove() {
        super.remove();
        parentPresenter.fireChangeEvent();
    }

    public void onClassNameChange(final String className){
        this.remoteableClass.setValue(className);
        parentPresenter.fireChangeEvent();
    }

    @Override
    public SingleValueItemObjectModel getObject() {
        return remoteableClass;
    }

    public interface View extends ListItemView<RemoteableClassListItemPresenter>,
                                  IsElement {

        void setClass(final String role);
    }
}
