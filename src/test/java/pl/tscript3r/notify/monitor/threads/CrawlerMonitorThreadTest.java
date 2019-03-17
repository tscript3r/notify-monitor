package pl.tscript3r.notify.monitor.threads;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.tscript3r.notify.monitor.threads.drivers.CrawlerMonitorThreadDriver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class CrawlerMonitorThreadTest {

    @Mock
    CrawlerMonitorThreadDriver monitorThreadDriver;

    CrawlerMonitorThread crawlerMonitorThread;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        crawlerMonitorThread = new CrawlerMonitorThread(monitorThreadDriver);
    }

    @Test
    public void shouldNotThrowMonitorThreadException() throws InterruptedException {
        crawlerMonitorThread.start();
        Thread.sleep(50);
        crawlerMonitorThread.stop();
    }

    @Test
    public void isRunning() throws InterruptedException {
        assertFalse(crawlerMonitorThread.isRunning());
        crawlerMonitorThread.start();
        Thread.sleep(50);
        assertTrue(crawlerMonitorThread.isRunning());
        crawlerMonitorThread.stop();
        Thread.sleep(50);
        assertFalse(crawlerMonitorThread.isRunning());
    }

    @Test
    public void monitorThreadDriverExecuteCalled() throws InterruptedException {
        crawlerMonitorThread.start();
        Thread.sleep(50);
        crawlerMonitorThread.stop();
        verify(monitorThreadDriver, atLeastOnce()).execute(anyInt());
    }
}