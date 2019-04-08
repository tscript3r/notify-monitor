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

public class OTODomHtmlCrawlerTest extends AbstractCrawlerTest {

    private static final String HANDLED_HOSTNAME = "otodom.pl";
    private OTODomHtmlCrawler otoDomHtmlCrawler;

    @Before
    public void setUp() {
        otoDomHtmlCrawler = new OTODomHtmlCrawler();
    }

    @Test
    public void getAds() throws IOException {
        Task task = getDefaultTask();
        List<Ad> ads = otoDomHtmlCrawler.getAds(task, loadResource("OTODom.html"));
        assertEquals(2, ads.size());
        Ad ad = ads.get(0);
        assertNotNull(ad.getUrl());
        assertTrue(isValidUrl(ad.getUrl()));
        assertTrue(ad.hasKey(AdProperties.THUMBNAIL_URL));
        assertTrue(ad.hasKey(AdProperties.LOCATION));
        assertTrue(ad.hasKey(AdProperties.TITLE));
        assertTrue(ad.hasKey(AdProperties.YARDAGE));
        assertTrue(ad.hasKey(AdProperties.SELLER));
        assertTrue(ad.hasKey(AdProperties.ROOMS));
        assertTrue(ad.hasKey(AdProperties.PRICE));
        assertNotNull(ad.getTask());
    }

    @Test
    public void getHandledHostname() {
        assertEquals(HANDLED_HOSTNAME, otoDomHtmlCrawler.getHandledHostname());
    }

    @Test
    public void equalsTest() {
        assertEquals(otoDomHtmlCrawler, new OTODomHtmlCrawler());
    }

    @Test
    public void hashCodeTest() {
        assertEquals(Objects.hash(HANDLED_HOSTNAME), otoDomHtmlCrawler.hashCode());
    }

}
