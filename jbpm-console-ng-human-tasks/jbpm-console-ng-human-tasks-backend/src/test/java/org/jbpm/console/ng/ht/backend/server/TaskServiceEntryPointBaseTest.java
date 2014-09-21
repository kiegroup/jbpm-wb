//package org.jbpm.console.ng.ht.backend.server;
//
//import static org.junit.Assert.fail;
//
//import static org.junit.Assert.assertEquals;
//
//import java.io.StringReader;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.TimeZone;
//
//import org.jbpm.console.ng.ht.model.Day;
//import org.jbpm.console.ng.ht.model.TaskSummary;
//import org.jbpm.services.task.impl.factories.TaskFactory;
//import org.joda.time.DateTimeZone;
//import org.joda.time.Days;
//import org.joda.time.LocalDate;
//import org.junit.Test;
//import org.kie.api.task.model.Task;
//
//public abstract class TaskServiceEntryPointBaseTest extends HumanTasksBackendBaseTest {
//    private static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
//
//    @Test
//    public void testGetTasksForLongPeriod() {
//        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksOwnedFromDateToDateByDays("Bobba Fet",
//                getTaskStatuses(), createDate("2014-02-24"), 42);
//        assertEquals(42, tasksByDays.size());
//
//        tasksByDays = consoleTaskService.getTasksOwnedFromDateToDateByDays("Bobba Fet",
//                getTaskStatuses(), createDate("2014-02-24"), 3000);
//        assertEquals(3000, tasksByDays.size());
//
//        tasksByDays = consoleTaskService.getTasksOwnedFromDateToDateByDays("Bobba Fet",
//                getTaskStatuses(), createDate("2013-09-30"), 35);
//        assertEquals(35, tasksByDays.size());
//    }
//
//    @Test
//    public void testGetTasksForLongPeriodWithDayLightSaving() {
//        DateTimeZone defaultTZ = DateTimeZone.getDefault();
//        DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getTimeZone("Brazil/East")));
//        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksOwnedFromDateToDateByDays("Bobba Fet",
//                getTaskStatuses(), createDate("2014-02-24"), 42);
//        assertEquals(42, tasksByDays.size());
//
//        tasksByDays = consoleTaskService.getTasksOwnedFromDateToDateByDays("Bobba Fet",
//                getTaskStatuses(), createDate("2014-02-24"), 3000);
//        assertEquals(3000, tasksByDays.size());
//
//        tasksByDays = consoleTaskService.getTasksOwnedFromDateToDateByDays("Bobba Fet",
//                getTaskStatuses(), createDate("2013-09-30"), 35);
//        assertEquals(35, tasksByDays.size());
//        DateTimeZone.setDefault(defaultTZ);
//    }
//
//    @Test
//    public void testGetTasksOwnedFromDateToDateByDaysNoTasksAtAll() {
//        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksOwnedFromDateToDateByDays("Bobba Fet",
//                getTaskStatuses(), createDate("2013-04-18"), 3);
//        assertEquals(3, tasksByDays.size());
//        verifyNoTasksPresent(tasksByDays);
//    }
//
//    @Test
//    public void testGetTasksOwnedFromDateToDateByDaysNoTasksWithinRange() {
//        createTaskWithSpecifiedDueDateAndUserAndName("2012-06-08", "Bobba Fet", "Task before");
//        createTaskWithSpecifiedDueDateAndUserAndName("2013-04-17", "Bobba Fet", "Task before 2");
//        createTaskWithSpecifiedDueDateAndUserAndName("2013-04-21", "Bobba Fet", "Task after");
//        createTaskWithSpecifiedDueDateAndUserAndName("2014-01-12", "Bobba Fet", "Task after 2");
//        createTaskWithNoDueDateAndUserAndName("Bobba Fet", "No due date");
//        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksOwnedFromDateToDateByDays("Bobba Fet",
//                getTaskStatuses(), createDate("2013-04-18"), 3);
//        assertEquals(3, tasksByDays.size());
//        verifyNoTasksPresent(tasksByDays);
//    }
//
//    @Test
//    public void testGetTasksOwnedFromDateToDateByDaysOnlyTasksWithNoDueDate() {
//        createTaskWithNoDueDateAndUserAndName("Bobba Fet", "No due date");
//        createTaskWithNoDueDateAndUserAndName("Bobba Fet", "No due date 2");
//        LocalDate today = new LocalDate();
//        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksOwnedFromDateToDateByDays("Bobba Fet",
//                getTaskStatuses(), toJavaUtilDate(today), 3);
//        assertEquals(3, tasksByDays.size());
//        verifyCorrectTasksForSpecifiedDay(tasksByDays, today, 2);
//        verifyCorrectTasksForSpecifiedDay(tasksByDays, today.plusDays(1), 0);
//        verifyCorrectTasksForSpecifiedDay(tasksByDays, today.plusDays(2), 0);
//    }
//
//    @Test
//    public void testGetTasksOwnedFromDateToDateByDaysMultipleDifferentTasks() {
//        LocalDate today = new LocalDate();
//        LocalDate from = today.minusDays(2);
//        LocalDate to = today.plusDays(2);
//        int nrOfDaysTotal = Days.daysBetween(from, to).getDays() + 1;
//
//        int nrOfTasksWithNoExpDate = 2;
//        int nrOfTasksForFirstDay = 15;
//        int nrOfTasksForSecondDay = 0;
//        int nrOfTasksForThirdDay = 1;
//        int nrOfTasksForFourthDay = 100;
//        int nrOfTasksForFifthDay = 123;
//        LocalDate firstDay = from;
//        LocalDate secondDay = from.plusDays(1);
//        LocalDate thirdDay = from.plusDays(2);
//        LocalDate fourthDay = from.plusDays(3);
//        LocalDate fifthDay = to;
//        // ///////////////// Tasks that should _not_ be included in the result //////////////////
//        // tasks before the specified start date
//        createTaskWithSpecifiedDueDateAndUserAndName(from.minusDays(1), "Bobba Fet", "Before start date");
//        createTaskWithSpecifiedDueDateAndUserAndName(from.minusDays(200), "Bobba Fet", "Before start 2");
//        // tasks after the end date
//        createTaskWithSpecifiedDueDateAndUserAndName(to.plusDays(1), "Bobba Fet", "After end date");
//        createTaskWithSpecifiedDueDateAndUserAndName(to.plusDays(300), "Bobba Fet", "After end date 2 ");
//        // tasks with different owner
//        createTasksWithSpecifiedDueDateAndUserAndName(firstDay, "Darth Vader", "Correct day, but different owner", 20);
//        createTasksWithSpecifiedDueDateAndUserAndName(secondDay, "Yoda", "Correct day, but different owner", 100);
//        createTasksWithSpecifiedDueDateAndUserAndName(thirdDay, "Anakin", "Correct day, but different owner", 5);
//        createTasksWithSpecifiedDueDateAndUserAndName(fourthDay, "Darth Maul", "Correct day, but different owner", 34);
//        createTasksWithSpecifiedDueDateAndUserAndName(fifthDay, "Darth Maul", "Correct day, but different owner", 15);
//
//        // ///////////////// Tasks that should be included in the result //////////////////
//        // tasks with no due date -> included in Today
//        createTaskWithNoDueDateAndUserAndName("Bobba Fet", "No due date");
//        createTaskWithNoDueDateAndUserAndName("Bobba Fet", "No due date 2 ");
//
//        // tasks for each day
//        createTasksWithSpecifiedDueDateAndUserAndName(firstDay, "Bobba Fet", "First day task", nrOfTasksForFirstDay);
//        createTasksWithSpecifiedDueDateAndUserAndName(secondDay, "Bobba Fet", "Second day task", nrOfTasksForSecondDay);
//        createTasksWithSpecifiedDueDateAndUserAndName(thirdDay, "Bobba Fet", "Third day task", nrOfTasksForThirdDay);
//        createTasksWithSpecifiedDueDateAndUserAndName(fourthDay, "Bobba Fet", "Fourth day task", nrOfTasksForFourthDay);
//        createTasksWithSpecifiedDueDateAndUserAndName(fifthDay, "Bobba Fet", "fifth day task", nrOfTasksForFifthDay);
//
//        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksOwnedFromDateToDateByDays("Bobba Fet",
//                getTaskStatuses(), toJavaUtilDate(from), nrOfDaysTotal);
//
//        assertEquals(nrOfDaysTotal, tasksByDays.size());
//
//        // verify that correct number of tasks is present for each day
//        verifyCorrectTasksForSpecifiedDay(tasksByDays, firstDay, nrOfTasksForFirstDay);
//        verifyCorrectTasksForSpecifiedDay(tasksByDays, secondDay, nrOfTasksForSecondDay);
//        verifyCorrectTasksForSpecifiedDay(tasksByDays, thirdDay, nrOfTasksForThirdDay + nrOfTasksWithNoExpDate);
//        verifyCorrectTasksForSpecifiedDay(tasksByDays, fourthDay, nrOfTasksForFourthDay);
//        verifyCorrectTasksForSpecifiedDay(tasksByDays, fifthDay, nrOfTasksForFifthDay);
//    }
//
//    @Test
//    public void testGetTasksAssignedAsPotentialOwnerFromDateToDateByDaysNoTasksAtAll() {
//        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksAssignedAsPotentialOwnerFromDateToDateByDays(
//                "Bobba Fet", getTaskStatuses(), createDate("2013-04-18"), 3);
//        assertEquals(3, tasksByDays.size());
//        verifyNoTasksPresent(tasksByDays);
//    }
//
//    @Test
//    public void testGetTasksAssignedAsPotentialOwnerFromDateToDateByDaysNoTasksWithinRange() {
//        createTaskWithSpecifiedDueDateAndUserAndName("2012-06-08", "Bobba Fet", "Task before");
//        createTaskWithSpecifiedDueDateAndUserAndName("2013-04-17", "Bobba Fet", "Task before 2");
//        createTaskWithSpecifiedDueDateAndUserAndName("2013-04-21", "Bobba Fet", "Task after");
//        createTaskWithSpecifiedDueDateAndUserAndName("2014-01-12", "Bobba Fet", "Task after 2");
//        createTaskWithNoDueDateAndUserAndName("Bobba Fet", "No due date");
//        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksAssignedAsPotentialOwnerFromDateToDateByDays(
//                "Bobba Fet", getTaskStatuses(), createDate("2013-04-18"), 3);
//        assertEquals(3, tasksByDays.size());
//        verifyNoTasksPresent(tasksByDays);
//    }
//
//    @Test
//    public void testGetTasksAssignedAsPotentialOwnerFromDateToDateByDaysOnlyTasksWithNoDueDate() {
//        createTaskWithNoDueDateAndUserAndName("Bobba Fet", "No due date");
//        createTaskWithNoDueDateAndGroupAndName("Bounty Hunters", "No due date 2");
//        LocalDate today = new LocalDate();
//        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksAssignedAsPotentialOwnerFromDateToDateByDays(
//                "Bobba Fet", getTaskStatuses(), toJavaUtilDate(today), 3);
//        assertEquals(3, tasksByDays.size());
//        verifyCorrectTasksForSpecifiedDay(tasksByDays, today, 2);
//        verifyCorrectTasksForSpecifiedDay(tasksByDays, today.plusDays(1), 0);
//        verifyCorrectTasksForSpecifiedDay(tasksByDays, today.plusDays(2), 0);
//    }
//
//    @Test
//    public void testGetTasksAssignedAsPotentialOwnerFromDateToDateByDaysOnlyGroupTasksNoTasksAtAll() {
//        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksAssignedAsPotentialOwnerFromDateToDateByDays(
//                "Bobba Fet", getStatusesForGroupTasksOnly(), createDate("2013-04-18"), 3);
//        assertEquals(3, tasksByDays.size());
//        verifyNoTasksPresent(tasksByDays);
//    }
//
//    @Test
//    public void testGetTasksAssignedAsPotentialOwnerFromDateToDateByDaysOnlyGroupTasks() {
//        LocalDate today = new LocalDate();
//        // ///////////////// Tasks that should _not_ be included in the result ////////////
//        createTaskWithNoDueDateAndUserAndName("Bobba Fet", "No due date - personal task");
//        createTaskWithSpecifiedDueDateAndUserAndName(today, "Jedi Knight", "Different group");
//        // ///////////////// Tasks that should be included in the result //////////////////
//        createTaskWithNoDueDateAndGroupAndName("Bounty Hunters", "No due date 2 - group task");
//        createTaskWithSpecifiedDueDateAndGroupAndName(today, "Star Wars", "Valid group");
//        createTaskWithSpecifiedDueDateAndGroupAndName(today.plusDays(2), "Bounty Hunters", "Valid group - another day");
//
//        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksAssignedAsPotentialOwnerFromDateToDateByDays(
//                "Bobba Fet", getStatusesForGroupTasksOnly(), toJavaUtilDate(today), 3);
//        assertEquals(3, tasksByDays.size());
//        verifyCorrectTasksForSpecifiedDay(tasksByDays, today, 2);
//        verifyCorrectTasksForSpecifiedDay(tasksByDays, today.plusDays(1), 0);
//        verifyCorrectTasksForSpecifiedDay(tasksByDays, today.plusDays(2), 1);
//    }
//
//    @Test
//    public void testGetTasksAssignedAsPotentialOwnerFromDateToDateByDaysMultipleDifferentTasks() {
//        LocalDate today = new LocalDate();
//        LocalDate from = today.minusDays(2);
//        LocalDate to = today.plusDays(2);
//        int nrOfDaysTotal = Days.daysBetween(from, to).getDays() + 1;
//
//        // numbers needs to even as they will be divided by 2
//        int nrOfTasksWithNoExpDate = 2;
//        int nrOfTasksForFirstDay = 18;
//        int nrOfTasksForSecondDay = 0;
//        int nrOfTasksForThirdDay = 2;
//        int nrOfTasksForFourthDay = 10;
//        int nrOfTasksForFifthDay = 124;
//        LocalDate firstDay = from;
//        LocalDate secondDay = from.plusDays(1);
//        LocalDate thirdDay = from.plusDays(2);
//        LocalDate fourthDay = from.plusDays(3);
//        LocalDate fifthDay = to;
//        // ///////////////// Tasks that should _not_ be included in the result //////////////////
//        // tasks before the specified start date
//        createTaskWithSpecifiedDueDateAndUserAndName(from.minusDays(1), "Bobba Fet", "Before start date");
//        createTaskWithSpecifiedDueDateAndGroupAndName(from.minusDays(200), "Jedi Knights", "Before start 2");
//        // tasks after the end date
//        createTaskWithSpecifiedDueDateAndGroupAndName(to.plusDays(1), "Jedi Knights", "After end date");
//        createTaskWithSpecifiedDueDateAndUserAndName(to.plusDays(300), "Bobba Fet", "After end date 2 ");
//        // tasks with different owner
//        createTasksWithSpecifiedDueDateAndUserAndName(firstDay, "Darth Vader", "Correct day, but different owner", 10);
//        createTasksWithSpecifiedDueDateAndUserAndName(secondDay, "Yoda", "Correct day, but different owner", 5);
//        createTasksWithSpecifiedDueDateAndUserAndName(thirdDay, "Anakin", "Correct day, but different owner", 5);
//        createTasksWithSpecifiedDueDateAndUserAndName(fourthDay, "Darth Maul", "Correct day, but different owner", 10);
//        createTasksWithSpecifiedDueDateAndUserAndName(fifthDay, "Darth Maul", "Correct day, but different owner", 5);
//        // tasks with different group
//        createTasksWithSpecifiedDueDateAndGroupAndName(firstDay, "Jedi Knights", "Correct day, but different group", 10);
//        createTasksWithSpecifiedDueDateAndGroupAndName(secondDay, "Jedi Knights", "Correct day, but different group", 10);
//        createTasksWithSpecifiedDueDateAndGroupAndName(fifthDay, "Sith Lords", "Correct day, but different group", 10);
//        // ///////////////// Tasks that should be included in the result //////////////////
//        // tasks with no due date -> included in Today
//        createTaskWithNoDueDateAndUserAndName("Bobba Fet", "No due date");
//        createTaskWithNoDueDateAndGroupAndName("Bounty Hunters", "No due date 2 ");
//
//        // tasks for each day
//        createTasksWithSpecifiedDueDateAndUserAndName(firstDay, "Bobba Fet", "First day task", nrOfTasksForFirstDay / 2);
//        createTasksWithSpecifiedDueDateAndGroupAndName(firstDay, "Bounty Hunters", "1st group task", nrOfTasksForFirstDay / 2);
//
//        createTasksWithSpecifiedDueDateAndUserAndName(secondDay, "Bobba Fet", "Second day task", nrOfTasksForSecondDay / 2);
//        createTasksWithSpecifiedDueDateAndGroupAndName(secondDay, "Bounty Hunters", "2nd gr task", nrOfTasksForSecondDay / 2);
//
//        createTasksWithSpecifiedDueDateAndUserAndName(thirdDay, "Bobba Fet", "Third day task", nrOfTasksForThirdDay / 2);
//        createTasksWithSpecifiedDueDateAndGroupAndName(thirdDay, "Star Wars", "3rd day group task", nrOfTasksForThirdDay / 2);
//
//        createTasksWithSpecifiedDueDateAndUserAndName(fourthDay, "Bobba Fet", "Fourth day task", nrOfTasksForFourthDay / 2);
//        createTasksWithSpecifiedDueDateAndGroupAndName(fourthDay, "Star Wars", "4th day group task", nrOfTasksForFourthDay / 2);
//
//        createTasksWithSpecifiedDueDateAndUserAndName(fifthDay, "Bobba Fet", "Fifth day task", nrOfTasksForFifthDay / 2);
//        createTasksWithSpecifiedDueDateAndGroupAndName(fifthDay, "Bounty Hunters", "5th group task", nrOfTasksForFifthDay / 2);
//
//        Map<Day, List<TaskSummary>> tasksByDays = consoleTaskService.getTasksAssignedAsPotentialOwnerFromDateToDateByDays(
//                "Bobba Fet", getTaskStatuses(), toJavaUtilDate(from), nrOfDaysTotal);
//
//        assertEquals(nrOfDaysTotal, tasksByDays.size());
//
//        // verify that correct number of tasks is present for each day
//        verifyCorrectTasksForSpecifiedDay(tasksByDays, firstDay, nrOfTasksForFirstDay);
//        verifyCorrectTasksForSpecifiedDay(tasksByDays, secondDay, nrOfTasksForSecondDay);
//        verifyCorrectTasksForSpecifiedDay(tasksByDays, thirdDay, nrOfTasksForThirdDay + nrOfTasksWithNoExpDate);
//        verifyCorrectTasksForSpecifiedDay(tasksByDays, fourthDay, nrOfTasksForFourthDay);
//        verifyCorrectTasksForSpecifiedDay(tasksByDays, fifthDay, nrOfTasksForFifthDay);
//    }
//
//    private void createTaskWithSpecifiedDueDateAndUserAndName(String date, String userId, String name) {
//        String taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"" + DEFAULT_DATE_FORMAT + "\").parse(\"" + date + "\") } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('" + userId + "')], businessAdministrators = [ new User('Administrator') ], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', '" + name + "')] })";
//        createAndAddTaskFromString(taskStr);
//    }
//
//    private void createTaskWithSpecifiedDueDateAndUserAndName(LocalDate date, String userId, String name) {
//        createTaskWithSpecifiedDueDateAndUserAndName(date.toString(DEFAULT_DATE_FORMAT), userId, name);
//    }
//
//    private void createTaskWithSpecifiedDueDateAndGroupAndName(String date, String groupId, String name) {
//        String taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = new java.text.SimpleDateFormat(\"" + DEFAULT_DATE_FORMAT + "\").parse(\"" + date + "\") } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('" + groupId + "')], businessAdministrators = [ new User('Administrator') ], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', '" + name + "')] })";
//        createAndAddTaskFromString(taskStr);
//    }
//
//    private void createTaskWithSpecifiedDueDateAndGroupAndName(LocalDate date, String groupId, String name) {
//        createTaskWithSpecifiedDueDateAndGroupAndName(date.toString(DEFAULT_DATE_FORMAT), groupId, name);
//    }
//
//    private void createTaskWithNoDueDateAndUserAndName(String user, String name) {
//        String taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = null } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('" + user + "')], businessAdministrators = [ new User('Administrator') ], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', '" + name + "')] })";
//        createAndAddTaskFromString(taskStr);
//    }
//
//    private void createTasksWithSpecifiedDueDateAndUserAndName(LocalDate day, String userId, String taskName,
//            int nrOfTasksToCreate) {
//        for (int i = 0; i < nrOfTasksToCreate; i++) {
//            createTaskWithSpecifiedDueDateAndUserAndName(day, userId, taskName + i);
//        }
//    }
//
//    private void createTasksWithSpecifiedDueDateAndGroupAndName(LocalDate day, String groupId, String taskName,
//            int nrOfTasksToCreate) {
//        for (int i = 0; i < nrOfTasksToCreate; i++) {
//            createTaskWithSpecifiedDueDateAndGroupAndName(day, groupId, taskName + i);
//        }
//    }
//
//    private void createTaskWithNoDueDateAndGroupAndName(String groupId, String name) {
//        String taskStr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
//        taskStr += "expirationTime = null } ), ";
//        taskStr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('" + groupId + "')], businessAdministrators = [ new User('Administrator') ], }),";
//        taskStr += "names = [ new I18NText( 'en-UK', '" + name + "')] })";
//        createAndAddTaskFromString(taskStr);
//    }
//
//    protected void createAndAddTaskFromString(String taskStr) {
//        Task task = TaskFactory.evalTask(new StringReader(taskStr));
//        taskService.addTask(task, new HashMap<String, Object>());
//    }
//
//    private boolean sameDays(Date date, LocalDate localDate) {
//        return localDate.equals(new LocalDate(date));
//    }
//
//    private Date toJavaUtilDate(LocalDate localDate) {
//        return localDate.toDateMidnight().toDate();
//    }
//
//    private List<String> getTaskStatuses() {
//        List<String> statuses = new ArrayList<String>();
//        statuses.add("InProgress");
//        statuses.add("Ready");
//        statuses.add("Reserved");
//        statuses.add("Created");
//        return statuses;
//    }
//
//    private List<String> getStatusesForGroupTasksOnly() {
//        List<String> statuses = new ArrayList<String>();
//        statuses.add("Ready");
//        return statuses;
//    }
//
//    private void verifyNoTasksPresent(Map<Day, List<TaskSummary>> tasksByDays) {
//        for (Map.Entry<Day, List<TaskSummary>> entry : tasksByDays.entrySet()) {
//            assertEquals(0, entry.getValue().size());
//        }
//    }
//
//    private void verifyCorrectTasksForSpecifiedDay(Map<Day, List<TaskSummary>> tasksByDays, LocalDate day,
//            int nrOfTasks) {
//        for (Map.Entry<Day, List<TaskSummary>> entry : tasksByDays.entrySet()) {
//            Date date = entry.getKey().getDate();
//            List<TaskSummary> tasks = entry.getValue();
//            if (sameDays(date, day)) {
//                assertEquals(nrOfTasks, tasks.size());
//                checkTasksHaveExpectedExpirationDate(date, tasks);
//                return;
//            }
//        }
//        fail("Specified date " + day + " not found in days Map!");
//    }
//
//    /**
//     * Verifies that the specified tasks have the expected expiration date. If the expiration date is not set, ignore that.
//     *
//     * @param expectedDate expected task expiration date
//     * @param tasks        list of tasks to check
//     */
//    protected void checkTasksHaveExpectedExpirationDate(Date expectedDate, List<TaskSummary> tasks) {
//        for (TaskSummary task : tasks) {
//            // do not consider tasks with no expiration hate
//            if (task.getExpirationTime() != null) {
//                assertEquals(expectedDate, task.getExpirationTime());
//            }
//        }
//    }
//
//}
