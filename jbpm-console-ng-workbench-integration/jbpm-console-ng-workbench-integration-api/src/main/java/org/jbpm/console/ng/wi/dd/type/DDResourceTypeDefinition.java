package org.jbpm.console.ng.wi.dd.type;
import javax.enterprise.context.ApplicationScoped;

import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class DDResourceTypeDefinition implements ResourceTypeDefinition {

        @Override
        public String getShortName() {
            return "kie deployment descriptor";
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public String getPrefix() {
            return "kie-deployment-descriptor";
        }

        @Override
        public String getSuffix() {
            return "xml";
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public String getSimpleWildcardPattern() {
            return getPrefix() + "." + getSuffix();
        }

        @Override
        public boolean accept( final Path path ) {
            return path.getFileName().equals( getPrefix() + "." + getSuffix() );
        }
    }
