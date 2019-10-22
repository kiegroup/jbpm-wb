/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.wi.backend.server.workitem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.jbpm.process.workitem.repository.service.RepoData;
import org.jbpm.process.workitem.repository.service.RepoService;
import org.jbpm.workbench.wi.workitems.model.ServiceTaskSummary;
import org.junit.Before;
import org.junit.Test;
import org.guvnor.common.services.project.model.GAV;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServiceTaskServiceImplTest {

    private static final String GROUP_ID = "org.jbpm.contrib";
    private static final String ARTIFACT_ID = "vimeo-workitem";
    private static final String NEW_VERSION = "7.30.0-SNAPSHOT";
    private static final String OLD_VERSION = "7.30.0-SNAPSHOT";
    private static final String GET_INFO_VIMEO = "GetInfoVimeo";
    private static final String DELETE_VIMEO = "DeleteVimeo";
    private static final String OLD_GAV = GROUP_ID + ":" + ARTIFACT_ID + ":" + OLD_VERSION;
    private static final String NEW_GAV = GROUP_ID + ":" + ARTIFACT_ID + ":" + NEW_VERSION;

    private RepoService repoService;

    private ServiceTaskServiceImpl serviceTaskServiceImpl;

    @Before
    public void init() throws IOException {
        String jsonInput = IOUtils.toString(ServiceTaskServiceImplTest.class.getResourceAsStream("/serviceinfo.js"), StandardCharsets.UTF_8);
        repoService = new RepoService(jsonInput);

        serviceTaskServiceImpl = new ServiceTaskServiceImpl();
        serviceTaskServiceImpl.setRepoService(repoService);
    }

    @Test
    public void testGetServiceTasks() {
        List<ServiceTask> serviceTasks = repoService.getServices().stream()
                .map(repoData -> new ServiceTask(repoData.getId(), "fa fa-cogs", repoData.getName(), repoData.getDescription(), repoData.getActiontitle(),
                                                 repoData.isEnabled(), repoData.getInstalledOn(), serviceTaskServiceImpl.extractAuthParameters(repoData), repoData.getAuthreferencesite(),
                                                 repoData.getInstalledOnBranch(), serviceTaskServiceImpl.getGAV(repoData)))
                .sorted((service, service2) -> service.getName().compareTo(service2.getName()))
                .collect(Collectors.toList());
        assertThat(serviceTasks.size()).isEqualTo(4);
        assertThat(serviceTasks.stream()).filteredOn(serviceTask -> serviceTask.getName().equals(GET_INFO_VIMEO))
                .hasSize(2).extracting(st -> st.getGav().toString()).containsOnlyOnce(OLD_GAV)
                .containsOnlyOnce(NEW_GAV);

        assertThat(serviceTasks.stream()).filteredOn(serviceTask -> serviceTask.getName().equals(DELETE_VIMEO))
                .hasSize(2).extracting(st -> st.getGav().toString()).containsOnlyOnce(OLD_GAV)
                .containsOnlyOnce(NEW_GAV);

        List<ServiceTaskSummary> serviceTaskSummaries = serviceTaskServiceImpl.getServiceTasks();
        assertEquals(2, serviceTaskSummaries.size());
        assertThat(serviceTaskSummaries.stream()).hasSize(2).extracting(st -> st.getName()).containsOnlyOnce(GET_INFO_VIMEO).containsOnlyOnce(DELETE_VIMEO);
    }

    @Test
    public void testDisableOldGAVServiceTask() {
        Optional<RepoData> optional = repoService.getServices().stream().filter(serviceTask -> serviceTask.getName().equals(GET_INFO_VIMEO)
                && serviceTask.getGav().toString().equals(OLD_GAV)).findFirst();

        assertTrue(optional.isPresent());
        assertThat(repoService.getServices().stream()).filteredOn(repoData -> repoData.isEnabled()).hasSize(4);

        GAV gav = new GAV(GROUP_ID, ARTIFACT_ID, NEW_VERSION);
        serviceTaskServiceImpl.disableOldGAVServiceTask(gav);
        assertThat(repoService.getServices().stream()).filteredOn(repoData -> repoData.isEnabled()).hasSize(0);
    }
}
