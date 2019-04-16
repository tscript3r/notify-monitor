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

public class OTOMotoHtmlCrawlerTest extends AbstractCrawlerTest {

    private static final String HANDLED_HOSTNAME = "otomoto.pl";
    private OTOMotoHtmlCrawler otoMotoHtmlCrawler;

    @Mock
    JsoupDocumentDownloader jsoupDocumentDownloader;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        otoMotoHtmlCrawler = new OTOMotoHtmlCrawler(jsoupDocumentDownloader);
        when(jsoupDocumentDownloader.download(anyString())).thenReturn(loadResource("OTOMoto.html"));
    }

    @Test
    public void getAds() throws IOException {
        Task task = getDefaultTask();
        List<Ad> ads = otoMotoHtmlCrawler.getAds(task);
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
        assertEquals(otoMotoHtmlCrawler, new OTOMotoHtmlCrawler(jsoupDocumentDownloader));
    }

    @Test
    public void hashCodeTest() {
        assertEquals(Objects.hash(HANDLED_HOSTNAME), otoMotoHtmlCrawler.hashCode());
    }

}
