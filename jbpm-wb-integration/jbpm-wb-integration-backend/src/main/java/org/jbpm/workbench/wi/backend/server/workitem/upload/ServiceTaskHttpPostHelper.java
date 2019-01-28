/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workbench.wi.backend.server.workitem.upload;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.m2repo.backend.server.helpers.FormData;
import org.guvnor.m2repo.backend.server.helpers.HttpPostHelper;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.guvnor.m2repo.model.HTMLFileManagerFields;

@Typed(ServiceTaskHttpPostHelper.class)
public class ServiceTaskHttpPostHelper extends HttpPostHelper {
    
    @Inject
    private GuvnorM2Repository repository;
    
    private Predicate<ArtifactRepository> filter = new Predicate<ArtifactRepository>() {
        
        @Override
        public boolean test(ArtifactRepository t) {
            return t.getName().equals(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME)
                    || t.getName().equals(ArtifactRepositoryService.WORKSPACE_M2_REPO_NAME);
        }
    };

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            response.setContentType("text/html");
            final FormData formData = extractFormData(request);
            final String result = upload(formData);
            
            if (result.equals(HTMLFileManagerFields.UPLOAD_OK)) {   
                        
                response.getWriter().write(formData.getGav().toString());
            } else {
                response.getWriter().write(result);
            }
        } catch (Exception e) {
            response.getWriter().write("ERROR");
        }
    }

    @Override
    protected void deploy(GAV gav, InputStream jarStream) {
        repository.deployArtifact(jarStream, gav, false, filter);
    }

}
