package pl.tscript3r.notify.monitor.dispatchers;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.threads.CrawlerMonitorThread;
import pl.tscript3r.notify.monitor.threads.drivers.CrawlerMonitorThreadDriver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TaskDispatcherTest {

    @Mock
    ApplicationContext context;

    @Mock
    CrawlerMonitorThread crawlerMonitorThread;

    @Mock
    CrawlerMonitorThread secondCrawlerMonitorThread;

    @Mock
    CrawlerMonitorThreadDriver crawlerMonitorThreadDriver;

    TaskDispatcher taskDispatcher;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(context.getBean(anyString())).thenReturn(crawlerMonitorThread);
        when(crawlerMonitorThread.getDriver()).thenReturn(crawlerMonitorThreadDriver);
        taskDispatcher = new TaskDispatcher();
        taskDispatcher.setApplicationContext(context);
    }

    private Task getDefaultTask() {
        return Task.builder()
                .id(1L)
                .url("https://www.olx.pl/oddam-za-darmo/")
                .usersId(Sets.newHashSet(1L))
                .build();
    }

    @Test
    public void addFirstTask() {
        Task task = getDefaultTask();
        taskDispatcher.addTask(task);
        when(crawlerMonitorThread.getDriver().hasTask(any())).thenReturn(true);
        assertTrue(taskDispatcher.containsTask(task));
        verify(context, times(1)).getBean(anyString());
        verify(crawlerMonitorThread, times(1)).start();
        verify(crawlerMonitorThreadDriver, times(1)).addTask(any());
    }

    @Test
    public void addSecondTask() {
        addFirstTask();
        when(crawlerMonitorThread.getDriver().isFull()).thenReturn(false);
        Task task = Task.builder()
                .id(2L)
                .url("https://www.olx.pl/oddam-za-darmo2/")
                .usersId(Sets.newHashSet(1L))
                .build();
        taskDispatcher.addTask(task);
        verify(crawlerMonitorThreadDriver, times(2)).addTask(any());
        verify(context, times(1)).getBean(anyString());
        verify(crawlerMonitorThread, times(1)).start();
    }

    @Test
    public void addTaskWithNewCrawlerThread() {
        addFirstTask();
        when(crawlerMonitorThreadDriver.isFull()).thenReturn(true);
        when(context.getBean(anyString())).thenReturn(secondCrawlerMonitorThread);
        when(secondCrawlerMonitorThread.getDriver()).thenReturn(crawlerMonitorThreadDriver);
        when(secondCrawlerMonitorThread.getDriver().hasTask(any())).thenReturn(true);
        Task task = Task.builder()
                .id(2L)
                .url("https://www.olx.pl/oddam-za-darmo2/")
                .usersId(Sets.newHashSet(1L))
                .build();
        taskDispatcher.addTask(task);
        assertTrue(taskDispatcher.containsTask(task));
        verify(context, times(2)).getBean(anyString());
        verify(crawlerMonitorThread, times(1)).start();
        verify(crawlerMonitorThreadDriver, times(2)).addTask(any());
        verify(secondCrawlerMonitorThread, times(1)).start();
        verify(crawlerMonitorThreadDriver, times(2)).addTask(any());
    }

    @Test
    public void removeTask() {
        addFirstTask();
        when(crawlerMonitorThreadDriver.hasTask(any())).thenReturn(true);
        when(crawlerMonitorThreadDriver.removeTask(any())).thenReturn(true);
        assertTrue(taskDispatcher.removeTask(getDefaultTask()));
        verify(crawlerMonitorThreadDriver, times(2)).hasTask(any());
        verify(crawlerMonitorThreadDriver, times(1)).removeTask(any());
    }

    @Test
    public void containsTask() {
        Task task = getDefaultTask();
        assertFalse(taskDispatcher.containsTask(task));
        addFirstTask();
        when(crawlerMonitorThread.getDriver().hasTask(any())).thenReturn(true);
        assertTrue(taskDispatcher.containsTask(task));
        verify(crawlerMonitorThreadDriver, times(2)).hasTask(any());
    }
}