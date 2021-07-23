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

package org.jbpm.dashboard.dataset.editor.validator;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;

import org.dashbuilder.dataset.validation.groups.SQLDataSetDefDbSQLValidation;
import org.dashbuilder.dataset.validation.groups.SQLDataSetDefValidation;
import org.jbpm.workbench.ks.integration.RemoteDataSetDef;
import org.jbpm.workbench.ks.integration.validation.RemoteDataSetDefValidation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class RemoteDataSetDefValidatorTest {

    @Mock RemoteDataSetDef remoteDataSetDef;
    private RemoteDataSetDefValidator tested;

    protected Validator validator;

    @Before
    public void setup() {
        validator = spy(new ValidatorMock());
        tested = spy(new RemoteDataSetDefValidator( validator ));
    }

    @Test
    public void testValidateAttributesUsingQuery() {        
        tested.validateCustomAttributes( remoteDataSetDef );
        verify(validator, times(1)).validate(remoteDataSetDef, RemoteDataSetDefValidation.class, SQLDataSetDefValidation.class, SQLDataSetDefDbSQLValidation.class);
    }


    // Mockito is not able to mock javax.validation.Validator, so let's create an empty implementation and spy it.
    class ValidatorMock implements Validator {

        @Override
        public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
            return null;
        }

        @Override
        public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
            return null;
        }

        @Override
        public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
            return null;
        }

        @Override
        public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
            return null;
        }

        @Override
        public <T> T unwrap(Class<T> type) {
            return null;
        }
    }
}
