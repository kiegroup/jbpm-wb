//package org.jbpm.console.ng.ht.backend.server;
//
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.junit.Arquillian;
//import org.jboss.shrinkwrap.api.Archive;
//import org.jboss.shrinkwrap.api.ArchivePaths;
//import org.jboss.shrinkwrap.api.ShrinkWrap;
//import org.jboss.shrinkwrap.api.spec.JavaArchive;
//import org.junit.runner.RunWith;
//
//@RunWith(Arquillian.class)
//public class TaskServiceEntryPointLocalTest extends TaskServiceEntryPointBaseTest {
//
//    @Deployment()
//    public static Archive<?> createDeployment() {
//        return ShrinkWrap
//                .create(JavaArchive.class, "jbpm-console-ng-human-task-cdi.jar")
//                
//                .addPackage("org.jboss.seam.transaction")
//                // core jbpm
//                .addPackage("org.jbpm.shared.services.api")
//                .addPackage("org.jbpm.shared.services.impl")
//                .addPackage("org.jbpm.services.task")
//                
//                .addPackage("org.jbpm.services.task.annotations")
//                .addPackage("org.jbpm.services.task.api")
//                .addPackage("org.jbpm.services.task.impl")
//                .addPackage("org.jbpm.services.task.impl.command")
//                .addPackage("org.jbpm.services.task.impl.model")
//                .addPackage("org.jbpm.services.task.events")
//                .addPackage("org.jbpm.services.task.exception")
//                .addPackage("org.jbpm.services.task.rule")
//                .addPackage("org.jbpm.services.task.rule.impl")
//                .addPackage("org.jbpm.services.task.identity")
//                .addPackage("org.jbpm.services.task.factories")
//                .addPackage("org.jbpm.services.task.internals")
//                .addPackage("org.jbpm.services.task.internals.lifecycle")
//                .addPackage("org.jbpm.services.task.lifecycle.listeners")
//                .addPackage("org.jbpm.services.task.query")
//                .addPackage("org.jbpm.services.task.util")
//                .addPackage("org.jbpm.services.task.deadlines")
//                .addPackage("org.jbpm.services.task.audit")
//                .addPackage("org.jbpm.services.task.audit.service")
//                // deadlines
//                .addPackage("org.jbpm.services.task.deadlines.notifications.impl")
//                .addPackage("org.jbpm.services.task.subtask")
//                // console-ng task service
//                .addPackage("org.jbpm.console.ng.ht.service")
//                .addPackage("org.jbpm.console.ng.ht.backend.server")
//                .addPackage("org.jbpm.shared.services.api")
//                .addPackage("org.jbpm.shared.services.impl")
//                .addPackage("org.jbpm.shared.services.impl.tx")
//
//                .addPackage("org.jbpm.kie.services.api")
//                .addPackage("org.jbpm.kie.services.impl")
//                .addPackage("org.jbpm.kie.services.api.bpmn2")
//                .addPackage("org.jbpm.kie.services.impl.bpmn2")
//                .addPackage("org.jbpm.kie.services.impl.event.listeners")
//                .addPackage("org.jbpm.kie.services.impl.audit")
//                .addPackage("org.jbpm.kie.services.impl.form")
//                .addPackage("org.jbpm.kie.services.impl.form.provider")
//
//                .addPackage("org.jbpm.services.cdi")
//                .addPackage("org.jbpm.services.cdi.impl")
//                .addPackage("org.jbpm.services.cdi.impl.form")
//                .addPackage("org.jbpm.services.cdi.impl.manager")
//                .addPackage("org.jbpm.services.cdi.producer")
//
//                .addClass("org.jbpm.console.ng.ht.backend.server.TestProducers")
//                // .addPackage("org.jbpm.services.task.commands") // This should not be
//                // required here
//                .addAsManifestResource("META-INF/persistence.xml", ArchivePaths.create("persistence.xml"))
//                .addAsManifestResource("META-INF/Taskorm.xml", ArchivePaths.create("Taskorm.xml"))
//                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));
//    }
//
//}
