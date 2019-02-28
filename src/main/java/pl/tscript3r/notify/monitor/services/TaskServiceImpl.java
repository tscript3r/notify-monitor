package pl.tscript3r.notify.monitor.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.tscript3r.notify.monitor.api.v1.mapper.TaskMapper;
import pl.tscript3r.notify.monitor.api.v1.mapper.TaskSettingsMapper;
import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;
import pl.tscript3r.notify.monitor.config.MonitorSettings;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.domain.TaskSettings;
import pl.tscript3r.notify.monitor.exceptions.IncompatibleHostnameException;
import pl.tscript3r.notify.monitor.exceptions.TaskNotFoundException;
import pl.tscript3r.notify.monitor.parsers.ParserFactory;
import pl.tscript3r.notify.monitor.utils.HostnameExtractor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskServiceImpl extends AbstractMapService<Task, Long> implements TaskService {

    private final MonitorSettings monitorSettings;
    private final TaskMapper taskMapper;
    private final TaskSettingsMapper taskSettingsMapper;
    private final TaskSettings defaultTaskSettings;
    private final TaskManagerService taskManagerService;
    private final ParserFactory parserFactory;

    public TaskServiceImpl(MonitorSettings monitorSettings, TaskMapper taskMapper, TaskSettingsMapper taskSettingsMapper,
                           TaskManagerService taskManagerService, ParserFactory parserFactory) {
        this.monitorSettings = monitorSettings;
        this.taskMapper = taskMapper;
        this.taskSettingsMapper = taskSettingsMapper;
        this.taskManagerService = taskManagerService;
        this.parserFactory = parserFactory;
        defaultTaskSettings = new TaskSettings(monitorSettings.getDefaultInterval());
    }

    @Override
    public TaskDTO getTaskById(Long id) {
        log.debug("Retrieving task id=" + id);
        return taskMapper.taskToTaskDTO(Optional.ofNullable(super.findById(id))
                .orElseThrow(() -> new TaskNotFoundException(id)));
    }

    @Override
    public void saveAll(List<Task> tasks) {
        log.debug("Saving " + tasks.size() + " tasks");
        tasks.forEach(task -> {
            super.save(task);
            taskManagerService.addTask(task);
        });
    }

    @Override
    public List<TaskDTO> getAllTasks() {
        log.debug("Retrieving all tasks");
        return super.findAll()
                .stream()
                .map(taskMapper::taskToTaskDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDTO addTask(TaskDTO taskDTO) {
        log.debug("Adding new task from taskDTO");
        if (taskDTO.getTaskSettings() == null)
            taskDTO.setTaskSettings(
                    taskSettingsMapper.taskSettingsToTaskSettingsDTO(defaultTaskSettings));
        if( !parserFactory.isCompatible(
                HostnameExtractor.getDomain(taskDTO.getUrl())))
            throw new IncompatibleHostnameException(HostnameExtractor.getDomain(taskDTO.getUrl()));
        Task task = super.save(taskMapper.taskDTOToTask(taskDTO));
        taskManagerService.addTask(task);
        return taskMapper.taskToTaskDTO(task);
    }

    @Override
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        log.debug("Updating task id=" + id);
        Task task = taskMapper.taskDTOToTask(taskDTO);
        task.setId(id);
        Task returnedTask = super.save(task);
        taskManagerService.updateTask(task);
        return taskMapper.taskToTaskDTO(returnedTask);
    }

    @Override
    public Boolean deleteTaskById(Long id) {
        log.debug("Deleting task id=" + id);
        taskManagerService.deleteTask(
                Optional.ofNullable(super.findById(id))
                        .orElseThrow(() -> new TaskNotFoundException(id)));
        return deleteById(id);
    }
}