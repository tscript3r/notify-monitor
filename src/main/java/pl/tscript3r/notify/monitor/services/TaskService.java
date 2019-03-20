package pl.tscript3r.notify.monitor.services;

import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.status.Statusable;

import java.util.List;

public interface TaskService extends Statusable {

    Task getTaskById(Long id);

    TaskDTO getTaskDTOById(Long id);

    void saveAll(List<Task> tasks);

    List<TaskDTO> getAll();

    TaskDTO saveDTO(TaskDTO taskDTO);

    TaskDTO update(Long id, TaskDTO taskDTO);

    Boolean deleteById(Long id);

    Boolean isAdded(Task task);

}