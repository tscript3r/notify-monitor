package pl.tscript3r.notify.monitor.crawlers;

import com.google.common.collect.Sets;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import pl.tscript3r.notify.monitor.consts.AdProperties;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

public class OLXCrawlerTest {

    public static final String OLX_PL = "olx.pl";
    OLXCrawler olxParser;

    @Before
    public void setUp() throws Exception {
        olxParser = new OLXCrawler();
    }

    private Document loadResource(String fileName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        return Jsoup.parse(file, "UTF-8", "");
    }

    private Task getDefaultTask() {
        return Task.builder().id(1L).usersId(Sets.newHashSet(1L)).build();
    }

    @Test
    public void getHandledHostname() {
        assertEquals(OLX_PL, olxParser.getHandledHostname());
    }

    @Test
    public void getAdsMainLayout() throws IOException {
        Task task = getDefaultTask();
        List<Ad> ads = olxParser.getAds(task, loadResource("mainLayout.html"));
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

    @Test
    public void receiveStatusNotNull() {
        assertNotNull(olxParser.receiveStatus());
    }
}