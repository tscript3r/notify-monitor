package pl.tscript3r.notify.monitor.components;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class AdContainerTest {

    AdContainer adContainer;
    Ad one;
    Ad two;
    Ad three;
    Task task;

    @Before
    public void setUp() {
        adContainer = new AdContainer();
        task = Task.builder()
                .refreshInterval(0)
                .adContainerLimit(10)
                .url("http://www.olx.pl/oddam-za-darmo/")
                .id(1L)
                .usersId(Sets.newHashSet(1L)).build();
        one = new Ad(task, task.getUrl() + "one");
        two = new Ad(task, task.getUrl() + "two");
        three = new Ad(task, task.getUrl() + "three");
    }

    private List<Ad> getInitialAds() {
        return new ArrayList<>(Arrays.asList(one, two, three));
    }

    @Test
    public void addAds() {
        adContainer.addAds(task, getInitialAds());
        assertTrue(adContainer.anyAds(task));
        assertEquals(3, adContainer.returnAllAds(task).size());
    }

    @Test
    public void addAndMergeAds() {
        adContainer.addAds(task, getInitialAds());
        List<Ad> additionalAd = new ArrayList<>(Arrays.asList(new Ad(task, task.getUrl() + "four")));
        adContainer.addAds(task, additionalAd);
        assertEquals(4, adContainer.returnAllAds(task).size());
    }

    @Test
    public void addAndMergeAdsWithDuplicate() {
        adContainer.addAds(task, getInitialAds());
        List<Ad> additionalAd = new ArrayList<>(Arrays.asList(new Ad(task, task.getUrl() + "four"), one, two));
        adContainer.addAds(task, additionalAd);
        assertEquals(4, adContainer.returnAllAds(task).size());
    }

    @Test
    public void receiveAds() {
        adContainer.addAds(task, getInitialAds());
        assertEquals(3, adContainer.returnAllAds(task).size());
    }

    @Test
    public void receiveNewAds() {
        adContainer.addAds(task, getInitialAds());
        assertEquals(0, adContainer.returnNewAdsAndMarkAsReturned(task).size());
        List<Ad> additionalAds = new ArrayList<>(Arrays.asList(new Ad(task, task.getUrl() + "four"), one, two));
        adContainer.addAds(task, additionalAds);
        assertEquals(1, adContainer.returnNewAdsAndMarkAsReturned(task).size());
    }

    @Test
    public void anyAds() {
        assertFalse(adContainer.anyAds(task));
        adContainer.addAds(task, getInitialAds());
        assertTrue(adContainer.anyAds(task));
    }

    @Test
    public void returnAllAdsEmpty() {
        assertEquals(Sets.newHashSet(), adContainer.returnAllAds(task));
        adContainer.addAds(task, Sets.newHashSet());
        assertEquals(Sets.newHashSet(), adContainer.returnAllAds(task));
    }

    @Test
    public void returnNewAdsAndMarkAsReturned() {
        assertEquals(Sets.newHashSet(), adContainer.returnNewAdsAndMarkAsReturned(task));
        adContainer.addAds(task, Sets.newHashSet());
        assertEquals(Sets.newHashSet(), adContainer.returnNewAdsAndMarkAsReturned(task));
    }

    @Test
    public void receiveStatusNotNull() {
        assertNotNull(adContainer.receiveStatus());
    }

}