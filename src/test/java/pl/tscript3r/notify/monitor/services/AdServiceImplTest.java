package pl.tscript3r.notify.monitor.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.tscript3r.notify.monitor.components.AdContainer;

public class AdServiceImplTest {

    @Mock
    AdContainer adContainer;

    AdService adService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        adService = new AdServiceImpl(adContainer);
    }

    @Test
    public void getCurrentAds() {
        adService.getCurrentAds(null);
    }
}