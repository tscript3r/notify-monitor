package pl.tscript3r.notify.monitor.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.tscript3r.notify.monitor.api.v1.mapper.TaskMapper;
import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;
import pl.tscript3r.notify.monitor.components.TaskDefaultValueSetter;
import pl.tscript3r.notify.monitor.crawlers.CrawlerFactory;
import pl.tscript3r.notify.monitor.dispatchers.TaskDispatcher;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.IncompatibleHostnameException;
import pl.tscript3r.notify.monitor.exceptions.TaskNotFoundException;
import pl.tscript3r.notify.monitor.utils.HostnameExtractor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskServiceImpl extends AbstractMapService<Task, Long> implements TaskService {

    // TODO: add similar URLs auto detection & add user id to existing one
    private final TaskDefaultValueSetter taskDefaultValueSetter;
    private final TaskMapper taskMapper;
    private final CrawlerFactory crawlerFactory;
    private final TaskDispatcher taskDispatcher;

    public TaskServiceImpl(TaskDefaultValueSetter taskDefaultValueSetter, TaskMapper taskMapper,
                           CrawlerFactory parserFactory, TaskDispatcher taskDispatcher) {
        this.taskDefaultValueSetter = taskDefaultValueSetter;
        this.taskMapper = taskMapper;
        this.crawlerFactory = parserFactory;
        this.taskDispatcher = taskDispatcher;
    }

    @Override
    public Task getTaskById(Long id) {
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
            taskDispatcher.addTask(super.save(task));
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
        if (!crawlerFactory.isCompatible(
                HostnameExtractor.getDomain(taskDTO.getUrl())))
            throw new IncompatibleHostnameException(HostnameExtractor.getDomain(taskDTO.getUrl()));
        taskDefaultValueSetter.validateAndSetDefaults(taskDTO);
        Task task = super.save(taskMapper.taskDTOToTask(taskDTO));
        taskDispatcher.addTask(task);
        return taskMapper.taskToTaskDTO(task);
    }

    @Override
    public TaskDTO update(Long id, TaskDTO taskDTO) {
        log.debug("Updating task id=" + id);
        taskDefaultValueSetter.validateAndSetDefaults(taskDTO);
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
        return taskDispatcher.removeTask(super.deleteId(id));
    }

    @Override
    public Boolean isAdded(Task task) {
        return getTaskById(task.getId()) != null;
    }
}