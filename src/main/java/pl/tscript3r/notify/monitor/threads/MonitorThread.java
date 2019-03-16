package pl.tscript3r.notify.monitor.threads;

import pl.tscript3r.notify.monitor.threads.drivers.MonitorThreadDriver;

public interface MonitorThread extends Runnable {

    void start();

    void stop();

    Boolean isRunning();

    MonitorThreadDriver getDriver();

}
