package pl.tscript3r.notify.monitor.services;

import pl.tscript3r.notify.monitor.domain.Task;

public interface TaskManagerService {
    Boolean addTask(Task task);

    Boolean deleteTask(Task task);

    Boolean isTask(Task task);

    Boolean updateTask(Task task);

    Integer getThreadsCount();
}
