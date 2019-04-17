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
package org.kie.processmigration.gui.rest;

import javax.ws.rs.Path;

import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.inject.Inject;
import org.kie.processmigration.model.exceptions.InvalidKieServerException;
import org.kie.processmigration.gui.service.GuiKieService;

@Path("/")
public class ReactGuiResource {

    @Inject
    GuiKieService kieService;

    @GET
    @Path("/kieserverids")
    public Response getKieServerIds() {
        String result = kieService.getKieServerIdsFromConfig();
        return Response.ok(result).build();
    }

    @GET
    @Path("/both")
    public Response getBothProcessInfo(
            @QueryParam("sourceProcessId") String sourceProcessId, @QueryParam("sourceContainerId") String sourceContainerId,
            @QueryParam("targetProcessId") String targetProcessId, @QueryParam("targetContainerId") String targetContainerId, @QueryParam("kieserverId") String kieserverId
    ) throws InvalidKieServerException {
        String result = kieService.getBothInfoJson(sourceContainerId, sourceProcessId, targetContainerId, targetProcessId, kieserverId);
        return Response.ok(result).build();
    }

    @GET
    @Path("/instances")
    public Response getRunningInstances(@QueryParam("containerId") String containerId, @QueryParam("kieserverId") String kieserverId
    ) throws InvalidKieServerException {
        String result = kieService.getRunningInstances(containerId, kieserverId);
        return Response.ok(result).build();
    }

}
