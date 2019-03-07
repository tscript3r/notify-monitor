package pl.tscript3r.notify.monitor.services;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import pl.tscript3r.notify.monitor.config.ParsersSettings;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.domain.TaskSettings;
import pl.tscript3r.notify.monitor.parsers.ParserFactory;
import pl.tscript3r.notify.monitor.threads.ParserThreadImpl;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class TaskManagerServiceImplTest {

    public static final long ID = 1L;
    public static final long USER_ID = 2L;
    public static final String URL = "www.olx.pl/testPackage";
    public static final String URL_UPDATED = "http://www.olx.pl/updated";

    @Mock
    ParsersSettings parsersSettings;

    @Mock
    ApplicationContext context;

    @Mock
    ParserFactory parserFactory;


    private ParserThreadImpl parserThread = new ParserThreadImpl(parserFactory);
    private TaskManagerServiceImpl taskManagerService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(parsersSettings.getDefaultInterval()).thenReturn(3000);
        parserThread.setParserThreadCapacity(2);
        when(context.getBean(anyString())).thenReturn(parserThread);
        taskManagerService = new TaskManagerServiceImpl(context);
    }

    private Task getDefaultTask() {
        return Task.builder()
                .id(ID)
                .usersId(Sets.newHashSet(USER_ID))
                .url(URL)
                .taskSettings(new TaskSettings())
                .build();
    }

    @Test
    public void addTask() {
        assertTrue(taskManagerService.addTask(getDefaultTask()));
    }

    @Test
    public void addTaskFails() {
        when(context.getBean(anyString())).thenReturn(new ParserThreadImpl(parserFactory));

        Task task = getDefaultTask();
        assertFalse(taskManagerService.addTask(null));
        assertTrue(taskManagerService.addTask(task));
        assertFalse(taskManagerService.addTask(task));
        task = Task.builder()
                .id(ID + 1L)
                .usersId(Sets.newHashSet(USER_ID))
                .url(URL)
                .taskSettings(new TaskSettings())
                .build();
        assertTrue(taskManagerService.addTask(task));

        task = Task.builder()
                .id(ID + 2L)
                .usersId(Sets.newHashSet(USER_ID))
                .url(URL)
                .taskSettings(new TaskSettings())
                .build();
        assertTrue(taskManagerService.addTask(task));

        assertEquals(Integer.valueOf(2), taskManagerService.getThreadsCount());
    }

    @Test
    public void deleteTask() {
        Task task = getDefaultTask();
        assertTrue(taskManagerService.addTask(task));
        assertTrue(taskManagerService.deleteTask(task));
        assertFalse(taskManagerService.isTask(task));
    }

    @Test
    public void deleteTaskFail() {
        assertFalse(taskManagerService.deleteTask(new Task()));
        assertFalse(taskManagerService.deleteTask(null));
    }

    @Test
    public void isTask() {
        assertTrue(taskManagerService.addTask(getDefaultTask()));
        assertTrue(taskManagerService.isTask(getDefaultTask()));
        assertFalse(taskManagerService.isTask(new Task()));
    }

    @Test
    public void updateTask() {
        Task task = getDefaultTask();
        Task updatedTask = Task.builder()
                .id(ID)
                .usersId(Sets.newHashSet(USER_ID))
                .url(URL_UPDATED)
                .taskSettings(new TaskSettings())
                .build();
        assertTrue(taskManagerService.addTask(task));
        assertTrue(taskManagerService.updateTask(updatedTask));
    }

    @Test
    public void updateTaskFail() {
        assertFalse(taskManagerService.updateTask(null));
        Task task = getDefaultTask();
        Task updatedTask = Task.builder()
                .id(ID + 1L)
                .usersId(Sets.newHashSet(USER_ID))
                .url(URL)
                .taskSettings(new TaskSettings())
                .build();
        assertTrue(taskManagerService.addTask(task));
        assertTrue(taskManagerService.updateTask(updatedTask));
    }
}