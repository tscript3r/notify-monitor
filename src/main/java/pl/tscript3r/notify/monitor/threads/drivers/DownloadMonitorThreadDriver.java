package pl.tscript3r.notify.monitor.threads.drivers;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.components.JsoupDocumentDownloader;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.CrawlerException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Scope("prototype")
public class DownloadMonitorThreadDriver implements MonitorThreadDriver {

    private static final int IGNORED_EXCEPTION_COOLDOWN_TIME = 300 * 1000; // 5min;
    
    private final JsoupDocumentDownloader jsoupDocumentDownloader;
    private final Integer downloaderQueueLimit;
    private final Map<Task, Document> downloadTasks = Collections.synchronizedMap(new HashMap<>());

    public DownloadMonitorThreadDriver(JsoupDocumentDownloader jsoupDocumentDownloader,
                                       @Value("#{new Integer('${notify.monitor.threads.downloader.maxQueue}')}") Integer downloaderQueueLimit) {
        this.downloaderQueueLimit = downloaderQueueLimit;
        this.jsoupDocumentDownloader = jsoupDocumentDownloader;
    }

    @Override
    public Boolean isFull() {
        return downloadTasks.size() >= downloaderQueueLimit;
    }

    @Override
    public Boolean hasTask(Task task) {
        return downloadTasks.containsKey(task);
    }

    @Override
    public Boolean removeTask(Task task) {
        Boolean result = hasTask(task);
        downloadTasks.remove(task);
        return result;
    }

    @Override
    public Boolean addTask(Task task) {
        synchronized (downloadTasks) {
            if (!downloadTasks.containsKey(task)) {
                downloadTasks.put(task, null);
                return true;
            } else
                return false;
        }
    }

    public Boolean isDownloaded(Task task) {
        synchronized (downloadTasks) {
            return downloadTasks.containsKey(task) && downloadTasks.get(task) != null;
        }
    }

    public Document returnDocument(Task task) {
        synchronized (downloadTasks) {
            return downloadTasks.remove(task);
        }
    }

    @Override
    public void execute(Integer betweenDelay) throws InterruptedException {
        for (Task task : getNotDownloadedTasks()) {
            checkDownloadAndPut(task);
            Thread.sleep(betweenDelay);
        }
    }

    private Set<Task> getNotDownloadedTasks() {
        synchronized (downloadTasks) {
            return downloadTasks.keySet()
                    .stream()
                    .filter(task -> !downloadTasks.containsValue(task))
                    .collect(Collectors.toSet());
        }
    }
    
    private void checkDownloadAndPut(Task task) throws InterruptedException {
        if (task.isRefreshable())
            try {
                Document document = downloadDocument(task);
                if(document != null)
                    synchronized (downloadTasks) {
                        downloadTasks.put(task, document);
                    }
            } catch (IOException e) {
                handleException(e);
            }
    }

    private Document downloadDocument(Task task) throws IOException {
        return jsoupDocumentDownloader.download(task.getUrl());
    }

    private void handleException(Exception e) throws InterruptedException {
        if(e instanceof ConnectException) {
            log.error("ConnectionException: " + e.getMessage());
            Thread.sleep(IGNORED_EXCEPTION_COOLDOWN_TIME);
        } else if(e instanceof UnknownHostException) {
            log.error("UnknownHostException: " + e.getMessage());
            Thread.sleep(IGNORED_EXCEPTION_COOLDOWN_TIME);
        } else
            throw new CrawlerException(e.getMessage());
    }
}
