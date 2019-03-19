package pl.tscript3r.notify.monitor.services;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pl.tscript3r.notify.monitor.api.v1.mapper.TaskMapper;
import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;
import pl.tscript3r.notify.monitor.dispatchers.TaskDispatcher;
import pl.tscript3r.notify.monitor.domain.Task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskServiceIT {

    private static final long USER_ID = 1;
    private Task returnedTask;


    @Autowired
    TaskService taskService;

    @Autowired
    TaskMapper taskMapper;

    @Autowired
    TaskDispatcher taskDispatcher;

    @Test
    public void all() {
        saveDTO();
        isTaskDispatcherContaining(returnedTask);
        isAdded(returnedTask);
        update(returnedTask.getId(), getUpdateTaskDTO());
        isTaskDispatcherContaining(returnedTask);
        assertTrue(taskService.deleteById(returnedTask.getId()));
    }

    public void saveDTO() {
        returnedTask = taskMapper.taskDTOToTask(taskService.saveDTO(getTaskDTO()));
    }

    private TaskDTO getTaskDTO() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setUsersId(Sets.newHashSet(USER_ID));
        taskDTO.setUrl("https://www.olx.pl/oddam-za-darmo/");
        return taskDTO;
    }

    private void isTaskDispatcherContaining(Task task) {
        assertTrue(taskDispatcher.containsTask(task));
    }

    private void isAdded(Task task) {
        assertTrue(taskService.isAdded(task));
    }

    private void update(Long id, TaskDTO taskDTO) {
        TaskDTO returnedTaskDTO = taskService.update(id, taskDTO);
        assertEquals(getUpdateTaskDTO().getAdContainerLimit(), returnedTaskDTO.getAdContainerLimit());
        assertEquals(getUpdateTaskDTO().getRefreshInterval(), returnedTaskDTO.getRefreshInterval());
        assertEquals(getUpdateTaskDTO().getUrl(), returnedTaskDTO.getUrl());
        assertEquals(getUpdateTaskDTO().getUsersId(), returnedTaskDTO.getUsersId());
    }

    private TaskDTO getUpdateTaskDTO() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setUrl("https://www.olx.pl/task-updated");
        taskDTO.setUsersId(Sets.newHashSet(666L));
        taskDTO.setAdContainerLimit(99);
        taskDTO.setRefreshInterval(999);
        return taskDTO;
    }

}
