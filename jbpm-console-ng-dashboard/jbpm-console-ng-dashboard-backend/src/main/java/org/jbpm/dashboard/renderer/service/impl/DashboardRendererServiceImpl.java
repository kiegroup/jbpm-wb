package org.jbpm.dashboard.renderer.service.impl;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.dashboard.renderer.service.DashboardRendererService;
import org.jbpm.dashboard.renderer.service.ConnectionStatus;

import javax.enterprise.context.ApplicationScoped;

import java.net.HttpURLConnection;
import java.net.URL;

@Service
@ApplicationScoped
public class DashboardRendererServiceImpl implements DashboardRendererService {

    @Override
    public ConnectionStatus getAppStatus(String anUrl) {
        ConnectionStatus connectionStatus = new ConnectionStatus();
        try{
            URL url = new URL(anUrl);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            connectionStatus.setStatus(urlConnection.getResponseCode()) ;
        }catch (Exception e){
            e.printStackTrace();
            connectionStatus.setStatus(-1);
        }
        return connectionStatus;
    }
}
