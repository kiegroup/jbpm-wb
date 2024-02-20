/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.ks.integration;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.jbpm.workbench.ks.integration.event.QueryDefinitionLoaded;
import org.kie.server.api.marshalling.MarshallerFactory;
import org.kie.server.api.marshalling.MarshallingException;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.definition.QueryDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;

@ApplicationScoped
@Startup
public class KieServerQueryDefinitionLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(KieServerQueryDefinitionLoader.class);

    static final String JBPM_WB_QUERY_MODE = "jbpm.wb.querymode";
    enum QueryMode {
        DEFAULT,
        STRICT;

        static QueryMode convert(final String mode) {
            try {
                return QueryMode.valueOf(mode.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
            return QueryMode.DEFAULT;
        }
    }

    @Inject
    Event<QueryDefinitionLoaded> event;

    @PostConstruct
    public void init() {
        init(System.getenv());
    }

    void init(final Map<String,String> properties) {
        loadDefaultQueryDefinitions(QueryMode.convert(properties.getOrDefault(JBPM_WB_QUERY_MODE, QueryMode.DEFAULT.toString())));
    }

    protected void loadDefaultQueryDefinitions(final QueryMode queryMode) {
        final Map<String, String> applyStrict = new HashMap<>();

        if (QueryMode.STRICT.equals(queryMode)) {
            LOGGER.info("Query Mode Strict enabled!");
            QueryDefinition[] queries = loadQueryDefinitions("/default-query-definitions-strict.json");
            for (QueryDefinition q : queries) {
                applyStrict.put(q.getName(), q.getTarget());
            }
        } else {
            LOGGER.info("Query Mode Default enabled!");
        }

        final QueryDefinition[] queries = loadQueryDefinitions("/default-query-definitions.json");
        for (QueryDefinition q : queries) {
            if (applyStrict.containsKey(q.getName())){
                q.setTarget(applyStrict.get(q.getName()));
            }
            LOGGER.info("Loaded query definition: {}", q);
            event.fire(new QueryDefinitionLoaded(q));
        }
    }

    protected QueryDefinition[] loadQueryDefinitions(String resourceName) {

        try (InputStream qdStream = this.getClass().getResourceAsStream(resourceName)) {
            if (qdStream == null) {
                LOGGER.info("Default query definitions file " + resourceName + " not found");
                return new QueryDefinition[0];
            }

            final String qdString = IOUtils.toString(qdStream, Charset.forName("UTF-8"));

            QueryDefinition[] queries = MarshallerFactory.getMarshaller(MarshallingFormat.JSON, this.getClass().getClassLoader()).unmarshall(qdString, QueryDefinition[].class);

            LOGGER.info("Found {} query definitions", queries == null ? 0 : queries.length);

            if (queries == null){
                return new QueryDefinition[0];
            }

            return queries;
        } catch (MarshallingException e) {
            LOGGER.error("Error when unmarshalling query definitions from stream.", e);
        } catch (Exception e) {
            LOGGER.error("Error when loading default query definitions from " + resourceName, e);
        }

        return new QueryDefinition[0];
    }
}
