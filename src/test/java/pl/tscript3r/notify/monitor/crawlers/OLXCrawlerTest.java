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

public class OLXCrawlerTest extends AbstractCrawlerTest {

    public static final String OLX_PL = "olx.pl";
    OLXCrawler olxParser;

    @Before
    public void setUp() {
        olxParser = new OLXCrawler();
    }

    @Test
    public void getHandledHostname() {
        assertEquals(OLX_PL, olxParser.getHandledHostname());
    }

    @Test
    public void getAdsMainLayout() throws IOException {
        Task task = getDefaultTask();
        List<Ad> ads = olxParser.getAds(task, loadResource("OLX.html"));
        assertEquals(3, ads.size());
        Ad ad = ads.get(0);
        assertNotNull(ad.getUrl());
        assertTrue(ad.hasKey(AdProperties.LOCATION));
        assertTrue(ad.hasKey(AdProperties.TITLE));
        assertTrue(ad.hasKey(AdProperties.CATEGORY));
        assertNotNull(ad.getTask());
    }

    @Test
    public void equalsTest() {
        // TODO: implement
    }

    @Test
    public void hashCodeTest() {
        assertEquals(Objects.hash(OLX_PL), olxParser.hashCode());
    }

}