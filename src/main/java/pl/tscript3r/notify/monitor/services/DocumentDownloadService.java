package pl.tscript3r.notify.monitor.services;

import org.jsoup.nodes.Document;

import java.io.IOException;

public interface DocumentDownloadService {
    Document getDocument(String url) throws IOException;
}
