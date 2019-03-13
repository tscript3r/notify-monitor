package pl.tscript3r.notify.monitor.controllers.v1;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.tscript3r.notify.monitor.api.v1.model.TaskDTO;
import pl.tscript3r.notify.monitor.api.v1.model.TaskSettingsDTO;
import pl.tscript3r.notify.monitor.consts.v1.Paths;
import pl.tscript3r.notify.monitor.services.TaskService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TaskControllerTest extends AbstractRestControllerTest {

    private static final String URL = "https://www.olx.pl/testPackage";

    @Mock
    TaskService taskService;

    @InjectMocks
    TaskController taskController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    public void getAllTasks() throws Exception {
        TaskDTO taskDTO1 = new TaskDTO();
        taskDTO1.setId(1L);
        TaskDTO taskDTO2 = new TaskDTO();
        taskDTO2.setId(2L);
        List<TaskDTO> taskDTOs = Arrays.asList(taskDTO1, taskDTO2);
        when(taskService.getAll()).thenReturn(taskDTOs);

        mockMvc.perform(get(Paths.BASE_PATH + Paths.TASK_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks", hasSize(2)));
    }

    @Test
    public void getTaskById() throws Exception {
        TaskDTO taskDTO = new TaskDTO(1L, Sets.newHashSet(2L), URL, null);
        when(taskService.getTaskDTOById(anyLong())).thenReturn(taskDTO);

        mockMvc.perform(get(Paths.BASE_PATH + Paths.TASK_PATH + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.users_id", hasSize(1)))
                .andExpect(jsonPath("$.url", equalTo(URL)));
    }

    @Test
    public void addTask() throws Exception {
        TaskSettingsDTO taskSettingsDTO = new TaskSettingsDTO();
        TaskDTO taskDTO = new TaskDTO(1L, Sets.newHashSet(2L), URL, taskSettingsDTO);
        when(taskService.add(any())).thenReturn(taskDTO);
        mockMvc.perform(post(Paths.BASE_PATH + Paths.TASK_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(taskDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.url", equalTo(URL)));
    }

    @Test
    public void updateTask() throws Exception {
        TaskSettingsDTO taskSettingsDTO = new TaskSettingsDTO();
        TaskDTO taskDTO = new TaskDTO(1L, Sets.newHashSet(2L), URL, taskSettingsDTO);
        when(taskService.update(anyLong(), any(TaskDTO.class))).thenReturn(taskDTO);

        mockMvc.perform(put(Paths.BASE_PATH + Paths.TASK_PATH + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(taskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.url", equalTo(URL)));
    }

    @Test
    public void deleteTask() throws Exception {
        mockMvc.perform(delete(Paths.BASE_PATH + Paths.TASK_PATH + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getStatus() {

    }
}