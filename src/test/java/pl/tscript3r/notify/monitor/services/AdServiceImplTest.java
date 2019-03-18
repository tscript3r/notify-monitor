package pl.tscript3r.notify.monitor.services;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.tscript3r.notify.monitor.api.v1.mapper.AdMapper;
import pl.tscript3r.notify.monitor.components.AdContainer;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.TaskNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AdServiceImplTest {

    @Mock
    TaskService taskService;

    @Mock
    AdContainer adContainer;

    AdMapper adMapper = AdMapper.INSTANCE;

    AdService adService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        adService = new AdServiceImpl(taskService, adContainer, adMapper);
    }

    @Test(expected = TaskNotFoundException.class)
    public void getCurrentAdsNull() {
        adService.getNewAds(null);
    }

    @Test
    public void getCurrentAds() {
        Task task = Task.builder().id(1L).build();
        when(taskService.isAdded(any())).thenReturn(true);
        when(adContainer.returnNewAdsAndMarkAsReturned(any())).thenReturn(Sets.newHashSet(new Ad(null, null)));
        assertEquals(1, adService.getNewAds(task).size());
    }

    @Test(expected = TaskNotFoundException.class)
    public void getCurrentAdsNotFound() {
        when(taskService.isAdded(any())).thenReturn(false);
        adService.getNewAds(Task.builder().id(1L).build());
    }


}