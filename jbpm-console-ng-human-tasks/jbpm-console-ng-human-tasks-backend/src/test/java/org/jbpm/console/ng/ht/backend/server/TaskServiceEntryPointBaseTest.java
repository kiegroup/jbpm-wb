package org.jbpm.console.ng.ht.backend.server;

import static org.junit.Assert.fail;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.console.ng.ht.model.Day;
import org.jbpm.console.ng.ht.model.TaskSummary;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.services.task.impl.model.TaskImpl;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for console-ng specific task methods (e.g. queries).
 */
public abstract class TaskServiceEntryPointBaseTest extends HumanTasksBackendBaseTest {

    
    @Test
    public void dummyTest(){}
    
    
    
//    @Test
//    public void testGetTasksOwnedFromDateToDateByDaysNoTasks() {
//        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksOwnedFromDateToDateByDays("Bobba Fet",
//                createDate("2013-04-18"), createDate("2013-04-20"), "en-UK");
//        assertEquals(3, tasksByDays.size());
//        for (Map.Entry<Day, List<TaskSummary>> entry : tasksByDays.entrySet()) {
//            // no tasks in task lists
//            assertEquals(0, entry.getValue().size());
//        }
//        // uses total number of days instead of end date
//        Map<Day, List<TaskSummary>> tasksByDays2 = consoleTaskService.getTasksOwnedFromDateToDateByDays("Bobba Fet",
//                createDate("2013-04-18"), 3, "en-UK");
//        assertEquals(tasksByDays, tasksByDays2);
//    }
//
//    /**
//     * Four days in total (from 2013-04-20 to 2013-04-23).
//     * 
//     * Tasks for user 'Bobba Fet'.
//     */
//    @Test
//    public void testGetTasksOwnedFromDateToDateByDays() {
//        // ///////////////// Tasks that should _not_ be included in the result //////////////////
//        // tasks before the specified start date
//        String taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2011-10-15\") } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', 'Before the start date task 1')] })";
//        createAndAddTask(taskStr);
//        taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-19\") } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', 'Before the start date task 2')] })";
//        createAndAddTask(taskStr);
//
//        // tasks after the end date
//        taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-24\") } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', 'After the end date task 1')] })";
//        createAndAddTask(taskStr);
//        taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2014-05-18\") } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', 'After the end date task 2')] })";
//        createAndAddTask(taskStr);
//
//        // tasks with different owner
//        taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-20\") } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Darth Vader')], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', 'Correct day but different owner')] })";
//        createAndAddTask(taskStr);
//        // ///////////////// Tasks that should be included in the result //////////////////
//        // tasks with no expiration date should be included in first day
//        taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = null } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', 'No expiration date specified')] })";
//        createAndAddTask(taskStr);
//
//        int noExpDateTasksNr = 1;
//        int firstDayTasksNr = 15;
//        int secondDayTasksNr = 0;
//        int thirdDayTasksNr = 1;
//        int fourthDayTasksNr = 100;
//        Date firstDay = createDate("2013-04-20");
//        Date secondDay = createDate("2013-04-21");
//        Date thirdDay = createDate("2013-04-22");
//        Date fourthDay = createDate("2013-04-23");
//        // first day tasks
//        for (int i = 0; i < firstDayTasksNr; i++) {
//            taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//            taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-20\") } ), ";
//            taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')], }),";
//            taskStr += "names = [ new I18NText( 'en-UK', 'First day task " + i + "')] })";
//            createAndAddTask(taskStr);
//        }
//        // second day does not have any tasks
//        // third day tasks
//        for (int i = 0; i < thirdDayTasksNr; i++) {
//            taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//            taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-22\") } ), ";
//            taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')], }),";
//            taskStr += "names = [ new I18NText( 'en-UK', 'Third day task " + i + "')] })";
//            createAndAddTask(taskStr);
//        }
//        // fourth day tasks
//        for (int i = 0; i < fourthDayTasksNr; i++) {
//            taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//            taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-23\") } ), ";
//            taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')], }),";
//            taskStr += "names = [ new I18NText( 'en-UK', 'Fourth day task " + i + "')] })";
//            createAndAddTask(taskStr);
//        }
//
//        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksOwnedFromDateToDateByDays("Bobba Fet",
//                createDate("2013-04-20"), createDate("2013-04-23"), "en-UK");
//        assertEquals(4, tasksByDays.size());
//
//        // verify that correct tasks are present for each day
//        for (Map.Entry<Day, List<TaskSummary>> entry : tasksByDays.entrySet()) {
//            Date date = entry.getKey().getDate();
//            List<TaskSummary> tasks = entry.getValue();
//            if (date.equals(firstDay)) {
//                // first day includes also tasks with no expiration date set
//                assertEquals(firstDayTasksNr + noExpDateTasksNr, tasks.size());
//            } else if (date.equals(secondDay)) {
//                assertEquals(secondDayTasksNr, tasks.size());
//            } else if (date.equals(thirdDay)) {
//                assertEquals(thirdDayTasksNr, tasks.size());
//            } else if (date.equals(fourthDay)) {
//                assertEquals(fourthDayTasksNr, tasks.size());
//            } else {
//                // not expected date, fail the test
//                fail("Unexpected date in results map! Date=" + date.toString());
//            }
//            checkTasksExpDate(date, tasks);
//        }
//
//        // uses total number of days instead of end date
//        Map<Day, List<TaskSummary>> tasksByDays2 = consoleTaskService.getTasksOwnedFromDateToDateByDays("Bobba Fet",
//                createDate("2013-04-20"), 4, "en-UK");
//        assertEquals(tasksByDays, tasksByDays2);
//    }
//
//    @Test
//    public void testGetTasksAsssignedFromDateToDateByGroupsByDaysNoTasks() {
//        List<String> groupIds = new ArrayList<String>();
//        groupIds.add("Jedi Knights");
//        groupIds.add("Sith Lords");
//        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksAssignedFromDateToDateByGroupsByDays("Anakin",groupIds,
//                createDate("2013-04-18"), createDate("2013-04-20"), "en-UK");
//        assertEquals(3, tasksByDays.size());
//        for (Map.Entry<Day, List<TaskSummary>> entry : tasksByDays.entrySet()) {
//            // no tasks in task lists
//            assertEquals(0, entry.getValue().size());
//        }
//        // uses total number of days instead of end date
//        Map<Day, List<TaskSummary>> tasksByDays2 = consoleTaskService.getTasksAssignedFromDateToDateByGroupsByDays("Anakin", groupIds,
//                createDate("2013-04-18"), 3, "en-UK");
//        assertEquals(tasksByDays, tasksByDays2);
//    }
//
//    /**
//     * Four days in total (from 2013-04-20 to 2013-04-23)
//     * 
//     * Tasks for groups 'Jedi Knights' or 'Sith Lords'
//     */
//    @Test
//    @Ignore // @TODO: requires revision
//    public void testGetTasksAsssignedFromDateToDateByGroupsByDays() {
//        // ///////////////// Tasks that should _not_ be included in the result //////////////////
//        // tasks before the specified start date
//        String taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2011-10-15\") } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Jedi Knights')], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', 'Before the start date task 1')] })";
//        createAndAddTask(taskStr);
//        taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-19\") } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Jedi Knights')], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', 'Before the start date task 2')] })";
//        createAndAddTask(taskStr);
//
//        // tasks after the end date
//        taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-24\") } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Sith Lords')], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', 'After the end date task 1')] })";
//        createAndAddTask(taskStr);
//        taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2014-05-18\") } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Jedi Knights')], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', 'After the end date task 2')] })";
//        createAndAddTask(taskStr);
//
//        // tasks with different owner
//        taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-20\") } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Bounty Hunters')], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', 'Correct day but different owner')] })";
//        createAndAddTask(taskStr);
//        // ///////////////// Tasks that should be included in the result //////////////////
//        // tasks with no expiration date should be included in first day
//        taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = null } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Jedi Knights')], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', 'No expiration date specified')] })";
//        createAndAddTask(taskStr);
//
//        int noExpDateTasksNr = 1;
//        int firstDayTasksNr = 0;
//        int secondDayTasksNr = 10;
//        int thirdDayTasksNr = 1;
//        int fourthDayTasksNr = 50;
//        Date firstDay = createDate("2013-04-20");
//        Date secondDay = createDate("2013-04-21");
//        Date thirdDay = createDate("2013-04-22");
//        Date fourthDay = createDate("2013-04-23");
//
//        // first day does not have any tasks
//        // second day tasks
//        for (int i = 0; i < secondDayTasksNr; i++) {
//            taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//            taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-21\") } ), ";
//            taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Jedi Knights')], }),";
//            taskStr += "names = [ new I18NText( 'en-UK', 'Second day task " + i + "')] })";
//            createAndAddTask(taskStr);
//        }
//        // third day tasks
//        for (int i = 0; i < thirdDayTasksNr; i++) {
//            taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//            taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-22\") } ), ";
//            taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Jedi Knights')], }),";
//            taskStr += "names = [ new I18NText( 'en-UK', 'Third day task " + i + "')] })";
//            createAndAddTask(taskStr);
//        }
//        // fourth day tasks
//        for (int i = 0; i < fourthDayTasksNr; i++) {
//            taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//            taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-23\") } ), ";
//            taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Sith Lords')], }),";
//            taskStr += "names = [ new I18NText( 'en-UK', 'Fourth day task " + i + "')] })";
//            createAndAddTask(taskStr);
//        }
//
//        List<String> groupIds = new ArrayList<String>();
//        groupIds.add("Jedi Knights");
//        groupIds.add("Sith Lords");
//        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksAssignedFromDateToDateByGroupsByDays("Anakin", groupIds,
//                createDate("2013-04-20"), createDate("2013-04-23"), "en-UK");
//        assertEquals(4, tasksByDays.size());
//
//        // verify that correct tasks are present for each day
//        for (Map.Entry<Day, List<TaskSummary>> entry : tasksByDays.entrySet()) {
//            Date date = entry.getKey().getDate();
//            List<TaskSummary> tasks = entry.getValue();
//            if (date.equals(firstDay)) {
//                // first day includes also tasks with no expiration date set
//                assertEquals(firstDayTasksNr + noExpDateTasksNr, tasks.size());
//            } else if (date.equals(secondDay)) {
//                assertEquals(secondDayTasksNr, tasks.size());
//            } else if (date.equals(thirdDay)) {
//                assertEquals(thirdDayTasksNr, tasks.size());
//            } else if (date.equals(fourthDay)) {
//                assertEquals(fourthDayTasksNr, tasks.size());
//            } else {
//                // not expected date, fail the test
//                fail("Unexpected date in results map! Date=" + date.toString());
//            }
//            checkTasksExpDate(date, tasks);
//        }
//
//        // uses total number of days instead of end date
//        Map<Day, List<TaskSummary>> tasksByDays2 = consoleTaskService.getTasksAssignedFromDateToDateByGroupsByDays("Anakin", groupIds,
//                createDate("2013-04-20"), 4, "en-UK");
//        assertEquals(tasksByDays, tasksByDays2);
//    }
//
//    @Test
//    public void testGetTasksAsssignedFromDateToDatePersonalAndGroupsTasksByDaysNoTasks() {
//        List<String> groupIds = new ArrayList<String>();
//        groupIds.add("Jedi Knights");
//        groupIds.add("Sith Lords");
//        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService
//                .getTasksAssignedFromDateToDateByDays("Anakin", createDate("2013-04-18"),
//                        createDate("2013-04-20"), "en-UK");
//        assertEquals(3, tasksByDays.size());
//
//        for (Map.Entry<Day, List<TaskSummary>> entry : tasksByDays.entrySet()) {
//            // no tasks in task lists
//            assertEquals(0, entry.getValue().size());
//        }
//        // uses total number of days instead of end date
//        Map<Day, List<TaskSummary>> tasksByDays2 = consoleTaskService
//                .getTasksAssignedFromDateToDateByDays("Yoda", createDate("2013-04-18"), 3,
//                        "en-UK");
//        assertEquals(tasksByDays, tasksByDays2);
//    }
//
//    /**
//     * Four days in total (from 2013-04-20 to 2013-04-23).
//     * 
//     * Tasks for user "Yoda" and groups "Star Wars or "Jedi Knights"
//     */
//    @Test
//    @Ignore // This needs review
//    public void testGetTasksAsssignedFromDateToDatePersonalAndGroupsTasksByDays() {
//        // ///////////////// Tasks that should _not_ be included in the result //////////////////
//        // tasks before the specified start date
//        String taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2011-10-15\") } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Yoda'), new Group('Star Wars')], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', 'Before the start date task 1')] })";
//        createAndAddTask(taskStr);
//        taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-19\") } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Yoda'), new Group('Jedi Knights')], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', 'Before the start date task 2')] })";
//        createAndAddTask(taskStr);
//
//        // tasks after the end date
//        taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-24\") } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Yoda'), new Group('Star Wars')], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', 'After the end date task 1')] })";
//        createAndAddTask(taskStr);
//        taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2014-05-18\") } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Yoda'), new Group('Jedi Knights')], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', 'After the end date task 2')] })";
//        createAndAddTask(taskStr);
//
//        // tasks with different owner
//        taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-20\") } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Bounty Hunters')], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', 'Correct day but different owner')] })";
//        createAndAddTask(taskStr);
//        // ///////////////// Tasks that should be included in the result //////////////////
//        // tasks with no expiration date should be included in first day
//        taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = null } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Jedi Knights')], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', 'No expiration date specified')] })";
//        createAndAddTask(taskStr);
//
//        taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = null } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Yoda')], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', 'No expiration date specified')] })";
//        createAndAddTask(taskStr);
//
//        // the numbers needs to be _even_ (or zero), because they are divided by 2 below!
//        int noExpDateTasksNr = 2;
//        int firstDayTasksNr = 0;
//        int secondDayTasksNr = 4;
//        int thirdDayTasksNr = 2;
//        int fourthDayTasksNr = 50;
//        Date firstDay = createDate("2013-04-20");
//        Date secondDay = createDate("2013-04-21");
//        Date thirdDay = createDate("2013-04-22");
//        Date fourthDay = createDate("2013-04-23");
//
//        // first day does not have any tasks
//        // second day tasks
//        for (int i = 0; i < secondDayTasksNr / 2; i++) {
//            // one group task and one user task
//            taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//            taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-21\") } ), ";
//            taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Star Wars')], }),";
//            taskStr += "names = [ new I18NText( 'en-UK', 'Second day group task " + i + "')] })";
//            createAndAddTask(taskStr);
//
//            taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//            taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-21\") } ), ";
//            taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Yoda')], }),";
//            taskStr += "names = [ new I18NText( 'en-UK', 'Second day user task " + i + "')] })";
//            createAndAddTask(taskStr);
//        }
//        // third day tasks
//        for (int i = 0; i < thirdDayTasksNr / 2; i++) {
//            taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//            taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-22\") } ), ";
//            taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Jedi Knights')], }),";
//            taskStr += "names = [ new I18NText( 'en-UK', 'Third day group task " + i + "')] })";
//            createAndAddTask(taskStr);
//
//            taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//            taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-22\") } ), ";
//            taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Yoda')], }),";
//            taskStr += "names = [ new I18NText( 'en-UK', 'Third day user task " + i + "')] })";
//            createAndAddTask(taskStr);
//        }
//        // fourth day tasks
//        for (int i = 0; i < fourthDayTasksNr / 2; i++) {
//            taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//            taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-23\") } ), ";
//            taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Star Wars')], }),";
//            taskStr += "names = [ new I18NText( 'en-UK', 'Fourth day group task " + i + "')] })";
//            createAndAddTask(taskStr);
//
//            taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//            taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-23\") } ), ";
//            taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Yoda')], }),";
//            taskStr += "names = [ new I18NText( 'en-UK', 'Fourth day user task " + i + "')] })";
//            createAndAddTask(taskStr);
//        }
//
//        List<String> groupIds = new ArrayList<String>();
//        groupIds.add("Star Wars");
//        groupIds.add("Jedi Knights");
//        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService
//                .getTasksAssignedFromDateToDateByDays("Yoda", createDate("2013-04-20"),
//                        createDate("2013-04-23"), "en-UK");
//        assertEquals(4, tasksByDays.size());
//        // verify that correct tasks are present for each day
//        for (Map.Entry<Day, List<TaskSummary>> entry : tasksByDays.entrySet()) {
//            Date date = entry.getKey().getDate();
//            List<TaskSummary> tasks = entry.getValue();
//            if (date.equals(firstDay)) {
//                // first day includes also tasks with no expiration date set
//                assertEquals(firstDayTasksNr + noExpDateTasksNr, tasks.size());
//            } else if (date.equals(secondDay)) {
//                assertEquals(secondDayTasksNr, tasks.size());
//            } else if (date.equals(thirdDay)) {
//                assertEquals(thirdDayTasksNr, tasks.size());
//            } else if (date.equals(fourthDay)) {
//                assertEquals(fourthDayTasksNr, tasks.size());
//            } else {
//                // not expected date, fail the test
//                fail("Unexpected date in results map! Date=" + date.toString());
//            }
//            checkTasksExpDate(date, tasks);
//        }
//        // uses total number of days instead of end date
//        Map<Day, List<TaskSummary>> tasksByDays2 = consoleTaskService
//                .getTasksAssignedFromDateToDateByDays("Yoda", createDate("2013-04-20"), 4,
//                        "en-UK");
//        assertEquals(tasksByDays, tasksByDays2);
//    }

    protected void createAndAddTask(String taskStr) {
        TaskImpl task = TaskFactory.evalTask(new StringReader(taskStr));
        taskService.addTask(task, new HashMap<String, Object>());
    }

    /**
     * Verifies that the specified tasks have the expected expiration date. If the expiration date is not set, ignore that.
     * 
     * @param expectedDate expected task expiration date
     * @param tasks list of tasks to check
     */
    protected void checkTasksExpDate(Date expectedDate, List<TaskSummary> tasks) {
        for (TaskSummary task : tasks) {
            // do not consider tasks with no expiration hate
            if (task.getExpirationTime() != null) {
                assertEquals(expectedDate, task.getExpirationTime());
            }
        }
    }

}
