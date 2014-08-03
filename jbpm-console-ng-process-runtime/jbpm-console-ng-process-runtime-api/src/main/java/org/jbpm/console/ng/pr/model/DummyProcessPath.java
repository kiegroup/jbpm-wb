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

    
    public FileSystem getFileSystem() {
        return null;
    }

    @Override
    public String getFileName() {
        return fileName + ".bpmn2";
    }


    @Override
    public String toURI() {
        return "default://master@dummy/" + getFileName();
    }

    @Override
    public int compareTo(Path path) {
        return 0;
    }

}
