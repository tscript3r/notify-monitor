package pl.tscript3r.notify.monitor.threads;

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
public class DownloadThread implements Runnable {

    private final JsoupDocumentDownloader jsoupDocumentDownloader;
    private final Map<Task, Document> downloadTasks = Collections.synchronizedMap(new HashMap<>());

    private Thread thread;
    private Boolean isStopped = false;

    public DownloadThread(JsoupDocumentDownloader jsoupDocumentDownloader) {
        this.jsoupDocumentDownloader = jsoupDocumentDownloader;
        start();
    }

    public void add(Task task) {
        synchronized (downloadTasks) {
            downloadTasks.put(task, null);
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

    public void start() {
        if (!isStopped && thread == null || !thread.isAlive()) {
            isStopped = false;
            thread = new Thread(this);
            thread.setName("DownloadThread=" + thread.getId());
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            log.warn("Stopped");
            isStopped = true;
            thread.interrupt();
        }
    }

    public boolean isRunning() {
        return thread.isAlive();
    }

    @Override
    public void run() {
        while (!thread.isInterrupted()) {
            try {
                for (Task task : getNotDownloadedTasks()) {
                    checkDownloadAndPut(task);
                    Thread.sleep(5000);
                }
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                if (!isStopped)
                    throwException(e);
            }
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

    private Document downloadDocument(Task task) throws IOException {
        return jsoupDocumentDownloader.download(task.getUrl());
    }

    private void throwException(Exception e) {
        throw new CrawlerException(e.getMessage());
    }

}
