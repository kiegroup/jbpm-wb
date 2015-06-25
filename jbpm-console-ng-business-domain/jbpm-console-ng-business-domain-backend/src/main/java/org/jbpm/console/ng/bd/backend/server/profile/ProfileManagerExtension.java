/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.console.ng.bd.backend.server.profile;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfileManagerExtension implements Extension {

    private static final Logger logger = LoggerFactory.getLogger(ProfileManagerExtension.class);

    private Map<String, String> profilesExcludes = new HashMap<String, String>();
    private String activeProfile;
    
    public ProfileManagerExtension() {
        // configure what to disable for different profiles
        // don't add anything for full profile as it should not exclude any beans
        profilesExcludes.put("exec-server", "(org\\.jboss\\.errai\\.*.)||(org\\.jbpm\\.console\\.ng.*\\.client\\..*)||(org\\.kie\\.workbench.*\\.client\\..*)||(org\\.guvnor.*\\.client\\..*)");
        profilesExcludes.put("ui-server", "(org\\.kie\\.services\\.remote\\..*)||(org\\.kie\\.services\\.client\\..*)||(org\\.kie\\.workbench\\.common\\.services\\.rest\\..*)");

        activeProfile = System.getProperty("org.kie.active.profile", "full");

    }

    <X> void processAnnotatedType(@Observes final ProcessAnnotatedType<X> pat, BeanManager beanManager) {
        final AnnotatedType<X> annotatedType = pat.getAnnotatedType();
        final Class<X> javaClass = annotatedType.getJavaClass();
        final Package pkg = javaClass.getPackage();

        String pattern = profilesExcludes.get(activeProfile);
        if (pattern != null) {
            if ((pkg != null && pkg.isAnnotationPresent(Profile.class))) {
                Profile profileOnBean = pkg.getAnnotation(Profile.class);
                if (!isInActiveProfile(profileOnBean)) {
                    logger.info("Excluding Bean {} since it is not in active profile {}", javaClass, activeProfile);
                    pat.veto();
                }
            } else if (annotatedType.isAnnotationPresent(Profile.class)) {
                Profile profileOnBean = annotatedType.getAnnotation(Profile.class);
                if (!isInActiveProfile(profileOnBean)) {
                    logger.info("Excluding Bean {} since it is not in active profile {}", javaClass, activeProfile);
                    pat.veto();
                }
            } else if (pkg != null && pkg.getName().matches(pattern)) {
                logger.info("Excluding Bean {} since it is not in active profile {}", javaClass, activeProfile);
                pat.veto();
            }
        }
        return;
    }

    private boolean isInActiveProfile(Profile profileOnBean) {

        String[] beansProfiles = profileOnBean.names();

        for (String profile : beansProfiles) {
            if (activeProfile.equals(profile)) {
                return true;
            }
        }

        return false;
    }

}
