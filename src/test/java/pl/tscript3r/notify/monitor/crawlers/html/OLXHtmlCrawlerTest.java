package pl.tscript3r.notify.monitor.crawlers.html;

import org.junit.Before;
import org.junit.Test;
import pl.tscript3r.notify.monitor.consts.AdProperties;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

public class OLXHtmlCrawlerTest extends AbstractCrawlerTest {

    private static final String HANDLED_HOSTNAME = "olx.pl";
    private OLXHtmlCrawler olxParser;

    @Before
    public void setUp() {
        olxParser = new OLXHtmlCrawler();
    }

    @Test
    public void getHandledHostname() {
        assertEquals(HANDLED_HOSTNAME, olxParser.getHandledHostname());
    }

    @Test
    public void getAdsMainLayout() throws IOException {
        Task task = getDefaultTask();
        List<Ad> ads = olxParser.getAds(task, loadResource("OLX.html"));
        assertEquals(3, ads.size());
        Ad ad = ads.get(0);
        assertNotNull(ad.getUrl());
        assertTrue(isValidUrl(ad.getUrl()));
        assertTrue(ad.hasKey(AdProperties.LOCATION));
        assertTrue(ad.hasKey(AdProperties.TITLE));
        assertTrue(ad.hasKey(AdProperties.CATEGORY));
        assertNotNull(ad.getTask());
    }

    @Test
    public void equalsTest() {
        assertEquals(olxParser, new OLXHtmlCrawler());
    }

    @Test
    public void hashCodeTest() {
        assertEquals(Objects.hash(HANDLED_HOSTNAME), olxParser.hashCode());
    }

}
