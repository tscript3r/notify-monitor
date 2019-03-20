package pl.tscript3r.notify.monitor.components;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.config.DownloaderConfig;

import java.io.IOException;

@Component
@Scope("prototype")
public class JsoupDocumentDownloader {

    private final DownloaderConfig downloaderConfig;

    public JsoupDocumentDownloader(DownloaderConfig downloaderConfig) {
        this.downloaderConfig = downloaderConfig;
    }

    public Document download(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent(downloaderConfig.getUserAgent())
                .timeout(downloaderConfig.getConnectionTimeout() * 1000)
                .followRedirects(downloaderConfig.getFollowRedirects())
                .maxBodySize(downloaderConfig.getMaxBodySize() * 1024)
                .ignoreHttpErrors(true)
                .get();
    }

}
