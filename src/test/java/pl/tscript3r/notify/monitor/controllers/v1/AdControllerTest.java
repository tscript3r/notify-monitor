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
import pl.tscript3r.notify.monitor.consts.v1.Paths;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.domain.TaskSettings;
import pl.tscript3r.notify.monitor.services.AdService;
import pl.tscript3r.notify.monitor.services.TaskService;

import java.util.Set;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdControllerTest extends AbstractRestControllerTest {

    @Mock
    AdService adService;

    @Mock
    TaskService taskService;

    @InjectMocks
    AdController adController;

    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adController).build();
    }

    @Test
    public void getCurrentAds() throws Exception {
        Task task = Task.builder()
                .taskSettings(new TaskSettings(123))
                .url("http://www.olx.pl/oddam-za-darmo/")
                .id(1L)
                .usersId(Sets.newHashSet(1L)).build();

        Ad first = new Ad();
        first.setTask(task);
        first.setId(1L);
        first.setUrl("https://www.test/1");
        first.setTitle("a");
        first.setLocation("c");
        Ad second = new Ad();
        second.setTask(task);
        second.setId(2L);
        second.setUrl("https://www.test/2");
        second.setTitle("b");
        second.setLocation("d");
        Set<Ad> ads = Sets.newHashSet(first, second);
        when(adService.getCurrentAds(any())).thenReturn(ads);

        String url = Paths.AD_TASK_PATH + Paths.CURRENT_AD_TASK_PATH;
        url = url.replace("{id}", "1");

        mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andReturn();
    }
}