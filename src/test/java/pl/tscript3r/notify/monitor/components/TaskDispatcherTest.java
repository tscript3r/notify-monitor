package pl.tscript3r.notify.monitor.components;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.threads.CrawlerThread;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TaskDispatcherTest {

    @Mock
    ApplicationContext context;

    @Mock
    CrawlerThread crawlerThread;

    @Mock
    CrawlerThread secondCrawlerThread;

    TaskDispatcher taskDispatcher;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(context.getBean(anyString())).thenReturn(crawlerThread);
        taskDispatcher = new TaskDispatcher(context);
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
        when(crawlerThread.hasTask(any())).thenReturn(true);
        assertTrue(taskDispatcher.containsTask(task));
        verify(context, times(1)).getBean(anyString());
        verify(crawlerThread, times(1)).start();
        verify(crawlerThread, times(1)).addTask(any());
    }

    @Test
    public void addSecondTask() {
        addFirstTask();
        when(crawlerThread.isFull()).thenReturn(false);
        Task task = Task.builder()
                .id(2L)
                .url("https://www.olx.pl/oddam-za-darmo2/")
                .usersId(Sets.newHashSet(1L))
                .build();
        taskDispatcher.addTask(task);
        verify(crawlerThread, times(2)).addTask(any());
        verify(context, times(1)).getBean(anyString());
        verify(crawlerThread, times(1)).start();
    }

    @Test
    public void addTaskWithNewCrawlerThread() {
        addFirstTask();
        when(crawlerThread.isFull()).thenReturn(true);
        when(context.getBean(anyString())).thenReturn(secondCrawlerThread);
        Task task = Task.builder()
                .id(2L)
                .url("https://www.olx.pl/oddam-za-darmo2/")
                .usersId(Sets.newHashSet(1L))
                .build();
        taskDispatcher.addTask(task);
        assertTrue(taskDispatcher.containsTask(task));
        verify(context, times(2)).getBean(anyString());
        verify(crawlerThread, times(1)).start();
        verify(crawlerThread, times(1)).addTask(any());
        verify(secondCrawlerThread, times(1)).start();
        verify(secondCrawlerThread, times(1)).addTask(any());
    }

    @Test
    public void removeTask() {
        addFirstTask();
        when(crawlerThread.hasTask(any())).thenReturn(true);
        when(crawlerThread.removeTask(any())).thenReturn(true);
        assertTrue(taskDispatcher.removeTask(getDefaultTask()));
        verify(crawlerThread, times(2)).hasTask(any());
        verify(crawlerThread, times(1)).removeTask(any());
    }

    @Test
    public void containsTask() {
        Task task = getDefaultTask();
        assertFalse(taskDispatcher.containsTask(task));
        addFirstTask();
        when(crawlerThread.hasTask(any())).thenReturn(true);
        assertTrue(taskDispatcher.containsTask(task));
        verify(crawlerThread, times(2)).hasTask(any());
    }
}