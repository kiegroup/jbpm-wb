/*
 * Copyright 2012 JBoss Inc
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

package org.jbpm.console.ng.server.impl;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import javax.inject.Inject;
import org.jbpm.shared.services.api.FileService;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.console.ng.shared.ExecutorServiceEntryPoint;
import org.jbpm.console.ng.shared.model.RequestSummary;

@Singleton
public class AppSetup {

    //private ActiveFileSystems fileSystems = new ActiveFileSystemsImpl();

    @Inject
    private FileService fs;
    
    @Inject
    private Caller<ExecutorServiceEntryPoint> executorServices;
    
    @PostConstruct
    public void onStartup() {
        System.out.println("Starting Executor Service ...");
        executorServices.call(new RemoteCallback<Void>() {
            @Override
            public void callback(Void nothing) {
                System.out.println("Executor Service Started ...");
            }
        }).init();
//        try {
//        //    fs.checkFileSystem();
//    //        final String gitURL = "https://github.com/guvnorngtestuser1/jbpm-console-ng-playground.git";
//    //        final String userName = "guvnorngtestuser1";
//    //        final String password = "test1234";
//    //        final URI fsURI = URI.create("git://jbpm-playground");
//    //
//    //        final Map<String, Object> env = new HashMap<String, Object>();
//    //        env.put("username", userName);
//    //        env.put("password", password);
//    //        env.put("origin", gitURL);
//    //
//    //        try {
//    //            FileSystems.newFileSystem(fsURI, env);
//    //        } catch (FileSystemAlreadyExistsException ex) {
//    //        }
//    //
//    //        final Path root = new PathImpl("jbpm-playground", "default://jbpm-playground");
//    //        fileSystems.addBootstrapFileSystem(new FileSystemImpl(asList(root)));
//    
//        } catch (FileException ex) {
//            Logger.getLogger(AppSetup.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

//    @Produces @Named("fs")
//    public ActiveFileSystems fileSystems() {
//        return fileSystems;
//    }

   
}
