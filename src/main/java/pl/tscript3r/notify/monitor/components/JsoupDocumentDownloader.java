package pl.tscript3r.notify.monitor.components;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.config.DownloaderSettings;

import java.io.IOException;

@Component
@Scope("prototype")
public class JsoupDocumentDownloader {

    private final DownloaderSettings downloaderSettings;

    public JsoupDocumentDownloader(DownloaderSettings downloaderSettings) {
        this.downloaderSettings = downloaderSettings;
    }

    public Document download(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent(downloaderSettings.getUserAgent())
                .timeout(downloaderSettings.getConnectionTimeout() * 1000)
                .followRedirects(downloaderSettings.getFollowRedirects())
                .maxBodySize(downloaderSettings.getMaxBodySize() * 1024)
                .ignoreHttpErrors(true)
                .get();
    }

}
