package pl.tscript3r.notify.monitor.controllers.v1;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.tscript3r.notify.monitor.api.v1.mapper.AdMapper;
import pl.tscript3r.notify.monitor.api.v1.model.AdDTO;
import pl.tscript3r.notify.monitor.consts.v1.Paths;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.services.AdService;
import pl.tscript3r.notify.monitor.services.TaskService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertNotNull;
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

    private final AdMapper adMapper = new AdMapper();

    @InjectMocks
    AdController adController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adController).build();
    }

    @Test
    public void getNewAds() throws Exception {
        Task task = Task.builder()
                .refreshInterval(120)
                .url("http://www.olx.pl/oddam-za-darmo/")
                .id(1L)
                .usersId(Sets.newHashSet(1L)).build();

        Ad first = new Ad(task, "https://www.test/1");
        Ad second = new Ad(task, "https://www.test/2");
        List<AdDTO> ads = new ArrayList<>();
        ads.add(adMapper.adToAdDTO(first));
        ads.add(adMapper.adToAdDTO(second));
        when(adService.getNewAds(any())).thenReturn(ads);

        String url = Paths.AD_TASK_PATH;
        url = url.replace("{id}", "1");

        mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ads", hasSize(2)))
                .andReturn();
    }


    @Test
    public void statusNotNull() {
        assertNotNull(adController.receiveStatus());
    }

}