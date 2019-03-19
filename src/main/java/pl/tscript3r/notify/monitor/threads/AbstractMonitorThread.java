package pl.tscript3r.notify.monitor.threads;

import org.slf4j.Logger;
import pl.tscript3r.notify.monitor.exceptions.MonitorThreadException;
import pl.tscript3r.notify.monitor.threads.drivers.MonitorThreadDriver;

public class AbstractMonitorThread implements MonitorThread {

    private final Logger log;
    private final MonitorThreadDriver monitorThreadDriver;

    private Thread thread;
    private Boolean isStopped = false;

    public AbstractMonitorThread(Logger log, MonitorThreadDriver monitorThreadDriver) {
        this.log = log;
        this.monitorThreadDriver = monitorThreadDriver;
    }

    @Override
    public void start() {
        if (thread == null || !thread.isAlive()) {
            isStopped = false;
            thread = new Thread(this);
            thread.setName(getClass().getSimpleName() + "=" + thread.getId());
            thread.start();
        }
    }

    @Override
    public void stop() {
        if (thread != null) {
            log.warn("Stopped");
            thread.interrupt();
        }
        isStopped = true;
    }

    @Override
    public Boolean isRunning() {
        if (thread != null)
            return thread.isAlive() && !isStopped;
        else
            return false;
    }

    @Override
    public MonitorThreadDriver getDriver() {
        return monitorThreadDriver;
    }

    @Override
    public void run() {
        while (!thread.isInterrupted() || !isStopped) {
            try {
                monitorThreadDriver.execute(5000);
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
