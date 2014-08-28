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
package org.jbpm.console.ng.bd.service;

import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.console.ng.bd.api.FileException;
import org.jbpm.console.ng.bd.api.FileService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 *
 */
@ApplicationScoped
public class VFSFileServiceImpl implements FileService {

    private static final String REPO_PLAYGROUND = "git://jbpm-playground/";

    private boolean active;

    @Inject
    @Named("ioStrategy")
    private Instance<IOService> ioService;

    @PostConstruct
    public void init() {

        try {
            getIOService().get(URI.create( REPO_PLAYGROUND));
            fetchChanges();
            active = true;
        } catch (FileSystemNotFoundException e) {
            active = false;
        }


    }

    public void fetchChanges() {
        getIOService().getFileSystem( URI.create( REPO_PLAYGROUND + "?fetch" ) );
        active = true;

    }
    
    @Override
    public byte[] loadFile( final Path file ) throws FileException {
        if (!isActive()) {
            return new byte[0];
        }
        checkNotNull( "file", file );

        try {
            return getIOService().readAllBytes( file );
        } catch ( IOException ex ) {
            throw new FileException( ex.getMessage(), ex );
        }
    }
    
    

    @Override
    public Iterable<Path> loadFilesByType( final Path path,
                                           final String fileType ) {
        if (!isActive()) {
            return new ArrayList<Path>();
        }
        return getIOService().newDirectoryStream( path, new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( final Path entry ) throws IOException {
                if ( !Files.isDirectory( entry ) &&
                        (entry.getFileName().toString().endsWith( fileType )
                                || entry.getFileName().toString().matches(fileType))) {
                    return true;
                }
                return false;
            }
        } );
    }
    
    public Iterable<Path> listDirectories(final Path path){
        if (!isActive()) {
            return new ArrayList<Path>();
        }
      return getIOService().newDirectoryStream( path, new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( final Path entry ) throws IOException {
                if ( Files.isDirectory(entry) ) {
                    return true;
                }
                return false;
            }
        } );
    
    }
    
    public Path getPath(String path){
        if (!isActive()) {
            return null;
        }
        return getIOService().get(path);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public boolean exists(Path file){
        if (!isActive()) {
            return false;
        }
        return getIOService().exists(file);
    }

    @Override
    public void move(Path source, Path dest){
        if (!isActive()) {
            return;
        }
        this.copy(source, dest);
        getIOService().delete(source);
    }
    
    @Override
    public void copy(Path source, Path dest){
        if (!isActive()) {
            return;
        }
        checkNotNull( "source", source );
        checkNotNull( "dest", dest );
        
        getIOService().copy(source, dest);
    }
    
    @Override
    public Path createDirectory(Path path){
        if (!isActive()) {
            return null;
        }
        checkNotNull( "path", path );
        
        return getIOService().createDirectory(path);
    }
    
    @Override
    public Path createFile(Path path){
        if (!isActive()) {
            return null;
        }
        return getIOService().createFile(path);
    }
    
    @Override
    public boolean deleteIfExists(Path path){
        if (!isActive()) {
            return false;
        }
        checkNotNull( "path", path );
        
        return getIOService().deleteIfExists(path);
    }
    
    @Override
    public OutputStream openFile(Path path){
        if (!isActive()) {
            return null;
        }
        checkNotNull( "path", path );
        
        return getIOService().newOutputStream(path);
    }
    
    protected IOService getIOService() {
        return ioService.get();
    }
    
}
