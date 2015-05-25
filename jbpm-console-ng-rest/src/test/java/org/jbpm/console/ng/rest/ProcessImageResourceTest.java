package org.jbpm.console.ng.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
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
     * This tests what happens when the process definition/BPMN2 file is available, 
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
        
        InputStream origSvgInputStream = this.getClass().getResourceAsStream("/kjar/BPMN2-EvaluationProcess-svg.svg");
        assertNotNull( "Null response content string", contentStr );
        String origContent= IOUtils.toString(origSvgInputStream);
        
        assertEquals( origContent, contentStr);
    }
}
