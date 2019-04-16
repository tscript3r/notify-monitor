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

public class OLXHtmlCrawlerTest extends AbstractCrawlerTest {

    private static final String HANDLED_HOSTNAME = "olx.pl";
    private OLXHtmlCrawler olxParser;

    @Mock
    JsoupDocumentDownloader jsoupDocumentDownloader;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        olxParser = new OLXHtmlCrawler(jsoupDocumentDownloader);
        when(jsoupDocumentDownloader.download(anyString())).thenReturn(loadResource("OLX.html"));
    }

    @Test
    public void getHandledHostname() {
        assertEquals(HANDLED_HOSTNAME, olxParser.getHandledHostname());
    }

    @Test
    public void getAdsMainLayout() throws IOException {
        Task task = getDefaultTask();
        List<Ad> ads = olxParser.getAds(task);
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
        assertEquals(olxParser, new OLXHtmlCrawler(jsoupDocumentDownloader));
    }

    @Test
    public void hashCodeTest() {
        assertEquals(Objects.hash(HANDLED_HOSTNAME), olxParser.hashCode());
    }

}
