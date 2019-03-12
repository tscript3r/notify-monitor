package pl.tscript3r.notify.monitor.services;

import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;
import pl.tscript3r.notify.monitor.domain.Task;

import java.util.List;

public interface TaskService {

    TaskDTO getById(Long id);

    void saveAll(List<Task> tasks);

    List<TaskDTO> getAll();

    TaskDTO add(TaskDTO taskDTO);

    TaskDTO update(Long id, TaskDTO taskDTO);

    Boolean deleteById(Long id);

}