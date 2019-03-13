package pl.tscript3r.notify.monitor.services;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.tscript3r.notify.monitor.api.v1.mapper.TaskMapper;
import pl.tscript3r.notify.monitor.api.v1.mapper.TaskSettingsMapper;
import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;
import pl.tscript3r.notify.monitor.api.v1.model.TaskSettingsDTO;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.domain.TaskSettings;
import pl.tscript3r.notify.monitor.exceptions.IncompatibleHostnameException;
import pl.tscript3r.notify.monitor.exceptions.TaskNotFoundException;
import pl.tscript3r.notify.monitor.parsers.ParserFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class TaskServiceTest {

    private static final int REFRESH_INTERVAL = 555;
    private static final long ID = 1L;
    private static final long USER_ID = 2L;
    private static final String URL = "https://www.olx.pl/";
    public static final String URL_2 = "https://www.olx.pl/testPackage";
    public static final Long USER_ID2 = 2L;
    public static final long ID2 = 2L;

    @Mock
    TaskManagerService taskManagerService;

    @Mock
    ParserFactory parserFactory;

    @InjectMocks
    TaskServiceImpl taskMapService;

    private TaskMapper taskMapper = TaskMapper.INSTANCE;
    private TaskSettingsMapper taskSettingsMapper = TaskSettingsMapper.INSTANCE;
    private TaskSettings taskSettings = new TaskSettings(REFRESH_INTERVAL);
    private Task task;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(parserFactory.isCompatible(anyString())).thenReturn(true);
        taskMapService = new TaskServiceImpl(30, taskMapper,
                taskSettingsMapper, taskManagerService, parserFactory);
        task = new Task(ID, Sets.newHashSet(1L), URL, taskSettings);
        taskMapService.add(taskMapper.taskToTaskDTO(task));
    }

    @Test(expected = RuntimeException.class)
    public void saveNullObject() {
        taskMapService.save(null);
    }

    @Test
    public void getTaskById() {
        TaskDTO taskResult = taskMapService.getTaskDTOById(ID);
        assertEquals(task.getId(), taskResult.getId());
    }

    @Test
    public void getAllTasksTest() {
        assertEquals(1, taskMapService.getAll().size());
    }

    @Test
    public void deleteTaskById() {
        assertEquals(true, taskMapService.deleteById(ID));
    }

    @Test(expected = TaskNotFoundException.class)
    public void deleteTaskByIdFail() {
        when(taskManagerService.deleteTask(any(Task.class))).thenReturn(true);
        taskMapService.deleteById(ID + 1L);
    }

    @Test
    public void updateTask() {
        TaskDTO taskDTO = taskMapper.taskToTaskDTO(task);
        taskDTO.setUrl(URL_2);
        taskDTO.setUsersId(Sets.newHashSet(USER_ID2));
        taskMapService.update(ID, taskDTO);
        TaskDTO returnedTaskDTO = taskMapService.getTaskDTOById(ID);
        assertEquals(URL_2, returnedTaskDTO.getUrl());
        assertTrue(returnedTaskDTO.getUsersId().contains(USER_ID2));
    }

    @Test(expected = TaskNotFoundException.class)
    public void updateTaskException() {
        taskMapService.update(ID + 1L, new TaskDTO());
    }

    @Test
    public void saveAll() {
        List<Task> tasks = Arrays.asList(
                Task.builder().url(URL).usersId(Sets.newHashSet(ID)).build(),
                Task.builder().url(URL_2).usersId(Sets.newHashSet(ID2)).build());
        taskMapService.saveAll(tasks);
        assertEquals(3, taskMapService.getAll().size());
    }

    @Test
    public void delete() {
        assertTrue(taskMapService.delete(task));
        assertEquals(0, taskMapService.getAll().size());
    }

    @Test
    public void addTaskWithoutTaskSettings() {
        TaskDTO taskDTO = new TaskDTO(ID, Sets.newHashSet(USER_ID), URL, null);
        assertNull(taskDTO.getTaskSettings());
        TaskDTO returnedTaskDTO = taskMapService.add(taskDTO);
        assertNotNull(returnedTaskDTO.getTaskSettings());
    }

    @Test(expected = IncompatibleHostnameException.class)
    public void addTaskWithWrongURL() {
        when(parserFactory.isCompatible(anyString())).thenReturn(false);
        TaskDTO taskDTO = new TaskDTO(ID, Sets.newHashSet(USER_ID), "www.google.pl", new TaskSettingsDTO());
        taskMapService.add(taskDTO);
    }
}