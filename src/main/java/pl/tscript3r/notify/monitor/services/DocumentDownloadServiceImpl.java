package pl.tscript3r.notify.monitor.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import pl.tscript3r.notify.monitor.config.DownloaderSettings;

import java.io.IOException;

@Service
public class DocumentDownloadServiceImpl implements DocumentDownloadService {

    private final DownloaderSettings downloaderSettings;

    public DocumentDownloadServiceImpl(DownloaderSettings downloaderSettings) {
        this.downloaderSettings = downloaderSettings;
    }

    @Override
    public Document getDocument(String url) throws IOException {
        // TODO: for now OK, but refactor is comming
        synchronized (this) {
            return Jsoup.connect(url)
                    .userAgent(downloaderSettings.getUserAgent())
                    .timeout(downloaderSettings.getConnectionTimeout() * 1000)
                    .followRedirects(downloaderSettings.getFollowRedirects())
                    .maxBodySize(downloaderSettings.getMaxBodySize() * 1024)
                    .get();
        }
    }

}
