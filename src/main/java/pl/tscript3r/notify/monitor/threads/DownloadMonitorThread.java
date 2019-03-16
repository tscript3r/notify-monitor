package pl.tscript3r.notify.monitor.threads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.exceptions.MonitorThreadException;
import pl.tscript3r.notify.monitor.threads.drivers.DownloadMonitorThreadDriver;
import pl.tscript3r.notify.monitor.threads.drivers.MonitorThreadDriver;

@Slf4j
@Component
@Scope("prototype")
public class DownloadMonitorThread implements MonitorThread {

    private final DownloadMonitorThreadDriver downloadMonitorThreadDriver;

    private Thread thread;
    private Boolean isStopped = false;

    public DownloadMonitorThread(DownloadMonitorThreadDriver downloadMonitorThreadDriver) {
        this.downloadMonitorThreadDriver = downloadMonitorThreadDriver;
    }

    @Override
    public MonitorThreadDriver getDriver() {
        return downloadMonitorThreadDriver;
    }

    @Override
    public void start() {
        if (!isStopped && thread == null || !thread.isAlive()) {
            isStopped = false;
            thread = new Thread(this);
            thread.setName("DownloadThread=" + thread.getId());
            thread.start();
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
    public void run() {
        while (!thread.isInterrupted()) {
            try {
                downloadMonitorThreadDriver.downloadTasks(5000);
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                if (!isStopped)
                    throwException(e);
            }
        }
    }

    private void throwException(Exception e) {
        throw new MonitorThreadException(e.getMessage());
    }

}
