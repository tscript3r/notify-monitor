package pl.tscript3r.notify.monitor.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.tscript3r.notify.monitor.api.v1.mapper.TaskMapper;
import pl.tscript3r.notify.monitor.api.v1.mapper.TaskSettingsMapper;
import pl.tscript3r.notify.monitor.config.MonitorSettings;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.domain.TaskSettings;

import static org.junit.Assert.*;

public class TaskMapServiceTest {

    private static final int REFRESH_INTERVAL = 555;
    private static final long ID = 1L;
    private static final long USER_ID = 2L;
    private static final String URL = "url";

    @Mock
    MonitorSettings monitorSettings;

    @InjectMocks
    TaskMapService taskMapService;

    private TaskMapper taskMapper = TaskMapper.INSTANCE;
    private TaskSettingsMapper taskSettingsMapper = TaskSettingsMapper.INSTANCE;
    private TaskSettings taskSettings = new TaskSettings(REFRESH_INTERVAL);
    private Task task;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        taskMapService = new TaskMapService(monitorSettings, taskMapper, taskSettingsMapper);
        task = new Task(ID, USER_ID, URL, taskSettings);
        taskMapService.addTask(taskMapper.taskToTaskDTO(task));
    }

    @Test
    public void getTaskById() {
        Task taskResult = taskMapService.findById(ID);
        assertEquals(task.getId(), taskResult.getId());
    }

    @Test
    public void getAllTasks() {
        assertEquals(1, taskMapService.getAllTasks().size());
    }

}