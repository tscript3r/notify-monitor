package pl.tscript3r.notify.monitor.controllers.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;
import pl.tscript3r.notify.monitor.api.v1.model.TaskListDTO;
import pl.tscript3r.notify.monitor.consts.v1.Paths;
import pl.tscript3r.notify.monitor.services.TaskService;

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

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public TaskListDTO getAllTasks() {
        log.debug("Viewing all tasks");
        return new TaskListDTO(taskService.getAllTasks());
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO getTaskById(@PathVariable Long id){
        log.debug("Getting task ID: " + id);
        return taskService.getTaskById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO addTask(@RequestBody TaskDTO taskDTO) {
        log.debug("Adding new task for user id: " + taskDTO.getUserId());
        return taskService.addTask(taskDTO);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        log.debug("Updating task id: " + id);
        // TODO: implement update task
        return null;
    }

    @GetMapping(Paths.TASK_STATUS_PATH)
    public String getStatus(@PathVariable Long id) {
        log.debug("Viewing task status for id: " + id);
        // TODO: implement get task status
        return "soon";
    }

}
