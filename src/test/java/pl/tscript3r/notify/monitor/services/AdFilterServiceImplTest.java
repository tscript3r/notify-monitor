package pl.tscript3r.notify.monitor.services;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.filters.AdFilter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AdFilterServiceImplTest {

    @Mock
    AdFilter adFilter;

    @Mock
    AdFilter secondFilter;

    private Task task;
    private AdFilterServiceImpl adFilterService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        task = Task.builder()
                .id(1L)
                .usersId(Sets.newHashSet(1L))
                .url("https://www.olx.pl/test")
                .refreshInterval(500)
                .adContainerMultiplier(1.5F)
                .build();
        adFilterService = new AdFilterServiceImpl();
    }

    private void addFilter() {
        adFilterService.add(task, adFilter);
    }

    private Ad getFirstAd() {
        Ad ad = new Ad(task, "https://www.olx.pl/first");
        ad.addProperty("key", "first");
        return ad;
    }

    private Ad getSecondAd() {
        Ad ad = new Ad(task, "https://www.olx.pl/second");
        ad.addProperty("key", "second");
        return ad;
    }

    private List<Ad> getAdArrayList() {
        List<Ad> ads = new ArrayList<>();
        ads.add(getFirstAd());
        ads.add(getSecondAd());
        return ads;
    }

    @Test
    public void filterWithAddedFilter_ExpectBothAdsFiltered() {
        when(adFilter.pass(any())).thenReturn(false);
        addFilter();
        List<Ad> ads = getAdArrayList();
        assertEquals(0, adFilterService.filter(ads).size());
        verify(adFilter, times(2)).pass(any());
    }

    @Test
    public void filterWithAddedFilter_ExpectBothNotFiltered() {
        when(adFilter.pass(any())).thenReturn(true);
        addFilter();
        List<Ad> ads = getAdArrayList();
        assertEquals(2, adFilterService.filter(ads).size());
        verify(adFilter, times(2)).pass(any());
    }

    @Test
    public void filterWithAddedFilters_ExpectBothNotFiltered() {
        when(adFilter.pass(any())).thenReturn(true);
        when(secondFilter.pass(any())).thenReturn(true);
        addFilter();
        adFilterService.add(task, secondFilter);
        List<Ad> ads = getAdArrayList();
        assertEquals(2, adFilterService.filter(ads).size());
        verify(adFilter, times(2)).pass(any());
        verify(secondFilter, times(2)).pass(any());
    }

    @Test
    public void filterWithNotAddedTask() {
        List<Ad> ads = new ArrayList<>();
        ads.add(getFirstAd());
        adFilterService.filter(ads);
    }

    @Test
    public void testRemove() {
        when(adFilter.pass(any())).thenReturn(false);
        addFilter();
        List<Ad> ads = getAdArrayList();
        assertEquals(0, adFilterService.filter(ads).size());
        adFilterService.remove(task);
        ads = getAdArrayList();
        assertEquals(2, adFilterService.filter(ads).size());
    }

}