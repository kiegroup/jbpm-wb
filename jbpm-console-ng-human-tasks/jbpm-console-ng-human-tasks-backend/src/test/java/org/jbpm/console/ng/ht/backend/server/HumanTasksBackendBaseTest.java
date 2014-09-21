///**
// * Copyright 2013 JBoss Inc
// *
// * Licensed under the Apache License, Version 2.0 (the "License"); you may not
// * use this file except in compliance with the License. You may obtain a copy of
// * the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
// * License for the specific language governing permissions and limitations under
// * the License.
// */
//package org.jbpm.console.ng.ht.backend.server;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import javax.inject.Inject;
//
//import org.jbpm.console.ng.ht.service.TaskServiceEntryPoint;
//import org.jbpm.services.task.impl.model.UserImpl;
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.kie.internal.task.api.InternalTaskService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public abstract class HumanTasksBackendBaseTest {
//
//    protected static Logger logger = LoggerFactory.getLogger(HumanTasksBackendBaseTest.class);
//
//    protected static boolean usersLoaded = false;
//
//    @Inject
//    protected InternalTaskService taskService;
//
//    @Inject
//    protected TaskServiceEntryPoint consoleTaskService;
//
//    @Before
//    public void setUp() {
//        if (!usersLoaded) {
//            try {
//                taskService.addUser(new UserImpl("Administrator"));
//                usersLoaded = true;
//            } catch (Exception ex) {
//                logger.error("Error while adding user Administrator! {}", ex.getMessage());
//            }
//        }
//    }
//
//    @After
//    public void tearDown() {
//        taskService.removeAllTasks();
//    }
//
//    @AfterClass
//    public static void tearDownClass() {
//        usersLoaded = false;
//    }
//
//    protected void printTestName() {
//        System.out.println("Running " + this.getClass().getSimpleName() + "."
//                + Thread.currentThread().getStackTrace()[2].getMethodName());
//    }
//
//    /**
//     * Creates date using default format - "yyyy-MM-dd"
//     */
//    protected Date createDate(String dateString) {
//        return createDate(dateString, "yyyy-MM-dd");
//    }
//
//    protected Date createDate(String dateString, String dateFormat) {
//        SimpleDateFormat fmt = new SimpleDateFormat(dateFormat);
//        try {
//            return fmt.parse(dateString);
//        } catch (ParseException e) {
//            throw new RuntimeException("Can't create date from string '" + dateString + "' using '" + dateFormat + "' format!",
//                    e);
//        }
//    }
//
//}
