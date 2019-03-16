package pl.tscript3r.notify.monitor.threads.drivers;

import com.google.common.collect.Sets;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.tscript3r.notify.monitor.components.JsoupDocumentDownloader;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.domain.TaskSettings;
import pl.tscript3r.notify.monitor.exceptions.CrawlerException;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class DownloadMonitorThreadDriverTest {

    @Mock
    JsoupDocumentDownloader jsoupDocumentDownloader;

    DownloadMonitorThreadDriver downloadMonitorThreadDriver;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(jsoupDocumentDownloader.download(any())).thenReturn(new Document(""));
        downloadMonitorThreadDriver = new DownloadMonitorThreadDriver(jsoupDocumentDownloader);
    }

    private Task getDefaultTask() {
        return Task.builder()
                .id(1L)
                .usersId(Sets.newHashSet(1L))
                .url("https://www.olx.pl/oddam-za-darmo/")
                .usersId(Sets.newHashSet(1L))
                .taskSettings(new TaskSettings(5000))
                .build();
    }

    @Test
    public void addTask() {
        assertTrue(downloadMonitorThreadDriver.addTask(getDefaultTask()));
    }

    @Test
    public void isFull() {

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
        downloadMonitorThreadDriver.downloadTasks(0);
        assertTrue(downloadMonitorThreadDriver.isDownloaded(task));
    }

    @Test
    public void returnDocument() throws InterruptedException {
        Task task = getDefaultTask();
        assertTrue(downloadMonitorThreadDriver.addTask(getDefaultTask()));
        assertFalse(downloadMonitorThreadDriver.isDownloaded(task));
        downloadMonitorThreadDriver.downloadTasks(0);
        assertTrue(downloadMonitorThreadDriver.isDownloaded(task));
        assertNotNull(downloadMonitorThreadDriver.returnDocument(task));
    }

    @Test(expected = CrawlerException.class)
    public void downloadTasks() throws IOException, InterruptedException {
        assertTrue(downloadMonitorThreadDriver.addTask(getDefaultTask()));
        when(jsoupDocumentDownloader.download(any())).thenThrow(new IOException());
        downloadMonitorThreadDriver.downloadTasks(0);
    }
}