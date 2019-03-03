package pl.tscript3r.notify.monitor.services;

import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;
import pl.tscript3r.notify.monitor.domain.Task;

import java.util.List;

public interface TaskService {

    TaskDTO getTaskById(Long id);

    void saveAll(List<Task> tasks);

    List<TaskDTO> getAllTasks();

    TaskDTO addTask(TaskDTO taskDTO);

    TaskDTO updateTask(Long id, TaskDTO taskDTO);

    Boolean deleteTaskById(Long id);

}