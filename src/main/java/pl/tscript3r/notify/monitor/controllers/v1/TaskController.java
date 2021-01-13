package pl.tscript3r.notify.monitor.controllers.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;
import pl.tscript3r.notify.monitor.api.v1.model.TaskListDTO;
import pl.tscript3r.notify.monitor.consts.v1.Paths;
import pl.tscript3r.notify.monitor.services.TaskService;
import pl.tscript3r.notify.monitor.status.Status;
import pl.tscript3r.notify.monitor.status.Statusable;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(Paths.TASK_PATH)
public class TaskController implements Statusable {

    private static final String GET_ALL_CALLS = "get_all_calls";
    private static final String GET_BY_ID_CALLS = "get_by_id_calls";
    private static final String ADD_CALLS = "add_calls";
    private static final String UPDATE_CALLS = "update_calls";
    private static final String DELETE_CALLS = "delete_calls";

    private final Status status = Status.create(this.getClass());
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
        status.initIntegerCounterValues(GET_ALL_CALLS, GET_BY_ID_CALLS, ADD_CALLS, UPDATE_CALLS, DELETE_CALLS);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public TaskListDTO getAll() {
        log.debug("Viewing all tasks");
        status.incrementValue(GET_ALL_CALLS);
        return new TaskListDTO(taskService.getAll());
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO getById(@PathVariable Long id) {
        log.debug("Getting task id=" + id);
        status.incrementValue(GET_BY_ID_CALLS);
        return taskService.getTaskDTOById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO add(@Valid @RequestBody TaskDTO taskDTO) {
        log.debug("Adding new task for users id=" + taskDTO.getUsersId());
        status.incrementValue(ADD_CALLS);
        return taskService.saveDTO(taskDTO);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO update(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        log.debug("Updating task id=" + id);
        status.incrementValue(UPDATE_CALLS);
        return taskService.update(id, taskDTO);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id) {
        log.debug("Deleting task id=" + id);
        status.incrementValue(DELETE_CALLS);
        taskService.deleteById(id);
    }

    @Override
    public Status receiveStatus() {
        return status;
    }
}
