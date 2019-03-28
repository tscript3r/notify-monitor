package pl.tscript3r.notify.monitor.dispatchers;

import com.google.common.collect.Sets;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.DownloadException;
import pl.tscript3r.notify.monitor.threads.DownloadMonitorThread;
import pl.tscript3r.notify.monitor.threads.drivers.DownloadMonitorThreadDriver;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class DownloadDispatcherTest {

    @Mock
    ApplicationContext context;

    @Mock
    DownloadMonitorThread downloadMonitorThread;

    @Mock
    DownloadMonitorThreadDriver downloadMonitorThreadDriver;

    private DownloadDispatcher downloadDispatcher;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        downloadDispatcher = new DownloadDispatcher();
        when(context.getBean(anyString())).thenReturn(downloadMonitorThread);
        when(downloadMonitorThread.getDriver()).thenReturn(downloadMonitorThreadDriver);
        downloadDispatcher.setApplicationContext(context);
    }


    private Task getDefaultTask() {
        return Task.builder()
                .id(1L)
                .usersId(Sets.newHashSet(1L))
                .url("https://www.olx.pl/oddam-za-darmo/")
                .usersId(Sets.newHashSet(1L))
                .refreshInterval(5000)
                .build();
    }

    @Test
    public void isDownloaded() {
        Task task = getDefaultTask();
        downloadDispatcher.addTask(task);
        when(downloadMonitorThreadDriver.isDownloaded(any())).thenReturn(true);
        when(downloadMonitorThreadDriver.hasTask(any())).thenReturn(true);
        assertTrue(downloadDispatcher.isDownloaded(task));
    }

    @Test
    public void returnDocument() {
        Task task = getDefaultTask();
        downloadDispatcher.addTask(task);
        when(downloadMonitorThreadDriver.isDownloaded(any())).thenReturn(true);
        when(downloadMonitorThreadDriver.hasTask(any())).thenReturn(true);
        when(downloadMonitorThreadDriver.returnDocument(any())).thenReturn(new Document(""));
        assertNotNull(downloadDispatcher.returnDocument(task));
    }

    @Test(expected = DownloadException.class)
    public void returnNullDocument() {
        Task task = getDefaultTask();
        downloadDispatcher.addTask(task);
        when(downloadMonitorThreadDriver.isDownloaded(any())).thenReturn(false);
        when(downloadMonitorThreadDriver.hasTask(any())).thenReturn(true);
        when(downloadMonitorThreadDriver.returnDocument(any())).thenReturn(new Document(""));
        downloadDispatcher.returnDocument(task);
    }

    @Test
    public void statusNotNull() {
        assertNotNull(downloadDispatcher.receiveStatus());
    }

}