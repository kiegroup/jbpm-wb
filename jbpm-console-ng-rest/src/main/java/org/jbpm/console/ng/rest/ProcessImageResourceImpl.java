package org.jbpm.console.ng.rest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.svg.SVGImageProcessor;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.jbpm.services.api.model.ProcessDefinition;

@Path("/runtime/{deploymentId: [\\w\\.-]+(:[\\w\\.-]+){2,2}(:[\\w\\.-]*){0,2}}/process/{processDefId: [_a-zA-Z0-9-:\\.]+}/image")
@ApplicationScoped
public class ProcessImageResourceImpl {

    /* REST information */

    @Context
    private HttpHeaders headers;

    /* KIE information and processing */

    @Inject
    private GuvnorM2Repository repository;

    @Inject
    private RuntimeDataService dataService;

    // Test setter methods --------------------------------------------------------------------------------------------------------

    public void setRepository( GuvnorM2Repository repository ) {
        this.repository = repository;
    }

    public void setRuntimeDataService( RuntimeDataService dataService ) {
        this.dataService = dataService;
    }

    // Helper methods -------------------------------------------------------------------------------------------------------------

    private String getProcesImageSVGFromDeployment( String deploymentId, ProcessDefinition procDef ) {
        String procDefSvg = null;

        String svgFileName = ((ProcessAssetDesc) procDef).getOriginalPath();
        int slashIndex = svgFileName.lastIndexOf("/");
        if( slashIndex < 0 ) {
            slashIndex = 0;
        }
        svgFileName = svgFileName.substring( slashIndex, svgFileName.lastIndexOf(".")) + "-svg.svg";

        // Get kjar and see if svg file is present in it.
        String [] depIdParts = deploymentId.split(":");
        File depFile = repository.getArtifactFileFromRepository(new GAV(depIdParts[0], depIdParts[1], depIdParts[2]));
        if( depFile != null ) {
            JarFile depJarFile = null;
            try {
                depJarFile = new JarFile(depFile);
                Enumeration<JarEntry> entries = depJarFile.entries();
                while( entries.hasMoreElements() ) {
                    JarEntry jarEntry = entries.nextElement();
                    if( jarEntry.getName().contains(svgFileName) ) {
                        InputStream svgInputStream = depJarFile.getInputStream(jarEntry);

                        procDefSvg = IOUtils.toString(svgInputStream);
                        break;
                    }
                }
            } catch( IOException e ) {
                // no-op: could not retrieve file from deployment
            } finally {
                if (depJarFile != null) {
                    try {
                        depJarFile.close();
                    } catch (IOException ignore) {
                        // Nothing to do
                    }
                }
            }
        }

        return procDefSvg;
    }

    // Rest methods --------------------------------------------------------------------------------------------------------------

    @GET
    @Path("/")
//    @Produces({MediaType.APPLICATION_SVG_XML, MediaType.APPLICATION_OCTET_STREAM})
    public Response getProcessImage(  @PathParam("deploymentId") String deploymentId, @PathParam("processDefId" ) String processId) {

        // find procdef (or throw 404) if the deployment id or pro
        ProcessDefinition procDef = dataService.getProcessesByDeploymentIdProcessId(deploymentId, processId);
        if( procDef == null ) {
           return Response.status(Response.Status.NOT_FOUND).build();
        }

        // get SVG String
        String imageSVGString = getProcesImageSVGFromDeployment(deploymentId, procDef);
        if( imageSVGString == null ) {
            return Response.status(Response.Status.PRECONDITION_FAILED).build();
        }

        return Response.ok(imageSVGString, MediaType.APPLICATION_SVG_XML_TYPE).build();
    }


    @GET
    @Path("/{procInstId: [0-9]+}")
    public Response getActiveProcessImage(  @PathParam("deploymentId") String deploymentId, @PathParam("processDefId" ) String processId,
            @PathParam("procInstId") long procInstId) {

        ProcessDefinition procDef = dataService.getProcessesByDeploymentIdProcessId(deploymentId, processId);
        if( procDef == null ) {
           return Response.status(Response.Status.NOT_FOUND).build();
        }

        // get SVG String
        String imageSVGString = getProcesImageSVGFromDeployment(deploymentId, procDef);
        if( imageSVGString == null ) {
            return Response.status(Response.Status.PRECONDITION_FAILED).build();
        }

        // find active nodes and modify image
        Collection<NodeInstanceDesc> logs = dataService.getProcessInstanceFullHistory(procInstId, null);
        List<String> active = new ArrayList<String>(2);
        List<String> completed = new ArrayList<String>(logs.size()/2);
        for( NodeInstanceDesc nodeLog : logs ) {
            String nodeId = nodeLog.getNodeId();
            if( NodeInstanceLog.TYPE_ENTER == ((org.jbpm.kie.services.impl.model.NodeInstanceDesc) nodeLog).getType() ) {
                active.add(nodeId);
            } else {
                completed.add(nodeId);
            }
        }
        imageSVGString = SVGImageProcessor.transform(
                    new ByteArrayInputStream(imageSVGString.getBytes()),
                    completed,
                    active);

        return Response.ok(imageSVGString, MediaType.APPLICATION_SVG_XML_TYPE).build();
    }

}