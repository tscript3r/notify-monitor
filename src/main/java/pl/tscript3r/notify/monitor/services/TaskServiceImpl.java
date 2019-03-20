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
import pl.tscript3r.notify.monitor.status.Status;
import pl.tscript3r.notify.monitor.status.Statusable;
import pl.tscript3r.notify.monitor.utils.HostnameExtractor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskServiceImpl extends AbstractMapService<Task, Long> implements TaskService, Statusable {

    private static final String GET_TASK_ID_CALLS = "get_task_id_calls";
    private static final String GET_TASK_DTO_ID_CALLS = "get_task_dto_id_calls";
    private static final String SAVE_ALL_CALLS = "save_all_calls";
    private static final String GET_ALL_CALLS = "get_all_calls";
    private static final String SAVE_DTO_CALLS = "save_dto_calls";
    private static final String UPDATE_CALLS = "update_calls";
    private static final String DELETE_ID_CALLS = "delete_id_calls";
    private static final String IS_ADDED_CALLS = "is_added_calls";

    // TODO: saveDTO similar URLs auto detection & saveDTO user id to existing one
    private final Status status = Status.create(this.getClass());
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
        status.initIntegerCounterValues(GET_TASK_ID_CALLS, GET_TASK_DTO_ID_CALLS, SAVE_ALL_CALLS, GET_ALL_CALLS,
                SAVE_DTO_CALLS, UPDATE_CALLS, DELETE_ID_CALLS, IS_ADDED_CALLS);
    }

    @Override
    public Task getTaskById(Long id) {
        status.incrementValue(GET_TASK_ID_CALLS);
        return Optional.ofNullable(super.findById(id))
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Override
    public TaskDTO getTaskDTOById(Long id) {
        status.incrementValue(GET_TASK_DTO_ID_CALLS);
        log.debug("Retrieving task id=" + id);
        return taskMapper.taskToTaskDTO(Optional.ofNullable(super.findById(id))
                .orElseThrow(() -> new TaskNotFoundException(id)));
    }

    @Override
    public void saveAll(List<Task> tasks) {
        status.incrementValue(SAVE_ALL_CALLS);
        log.debug("Saving " + tasks.size() + " tasks");
        tasks.forEach(task -> {
            taskDispatcher.addTask(super.save(task));
        });
    }

    @Override
    public List<TaskDTO> getAll() {
        status.incrementValue(GET_ALL_CALLS);
        log.debug("Retrieving all tasks");
        return super.findAll()
                .stream()
                .map(taskMapper::taskToTaskDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDTO saveDTO(TaskDTO taskDTO) {
        status.incrementValue(SAVE_DTO_CALLS);
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
        status.incrementValue(UPDATE_CALLS);
        log.debug("Updating task id=" + id);
        Task task = findById(id);
        if (findById(id) == null)
            throw new TaskNotFoundException(id);
        updateTask(task, taskDTO);
        taskDefaultValueSetter.validateAndSetDefaults(taskDTO);
        Task returnedTask = super.save(task);
        return taskMapper.taskToTaskDTO(returnedTask);
    }

    private void updateTask(Task task, TaskDTO taskDTO) {
        if (taskDTO.getUsersId() != null)
            task.setUsersId(taskDTO.getUsersId());
        if (taskDTO.getUrl() != null)
            task.setUrl(taskDTO.getUrl());
        if (taskDTO.getRefreshInterval() != null)
            task.setRefreshInterval(taskDTO.getRefreshInterval());
        if (taskDTO.getAdContainerLimit() != null)
            task.setAdContainerLimit(taskDTO.getAdContainerLimit());
    }

    @Override
    public Boolean deleteById(Long id) {
        status.incrementValue(DELETE_ID_CALLS);
        log.debug("Deleting task id=" + id);
        return taskDispatcher.removeTask(super.deleteId(id));
    }

    @Override
    public Boolean isAdded(Task task) {
        status.incrementValue(IS_ADDED_CALLS);
        return getTaskById(task.getId()) != null;
    }

    @Override
    public Status receiveStatus() {
        return status;
    }
}