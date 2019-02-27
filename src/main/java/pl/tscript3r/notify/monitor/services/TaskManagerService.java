package pl.tscript3r.notify.monitor.services;

import pl.tscript3r.notify.monitor.domain.Task;

public interface TaskManagerService {
    Boolean addTask(Task task);

    Boolean deleteTaskById(Long id);

    Boolean isTaskIdAdded(Long id);

    Boolean updateTask(Task task);
}
