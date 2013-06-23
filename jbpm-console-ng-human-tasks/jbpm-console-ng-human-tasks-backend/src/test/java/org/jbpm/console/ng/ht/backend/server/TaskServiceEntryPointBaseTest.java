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
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Test;

/**
 * Tests for console-ng specific task methods (e.g. queries).
 */
public abstract class TaskServiceEntryPointBaseTest extends HumanTasksBackendBaseTest {
    private static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    @Test
    public void testGetTasksOwnedFromDateToDateByDaysNoTasksAtAll() {
        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksOwnedFromDateToDateByDays("Bobba Fet",
                getTaskStatuses(), createDate("2013-04-18"), 3, "en-UK");
        assertEquals(3, tasksByDays.size());
        for (Map.Entry<Day, List<TaskSummary>> entry : tasksByDays.entrySet()) {
            // no tasks in task lists
            assertEquals(0, entry.getValue().size());
        }
    }

    @Test
    public void testGetTasksOwnedFromDateToDateByDaysNoTasksWithinRange() {
        createTaskWithSpecifiedDueDateAndUserAndName("2012-06-08", "Bobba Fet", "Task before");
        createTaskWithSpecifiedDueDateAndUserAndName("2013-04-17", "Bobba Fet", "Task before 2");
        createTaskWithSpecifiedDueDateAndUserAndName("2013-04-21", "Bobba Fet", "Task after");
        createTaskWithSpecifiedDueDateAndUserAndName("2014-01-12", "Bobba Fet", "Task after 2");
        createTaskWithNoDueDateAndUserAndName("Bobba Fet", "No due date");
        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksOwnedFromDateToDateByDays("Bobba Fet",
                getTaskStatuses(), createDate("2013-04-18"), 3, "en-UK");
        assertEquals(3, tasksByDays.size());
        for (Map.Entry<Day, List<TaskSummary>> entry : tasksByDays.entrySet()) {
            // no tasks in task lists
            assertEquals(0, entry.getValue().size());
        }
    }

    @Test
    public void testGetTasksOwnedFromDateToDateByDaysOnlyTasksWithNoDueDate() {
        createTaskWithNoDueDateAndUserAndName("Bobba Fet", "No due date");
        createTaskWithNoDueDateAndUserAndName("Bobba Fet", "No due date 2");
        Date today = new Date();
        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksOwnedFromDateToDateByDays("Bobba Fet",
                getTaskStatuses(), today, 3, "en-UK");
        assertEquals(3, tasksByDays.size());
        for (Map.Entry<Day, List<TaskSummary>> entry : tasksByDays.entrySet()) {
            // no tasks in task lists
            if (sameDays(today, entry.getKey().getDate())) {
                assertEquals(2, entry.getValue().size());
            } else {
                assertEquals(0, entry.getValue().size());
            }
        }
    }

    /**
     * Five days in total
     * Tasks for user 'Bobba Fet'.
     */
    @Test
    public void testGetTasksOwnedFromDateToDateByDaysMultipleDifferentTasks() {
        LocalDate today = new LocalDate();
        LocalDate from = today.minusDays(2);
        LocalDate to = today.plusDays(2);
        int nrOfDaysTotal = Days.daysBetween(from, to).getDays() + 1;

        int nrOfTasksWithNoExpDate = 2;
        int firstDayTasksNr = 15;
        int secondDayTasksNr = 0;
        int thirdDayTasksNr = 1;
        int fourthDayTasksNr = 100;
        int fifthDayTasksNr = 123;
        LocalDate firstDay = from;
        LocalDate secondDay = from.plusDays(1);
        LocalDate thirdDay = from.plusDays(2);
        LocalDate fourthDay = from.plusDays(3);
        LocalDate fifthDay = to;
        // ///////////////// Tasks that should _not_ be included in the result //////////////////
        // tasks before the specified start date
        createTaskWithSpecifiedDueDateAndUserAndName(from.minusDays(1), "Bobba Fet", "Before start date");
        createTaskWithSpecifiedDueDateAndUserAndName(from.minusDays(200), "Bobba Fet", "Before start 2");
        // tasks after the end date
        createTaskWithSpecifiedDueDateAndUserAndName(to.plusDays(1), "Bobba Fet", "After end date");
        createTaskWithSpecifiedDueDateAndUserAndName(to.plusDays(300), "Bobba Fet", "After end date 2 ");
        // tasks with different owner
        createTasksWithSpecifiedDueDateAndUserAndName(firstDay, "Darth Vader", "Correct day, but different owner", 20);
        createTasksWithSpecifiedDueDateAndUserAndName(secondDay, "Yoda", "Correct day, but different owner", 100);
        createTasksWithSpecifiedDueDateAndUserAndName(thirdDay, "Anakin", "Correct day, but different owner", 5);
        createTasksWithSpecifiedDueDateAndUserAndName(fourthDay, "Darth Maul", "Correct day, but different owner", 34);
        createTasksWithSpecifiedDueDateAndUserAndName(fifthDay, "Darth Maul", "Correct day, but different owner", 15);

        // ///////////////// Tasks that should be included in the result //////////////////
        // tasks with no due date -> included in Today
        createTaskWithNoDueDateAndUserAndName("Bobba Fet", "No due date");
        createTaskWithNoDueDateAndUserAndName("Bobba Fet", "No due date 2 ");

        // tasks for each day
        createTasksWithSpecifiedDueDateAndUserAndName(firstDay, "Bobba Fet", "First day task", firstDayTasksNr);
        createTasksWithSpecifiedDueDateAndUserAndName(secondDay, "Bobba Fet", "Second day task", secondDayTasksNr);
        createTasksWithSpecifiedDueDateAndUserAndName(thirdDay, "Bobba Fet", "Third day task", thirdDayTasksNr);
        createTasksWithSpecifiedDueDateAndUserAndName(fourthDay, "Bobba Fet", "Fourth day task", fourthDayTasksNr);
        createTasksWithSpecifiedDueDateAndUserAndName(fifthDay, "Bobba Fet", "fifth day task", fifthDayTasksNr);

        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksOwnedFromDateToDateByDays("Bobba Fet",
                getTaskStatuses(), toJavaUtilDate(from), nrOfDaysTotal, "en-UK");

        assertEquals(nrOfDaysTotal, tasksByDays.size());

        // verify that correct number of tasks is present for each day
        for (Map.Entry<Day, List<TaskSummary>> entry : tasksByDays.entrySet()) {
            Date date = entry.getKey().getDate();
            List<TaskSummary> tasks = entry.getValue();
            if (sameDays(date, firstDay)) {
                // first day includes also tasks with no expiration date set
                assertEquals(firstDayTasksNr, tasks.size());
            } else if (sameDays(date, secondDay)) {
                assertEquals(secondDayTasksNr, tasks.size());
            } else if (sameDays(date, thirdDay)) {
                assertEquals(thirdDayTasksNr + nrOfTasksWithNoExpDate, tasks.size());
            } else if (sameDays(date, fourthDay)) {
                assertEquals(fourthDayTasksNr, tasks.size());
            } else if (sameDays(date, fifthDay)) {
                assertEquals(fifthDayTasksNr, tasks.size());
            } else {
                // not expected date, fail the test
                fail("Unexpected date in results map! Date=" + date.toString());
            }
            checkTasksHaveExpectedExpirationDate(date, tasks);
        }
    }

