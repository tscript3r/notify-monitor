package pl.tscript3r.notify.monitor.crawlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.tscript3r.notify.monitor.components.JsoupDocumentDownloader;
import pl.tscript3r.notify.monitor.consts.AdProperties;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class OTODomHtmlCrawlerTest extends AbstractCrawlerTest {

    private static final String HANDLED_HOSTNAME = "otodom.pl";
    private OTODomHtmlCrawler otoDomHtmlCrawler;

    @Mock
    JsoupDocumentDownloader jsoupDocumentDownloader;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        otoDomHtmlCrawler = new OTODomHtmlCrawler(jsoupDocumentDownloader);
        when(jsoupDocumentDownloader.download(anyString())).thenReturn(loadResource("OTODom.html"));
    }

    @Test
    public void getAds() throws IOException {
        Task task = getDefaultTask();
        List<Ad> ads = otoDomHtmlCrawler.getAds(task);
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
        assertEquals(otoDomHtmlCrawler, new OTODomHtmlCrawler(jsoupDocumentDownloader));
    }

    @Test
    public void hashCodeTest() {
        assertEquals(Objects.hash(HANDLED_HOSTNAME), otoDomHtmlCrawler.hashCode());
    }

}
