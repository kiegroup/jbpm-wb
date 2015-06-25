/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.dashboard.renderer.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.dashboard.renderer.service.DashboardRendererService;
import org.jbpm.dashboard.renderer.service.ConnectionStatus;

import javax.enterprise.context.ApplicationScoped;

import java.net.HttpURLConnection;
import java.net.URL;

@Service
@ApplicationScoped
public class DashboardRendererServiceImpl implements DashboardRendererService {

    /**
     * Maximum amount of time (in milliseconds) a ping alive request to the dashbuilder remote URL may last.
     * <p>The system property <i>dashbuilder.bind.timeout</i> can be used to set a custom value.</p>
     */
    public static int PING_TIMEOUT = 1000;

    @Override
    public ConnectionStatus getAppStatus(String theUrl) {
        ConnectionStatus connectionStatus = new ConnectionStatus();
        try {
            // Check whether the service is available
            String targetUrl = resolveUrl(theUrl);
            int status = pingUrl(targetUrl);
            connectionStatus.setStatus(status);
        } catch (Exception e) {
            e.printStackTrace();
            connectionStatus.setStatus(-1);
        }
        return connectionStatus;
    }

    protected int pingUrl(String anUrl) throws Exception {
        int timeout = PING_TIMEOUT;
        String bindTimeout = System.getProperty("dashbuilder.bind.timeout");
        if (!StringUtils.isBlank(bindTimeout)) {
            timeout = Integer.parseInt(bindTimeout);
        }

        URL url = new URL(anUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(timeout);
        return urlConnection.getResponseCode();
    }

    protected String resolveUrl(String anUrl) throws Exception {
        // Get the bind address/port.
        String bindAddress = System.getProperty("dashbuilder.bind.address");
        String bindPort = System.getProperty("dashbuilder.bind.port");

        URL url = new URL(anUrl);
        String host = url.getHost();
        int port = url.getPort();
        String targetUrl = anUrl;
        if (!StringUtils.isBlank(bindAddress) && !host.equals(bindAddress)) {
            targetUrl = targetUrl.replace(host, bindAddress);
        }
        if (!StringUtils.isBlank(bindPort) && port != Integer.parseInt(bindPort)) {
            targetUrl = targetUrl.replace(Integer.toString(port), bindPort);
        }
        return !anUrl.equals(targetUrl) ? targetUrl :anUrl;
    }
}
