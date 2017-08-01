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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor.validation;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.wi.dd.validation.DeploymentDescriptorValidationMessage;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationMessageTranslator;

@ApplicationScoped
public class DeploymentDescriptorValidationMessageTranslator implements ValidationMessageTranslator {

    private TranslationService translationService;

    @Inject
    public DeploymentDescriptorValidationMessageTranslator(TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public boolean accept(ValidationMessage checkMessage) {
        return checkMessage instanceof DeploymentDescriptorValidationMessage;
    }

    @Override
    public ValidationMessage translate(ValidationMessage checkMessage) {
        DeploymentDescriptorValidationMessage ddMessage = (DeploymentDescriptorValidationMessage) checkMessage;
        ValidationMessage result = new ValidationMessage();
        result.setId(checkMessage.getId());
        result.setLevel(checkMessage.getLevel());
        String translationKey = ddMessage.getKey();

        if (translationService.getTranslation(translationKey) != null) {
            result.setText(translationService.format(translationKey, ddMessage.getArgs()));
        } else {
            result.setText(checkMessage.getText());
        }
        return result;
    }
}
