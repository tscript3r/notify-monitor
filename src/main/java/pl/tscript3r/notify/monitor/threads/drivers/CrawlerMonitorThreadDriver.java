package pl.tscript3r.notify.monitor.threads.drivers;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.components.AdContainer;
import pl.tscript3r.notify.monitor.crawlers.Crawler;
import pl.tscript3r.notify.monitor.crawlers.CrawlerFactory;
import pl.tscript3r.notify.monitor.dispatchers.DownloadDispatcher;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.MonitorThreadException;
import pl.tscript3r.notify.monitor.utils.HostnameExtractor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
@Scope("prototype")
public class CrawlerMonitorThreadDriver implements MonitorThreadDriver {
    private final DownloadDispatcher downloadDispatcher;
    private final AdContainer adContainer;
    private final CrawlerFactory crawlerFactory;
    private final List<Crawler> crawlers;
    private final HashSet<Task> tasks;
    private final Integer crawlerThreadCapacity;

    public CrawlerMonitorThreadDriver(DownloadDispatcher downloadDispatcher, AdContainer adContainer, CrawlerFactory crawlerFactory,
                                      @Value("#{new Integer('${notify.monitor.threads.crawler.taskLimit}')}") Integer crawlerThreadCapacity) {
        this.downloadDispatcher = downloadDispatcher;
        this.adContainer = adContainer;
        this.crawlerFactory = crawlerFactory;
        this.crawlerThreadCapacity = crawlerThreadCapacity;
        tasks = new HashSet<>(crawlerThreadCapacity + 1);
        crawlers = new ArrayList<>(3);
    }

    @Override
    public Boolean isFull() {
        return !(tasks.size() < crawlerThreadCapacity);
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
                throw new MonitorThreadException("Tried to add a task to a full CrawlerMonitorThread");
            return tasks.add(task);
        }
    }

    @Override
    public void execute(Integer betweenDelay) throws InterruptedException {
        for (Task task : getShallowCopiedTasks()) {
            if (downloadDispatcher.isDownloaded(task)) {
                crawlTask(task);
                task.setRefreshTime();
                log.debug("Task id=" + task.getId() + " downloaded");
            } else if (!downloadDispatcher.containsTask(task))
                sendToDownload(task);
            Thread.sleep(betweenDelay);
        }
    }

    private HashSet<Task> getShallowCopiedTasks() {
        synchronized (tasks) {
            return (HashSet<Task>) tasks.clone();
        }
    }

    private void crawlTask(Task task) {
        Document document = getDocument(task);
        if (document != null)
            adContainer.addAds(task, getParser(task).getAds(task, document));
    }

    private Document getDocument(Task task) {
        return downloadDispatcher.returnDocument(task);
    }

    private void sendToDownload(Task task) {
        downloadDispatcher.addTask(task);
    }

    private Crawler getParser(Task task) {
        if (!crawlers.isEmpty())
            for (Crawler crawler : crawlers)
                if (parserCompatible(crawler, task))
                    return crawler;

        Crawler result = crawlerFactory.getParser(HostnameExtractor.getDomain(task.getUrl()));
        crawlers.add(result);
        return result;
    }

    private Boolean parserCompatible(Crawler crawler, Task task) {
        return crawler.getHandledHostname().equals(HostnameExtractor.getDomain(task.getUrl()));
    }
}
