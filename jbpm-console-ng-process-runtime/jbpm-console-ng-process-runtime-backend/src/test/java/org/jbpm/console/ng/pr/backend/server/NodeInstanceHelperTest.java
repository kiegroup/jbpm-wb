/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.console.ng.pr.backend.server;


import java.util.Date;

import org.jbpm.console.ng.pr.model.NodeInstanceSummary;
import org.jbpm.kie.services.impl.model.NodeInstanceDesc;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class NodeInstanceHelperTest {

    NodeInstanceHelper nodeInstanceHelper= new NodeInstanceHelper();


    @Test
    public void adaptRemoveNullValuesTest() {
        NodeInstanceDesc nodeInstanceDes = new NodeInstanceDesc("1",null,null,null,"deploymentID",1,new Date(),null,1,Long.valueOf("1"));
        NodeInstanceSummary nis = nodeInstanceHelper.adapt(nodeInstanceDes);

        assertEquals(nis.getNodeName(),"");
        assertEquals(nis.getNodeUniqueName(),"");
        assertEquals(nis.getType(),"");
    }

}
