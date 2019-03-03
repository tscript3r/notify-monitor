package pl.tscript3r.notify.monitor.api.v1.mapper;

import com.google.common.collect.Sets;
import org.junit.Test;
import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;
import pl.tscript3r.notify.monitor.api.v1.model.TaskSettingsDTO;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.domain.TaskSettings;

import static org.junit.Assert.assertEquals;

public class TaskMapperTest {

    public static final String URL = "URL";
    public static final long ID = 3L;
    public static final long USER_ID = 1L;
    public static final int REFRESH_INTERVAL = 666;
    private TaskMapper taskMapper = TaskMapper.INSTANCE;

    @Test
    public void taskToTaskDTO() {
        TaskSettings taskSettings = new TaskSettings(REFRESH_INTERVAL);
        Task task = Task.builder()
                .url(URL)
                .usersId(Sets.newHashSet(USER_ID))
                .taskSettings(taskSettings)
                .build();
        TaskDTO taskDTOResult = taskMapper.taskToTaskDTO(task);

        assertEquals(task.getTaskSettings().getRefreshInterval(), taskDTOResult.getTaskSettings().getRefreshInterval());
        assertEquals(task.getUrl(), taskDTOResult.getUrl());
        assertEquals(task.getUsersId(), taskDTOResult.getUsersId());
    }

    @Test
    public void taskDTOToTask() {
        TaskSettingsDTO taskSettingsDTO = new TaskSettingsDTO(REFRESH_INTERVAL);
        TaskDTO taskDTO = new TaskDTO(ID, Sets.newHashSet(USER_ID), URL, taskSettingsDTO);
        Task taskResult = taskMapper.taskDTOToTask(taskDTO);

        assertEquals(taskDTO.getTaskSettings().getRefreshInterval(), taskResult.getTaskSettings().getRefreshInterval());
        assertEquals(taskDTO.getUrl(), taskResult.getUrl());
        assertEquals(taskDTO.getUsersId(), taskResult.getUsersId());
    }


}