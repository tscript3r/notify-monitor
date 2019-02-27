package pl.tscript3r.notify.monitor.controllers.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;
import pl.tscript3r.notify.monitor.api.v1.model.TaskListDTO;
import pl.tscript3r.notify.monitor.consts.v1.Paths;
import pl.tscript3r.notify.monitor.parsers.ParserFactory;
import pl.tscript3r.notify.monitor.services.TaskService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(Paths.BASE_PATH + Paths.TASK_PATH)
public class TaskController {

    /*
            host:8081/api/v1/tasks/add                      POST
            host:8081/api/v1/tasks/{id}                     GET / DELETE / PUT
            host:8081/api/v1/tasks/{id}/status              GET
     */

    private final TaskService taskService;
    private final ParserFactory parserFactory;

    public TaskController(TaskService taskService, ParserFactory parserFactory) {
        this.taskService = taskService;
        this.parserFactory = parserFactory;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public TaskListDTO getAllTasks() {
        log.debug("Viewing all tasks");
        return new TaskListDTO(taskService.getAllTasks());
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO getTaskById(@PathVariable Long id) {
        log.debug("Getting task id=" + id);
        return taskService.getTaskById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO addTask(@Valid @RequestBody TaskDTO taskDTO) {
        log.debug("Adding new task for user id=" + taskDTO.getUserId());
        return taskService.addTask(taskDTO);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        log.debug("Updating task id=" + id);
        return taskService.updateTask(id, taskDTO);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTask(@PathVariable Long id) {
        log.debug("Deleting task id=" + id);
        taskService.deleteTaskById(id);
    }

    @GetMapping(Paths.STATUS_TASK_PATH)
    public String getStatus() {
        log.debug("Viewing task status for id: ");
        parserFactory.getParser("dupa");
        return "soon";
    }

}
