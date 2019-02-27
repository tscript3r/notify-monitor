package pl.tscript3r.notify.monitor.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class HostnameExtractorTest {

    @Test
    public void getHostname() {
        assertEquals("olx.pl", HostnameExtractor.getDomain("https://www.olx.pl/"));
        assertEquals("olx.pl", HostnameExtractor.getDomain("https://www.olx.pl/test"));
        assertEquals("olx.pl", HostnameExtractor.getDomain("https://www.olx.pl/test/test"));
        assertEquals("olx.pl", HostnameExtractor.getDomain("http://www.olx.pl/"));
        assertEquals("olx.pl", HostnameExtractor.getDomain("https://olx.pl/"));
        assertEquals("olx.pl", HostnameExtractor.getDomain("www.olx.pl/"));
        assertEquals("olx.pl", HostnameExtractor.getDomain("olx.pl/"));
        assertEquals("olx.pl", HostnameExtractor.getDomain("olx.pl"));
    }

    @Test
    public void getHostnameFail() {
        assertNotEquals("olx.pl", HostnameExtractor.getDomain("https://www.olxpl/"));
        assertNotEquals("olx.pl", HostnameExtractor.getDomain("https:/www.olx.pl/"));
        assertNotEquals("olx.pl", HostnameExtractor.getDomain("http://wwwolx.pl/"));
        assertNotEquals("olx.pl", HostnameExtractor.getDomain("https//www.olx.pl/"));
    }
}