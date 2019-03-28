package pl.tscript3r.notify.monitor.components;

import org.junit.Before;
import org.junit.Test;
import pl.tscript3r.notify.monitor.config.DownloaderConfig;

public class JsoupDocumentDownloaderTest {

    private JsoupDocumentDownloader jsoupDocumentDownloader;

    @Before
    public void setUp() {
        DownloaderConfig downloaderConfig = new DownloaderConfig();
        downloaderConfig.setConnectionTimeout(5000);
        downloaderConfig.setFollowRedirects(true);
        downloaderConfig.setMaxBodySize(5 * 1024 * 1024);
        downloaderConfig.setUserAgent("Mozilla/5.0 Chrome/26.0.1410.64 Safari/537.31");

        jsoupDocumentDownloader = new JsoupDocumentDownloader(downloaderConfig);
    }

    @Test
    public void download() {
        // TODO: find a way to test it
    }
}