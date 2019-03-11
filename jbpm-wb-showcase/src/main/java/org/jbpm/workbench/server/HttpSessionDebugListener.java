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

package org.jbpm.workbench.server;

import java.util.Date;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpSessionDebugListener implements HttpSessionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpSessionDebugListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        final HttpSession session = httpSessionEvent.getSession();
        LOGGER.info("Web session created, id: {} ", session.getId());
        LOGGER.debug("session creation time: {}", new Date(session.getCreationTime()));
        LOGGER.debug("session max inactive interval: {}", session.getMaxInactiveInterval());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        final HttpSession session = httpSessionEvent.getSession();
        LOGGER.info("Web session destroyed, id: {} ", session.getId());
        LOGGER.debug("session creation time: {}", new Date(session.getCreationTime()));
        LOGGER.debug("session max inactive interval: {}", session.getMaxInactiveInterval());
        LOGGER.debug("session last accessed time: {}", new Date(session.getLastAccessedTime()));
    }
}
