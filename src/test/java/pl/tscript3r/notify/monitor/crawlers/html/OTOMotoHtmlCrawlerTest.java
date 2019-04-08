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

public class OTOMotoHtmlCrawlerTest extends AbstractCrawlerTest {

    private static final String HANDLED_HOSTNAME = "otomoto.pl";
    private OTOMotoHtmlCrawler otoMotoHtmlCrawler;

    @Before
    public void setUp() {
        otoMotoHtmlCrawler = new OTOMotoHtmlCrawler();
    }

    @Test
    public void getAds() throws IOException {
        Task task = getDefaultTask();
        List<Ad> ads = otoMotoHtmlCrawler.getAds(task, loadResource("OTOMoto.html"));
        assertNotNull(ads);
        assertEquals(2, ads.size());
        Ad ad = ads.get(0);
        assertNotNull(ad.getUrl());
        assertFalse(ad.getUrl().isEmpty());
        assertTrue(isValidUrl(ad.getUrl()));
        assertTrue(ad.hasKey(AdProperties.TITLE));
        assertTrue(ad.hasKey(AdProperties.PRICE));
        assertTrue(ad.hasKey(AdProperties.LOCATION));
        assertTrue(ad.hasKey(AdProperties.PRODUCTION_YEAR));
        assertTrue(ad.hasKey(AdProperties.MILEAGE));
        assertTrue(ad.hasKey(AdProperties.ENGINE_CAPACITY));
        assertTrue(ad.hasKey(AdProperties.FUEL_TYPE));
        assertNotNull(ad.getTask());
    }

    @Test
    public void getHandledHostname() {
        assertEquals(HANDLED_HOSTNAME, otoMotoHtmlCrawler.getHandledHostname());
    }

    @Test
    public void equalsTest() {
        assertEquals(otoMotoHtmlCrawler, new OTOMotoHtmlCrawler());
    }

    @Test
    public void hashCodeTest() {
        assertEquals(Objects.hash(HANDLED_HOSTNAME), otoMotoHtmlCrawler.hashCode());
    }

}
