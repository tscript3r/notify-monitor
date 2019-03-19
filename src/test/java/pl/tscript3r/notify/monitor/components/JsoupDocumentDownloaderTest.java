package pl.tscript3r.notify.monitor.components;

import org.junit.Before;
import org.junit.Test;
import pl.tscript3r.notify.monitor.config.DownloaderSettings;

public class JsoupDocumentDownloaderTest {

    JsoupDocumentDownloader jsoupDocumentDownloader;

    @Before
    public void setUp() throws Exception {
        DownloaderSettings downloaderSettings = new DownloaderSettings();
        downloaderSettings.setConnectionTimeout(5000);
        downloaderSettings.setFollowRedirects(true);
        downloaderSettings.setMaxBodySize(5 * 1024 * 1024);
        downloaderSettings.setUserAgent("Mozilla/5.0 Chrome/26.0.1410.64 Safari/537.31");

        jsoupDocumentDownloader = new JsoupDocumentDownloader(downloaderSettings);
    }

    @Test
    public void download() {
        // TODO: find a way to test it
    }
}