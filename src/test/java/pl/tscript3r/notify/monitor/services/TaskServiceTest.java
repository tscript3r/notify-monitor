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
import pl.tscript3r.notify.monitor.components.TaskDispatcher;
import pl.tscript3r.notify.monitor.crawlers.CrawlerFactory;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.domain.TaskSettings;
import pl.tscript3r.notify.monitor.exceptions.IncompatibleHostnameException;
import pl.tscript3r.notify.monitor.exceptions.TaskNotFoundException;

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
    CrawlerFactory crawlerFactory;

    @Mock
    TaskDispatcher taskDispatcher;

    @InjectMocks
    TaskServiceImpl taskService;

    private TaskMapper taskMapper = TaskMapper.INSTANCE;
    private TaskSettingsMapper taskSettingsMapper = TaskSettingsMapper.INSTANCE;
    private TaskSettings taskSettings = new TaskSettings(REFRESH_INTERVAL);
    private Task task;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(crawlerFactory.isCompatible(anyString())).thenReturn(true);
        taskService = new TaskServiceImpl(30, taskMapper,
                taskSettingsMapper, crawlerFactory, taskDispatcher);
        task = new Task(ID, Sets.newHashSet(1L), URL, taskSettings);
        taskService.add(taskMapper.taskToTaskDTO(task));
    }

    @Test(expected = RuntimeException.class)
    public void saveNullObject() {
        taskService.save(null);
    }

    @Test
    public void getTaskDTOById() {
        TaskDTO taskResult = taskService.getTaskDTOById(ID);
        assertEquals(task.getId(), taskResult.getId());
    }

    @Test
    public void getTaskById() {
        Task taskResult = taskService.getTaskById(ID);
        assertEquals(task, taskResult);
    }

    @Test(expected = TaskNotFoundException.class)
    public void getTaskByIdException() {
        taskService.getTaskById(2L);
    }

    @Test
    public void getAllTasksTest() {
        assertEquals(1, taskService.getAll().size());
    }

    @Test
    public void deleteTaskById() {
        when(taskDispatcher.removeTask(any())).thenReturn(true);
        assertTrue(taskService.deleteById(ID));
    }

    @Test
    public void deleteTaskByIdFail() {
        when(taskDispatcher.removeTask(any())).thenReturn(false);
        assertFalse(taskService.deleteById(ID));
    }

    @Test
    public void updateTask() {
        TaskDTO taskDTO = taskMapper.taskToTaskDTO(task);
        taskDTO.setUrl(URL_2);
        taskDTO.setUsersId(Sets.newHashSet(USER_ID2));
        taskService.update(ID, taskDTO);
        TaskDTO returnedTaskDTO = taskService.getTaskDTOById(ID);
        assertEquals(URL_2, returnedTaskDTO.getUrl());
        assertTrue(returnedTaskDTO.getUsersId().contains(USER_ID2));
    }

    @Test(expected = TaskNotFoundException.class)
    public void updateTaskException() {
        taskService.update(ID + 1L, new TaskDTO());
    }

    @Test
    public void saveAll() {
        List<Task> tasks = Arrays.asList(
                Task.builder().url(URL).usersId(Sets.newHashSet(ID)).build(),
                Task.builder().url(URL_2).usersId(Sets.newHashSet(ID2)).build());
        taskService.saveAll(tasks);
        assertEquals(3, taskService.getAll().size());
    }

    @Test
    public void delete() {
        assertTrue(taskService.delete(task));
        assertEquals(0, taskService.getAll().size());
    }

    @Test
    public void addTaskWithoutTaskSettings() {
        TaskDTO taskDTO = new TaskDTO(ID, Sets.newHashSet(USER_ID), URL, null);
        assertNull(taskDTO.getTaskSettings());
        TaskDTO returnedTaskDTO = taskService.add(taskDTO);
        assertNotNull(returnedTaskDTO.getTaskSettings());
    }

    @Test(expected = IncompatibleHostnameException.class)
    public void addTaskWithWrongURL() {
        when(crawlerFactory.isCompatible(anyString())).thenReturn(false);
        TaskDTO taskDTO = new TaskDTO(ID, Sets.newHashSet(USER_ID), "www.google.pl", new TaskSettingsDTO());
        taskService.add(taskDTO);
    }
}