/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.console.ng.pm.backend.server;

import com.google.inject.Inject;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.console.ng.pm.service.ProcessModelingService;
import org.jbpm.shared.services.api.FileService;

/**
 *
 *
 */
@Service
@ApplicationScoped
public class ProcessModelingServiceImpl implements ProcessModelingService {

  @Inject
  FileService fs;

  @Override
  public String createProcessDefinitionFile(String name) {
    return fs.createFile(name).toString();
  }
}
