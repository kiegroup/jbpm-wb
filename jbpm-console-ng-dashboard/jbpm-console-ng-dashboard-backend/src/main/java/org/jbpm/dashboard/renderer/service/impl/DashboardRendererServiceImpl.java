package org.jbpm.dashboard.renderer.service.impl;

import org.apache.commons.lang.StringUtils;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.dashboard.renderer.service.DashboardRendererService;
import org.jbpm.dashboard.renderer.service.ConnectionStatus;

import javax.enterprise.context.ApplicationScoped;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

@Service
@ApplicationScoped
public class DashboardRendererServiceImpl implements DashboardRendererService {

    @Override
    public ConnectionStatus getAppStatus(String theUrl) {
        ConnectionStatus connectionStatus = new ConnectionStatus();

        // Get a list of the urls to check for the given url
        List<String> urls = explodeUrl(theUrl);
        Exception exc = null;
        for (String anUrl : urls) {
            try {
                // Check whether the service is available
                int status = pingUrl(anUrl);
                connectionStatus.setStatus(status);
                return connectionStatus;
            } catch (Exception e){
                exc = e;
            }
        }
        if (exc != null) exc.printStackTrace();
        connectionStatus.setStatus(-1);
        return connectionStatus;
    }

    protected int pingUrl(String anUrl) throws Exception {
        URL url = new URL(anUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        return urlConnection.getResponseCode();
    }

    protected List<String> explodeUrl(String anUrl) {
        List<String> results = new ArrayList<String>();

        // Add the target URL
        results.add(anUrl);

        try {
            // Get the new address (defaults to localhost).
            String bindAddress = System.getProperty("dashbuilder.bind.address");
            String bindPort = System.getProperty("dashbuilder.bind.port");
            if (StringUtils.isBlank(bindAddress)) {
                bindAddress = InetAddress.getLocalHost().getHostAddress();
            }

            URL url = new URL(anUrl);
            String host = url.getHost();
            int port = url.getPort();
            String newUrl = anUrl;
            if (!host.equals(bindAddress)) {
                newUrl = newUrl.replace(host, bindAddress);
            }
            if (!StringUtils.isBlank(bindPort) && port != Integer.parseInt(bindPort)) {
                newUrl = newUrl.replace(Integer.toString(port), bindPort);
            }
            if (!anUrl.equals(newUrl)) {
                results.add(newUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public static void main(String[] args) {
        DashboardRendererServiceImpl s = new DashboardRendererServiceImpl();
        for (String s1 : s.explodeUrl("http://localhost:8080/dashbuilder/workspace")) {
            System.out.println(s1);
        }
    }
}
