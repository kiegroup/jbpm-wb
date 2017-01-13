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

package org.jbpm.workbench.wi.dd;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.junit.Test;
import org.kie.internal.runtime.conf.DeploymentDescriptor;

public class DeploymentDescriptorModelTest {

    @Test
    public void compareDeploymentDescriptorInterfaceToModel() throws Exception {

        List<String> skipMethods = Arrays.asList(new String [] {
                "getAuditPersistenceUnit", // covered by getAuditPersistenceUnitName
                "getBuilder", // not needed in the model
                "toXml", // not needed in the model
                "getClasses", // covered by getRemotableClasses
                "getPersistenceUnit" // covered by getPersistenceUnitName
        });
        Method [] methods = DeploymentDescriptor.class.getMethods();
        Arrays.sort(methods, new Comparator<Method>() {
            @Override
            public int compare( Method o1, Method o2 ) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        for( Method method : methods ) {
            String methodName = method.getName();
            if( skipMethods.contains(methodName) ) {
                continue;
            }
            boolean found = false;
            for( Method modelMethod : DeploymentDescriptorModel.class.getMethods() ) {
                if( modelMethod.getName().equals(methodName) ) {
                    found = true;
                    break;
                }
            }
            assertTrue( "Could not find method '" + methodName + "' in model, is it missing?", found );
        }
    }
}
