package pl.tscript3r.notify.monitor.services;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.tscript3r.notify.monitor.api.v1.mapper.AdFilterMapper;
import pl.tscript3r.notify.monitor.api.v1.mapper.TaskMapper;
import pl.tscript3r.notify.monitor.api.v1.model.AdFilterDTO;
import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;
import pl.tscript3r.notify.monitor.components.TaskDefaultValueSetter;
import pl.tscript3r.notify.monitor.crawlers.CrawlerFactory;
import pl.tscript3r.notify.monitor.dispatchers.TaskDispatcher;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.IncompatibleHostnameException;
import pl.tscript3r.notify.monitor.exceptions.TaskNotFoundException;
import pl.tscript3r.notify.monitor.filters.AdFilterSimpleFactory;
import pl.tscript3r.notify.monitor.filters.AdFilterType;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    private static final long ID = 1L;
    private static final long USER_ID = 2L;
    private static final String URL = "https://www.olx.pl/";
    private static final String URL_2 = "https://www.olx.pl/testPackage";
    private static final Long USER_ID2 = 3L;
    private static final long ID2 = 2L;

    @Mock
    CrawlerFactory crawlerFactory;

    @Mock
    TaskDispatcher taskDispatcher;

    @Mock
    AdFilterService adFilterService;

    private TaskServiceImpl taskService;

    private AdFilterMapper adFilterMapper = new AdFilterMapper(new AdFilterSimpleFactory());
    private TaskMapper taskMapper = new TaskMapper(adFilterMapper);
    private Task task;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(crawlerFactory.isCompatible(anyString())).thenReturn(true);
        TaskDefaultValueSetter taskDefaultValueSetter = new TaskDefaultValueSetter(120, 60, 3600);
        taskService = new TaskServiceImpl(taskDefaultValueSetter, taskMapper, adFilterMapper,
                crawlerFactory, taskDispatcher, adFilterService, 1.5F);
        task = new Task(ID, Sets.newHashSet(1L), URL, 120, 1.5F, Duration.ofHours(1));
        taskService.saveDTO(taskMapper.taskToTaskDTO(task));
    }

    @Test(expected = RuntimeException.class)
    public void saveNullObject() {
        taskService.save(null);
    }

    @Test(expected = TaskNotFoundException.class)
    public void isAdded() {
        assertTrue(taskService.isAdded(task));
        assertFalse(taskService.isAdded(Task.builder().id(2L).build()));
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
        assertEquals(1, taskService.getAllAsDTO().size());
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
        AdFilterDTO adFilterDTO = new AdFilterDTO();
        adFilterDTO.setCaseSensitive(false);
        adFilterDTO.setFilterType(AdFilterType.MATCH);
        adFilterDTO.setProperty("test");
        adFilterDTO.setStrings(Sets.newHashSet("a", "b"));
        taskDTO.setFilterListDTO(Sets.newHashSet(adFilterDTO));
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
                Task.builder().url(URL).usersId(Sets.newHashSet(ID)).emailSendDuration(Duration.ofHours(1)).build(),
                Task.builder().url(URL_2).usersId(Sets.newHashSet(ID2)).emailSendDuration(Duration.ofHours(1)).build());
        taskService.saveAll(tasks);
        assertEquals(3, taskService.getAllAsDTO().size());
    }

    @Test
    public void delete() {
        assertTrue(taskService.delete(task));
        assertEquals(0, taskService.getAllAsDTO().size());
    }

    @Test
    public void addTaskWithoutTaskSettings() {
        TaskDTO taskDTO = new TaskDTO(ID, Sets.newHashSet(USER_ID), URL, null, 3600, null);
        assertNull(taskDTO.getRefreshInterval());
        TaskDTO returnedTaskDTO = taskService.saveDTO(taskDTO);
        assertNotNull(returnedTaskDTO.getRefreshInterval());
        assertEquals(returnedTaskDTO.getRefreshInterval().longValue(), 120);
    }

    @Test(expected = IncompatibleHostnameException.class)
    public void addTaskWithWrongURL() {
        when(crawlerFactory.isCompatible(anyString())).thenReturn(false);
        TaskDTO taskDTO = new TaskDTO(ID, Sets.newHashSet(USER_ID), "www.google.pl", 120, 3600, null);
        taskService.saveDTO(taskDTO);
    }

    @Test
    public void testSendAdFilters() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(ID);
        taskDTO.setUrl("https://www.olx.pl/");
        taskDTO.setUsersId(Sets.newHashSet(USER_ID, USER_ID2));
        AdFilterDTO adFilterDTO = new AdFilterDTO();
        adFilterDTO.setCaseSensitive(false);
        adFilterDTO.setFilterType(AdFilterType.MATCH);
        adFilterDTO.setProperty("test");
        adFilterDTO.setStrings(Sets.newHashSet("a", "b"));
        taskDTO.setFilterListDTO(Sets.newHashSet(adFilterDTO));
        taskService.saveDTO(taskDTO);
        verify(adFilterService, times(1)).add(any(), any());
    }

    @Test
    public void statusNotNull() {
        assertNotNull(taskService.receiveStatus());
    }
}