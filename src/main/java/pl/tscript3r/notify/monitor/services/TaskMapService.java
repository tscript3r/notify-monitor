package pl.tscript3r.notify.monitor.services;

import org.springframework.stereotype.Service;
import pl.tscript3r.notify.monitor.api.v1.mapper.TaskMapper;
import pl.tscript3r.notify.monitor.api.v1.mapper.TaskSettingsMapper;
import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;
import pl.tscript3r.notify.monitor.config.MonitorSettings;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.domain.TaskSettings;
import pl.tscript3r.notify.monitor.exceptions.TaskNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskMapService extends AbstractMapService<Task, Long> implements TaskService {

    private final MonitorSettings monitorSettings;
    private final TaskMapper taskMapper;
    private final TaskSettingsMapper taskSettingsMapper;
    private final TaskSettings defaultTaskSettings;

    public TaskMapService(MonitorSettings monitorSettings, TaskMapper taskMapper, TaskSettingsMapper taskSettingsMapper) {
        this.monitorSettings = monitorSettings;
        this.taskMapper = taskMapper;
        this.taskSettingsMapper = taskSettingsMapper;
        defaultTaskSettings = new TaskSettings(monitorSettings.getDefaultInterval());
    }

    @Override
    public TaskDTO getTaskById(Long id) {
        return taskMapper.taskToTaskDTO(Optional.ofNullable(super.findById(id))
                .orElseThrow(() -> new TaskNotFoundException(id)));
    }

    @Override
    public void saveAll(List<Task> tasks) {
        tasks.forEach(task -> super.save(task));
    }

    @Override
    public List<TaskDTO> getAllTasks() {
        return super.findAll()
                .stream()
                .map(taskMapper::taskToTaskDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDTO addTask(TaskDTO taskDTO) {
        if(taskDTO.getTaskSettings() == null)
            taskDTO.setTaskSettings(
                    taskSettingsMapper.taskSettingsToTaskSettingsDTO(defaultTaskSettings));

        return taskMapper.taskToTaskDTO(
                super.save(taskMapper.taskDTOToTask(taskDTO)));
    }
}