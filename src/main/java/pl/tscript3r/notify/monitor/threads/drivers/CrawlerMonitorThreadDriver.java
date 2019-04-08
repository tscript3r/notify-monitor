package pl.tscript3r.notify.monitor.threads.drivers;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.containers.AdContainer;
import pl.tscript3r.notify.monitor.crawlers.Crawler;
import pl.tscript3r.notify.monitor.crawlers.CrawlerFactory;
import pl.tscript3r.notify.monitor.crawlers.api.ApiCrawler;
import pl.tscript3r.notify.monitor.crawlers.html.HtmlCrawler;
import pl.tscript3r.notify.monitor.dispatchers.DownloadDispatcher;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.CrawlerException;
import pl.tscript3r.notify.monitor.exceptions.MonitorThreadException;
import pl.tscript3r.notify.monitor.services.AdFilterService;
import pl.tscript3r.notify.monitor.utils.HostnameExtractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
@Scope("prototype")
public class CrawlerMonitorThreadDriver implements MonitorThreadDriver {

    private final DownloadDispatcher downloadDispatcher;
    private final AdContainer adContainer;
    private final CrawlerFactory crawlerFactory;
    private final AdFilterService adFilterService;
    private final List<Crawler> crawlers;
    private final HashSet<Task> tasks;
    private final Integer crawlerThreadCapacity;

    public CrawlerMonitorThreadDriver(DownloadDispatcher downloadDispatcher, AdContainer adContainer, CrawlerFactory crawlerFactory,
                                      AdFilterService adFilterService,
                                      @Value("#{new Integer('${notify.monitor.threads.crawler.taskLimit}')}") Integer crawlerThreadCapacity) {
        this.downloadDispatcher = downloadDispatcher;
        this.adContainer = adContainer;
        this.crawlerFactory = crawlerFactory;
        this.adFilterService = adFilterService;
        this.crawlerThreadCapacity = crawlerThreadCapacity;
        tasks = new HashSet<>(crawlerThreadCapacity + 1);
        crawlers = new ArrayList<>(3);
    }

    @Override
    public Boolean isFull() {
        return (tasks.size() >= crawlerThreadCapacity);
    }

    @Override
    public Boolean hasTask(Task task) {
        synchronized (tasks) {
            return tasks.contains(task);
        }
    }

    @Override
    public Boolean removeTask(Task task) {
        synchronized (tasks) {
            return tasks.remove(task);
        }
    }

    @Override
    public Boolean addTask(Task task) {
        synchronized (tasks) {
            log.debug("Received task id=" + task.getId());
            if (isFull())
                throw new MonitorThreadException("Tried to save a task to a full CrawlerMonitorThread");
            return tasks.add(task);
        }
    }

    @Override
    public void execute(Integer betweenDelay) throws InterruptedException {
        try {
            for (Task task : getShallowCopiedTasks()) {
                if (downloadDispatcher.isDownloaded(task)) {
                    crawlTask(task);
                    task.setRefreshTime();
                    log.debug("Task id=" + task.getId() + " downloaded");
                } else if (!downloadDispatcher.containsTask(task))
                    sendToDownload(task);
                Thread.sleep(betweenDelay);
            }
        } catch (CrawlerException e) {
            log.error("CrawlerException: %s", e.getMessage());
        }
    }

    private HashSet<Task> getShallowCopiedTasks() {
        synchronized (tasks) {
            return (HashSet<Task>) tasks.clone();
        }
    }

    private void sendToDownload(Task task) {
        downloadDispatcher.addTask(task);
    }

    private void crawlTask(Task task) {
        Crawler crawler = getCrawler(task);
        List<Ad> ads = getAds(crawler, task);
        if (ads != null && !ads.isEmpty())
            adContainer.addAds(task, adFilterService.filter(ads));
    }

    private Crawler getCrawler(Task task) {
        if (!crawlers.isEmpty())
            for (Crawler crawler : crawlers)
                if (crawlerCompatible(crawler, task))
                    return crawler;
        Crawler result = crawlerFactory.getParser(HostnameExtractor.getDomain(task.getUrl()));
        crawlers.add(result);
        return result;
    }

    public List<Ad> getAds(Crawler crawler, Task task) {
        if (crawler instanceof ApiCrawler)
            return ((ApiCrawler) crawler).getAds(task);
        if (crawler instanceof HtmlCrawler)
            return htmlCrawl(crawler, task);
        throw new CrawlerException("Unrecognized crawler instance");
    }

    private List<Ad> htmlCrawl(Crawler crawler, Task task) {
        Document document = getDocument(task);
        if (getDocument(task) != null)
            return ((HtmlCrawler) crawler).getAds(task, document);
        else
            return Collections.emptyList();
    }

    private Document getDocument(Task task) {
        return downloadDispatcher.returnDocument(task);
    }

    private Boolean crawlerCompatible(Crawler crawler, Task task) {
        return crawler.getHandledHostname().equals(HostnameExtractor.getDomain(task.getUrl()));
    }

}
