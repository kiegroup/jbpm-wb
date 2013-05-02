package org.jbpm.console.ng.pr.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.FileSystem;

@Portable
public class DummyProcessPath implements Path {

    private String fileName;

    public DummyProcessPath() {

    }

    public DummyProcessPath(String fileName) {
        this.fileName = fileName;
    }
    @Override
    public FileSystem getFileSystem() {
            return null;
    }

    @Override
    public String getFileName() {
            return fileName + ".bpmn2";
    }

    @Override
    public String toURI() {
            return "default://master@dummy/"+getFileName();
    }

    @Override public int compareTo(Path path) {
            return 0;
    }

}
