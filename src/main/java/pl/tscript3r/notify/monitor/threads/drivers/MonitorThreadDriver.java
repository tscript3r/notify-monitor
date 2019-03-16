package pl.tscript3r.notify.monitor.threads.drivers;

import pl.tscript3r.notify.monitor.domain.Task;

public interface MonitorThreadDriver {

    Boolean isFull();

    Boolean hasTask(Task task);

    Boolean removeTask(Task task);

    Boolean addTask(Task task);

}
