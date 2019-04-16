package pl.tscript3r.notify.monitor.threads.drivers;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.tscript3r.notify.monitor.containers.AdContainer;
import pl.tscript3r.notify.monitor.crawlers.Crawler;
import pl.tscript3r.notify.monitor.crawlers.CrawlerFactory;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.CrawlerException;
import pl.tscript3r.notify.monitor.exceptions.IncompatibleHostnameException;
import pl.tscript3r.notify.monitor.exceptions.MonitorThreadException;
import pl.tscript3r.notify.monitor.services.AdFilterService;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class CrawlerMonitorThreadDriverTest {

    @Mock
    AdContainer adContainer;

    @Mock
    CrawlerFactory crawlerFactory;

    @Mock
    AdFilterService adFilterService;

    @Mock
    Crawler crawler;

    @Mock
    Crawler secondCrawler;

    private CrawlerMonitorThreadDriver crawlerMonitorThreadDriver;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        crawlerMonitorThreadDriver = new CrawlerMonitorThreadDriver(adContainer, crawlerFactory, adFilterService, 1,
                5);
        when(crawlerFactory.getParser(any())).thenReturn(crawler);
    }

    private Task getDefaultTask() {
        return Task.builder()
                .id(1L)
                .usersId(Sets.newHashSet(1L))
                .url("https://www.olx.pl/oddam-za-darmo/")
                .usersId(Sets.newHashSet(1L))
                .refreshInterval(5000)
                .adContainerMultiplier(1.8F)
                .build();
    }

    @Test
    public void isFull_shouldReturnTrue() throws InterruptedException, IOException {
        crawlerMonitorThreadDriver = new CrawlerMonitorThreadDriver(adContainer, crawlerFactory, adFilterService, 1, 0);
        crawlerMonitorThreadDriver.addTask(getDefaultTask());
        when(crawler.getAds(any())).thenReturn(Collections.emptyList());
        crawlerMonitorThreadDriver.execute(1);
        assertTrue(crawlerMonitorThreadDriver.isFull());
    }

    @Test
    public void isFull_shouldReturnFalse() throws InterruptedException, IOException {
        crawlerMonitorThreadDriver.addTask(getDefaultTask());
        when(crawler.getAds(any())).thenReturn(Collections.emptyList());
        crawlerMonitorThreadDriver.execute(1);
        assertFalse(crawlerMonitorThreadDriver.isFull());
    }

    @Test
    public void hasTask_shouldReturnTrue() {
        Task task = getDefaultTask();
        crawlerMonitorThreadDriver.addTask(task);
        assertTrue(crawlerMonitorThreadDriver.hasTask(task));
    }

    @Test
    public void hasTask_shouldReturnFalse() {
        crawlerMonitorThreadDriver.addTask(getDefaultTask());
        assertFalse(crawlerMonitorThreadDriver.hasTask(Task.builder().id(2L).url("test").build()));
    }

    @Test
    public void removeTask_shouldReturnTrue() {
        Task task = getDefaultTask();
        crawlerMonitorThreadDriver.addTask(task);
        assertTrue(crawlerMonitorThreadDriver.removeTask(task));
    }

    @Test
    public void removeTask_shouldReturnFalse() {
        Task task = getDefaultTask();
        crawlerMonitorThreadDriver.addTask(task);
        assertFalse(crawlerMonitorThreadDriver.removeTask(Task.builder().id(2L).url("test").build()));
    }

    @Test(expected = MonitorThreadException.class)
    public void addTask_shouldThrowMonitorThreadException() throws IOException, InterruptedException {
        crawlerMonitorThreadDriver = new CrawlerMonitorThreadDriver(adContainer, crawlerFactory, adFilterService, 1, 0);
        crawlerMonitorThreadDriver.addTask(getDefaultTask());
        when(crawler.getAds(any())).thenReturn(Collections.emptyList());
        crawlerMonitorThreadDriver.execute(1);
        assertTrue(crawlerMonitorThreadDriver.isFull());
        crawlerMonitorThreadDriver.addTask(Task.builder().id(2L).url("test").build());
    }

    @Test(expected = CrawlerException.class)
    public void execute_shouldThrowCrawlerExceptionBecauseOfAnUnexpectedException() throws IOException, InterruptedException {
        when(crawler.getAds(any())).thenThrow(IllegalArgumentException.class);
        crawlerMonitorThreadDriver.addTask(getDefaultTask());
        crawlerMonitorThreadDriver.execute(0);
    }

    @Test
    public void execute_shouldIgnoreCrawlerException() throws IOException, InterruptedException {
        when(crawler.getAds(any())).thenThrow(CrawlerException.class);
        crawlerMonitorThreadDriver.addTask(getDefaultTask());
        crawlerMonitorThreadDriver.execute(0);
    }

    @Test
    public void execute_shouldIgnoreIncompatibleHostnameException() throws IOException, InterruptedException {
        when(crawler.getAds(any())).thenThrow(IncompatibleHostnameException.class);
        crawlerMonitorThreadDriver.addTask(getDefaultTask());
        crawlerMonitorThreadDriver.execute(0);
    }

    @Test
    public void execute_shouldCooldownBecauseOfConnectException() throws IOException, InterruptedException {
        when(crawler.getAds(any())).thenThrow(ConnectException.class);
        crawlerMonitorThreadDriver.addTask(getDefaultTask());
        long time = System.currentTimeMillis();
        crawlerMonitorThreadDriver.execute(0);
        assertTrue((System.currentTimeMillis() - time) > 100);
    }

    @Test
    public void execute_shouldCooldownBecauseOfUnknownHostException() throws IOException, InterruptedException {
        when(crawler.getAds(any())).thenThrow(UnknownHostException.class);
        crawlerMonitorThreadDriver.addTask(getDefaultTask());
        long time = System.currentTimeMillis();
        crawlerMonitorThreadDriver.execute(0);
        assertTrue((System.currentTimeMillis() - time) > 100);
    }

    @Test
    public void execute_shouldCooldownBecauseOfSocketTimeoutException() throws IOException, InterruptedException {
        when(crawler.getAds(any())).thenThrow(SocketTimeoutException.class);
        crawlerMonitorThreadDriver.addTask(getDefaultTask());
        long time = System.currentTimeMillis();
        crawlerMonitorThreadDriver.execute(0);
        assertTrue((System.currentTimeMillis() - time) > 100);
    }

    @Test
    public void execute_shouldPickSecondCrawler() throws InterruptedException, IOException {
        Task task = getDefaultTask();
        crawlerMonitorThreadDriver.addTask(task);
        Task secondTask = Task.builder()
                .id(2L)
                .usersId(Sets.newHashSet(1L, 2L))
                .url("https://wwww.google.pl/")
                .refreshInterval(1)
                .adContainerMultiplier(1.6F)
                .build();
        crawlerMonitorThreadDriver.execute(0);
        verify(crawler, times(1)).getAds(any());
        when(crawler.getHandledHostname()).thenReturn("olx.pl");
        when(crawlerFactory.getParser(any())).thenReturn(secondCrawler);
        when(secondCrawler.getAds(any())).thenReturn(Collections.emptyList());
        assertTrue(crawlerMonitorThreadDriver.addTask(secondTask));
        crawlerMonitorThreadDriver.execute(0);

        verify(crawlerFactory, times(2)).getParser(any());
        verify(crawler, times(1)).getAds(any());
        verify(secondCrawler, times(1)).getAds(any());
    }

    @Test
    public void execute_shouldUsePreviousAddedCrawler() throws InterruptedException, IOException {
        Task task = getDefaultTask();
        Task secondTask = Task.builder()
                .id(2L)
                .usersId(Sets.newHashSet(1L, 2L))
                .url("https://wwww.olx.pl/test")
                .refreshInterval(1)
                .adContainerMultiplier(1.6F)
                .build();
        crawlerMonitorThreadDriver.addTask(task);
        crawlerMonitorThreadDriver.addTask(secondTask);
        when(crawler.getHandledHostname()).thenReturn("olx.pl");
        crawlerMonitorThreadDriver.execute(0);
        verify(crawler, times(2)).getAds(any());
    }

}
