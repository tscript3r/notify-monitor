package pl.tscript3r.notify.monitor.threads.drivers;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.components.JsoupDocumentDownloader;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.CrawlerException;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Scope("prototype")
public class DownloadMonitorThreadDriver implements MonitorThreadDriver {

    private final JsoupDocumentDownloader jsoupDocumentDownloader;
    private final Map<Task, Document> downloadTasks = Collections.synchronizedMap(new HashMap<>());

    public DownloadMonitorThreadDriver(JsoupDocumentDownloader jsoupDocumentDownloader) {
        this.jsoupDocumentDownloader = jsoupDocumentDownloader;
    }

    @Override
    public Boolean isFull() {
        // TODO: implement, needs max value
        return false;
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

    public void downloadTasks(Integer betweenDelay) throws InterruptedException {
        for (Task task : getNotDownloadedTasks()) {
            checkDownloadAndPut(task);
            Thread.sleep(betweenDelay);
        }
    }

    private void checkDownloadAndPut(Task task) {
        if (task.isRefreshable())
            try {
                Document document = downloadDocument(task);
                synchronized (downloadTasks) {
                    downloadTasks.put(task, document);
                }
            } catch (IOException e) {
                throwException(e);
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

    private Document downloadDocument(Task task) throws IOException {
        return jsoupDocumentDownloader.download(task.getUrl());
    }

    private void throwException(Exception e) {
        throw new CrawlerException(e.getMessage());
    }
}
