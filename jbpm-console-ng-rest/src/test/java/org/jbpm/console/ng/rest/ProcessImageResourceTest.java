package org.jbpm.console.ng.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessImageResourceTest extends JbpmConsoleNgRestBaseIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(ProcessImageResourceTest.class);

    /**
     * This tests what happens when the deployment id or process id is incorrect.
     *
     * @throws Exception
     */
    @Test
    public void depOrProcNotFound() throws Exception {
        String badDeploymentId = "org.test:not-there:0-SNAPSHOT";
        String badProcessId = "is.a.process.not.a.process";

        String uriStr = "http://localhost:" + PORT + "/runtime/" + badDeploymentId + "/process/" + PROCESS_ID + "/image";
        Request request = Request.Get(uriStr);

        try {
            Response resp = request.execute();
            int code = resp.returnResponse().getStatusLine().getStatusCode();
            assertEquals( "Incorrect HTTP error code:" , 404, code);
        } catch( Exception e ) {
            logger.error("[GET] " + uriStr + " FAILED", e);
            fail("Unexpected exception: " + e.getMessage());
        }

        uriStr = "http://localhost:" + PORT + "/runtime/" + DEPLOYMENT_ID + "/process/" + badProcessId + "/image";
        request = Request.Get(uriStr);

        try {
            Response resp = request.execute();
            int code = resp.returnResponse().getStatusLine().getStatusCode();
            assertEquals( "Incorrect HTTP error code:" , 404, code);
        } catch( Exception e ) {
            logger.error("[GET] " + uriStr + " FAILED", e);
            fail("Unexpected exception: " + e.getMessage());
        }

    }

    /**
     * This tests what happens when the process definition/BPMN2 file is not available,
     * but the SVG file is not.
     *
     * @throws Exception
     */
    @Test
    public void procDefSVGNotAvailable() throws Exception {
        String procId = "org.test.error";

        String uriStr = "http://localhost:" + PORT + "/runtime/" + DEPLOYMENT_ID + "/process/" + procId + "/image";
        Request request = Request.Get(uriStr);

        try {
            Response resp = request.execute();
            int code = resp.returnResponse().getStatusLine().getStatusCode();
            assertEquals( "Incorrect HTTP error code:" , 412, code);
        } catch( Exception e ) {
            logger.error("[GET] " + uriStr + " FAILED", e);
            fail("Unexpected exception: " + e.getMessage());
        }

    }

    /**
     * This tests the happy flow (everything is correct and the SVG is returned.
     *
     * @throws Exception
     */
    @Test
    public void getProcessDefinitionSVG() throws Exception {
        String uriStr = "http://localhost:" + PORT + "/runtime/" + DEPLOYMENT_ID + "/process/" + PROCESS_ID + "/image";
        Request request = Request.Get(uriStr);

        String contentStr = null;
        try {
            Response resp = request.execute();
            contentStr = resp.returnContent().toString();
        } catch( Exception e ) {
            logger.error("[GET] " + uriStr + " FAILED", e);
            fail("Unable to complete request: " + e.getMessage());
        }

        assertNotNull( "Null response content string", contentStr );
        assertFalse( "Empty response content string", contentStr.trim().isEmpty() );

        InputStream origSvgInputStream = this.getClass().getResourceAsStream("/kjar/" + PROCESS_ID + "-svg.svg");
        assertNotNull( "Null response content string", contentStr );
        String origContent= IOUtils.toString(origSvgInputStream);

        assertEquals( origContent, contentStr);
    }

    /**
     * Tests in isolation processing of completed and active nodes that will be used for marking nodes
     */
    @Test
    public void testProcessNodeInstances() {
        ProcessImageResourceImpl imageResource = new ProcessImageResourceImpl();

        Collection<NodeInstanceDesc> logs =  new ArrayList<NodeInstanceDesc>();
        org.jbpm.kie.services.impl.model.NodeInstanceDesc startA = new org.jbpm.kie.services.impl.model.NodeInstanceDesc("1", "node1", "start", "", "", 1, null, null, NodeInstanceLog.TYPE_ENTER, null);
        org.jbpm.kie.services.impl.model.NodeInstanceDesc startC = new org.jbpm.kie.services.impl.model.NodeInstanceDesc("1", "node1", "start", "", "", 1, null, null, NodeInstanceLog.TYPE_EXIT, null);

        org.jbpm.kie.services.impl.model.NodeInstanceDesc taskA = new org.jbpm.kie.services.impl.model.NodeInstanceDesc("2", "node2", "task", "", "", 1, null, null, NodeInstanceLog.TYPE_ENTER, null);
        org.jbpm.kie.services.impl.model.NodeInstanceDesc taskC = new org.jbpm.kie.services.impl.model.NodeInstanceDesc("2", "node2", "task", "", "", 1, null, null, NodeInstanceLog.TYPE_EXIT, null);

        org.jbpm.kie.services.impl.model.NodeInstanceDesc task2A = new org.jbpm.kie.services.impl.model.NodeInstanceDesc("3", "node3", "task2", "", "", 1, null, null, NodeInstanceLog.TYPE_ENTER, null);
        logs.add(startA);
        logs.add(startC);
        logs.add(taskA);
        logs.add(taskC);
        logs.add(task2A);

        List<String> active = new ArrayList<String>(2);
        List<String> completed = new ArrayList<String>(logs.size()/2);

        imageResource.processNodeInstances(logs, active, completed);

        assertEquals(2, completed.size());
        assertEquals(1, active.size());
        //make sure there are no completed nodes in active list
        for (String id : completed) {
            assertFalse(active.contains(id));
        }
    }

    /**
     * This tests what happens when the process instance is not available,
     *
     * @throws Exception
     */
    @Test
    public void procInstNotAvailable() throws Exception {
        String procId = "org.test.error";

        String uriStr = "http://localhost:" + PORT + "/runtime/" + DEPLOYMENT_ID + "/process/" + procId + "/image/" + 1234;
        Request request = Request.Get(uriStr);

        try {
            Response resp = request.execute();
            int code = resp.returnResponse().getStatusLine().getStatusCode();
            assertEquals( "Incorrect HTTP error code:" , 404, code);
        } catch( Exception e ) {
            logger.error("[GET] " + uriStr + " FAILED", e);
            fail("Unexpected exception: " + e.getMessage());
        }
    }
}
