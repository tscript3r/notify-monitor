package pl.tscript3r.notify.monitor.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.tscript3r.notify.monitor.api.v1.mapper.TaskMapper;
import pl.tscript3r.notify.monitor.api.v1.mapper.TaskSettingsMapper;
import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;
import pl.tscript3r.notify.monitor.config.MonitorSettings;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.domain.TaskSettings;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TaskMapServiceTest {

    private static final int REFRESH_INTERVAL = 555;
    private static final long ID = 1L;
    private static final long USER_ID = 2L;
    private static final String URL = "url";
    public static final String URL_2 = "URL2";
    public static final Long USER_ID2 = 2L;
    public static final long ID2 = 2L;

    @Mock
    MonitorSettings monitorSettings;

    @Mock
    TaskManagerService taskManagerService;

    @InjectMocks
    TaskServiceImpl taskMapService;

    private TaskMapper taskMapper = TaskMapper.INSTANCE;
    private TaskSettingsMapper taskSettingsMapper = TaskSettingsMapper.INSTANCE;
    private TaskSettings taskSettings = new TaskSettings(REFRESH_INTERVAL);
    private Task task;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        taskMapService = new TaskServiceImpl(monitorSettings, taskMapper, taskSettingsMapper, taskManagerService);
        task = new Task(ID, USER_ID, URL, taskSettings);
        taskMapService.addTask(taskMapper.taskToTaskDTO(task));
    }

    @Test(expected = RuntimeException.class)
    public void saveNullObject(){
        taskMapService.save(null);
    }

    @Test
    public void getTaskById() {
        TaskDTO taskResult = taskMapService.getTaskById(ID);
        assertEquals(task.getId(), taskResult.getId());
    }

    @Test
    public void getAllTasksTest() {
        assertEquals(1, taskMapService.getAllTasks().size());
    }

    @Test
    public void deleteTaskById() {
        assertEquals(true, taskMapService.deleteTaskById(ID));
    }

    @Test
    public void deleteTaskByIdFail() {
        assertEquals(false, taskMapService.deleteTaskById(ID + 1L));
    }

    @Test
    public void updateTask() {
        TaskDTO taskDTO = taskMapper.taskToTaskDTO(task);
        taskDTO.setUrl(URL_2);
        taskDTO.setUserId(USER_ID2);
        taskMapService.updateTask(ID, taskDTO);
        TaskDTO returnedTaskDTO = taskMapService.getTaskById(ID);
        assertEquals(URL_2, returnedTaskDTO.getUrl());
        assertEquals(USER_ID2, returnedTaskDTO.getUserId());
    }

    @Test
    public void saveAll() {
        List<Task> tasks = Arrays.asList(
                Task.builder().url(URL).userId(ID).build(),
                Task.builder().url(URL_2).userId(ID2).build());
        taskMapService.saveAll(tasks);
        assertEquals(3, taskMapService.getAllTasks().size());
    }

    @Test
    public void delete() {
        assertTrue(taskMapService.delete(task));
        assertEquals(0, taskMapService.getAllTasks().size());
    }
}