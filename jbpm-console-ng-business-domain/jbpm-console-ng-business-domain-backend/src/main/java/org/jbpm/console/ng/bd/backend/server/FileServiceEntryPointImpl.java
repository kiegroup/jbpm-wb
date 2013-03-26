/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.console.ng.bd.backend.server;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.console.ng.bd.service.FileServiceEntryPoint;
import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.kie.commons.java.nio.file.Path;
import org.uberfire.backend.vfs.ActiveFileSystems;
import org.uberfire.backend.vfs.PathFactory;



/**
 *
 * @author salaboy
 */
@Service
@ApplicationScoped
@Transactional
public class FileServiceEntryPointImpl implements FileServiceEntryPoint {
    @Inject
    private FileService fs;
    @Inject
    private ActiveFileSystems fileSystems;
    
    
    public String createProcessDefinitionFile(String name) {
      return fs.createFile(name).toString();
    }
    
    public void fetchChanges() {
        fs.fetchChanges();
    }

    public byte[] loadFile(Path file) {
        try {
            return fs.loadFile(file);
        } catch (FileException ex) {
            Logger.getLogger(FileServiceEntryPointImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Iterable<Path> loadFilesByType(String path, String fileType) {
        try {
            return fs.loadFilesByType(path, fileType);
        } catch (FileException ex) {
            Logger.getLogger(FileServiceEntryPointImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public org.uberfire.backend.vfs.Path getPath(String path) {
        String reporoot = fs.getRepositoryRoot();
        if (reporoot.endsWith("/")) {
            reporoot = reporoot.substring(0, (reporoot.length() - 1));
        }
        String uri = reporoot + path;
        String name = uri.substring(uri.lastIndexOf("/") + 1);
        return PathFactory.newPath(fileSystems.getBootstrapFileSystem(), name, uri);
    }
}
