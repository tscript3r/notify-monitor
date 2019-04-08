package pl.tscript3r.notify.monitor.crawlers.api;

import pl.tscript3r.notify.monitor.crawlers.Crawler;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;

import java.util.List;

public interface ApiCrawler extends Crawler {

    List<Ad> getAds(Task task);

}
