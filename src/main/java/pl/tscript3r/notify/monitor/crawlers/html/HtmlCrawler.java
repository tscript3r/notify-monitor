package pl.tscript3r.notify.monitor.crawlers.html;

import org.jsoup.nodes.Document;
import pl.tscript3r.notify.monitor.crawlers.Crawler;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;

import java.util.List;

public interface HtmlCrawler extends Crawler {

    /**
     * Returns all currently found ads from the url
     */
    List<Ad> getAds(Task task, Document document);

}
