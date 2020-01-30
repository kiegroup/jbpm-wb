/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.workbench.wi.dd.model.ItemObjectModel;
import org.kie.workbench.common.screens.library.client.settings.util.sections.Section;
import org.kie.workbench.common.widgets.client.widget.ListItemPresenter;
import org.kie.workbench.common.widgets.client.widget.ListItemView;

@Dependent
public class ObjectItemPresenter extends ListItemPresenter<ItemObjectModel, Section<?>, ObjectItemPresenter.View> implements ObjectPresenter {

    private final ParametersModal parametersModal;

    ItemObjectModel model;
    Section<?> parentPresenter;

    @Inject
    public ObjectItemPresenter(final View view,
                               final ParametersModal parametersModal) {
        super(view);
        this.parametersModal = parametersModal;
    }

    @Override
    public ObjectItemPresenter setup(final ItemObjectModel model,
                                     final Section<?> parentPresenter) {
        this.model = model;
        this.parentPresenter = parentPresenter;

        if (model.getParameters() == null) {
            model.setParameters(new ArrayList<>());
        }

        parametersModal.setup(model.getParameters(), this);

        view.setupResolverSelect(model, parentPresenter);

        view.init(this);

        view.setValue(model.getValue());
        view.setParametersCount(model.getParameters().size());

        return this;
    }

    @Override
    public void remove() {
        super.remove();
        fireChangeEvent();
    }

    public void onValueChange(final String value){
        model.setValue(value);
        fireChangeEvent();
    }

    @Override
    public void fireChangeEvent() {
        parentPresenter.fireChangeEvent();
    }

    public void showParametersModal() {
        parametersModal.show();
    }

    @Override
    public ItemObjectModel getObject() {
        return model;
    }

    @Override
    public void signalParameterAddedOrRemoved() {
        view.setParametersCount(model.getParameters().size());
        fireChangeEvent();
    }

    public interface View extends ListItemView<ObjectItemPresenter> {

        void setValue(final String value);

        void setParametersCount(final int size);

        void setupResolverSelect(final ItemObjectModel model,
                                 final Section<?> parentPresenter);
    }
}
