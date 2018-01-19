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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor.items;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.wi.dd.model.Parameter;
import org.kie.workbench.common.screens.library.client.settings.util.ListItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.ListPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.modal.Elemental2Modal;

@Dependent
public class ParametersModal extends Elemental2Modal<ParametersModalView> {

    private final ParametersListPresenter parametersListPresenter;
    ObjectPresenter parentPresenter;

    @Inject
    public ParametersModal(final ParametersModalView view,
                           final ParametersListPresenter parametersListPresenter) {

        super(view);
        this.parametersListPresenter = parametersListPresenter;
    }

    public void setup(final List<Parameter> parameters,
                      final ObjectPresenter parentPresenter) {

        this.parentPresenter = parentPresenter;

        parametersListPresenter.setup(
                getView().getParametersTable(),
                parameters,
                (parameter, presenter) -> presenter.setup(parameter, this));

        super.setup();
    }

    public void add() {
        final Parameter parameter = new Parameter(String.class.getCanonicalName(), "");
        parametersListPresenter.add(parameter);
        parentPresenter.signalParameterAddedOrRemoved();
    }

    public void signalParameterAddedOrRemoved() {
        parentPresenter.signalParameterAddedOrRemoved();
    }

    public void fireChangeEvent() {
        parentPresenter.fireChangeEvent();
    }

    @Dependent
    public static class ParametersListPresenter extends ListPresenter<Parameter, ParameterItemPresenter> {

        @Inject
        public ParametersListPresenter(final ManagedInstance<ParameterItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }

    @Dependent
    public static class ParameterItemPresenter extends ListItemPresenter<Parameter, ParametersModal, ParametersModalView.Parameter> {

        private Parameter parameter;
        private ParametersModal parentPresenter;

        @Inject
        public ParameterItemPresenter(final ParametersModalView.Parameter parametersModalView) {
            super(parametersModalView);
        }

        @Override
        public ParameterItemPresenter setup(final Parameter parameter,
                                            final ParametersModal parentPresenter) {

            this.parameter = parameter;
            this.parentPresenter = parentPresenter;

            view.init(this);
            view.setType(parameter.getType());
            view.setValue(parameter.getValue());
            return this;
        }

        @Override
        public Parameter getObject() {
            return parameter;
        }

        @Override
        public void remove() {
            super.remove();
            parentPresenter.signalParameterAddedOrRemoved();
        }

        public void setName(final String name) {
            parameter.setType(name);
            parentPresenter.fireChangeEvent();
        }

        public void setValue(final String value) {
            parameter.setValue(value);
            parentPresenter.fireChangeEvent();
        }
    }
}
