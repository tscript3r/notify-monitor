package pl.tscript3r.notify.monitor.api.v1.mapper;

import com.google.common.collect.Sets;
import org.junit.Test;
import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;
import pl.tscript3r.notify.monitor.domain.Task;

import static org.junit.Assert.assertEquals;

public class TaskMapperTest {

    public static final String URL = "URL";
    public static final long ID = 3L;
    public static final long USER_ID = 1L;
    private TaskMapper taskMapper = TaskMapper.INSTANCE;

    @Test
    public void taskToTaskDTO() {
        Task task = Task.builder()
                .url(URL)
                .usersId(Sets.newHashSet(USER_ID))
                .refreshInterval(120)
                .build();
        TaskDTO taskDTOResult = taskMapper.taskToTaskDTO(task);

        assertEquals(task.getRefreshInterval(), taskDTOResult.getRefreshInterval());
        assertEquals(task.getUrl(), taskDTOResult.getUrl());
        assertEquals(task.getUsersId(), taskDTOResult.getUsersId());
    }

    @Test
    public void taskDTOToTask() {
        TaskDTO taskDTO = new TaskDTO(ID, Sets.newHashSet(USER_ID), URL, 120, 80, null);
        Task taskResult = taskMapper.taskDTOToTask(taskDTO);

        assertEquals(taskDTO.getRefreshInterval(), taskResult.getRefreshInterval());
        assertEquals(taskDTO.getUrl(), taskResult.getUrl());
        assertEquals(taskDTO.getUsersId(), taskResult.getUsersId());
    }


}