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
package org.kie.processmigration.gui.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.kie.processmigration.gui.model.ProcessInfo;
import org.kie.processmigration.gui.model.ProcessInfos;
import org.kie.processmigration.gui.model.RunningInstance;
import org.kie.processmigration.model.exceptions.InvalidKieServerException;
import org.kie.server.api.model.definition.NodeDefinition;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.UIServicesClient;
import org.kie.processmigration.gui.service.GuiKieService;
import org.kie.server.api.model.instance.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.swarm.spi.api.config.ConfigKey;
import org.wildfly.swarm.spi.api.config.ConfigView;
import org.wildfly.swarm.spi.api.config.SimpleKey;

@ApplicationScoped
public class GuiKieServiceImpl implements GuiKieService {

    @Inject
    private ConfigView configView;

    @Inject
    private org.kie.processmigration.service.KieService kieService;
    private ConfigKey kieServersKey = new SimpleKey("kieserversids");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = LoggerFactory.getLogger(GuiKieServiceImpl.class);
    
    private String kieServerIds = "";
    @SuppressWarnings("unchecked")
    @PostConstruct
    public void loadConfigs() {
        if (configView.hasKeyOrSubkeys(kieServersKey)) {
            kieServerIds = configView.resolve(kieServersKey).as(String.class).getValue();
        }
        if (kieServerIds.equals("")) {
            logger.error("No KIE server id defined, can still create migration plan but can't execute migration.");
        }
    }
    
    public String getKieServerIdsFromConfig(){
        return kieServerIds;
    }
    

    @Override
    public String getRunningInstances(String containerId, String kieserverId) throws InvalidKieServerException {
        ProcessServicesClient processServicesClient = kieService.getProcessServicesClient(kieserverId);
        List<ProcessInstance> instanceList = processServicesClient.findProcessInstances(containerId, 0, 1000);

        int i = 0;
        List<RunningInstance> result = new ArrayList<RunningInstance>();
        for (ProcessInstance instance : instanceList) {
            i++;
            result.add(new RunningInstance(i, instance));
        }

        return gson.toJson(result);
    }

    @Override
    public String getBothInfoJson(String sourceContainerId, String sourceProcessId, String targetContainerId, String targetProcessId, String kieserverId) throws InvalidKieServerException {

        ProcessServicesClient processServicesClient = kieService.getProcessServicesClient(kieserverId);
        UIServicesClient uiService = kieService.getUIServicesClient(kieserverId);

        ProcessInfos bothInfo = new ProcessInfos();

        ProcessInfo sourceInfo = getProcessInfo(sourceContainerId, sourceProcessId, processServicesClient, uiService);
        sourceInfo.setContainerId(sourceContainerId);;

        ProcessInfo targetInfo = getProcessInfo(targetContainerId, targetProcessId, processServicesClient, uiService);
        targetInfo.setContainerId(targetContainerId);;

        bothInfo.setSourceInfo(sourceInfo);
        bothInfo.setTargetInfo(targetInfo);
        return gson.toJson(bothInfo);

    }

    private ProcessInfo getProcessInfo(String containerId, String processId, ProcessServicesClient processServicesClient, UIServicesClient uiService) throws InvalidKieServerException {
        ProcessInfo processInfo = new ProcessInfo();

        //get SVG file
        String svgFile = uiService.getProcessImage(containerId, processId);

        //Add this replacement here because in react-svgmt, ? and = are not allowed. 
        svgFile = svgFile.replaceAll("\\?shapeType=BACKGROUND", "_shapeType_BACKGROUND");
        processInfo.setSvgFile(svgFile);

        ProcessDefinition pd = processServicesClient.getProcessDefinition(containerId, processId);

        Collection<NodeDefinition> nodes = pd.getNodes();

        ArrayList<String> values = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        for (NodeDefinition node : nodes) {
            if (node.getType().equals("HumanTaskNode")) {
                values.add(node.getUniqueId());
                labels.add(node.getName() + ":" + node.getUniqueId());
            }
        }
        processInfo.setValues(values);
        processInfo.setLabels(labels);

        return processInfo;
    }

}
