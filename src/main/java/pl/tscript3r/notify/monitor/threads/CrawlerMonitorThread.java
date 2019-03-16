package pl.tscript3r.notify.monitor.threads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.exceptions.MonitorThreadException;
import pl.tscript3r.notify.monitor.threads.drivers.CrawlerMonitorThreadDriver;
import pl.tscript3r.notify.monitor.threads.drivers.MonitorThreadDriver;

@Slf4j
@Component
@Scope("prototype")
public class CrawlerMonitorThread implements MonitorThread {

    private final CrawlerMonitorThreadDriver crawlerMonitorThreadDriver;

    private Thread thread;
    private Boolean isStopped = false;

    public CrawlerMonitorThread(CrawlerMonitorThreadDriver crawlerMonitorThreadDriver) {
        this.crawlerMonitorThreadDriver = crawlerMonitorThreadDriver;
    }

    @Override
    public void start() {
        if (!isStopped && thread == null || !thread.isAlive()) {
            isStopped = false;
            thread = new Thread(this);
            thread.setName("CrawlerThread=" + thread.getId());
            thread.start();
            log.debug("Started");
        }
    }

    @Override
    public void stop() {
        if (thread != null) {
            log.warn("Stopped");
            isStopped = true;
            thread.interrupt();
        }
    }

    @Override
    public Boolean isRunning() {
        return thread.isAlive();
    }

    @Override
    public MonitorThreadDriver getDriver() {
        return crawlerMonitorThreadDriver;
    }

    @Override
    public void run() {
        log.debug("Thread started");
        while (!thread.isInterrupted()) {
            try {
                crawlerMonitorThreadDriver.crawlTasks(500);
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                if (!isStopped)
                    throw new MonitorThreadException(e.getMessage());
            }
        }
        log.warn("Thread stopped");
    }


}
