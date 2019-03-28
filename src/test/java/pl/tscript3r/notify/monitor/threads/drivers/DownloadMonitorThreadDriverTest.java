package pl.tscript3r.notify.monitor.threads.drivers;

import com.google.common.collect.Sets;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.tscript3r.notify.monitor.components.JsoupDocumentDownloader;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.CrawlerException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class DownloadMonitorThreadDriverTest {

    @Mock
    JsoupDocumentDownloader jsoupDocumentDownloader;

    private DownloadMonitorThreadDriver downloadMonitorThreadDriver;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(jsoupDocumentDownloader.download(any())).thenReturn(new Document(""));
        downloadMonitorThreadDriver = new DownloadMonitorThreadDriver(jsoupDocumentDownloader, 2, 0);
    }

    private Task getDefaultTask() {
        return Task.builder()
                .id(1L)
                .usersId(Sets.newHashSet(1L))
                .url("https://www.olx.pl/oddam-za-darmo/")
                .usersId(Sets.newHashSet(1L))
                .refreshInterval(120)
                .build();
    }

    @Test
    public void addTask() {
        assertTrue(downloadMonitorThreadDriver.addTask(getDefaultTask()));
    }

    @Test
    public void addTaskFail() {
        assertTrue(downloadMonitorThreadDriver.addTask(getDefaultTask()));
        assertFalse(downloadMonitorThreadDriver.addTask(getDefaultTask()));
    }

    @Test
    public void isFull() {
        assertFalse(downloadMonitorThreadDriver.isFull());
        assertTrue(downloadMonitorThreadDriver.addTask(getDefaultTask()));
        assertFalse(downloadMonitorThreadDriver.isFull());
        assertTrue(downloadMonitorThreadDriver.addTask(Task.builder()
                .id(2L)
                .usersId(Sets.newHashSet(1L))
                .refreshInterval(120)
                .build()));
        assertTrue(downloadMonitorThreadDriver.isFull());
    }

    @Test
    public void hasTask() {
        assertTrue(downloadMonitorThreadDriver.addTask(getDefaultTask()));
        assertTrue(downloadMonitorThreadDriver.hasTask(getDefaultTask()));
    }

    @Test
    public void removeTask() {
        assertTrue(downloadMonitorThreadDriver.addTask(getDefaultTask()));
        assertTrue(downloadMonitorThreadDriver.removeTask(getDefaultTask()));
    }

    @Test
    public void isDownloaded() throws InterruptedException {
        Task task = getDefaultTask();
        assertTrue(downloadMonitorThreadDriver.addTask(getDefaultTask()));
        assertFalse(downloadMonitorThreadDriver.isDownloaded(task));
        downloadMonitorThreadDriver.execute(0);
        assertTrue(downloadMonitorThreadDriver.isDownloaded(task));
    }

    @Test
    public void returnDocument() throws InterruptedException {
        Task task = getDefaultTask();
        assertTrue(downloadMonitorThreadDriver.addTask(getDefaultTask()));
        assertFalse(downloadMonitorThreadDriver.isDownloaded(task));
        downloadMonitorThreadDriver.execute(0);
        assertTrue(downloadMonitorThreadDriver.isDownloaded(task));
        assertNotNull(downloadMonitorThreadDriver.returnDocument(task));
    }

    @Test(expected = CrawlerException.class)
    public void downloadTasks() throws IOException, InterruptedException {
        assertTrue(downloadMonitorThreadDriver.addTask(getDefaultTask()));
        when(jsoupDocumentDownloader.download(any())).thenThrow(new IOException());
        downloadMonitorThreadDriver.execute(0);
    }

    @Test
    public void handleSilentUnknownHostException() throws InterruptedException, IOException {
        assertTrue(downloadMonitorThreadDriver.addTask(getDefaultTask()));
        when(jsoupDocumentDownloader.download(any())).thenThrow(new UnknownHostException());
        downloadMonitorThreadDriver.execute(0);
    }

    @Test
    public void handleSilentConnectException() throws InterruptedException, IOException {
        assertTrue(downloadMonitorThreadDriver.addTask(getDefaultTask()));
        when(jsoupDocumentDownloader.download(any())).thenThrow(new ConnectException());
        downloadMonitorThreadDriver.execute(0);
    }

}