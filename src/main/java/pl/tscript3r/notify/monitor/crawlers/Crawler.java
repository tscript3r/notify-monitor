package pl.tscript3r.notify.monitor.crawlers;

import org.jsoup.nodes.Document;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.CrawlerException;

import java.util.List;

public interface Crawler {

    /**
     * Returns all currently found ads from the url
     */
    List<Ad> getAds(Task task, Document document) throws CrawlerException;

    /**
     * @return Handled hostname by example format: "olx.pl"
     */
    String getHandledHostname();

}
