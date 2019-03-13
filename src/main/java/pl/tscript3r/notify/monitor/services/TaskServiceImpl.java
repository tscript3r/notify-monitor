package pl.tscript3r.notify.monitor.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.tscript3r.notify.monitor.api.v1.mapper.TaskMapper;
import pl.tscript3r.notify.monitor.api.v1.mapper.TaskSettingsMapper;
import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.domain.TaskSettings;
import pl.tscript3r.notify.monitor.exceptions.IncompatibleHostnameException;
import pl.tscript3r.notify.monitor.exceptions.TaskNotFoundException;
import pl.tscript3r.notify.monitor.parsers.ParserFactory;
import pl.tscript3r.notify.monitor.utils.HostnameExtractor;
import pl.tscript3r.notify.monitor.utils.TaskDispatcher;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskServiceImpl extends AbstractMapService<Task, Long> implements TaskService {

    private final TaskMapper taskMapper;
    private final TaskSettingsMapper taskSettingsMapper;
    private final TaskSettings defaultTaskSettings;
    private final ParserFactory parserFactory;
    private final TaskDispatcher taskDispatcher;

    public TaskServiceImpl(@Value("#{new Integer('${notify.monitor.downloader.defaultInterval}')}") Integer defaultInterval, TaskMapper taskMapper,
                           TaskSettingsMapper taskSettingsMapper, ParserFactory parserFactory,
                           TaskDispatcher taskDispatcher) {
        this.taskMapper = taskMapper;
        this.taskSettingsMapper = taskSettingsMapper;
        this.parserFactory = parserFactory;
        this.taskDispatcher = taskDispatcher;
        defaultTaskSettings = new TaskSettings(defaultInterval);
    }

    @Override
    public Task getTaskById(Long id) {
        // TODO: add test
        return Optional.ofNullable(super.findById(id))
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Override
    public TaskDTO getTaskDTOById(Long id) {
        log.debug("Retrieving task id=" + id);
        return taskMapper.taskToTaskDTO(Optional.ofNullable(super.findById(id))
                .orElseThrow(() -> new TaskNotFoundException(id)));
    }

    @Override
    public void saveAll(List<Task> tasks) {
        log.debug("Saving " + tasks.size() + " tasks");
        tasks.forEach(task -> {
            super.save(task);

        });
    }

    @Override
    public List<TaskDTO> getAll() {
        log.debug("Retrieving all tasks");
        return super.findAll()
                .stream()
                .map(taskMapper::taskToTaskDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDTO add(TaskDTO taskDTO) {
        log.debug("Adding new task from taskDTO");
        if (taskDTO.getTaskSettings() == null)
            taskDTO.setTaskSettings(
                    taskSettingsMapper.taskSettingsToTaskSettingsDTO(defaultTaskSettings));
        if (!parserFactory.isCompatible(
                HostnameExtractor.getDomain(taskDTO.getUrl())))
            throw new IncompatibleHostnameException(HostnameExtractor.getDomain(taskDTO.getUrl()));
        Task task = super.save(taskMapper.taskDTOToTask(taskDTO));
        taskDispatcher.addTask(task);
        return taskMapper.taskToTaskDTO(task);
    }

    @Override
    public TaskDTO update(Long id, TaskDTO taskDTO) {
        log.debug("Updating task id=" + id);
        Task task = taskMapper.taskDTOToTask(taskDTO);
        if (findById(id) == null)
            throw new TaskNotFoundException(id);
        task.setId(id);
        Task returnedTask = super.save(task);

        return taskMapper.taskToTaskDTO(returnedTask);
    }

    @Override
    public Boolean deleteById(Long id) {
        log.debug("Deleting task id=" + id);

        return super.deleteById(id);
    }
}