package pl.tscript3r.notify.monitor.threads;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.components.AdContainer;
import pl.tscript3r.notify.monitor.crawlers.Crawler;
import pl.tscript3r.notify.monitor.crawlers.CrawlerFactory;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.CrawlerException;
import pl.tscript3r.notify.monitor.utils.HostnameExtractor;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Scope("prototype")
public class CrawlerThread implements Runnable {

    // TODO: unable to test, refactor

    private final DownloadThread downloadThread;
    private final AdContainer adContainer;
    private final CrawlerFactory crawlerFactory;
    private final List<Crawler> crawlers;
    private final ArrayList<Task> tasks;
    private final Integer crawlerThreadCapacity;

    private Thread thread;
    private Boolean isStopped = false;


    public CrawlerThread(DownloadThread downloadThread, AdContainer adContainer, CrawlerFactory crawlerFactory,
                         @Value("#{new Integer('${notify.monitor.threads.parserCapacity}')}") Integer crawlerThreadCapacity) {
        this.downloadThread = downloadThread;
        this.adContainer = adContainer;
        this.crawlerFactory = crawlerFactory;
        this.crawlerThreadCapacity = crawlerThreadCapacity;
        tasks = new ArrayList<>(crawlerThreadCapacity + 1);
        crawlers = new ArrayList<>(3);
    }

    public void start() {
        log.debug("Starting thread");
        if (!isStopped && thread == null || !thread.isAlive()) {
            downloadThread.start();
            isStopped = false;
            thread = new Thread(this);
            thread.setName("CrawlerThread=" + thread.getId());
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            downloadThread.stop();
            log.warn("Stopped");
            isStopped = true;
            thread.interrupt();
        }
    }

    public boolean isRunning() {
        return thread.isAlive() && downloadThread.isRunning();
    }

    public Boolean isFull() {
        return !(tasks.size() < crawlerThreadCapacity);
    }

    public Boolean hasTask(Task task) {
        synchronized (tasks) {
            return tasks.contains(task);
        }
    }

    public Boolean removeTask(Task task) {
        synchronized (tasks) {
            return tasks.remove(task);
        }
    }

    public Boolean addTask(Task task) {
        synchronized (tasks) {
            log.debug("Received task id=" + task.getId());
            return tasks.add(task);
        }
    }

    @Override
    public void run() {
        log.debug("Thread started");
        while (!thread.isInterrupted()) {
            try {
                for (Task task : getShallowCopiedTasks()) {
                    if (downloadThread.isDownloaded(task)) {
                        crawlTask(task);
                        task.setRefreshTime();
                        log.debug("Task id=" + task.getId() + " downloaded");
                    } else
                        sendToDownload(task);
                    Thread.sleep(5000);

                }
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                throw new CrawlerException(e.getMessage());
            }
        }
        log.warn("Thread stopped");
    }

    private ArrayList<Task> getShallowCopiedTasks() {
        synchronized (tasks) {
            return (ArrayList<Task>) tasks.clone();
        }
    }

    private void crawlTask(Task task) {
        adContainer.addAds(task, getParser(task).getAds(task, getDocument(task)));
    }

    private Document getDocument(Task task) {
        return downloadThread.returnDocument(task);
    }

    private void sendToDownload(Task task) {
        downloadThread.add(task);
    }

    private Crawler getParser(Task task) {
        if (!crawlers.isEmpty())
            for (int i = 0; i < crawlers.size(); i++) {
                Crawler crawler = crawlers.get(i);
                if (parserCompatible(crawler, task)) {
                    crawlers.remove(i);
                    return crawler;
                }
            }
        return crawlerFactory.getParser(HostnameExtractor.getDomain(task.getUrl()));
    }

    private Boolean parserCompatible(Crawler crawler, Task task) {
        return crawler.getHandledHostname().equals(HostnameExtractor.getDomain(task.getUrl()));
    }

}
