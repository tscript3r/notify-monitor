package pl.tscript3r.notify.monitor.components;

import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.services.TaskService;

@Component
public class TaskBinder {

    private final TaskService taskService;

    public TaskBinder(TaskService taskService) {
        this.taskService = taskService;
    }

    public void bind(Task existing, Task added) {
        // TODO: implement
    }

}
