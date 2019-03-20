package pl.tscript3r.notify.monitor.threads;

import org.slf4j.Logger;
import pl.tscript3r.notify.monitor.exceptions.MonitorThreadException;
import pl.tscript3r.notify.monitor.threads.drivers.MonitorThreadDriver;

public abstract class AbstractMonitorThread implements MonitorThread {

    private final Logger log;
    private final MonitorThreadDriver monitorThreadDriver;

    private Thread thread;
    private Boolean isStopped = false;
    private Integer betweenDelay;
    private Integer iterationDelay;

    public AbstractMonitorThread(Logger log, MonitorThreadDriver monitorThreadDriver,
                                 Integer betweenDelay, Integer iterationDelay) {
        this.log = log;
        this.monitorThreadDriver = monitorThreadDriver;
        this.betweenDelay = betweenDelay;
        this.iterationDelay = iterationDelay;
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
                monitorThreadDriver.execute(betweenDelay);
                Thread.sleep(iterationDelay);
            } catch (InterruptedException e) {
                if (isStopped)
                    break;
                else
                    throwException(e);
            }
        }
    }

    private void throwException(Exception e) {
        throw new MonitorThreadException(e.getMessage());
    }

}