    private void createTaskWithSpecifiedDueDateAndUserAndName(String date, String user, String name) {
        String taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"" + DEFAULT_DATE_FORMAT + "\").parse(\"" + date + "\") } ), ";
        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('" + user + "')], }),";
        taskStr += "names = [ new I18NText( 'en-UK', '" + name + "')] })";
        createAndAddTaskFromString(taskStr);
    }

    private void createTaskWithSpecifiedDueDateAndUserAndName(LocalDate date, String user, String name) {
        createTaskWithSpecifiedDueDateAndUserAndName(date.toString(DEFAULT_DATE_FORMAT), user, name);
    }

    private void createTaskWithNoDueDateAndUserAndName(String user, String name) {
        String taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
        taskStr += "expirationTime = null } ), ";
        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('" + user + "')], }),";
        taskStr += "names = [ new I18NText( 'en-UK', '" + name + "')] })";
        createAndAddTaskFromString(taskStr);
    }

    private void createTasksWithSpecifiedDueDateAndUserAndName(LocalDate day, String userId, String taskName,
            int nrOfTasksToCreate) {
        for (int i = 0; i < nrOfTasksToCreate; i++) {
            createTaskWithSpecifiedDueDateAndUserAndName(day, userId, taskName + i);
        }
    }

    private boolean sameDays(Date date, LocalDate localDate) {
        return localDate.equals(new LocalDate(date));
    }

    private boolean sameDays(Date date1, Date date2) {
        return new LocalDate(date1).equals(new LocalDate(date2));
    }

    private Date toJavaUtilDate(LocalDate localDate) {
        return localDate.toDateMidnight().toDate();
    }

    private List<String> getTaskStatuses() {
        List<String> statuses = new ArrayList<String>();
        statuses.add("InProgress");
        statuses.add("Reserved");
        statuses.add("Created");
        return statuses;
    }

    @Test
    public void testGetTasksAsssignedFromDateToDateByGroupsByDaysNoTasks() {
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Jedi Knights");
        groupIds.add("Sith Lords");
        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksAssignedFromDateToDateByGroupsByDays("Anakin",
                groupIds,
                createDate("2013-04-18"), 3, "en-UK");
        assertEquals(3, tasksByDays.size());
        for (Map.Entry<Day, List<TaskSummary>> entry : tasksByDays.entrySet()) {
            // no tasks in task lists
            assertEquals(0, entry.getValue().size());
        }
    }

    /**
     * Four days in total (from 2013-04-20 to 2013-04-23)
     * <p/>
     * Tasks for groups 'Jedi Knights' or 'Sith Lords'
     */
    @Test
    public void testGetTasksAsssignedFromDateToDateByGroupsByDays() {
        // ///////////////// Tasks that should _not_ be included in the result //////////////////
        // tasks before the specified start date
        String taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2011-10-15\") } ), ";
        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Jedi Knights')], }),";
        taskStr += "names = [ new I18NText( 'en-UK', 'Before the start date task 1')] })";
        createAndAddTaskFromString(taskStr);
        taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-19\") } ), ";
        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Jedi Knights')], }),";
        taskStr += "names = [ new I18NText( 'en-UK', 'Before the start date task 2')] })";
        createAndAddTaskFromString(taskStr);

        // tasks after the end date
        taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-24\") } ), ";
        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Sith Lords')], }),";
        taskStr += "names = [ new I18NText( 'en-UK', 'After the end date task 1')] })";
        createAndAddTaskFromString(taskStr);
        taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2014-05-18\") } ), ";
        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Jedi Knights')], }),";
        taskStr += "names = [ new I18NText( 'en-UK', 'After the end date task 2')] })";
        createAndAddTaskFromString(taskStr);

        // tasks with different owner
        taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-20\") } ), ";
        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Bounty Hunters')], }),";
        taskStr += "names = [ new I18NText( 'en-UK', 'Correct day but different owner')] })";
        createAndAddTaskFromString(taskStr);
        // ///////////////// Tasks that should be included in the result //////////////////
        // tasks with no expiration date should be included in first day
        taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
        taskStr += "expirationTime = null } ), ";
        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Jedi Knights')], }),";
        taskStr += "names = [ new I18NText( 'en-UK', 'No expiration date specified')] })";
        createAndAddTaskFromString(taskStr);

        int noExpDateTasksNr = 1;
        int firstDayTasksNr = 0;
        int secondDayTasksNr = 10;
        int thirdDayTasksNr = 1;
        int fourthDayTasksNr = 50;
        Date firstDay = createDate("2013-04-20");
        Date secondDay = createDate("2013-04-21");
        Date thirdDay = createDate("2013-04-22");
        Date fourthDay = createDate("2013-04-23");

        // first day does not have any tasks
        // second day tasks
        for (int i = 0; i < secondDayTasksNr; i++) {
            taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
            taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-21\") } ), ";
            taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Jedi Knights')], }),";
            taskStr += "names = [ new I18NText( 'en-UK', 'Second day task " + i + "')] })";
            createAndAddTaskFromString(taskStr);
        }
        // third day tasks
        for (int i = 0; i < thirdDayTasksNr; i++) {
            taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
            taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-22\") } ), ";
            taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Jedi Knights')], }),";
            taskStr += "names = [ new I18NText( 'en-UK', 'Third day task " + i + "')] })";
            createAndAddTaskFromString(taskStr);
        }
        // fourth day tasks
        for (int i = 0; i < fourthDayTasksNr; i++) {
            taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
            taskStr += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-23\") } ), ";
            taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Sith Lords')], }),";
            taskStr += "names = [ new I18NText( 'en-UK', 'Fourth day task " + i + "')] })";
            createAndAddTaskFromString(taskStr);
        }

        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Jedi Knights");
        groupIds.add("Sith Lords");
        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksAssignedFromDateToDateByGroupsByDays("Anakin",
                groupIds,
                createDate("2013-04-20"), 4, "en-UK");
        assertEquals(4, tasksByDays.size());

        // verify that correct tasks are present for each day
        for (Map.Entry<Day, List<TaskSummary>> entry : tasksByDays.entrySet()) {
            Date date = entry.getKey().getDate();
            List<TaskSummary> tasks = entry.getValue();
            if (date.equals(firstDay)) {
                // first day includes also tasks with no expiration date set
                assertEquals(firstDayTasksNr + noExpDateTasksNr, tasks.size());
            } else if (date.equals(secondDay)) {
                assertEquals(secondDayTasksNr, tasks.size());
            } else if (date.equals(thirdDay)) {
                assertEquals(thirdDayTasksNr, tasks.size());
            } else if (date.equals(fourthDay)) {
                assertEquals(fourthDayTasksNr, tasks.size());
            } else {
                // not expected date, fail the test
                fail("Unexpected date in results map! Date=" + date.toString());
            }
            checkTasksHaveExpectedExpirationDate(date, tasks);
        }

        // uses total number of days instead of end date
        Map<Day, List<TaskSummary>> tasksByDays2 = consoleTaskService.getTasksAssignedFromDateToDateByGroupsByDays("Anakin",
                groupIds,
                createDate("2013-04-20"), 4, "en-UK");
        assertEquals(tasksByDays, tasksByDays2);
    }

    protected void createAndAddTaskFromString(String taskStr) {
        TaskImpl task = TaskFactory.evalTask(new StringReader(taskStr));
        taskService.addTask(task, new HashMap<String, Object>());
    }

    /**
     * Verifies that the specified tasks have the expected expiration date. If the expiration date is not set, ignore that.
     *
     * @param expectedDate expected task expiration date
     * @param tasks        list of tasks to check
     */
    protected void checkTasksHaveExpectedExpirationDate(Date expectedDate, List<TaskSummary> tasks) {
        for (TaskSummary task : tasks) {
            // do not consider tasks with no expiration hate
            if (task.getExpirationTime() != null) {
                assertEquals(expectedDate, task.getExpirationTime());
            }
        }
    }

}
