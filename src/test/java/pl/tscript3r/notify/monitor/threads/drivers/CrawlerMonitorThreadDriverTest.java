package pl.tscript3r.notify.monitor.threads.drivers;

import com.google.common.collect.Sets;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.tscript3r.notify.monitor.components.AdContainer;
import pl.tscript3r.notify.monitor.crawlers.Crawler;
import pl.tscript3r.notify.monitor.crawlers.CrawlerFactory;
import pl.tscript3r.notify.monitor.dispatchers.DownloadDispatcher;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.MonitorThreadException;
import pl.tscript3r.notify.monitor.services.AdFilterService;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class CrawlerMonitorThreadDriverTest {

    @Mock
    DownloadDispatcher downloadDispatcher;

    @Mock
    AdContainer adContainer;

    @Mock
    CrawlerFactory crawlerFactory;

    @Mock
    AdFilterService adFilterService;

    @Mock
    Crawler crawler;

    private CrawlerMonitorThreadDriver crawlerMonitorThreadDriver;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        crawlerMonitorThreadDriver = new CrawlerMonitorThreadDriver(downloadDispatcher, adContainer, crawlerFactory, adFilterService, 2);
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
    public void isFull() {
        assertFalse(crawlerMonitorThreadDriver.isFull());
        crawlerMonitorThreadDriver.addTask(getDefaultTask());
        crawlerMonitorThreadDriver.addTask(Task.builder().build());
        assertTrue(crawlerMonitorThreadDriver.isFull());

    }

    @Test
    public void hasTask() {
        Task task = getDefaultTask();
        assertFalse(crawlerMonitorThreadDriver.hasTask(task));
        crawlerMonitorThreadDriver.addTask(task);
        assertTrue(crawlerMonitorThreadDriver.hasTask(task));
        assertFalse(crawlerMonitorThreadDriver.hasTask(Task.builder().id(2L).build()));
    }

    @Test
    public void removeTask() {
        Task task = getDefaultTask();
        assertFalse(crawlerMonitorThreadDriver.removeTask(task));
        assertTrue(crawlerMonitorThreadDriver.addTask(task));
        assertTrue(crawlerMonitorThreadDriver.removeTask(task));
        assertFalse(crawlerMonitorThreadDriver.removeTask(task));
    }

    @Test(expected = MonitorThreadException.class)
    public void addTask() {
        Task task = getDefaultTask();
        assertTrue(crawlerMonitorThreadDriver.addTask(task));
        assertFalse(crawlerMonitorThreadDriver.addTask(task)); // should ignore
        assertFalse(crawlerMonitorThreadDriver.isFull());
        assertTrue(crawlerMonitorThreadDriver.addTask(Task.builder().id(2L).build()));
        assertTrue(crawlerMonitorThreadDriver.isFull());
        crawlerMonitorThreadDriver.addTask(Task.builder().build());
    }

    @Test
    public void crawlTaskAsDownloaded() throws InterruptedException {
        Task task = getDefaultTask();
        assertTrue(task.isRefreshable());
        assertTrue(crawlerMonitorThreadDriver.addTask(task));
        when(downloadDispatcher.isDownloaded(any())).thenReturn(true);
        when(downloadDispatcher.returnDocument(any())).thenReturn(new Document(""));
        when(crawlerFactory.getParser(any())).thenReturn(crawler);
        when(crawler.getAds(any(), any())).thenReturn(Arrays.asList(new Ad(null, null), new Ad(null, null)));
        crawlerMonitorThreadDriver.execute(0);

        verify(downloadDispatcher, times(1)).isDownloaded(any());
        verify(adContainer, times(1)).addAds(any(), anyCollection());
        verify(crawlerFactory, times(1)).getParser(any());
        verify(crawler, times(1)).getAds(any(), any());
        verify(downloadDispatcher, times(1)).returnDocument(any());
        verify(adFilterService, times(1)).filter(anyList());
        assertFalse(task.isRefreshable());
    }

    @Test
    public void crawlTaskAsNotDownloadedAndNotDownloadAdded() throws InterruptedException {
        assertTrue(crawlerMonitorThreadDriver.addTask(getDefaultTask()));
        when(downloadDispatcher.isDownloaded(any())).thenReturn(false);
        when(downloadDispatcher.containsTask(any())).thenReturn(false);
        crawlerMonitorThreadDriver.execute(0);
        verify(downloadDispatcher, times(1)).addTask(any());
    }

    @Test
    public void crawlTaskAsNotDownloadedAndDownloadAdded() throws InterruptedException {
        assertTrue(crawlerMonitorThreadDriver.addTask(getDefaultTask()));
        when(downloadDispatcher.isDownloaded(any())).thenReturn(false);
        when(downloadDispatcher.containsTask(any())).thenReturn(true);
        crawlerMonitorThreadDriver.execute(0);
        verify(downloadDispatcher, never()).addTask(any());
    }

    @Test
    public void crawlTaskWithTheSameCrawlerInstance() throws InterruptedException {
        Task task = Task.builder()
                .id(2L)
                .usersId(Sets.newHashSet(1L))
                .url("https://www.olx.pl/oddam-za-darmo/2")
                .usersId(Sets.newHashSet(1L))
                .refreshInterval(5000)
                .build();
        Task task2 = getDefaultTask();
        assertTrue(task.isRefreshable());
        assertTrue(crawlerMonitorThreadDriver.addTask(task));
        assertTrue(crawlerMonitorThreadDriver.addTask(task2));
        when(downloadDispatcher.isDownloaded(any())).thenReturn(true);
        when(downloadDispatcher.returnDocument(any())).thenReturn(new Document(""));
        when(crawlerFactory.getParser(any())).thenReturn(crawler);
        when(crawler.getAds(any(), any())).thenReturn(Arrays.asList(new Ad(null, null), new Ad(null, null)));
        when(crawler.getHandledHostname()).thenReturn("olx.pl");
        crawlerMonitorThreadDriver.execute(0);

        verify(downloadDispatcher, times(2)).isDownloaded(any());
        verify(adContainer, times(2)).addAds(any(), anyCollection());
        verify(crawlerFactory, times(1)).getParser(any());
        verify(crawler, times(2)).getAds(any(), any());
        verify(downloadDispatcher, times(2)).returnDocument(any());
        assertFalse(task.isRefreshable());
        assertFalse(task2.isRefreshable());
    }
}