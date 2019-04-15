package pl.tscript3r.notify.monitor.crawlers.html;

import org.junit.Before;
import org.junit.Test;
import pl.tscript3r.notify.monitor.consts.AdProperties;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.CrawlerException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

public class HLTVHtmlCrawlerTest extends AbstractCrawlerTest {

    private static final String HANDLED_HOSTNAME = "hltv.org";
    private HLTVHtmlCrawler hltvHtmlCrawler;

    @Before
    public void setUp() {
        hltvHtmlCrawler = new HLTVHtmlCrawler();
    }

    @Test
    public void getAds() throws IOException {
        Task task = getDefaultTask();
        List<Ad> ads = hltvHtmlCrawler.getAds(task, loadResource("HLTV.html"));
        assertNotNull(ads);
        assertEquals(3, ads.size());
        Ad ad = ads.get(0);
        assertNotNull(ad.getUrl());
        assertFalse(ad.getUrl().isEmpty());
        assertTrue(isValidUrl(ad.getUrl()));
        assertTrue(ad.hasKey(AdProperties.DATE));
        assertNotNull(ad.getTask());
    }

    @Test(expected = CrawlerException.class)
    public void validateUrlTestFail() {
        hltvHtmlCrawler.validateUrl("https://invalidurl");
    }

    @Test
    public void validateUrl() {
        hltvHtmlCrawler.validateUrl("https://google.pl");
        hltvHtmlCrawler.validateUrl("http://google.pl");
        hltvHtmlCrawler.validateUrl("https://www.google.pl");
        hltvHtmlCrawler.validateUrl("http://www.google.pl");
    }

    @Test
    public void getHandledHostname() {
        assertEquals(HANDLED_HOSTNAME, hltvHtmlCrawler.getHandledHostname());
    }


    @Test
    public void equalsTest() {
        assertEquals(hltvHtmlCrawler, new HLTVHtmlCrawler());

    }

    @Test
    public void hashCodeTest() {
        assertEquals(Objects.hash(HANDLED_HOSTNAME), hltvHtmlCrawler.hashCode());
    }

}
