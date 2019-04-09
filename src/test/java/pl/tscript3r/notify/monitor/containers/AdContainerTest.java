package pl.tscript3r.notify.monitor.containers;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class AdContainerTest {

    private AdContainer adContainer;
    private Ad one;
    private Ad two;
    private Ad three;
    private Task task;

    @Before
    public void setUp() {
        adContainer = new AdContainer();
        task = Task.builder()
                .refreshInterval(0)
                .adContainerMultiplier(0.5F)
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
        assertEquals(2, adContainer.returnAllAds(task).size());
    }

    @Test
    public void addAndMergeAds() {
        adContainer.addAds(task, getInitialAds());
        List<Ad> additionalAd = new ArrayList<>(Arrays.asList(new Ad(task, task.getUrl() + "four")));
        adContainer.addAds(task, additionalAd);
        assertEquals(2, adContainer.returnAllAds(task).size());
    }

    @Test
    public void addAndMergeAdsWithDuplicate() {
        adContainer.addAds(task, getInitialAds());
        List<Ad> additionalAd = new ArrayList<>(Arrays.asList(new Ad(task, task.getUrl() + "four"), one, two));
        adContainer.addAds(task, additionalAd);
        assertEquals(2, adContainer.returnAllAds(task).size());
    }

    @Test
    public void receiveAds() {
        adContainer.addAds(task, getInitialAds());
        assertEquals(2, adContainer.returnAllAds(task).size());
    }

    @Test
    public void receiveNewAds() {
        task.setAdContainerMultiplier(2F);
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

    @Test
    public void limitListTest() {
        List<Ad> initialAds = getInitialAds();
        Ad four = new Ad(task, task.getUrl() + "four");
        Ad five = new Ad(task, task.getUrl() + "five");
        Ad six = new Ad(task, task.getUrl() + "six");
        initialAds.add(four);
        adContainer.addAds(task, initialAds);
        List<Ad> overLimitAds = Arrays.asList(five, six);
        adContainer.addAds(task, overLimitAds);
        assertEquals(2, adContainer.returnNewAdsAndMarkAsReturned(task).size());
        assertEquals(2, adContainer.returnAllAds(task).size());
        Set<Ad> allReturnedAds = adContainer.returnAllAds(task);
        assertTrue(allReturnedAds.contains(five));
        assertTrue(allReturnedAds.contains(six));
    }

}