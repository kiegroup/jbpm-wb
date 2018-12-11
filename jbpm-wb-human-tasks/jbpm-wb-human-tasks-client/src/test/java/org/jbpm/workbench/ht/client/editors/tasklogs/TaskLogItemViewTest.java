package org.jbpm.workbench.ht.client.editors.tasklogs;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.ht.client.resources.i18n.Constants;
import org.jbpm.workbench.ht.model.TaskEventSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TaskLogItemViewTest {

    @Mock
    private Constants constants;

    @Mock
    private Span logTime;

    @Mock
    protected Span logInfo;

    @Mock
    protected Span logIcon;

    @Mock
    protected Span logTypeDesc;

    @Mock
    private TranslationService translationService;

    @Mock
    private DataBinder<TaskEventSummary> logSummary;

    @InjectMocks
    private TaskLogItemView view;

    @Before
    public void setupMocks() {
        when(constants.Task()).thenReturn("Task");
        when(constants.ByUser()).thenReturn("by user");
        when(constants.ByProcess()).thenReturn("by process");
        when(translationService.format(any())).then(i -> i.getArgumentAt(0, String.class));
    }

    @Test
    public void testStoppedTask() {
        TaskEventSummary model = new TaskEventSummary(
                2L,
                1L,
                "STOPPED",
                "Maria",
                3L,
                Date.from(LocalDateTime.of(2018, 12, 5, 17, 15).atZone(ZoneId.systemDefault()).toInstant()),
                "Maria stopped this task"
        );
        view.setValue(model);

        verify(logSummary).setModel(model);
        verify(logTime).setAttribute("data-original-title", "05/12/2018 17:15");
        verify(logIcon).setAttribute("data-original-title", "Task stopped");
        verify(logIcon).setClassName("list-view-pf-icon-sm kie-timeline-list-view-pf-icon-sm fa fa-cogs kie-timeline-icon--completed");
        verify(logInfo).setTextContent("by user Maria ");
        verify(logTypeDesc).setTextContent("Task stopped");
    }

    @Test
    public void testUpdatedTask() {
        TaskEventSummary model = new TaskEventSummary(
                2L,
                2L,
                "UPDATED",
                "Maria",
                3L,
                createDate(2016, 2, 5, 15, 2),
                "Maria updated this task"
        );

        view.setValue(model);

        verify(logSummary).setModel(model);
        verify(logTime).setAttribute("data-original-title", "05/02/2016 15:02");
        verify(logIcon).setAttribute("data-original-title", "Task updated");
        verify(logIcon).setClassName("list-view-pf-icon-sm kie-timeline-list-view-pf-icon-sm fa fa-user");
        verify(logInfo).setTextContent("by user Maria  (Maria updated this task ) ");
        verify(logTypeDesc).setTextContent("Task updated");
    }

    @Test
    public void testClaimedTask() {
        TaskEventSummary model = new TaskEventSummary(
                3L,
                5L,
                "CLAIMED",
                "John",
                3L,
                createDate(2018, 1, 20, 5, 30),
                "John claimed this task"
        );

        view.setValue(model);

        verify(logSummary).setModel(model);
        verify(logTime).setAttribute("data-original-title", "20/01/2018 05:30");
        verify(logIcon).setAttribute("data-original-title", "Task claimed");
        verify(logIcon).setClassName("list-view-pf-icon-sm kie-timeline-list-view-pf-icon-sm fa fa-user");
        verify(logInfo).setTextContent("by user John ");
        verify(logTypeDesc).setTextContent("Task claimed");
    }

    @Test
    public void testDelegatedTask() {
        TaskEventSummary model = new TaskEventSummary(
                1L,
                3L,
                "DELEGATED",
                "Jan",
                3L,
                createDate(2017, 12, 15, 15, 0),
                "Jan delegated this task"
        );

        view.setValue(model);

        verify(logSummary).setModel(model);
        verify(logTime).setAttribute("data-original-title", "15/12/2017 15:00");
        verify(logIcon).setAttribute("data-original-title", "Task delegated");
        verify(logIcon).setClassName("list-view-pf-icon-sm kie-timeline-list-view-pf-icon-sm fa fa-cogs");
        verify(logInfo).setTextContent("by user Jan ");
        verify(logTypeDesc).setTextContent("Task delegated");
    }

    @Test
    public void testAddedTask() {
        TaskEventSummary model = new TaskEventSummary(
                1L,
                1L,
                "ADDED",
                "exampleProcess",
                3L,
                createDate(2017, 12, 15, 4, 59),
                "exampleProcess added this task"
        );

        view.setValue(model);

        verify(logSummary).setModel(model);
        verify(logTime).setAttribute("data-original-title", "15/12/2017 04:59");
        verify(logIcon).setAttribute("data-original-title", "Task added");
        verify(logIcon).setClassName("list-view-pf-icon-sm kie-timeline-list-view-pf-icon-sm fa fa-cogs");
        verify(logInfo).setTextContent("by process 'exampleProcess' ");
        verify(logTypeDesc).setTextContent("Task added");
    }

    @Test
    public void testCompletedTask() {
        TaskEventSummary model = new TaskEventSummary(
                1L,
                3L,
                "COMPLETED",
                "Andrew",
                3L,
                createDate(2018, 12, 11, 0, 15),
                "Andrew completed this task"
        );

        view.setValue(model);

        verify(logSummary).setModel(model);
        verify(logTime).setAttribute("data-original-title", "11/12/2018 00:15");
        verify(logIcon).setAttribute("data-original-title", "Task completed");
        verify(logIcon).setClassName("list-view-pf-icon-sm kie-timeline-list-view-pf-icon-sm fa fa-user kie-timeline-icon--completed");
        verify(logInfo).setTextContent("by user Andrew ");
        verify(logTypeDesc).setTextContent("Task completed");
    }

    private Date createDate(int year, int month, int dayOfMonth, int hour, int minute) {
        return Date.from(LocalDateTime.of(year, month, dayOfMonth, hour, minute).atZone(ZoneId.systemDefault()).toInstant());
    }
}