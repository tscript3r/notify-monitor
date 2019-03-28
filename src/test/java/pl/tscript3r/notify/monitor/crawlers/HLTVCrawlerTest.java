package pl.tscript3r.notify.monitor.crawlers;

import org.junit.Before;
import org.junit.Test;
import pl.tscript3r.notify.monitor.consts.AdProperties;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

public class HLTVCrawlerTest extends AbstractCrawlerTest {

    private static final String HANDLED_HOSTNAME = "hltv.org";
    private HLTVCrawler hltvCrawler;

    @Before
    public void setUp() throws Exception {
        hltvCrawler = new HLTVCrawler();
    }

    @Test
    public void getAds() throws IOException {
        Task task = getDefaultTask();
        List<Ad> ads = hltvCrawler.getAds(task, loadResource("HLTV.html"));
        assertNotNull(ads);
        assertEquals(3, ads.size());
        Ad ad = ads.get(0);
        assertNotNull(ad.getUrl());
        assertFalse(ad.getUrl().isEmpty());
        assertTrue(isValidUrl(ad.getUrl()));
        assertTrue(ad.hasKey(AdProperties.DATE));
        assertNotNull(ad.getTask());
    }

    @Test
    public void getHandledHostname() {
        assertEquals(HANDLED_HOSTNAME, hltvCrawler.getHandledHostname());
    }


    @Test
    public void equalsTest() {
        assertEquals(hltvCrawler, new HLTVCrawler());

    }

    @Test
    public void hashCodeTest() {
        assertEquals(Objects.hash(HANDLED_HOSTNAME), hltvCrawler.hashCode());
    }

}