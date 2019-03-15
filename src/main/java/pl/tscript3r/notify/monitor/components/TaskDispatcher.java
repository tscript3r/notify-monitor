package pl.tscript3r.notify.monitor.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.threads.CrawlerThread;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TaskDispatcher {

    private final ApplicationContext context;
    private final List<CrawlerThread> crawlerThreads = new ArrayList<>();

    public TaskDispatcher(ApplicationContext context) {
        this.context = context;
    }

    public void addTask(Task task) {
        log.debug("Adding task id=" + task.getId());
        findFreeCrawlerThread().addTask(task);
    }

    private CrawlerThread findFreeCrawlerThread() {
        if (!crawlerThreads.isEmpty())
            for (CrawlerThread crawlerThread : crawlerThreads) {
                if (!crawlerThread.isFull())
                    return crawlerThread;
            }

        return getNewCrawlerThread();
    }

    private CrawlerThread getNewCrawlerThread() {
        CrawlerThread crawlerThread = (CrawlerThread) context.getBean("crawlerThread");
        crawlerThread.start();
        crawlerThreads.add(crawlerThread);
        return crawlerThread;
    }

    public Boolean removeTask(Task task) {
        for (CrawlerThread crawlerThread : crawlerThreads)
            if (crawlerThread.hasTask(task))
                return crawlerThread.removeTask(task);
        return false;
    }

    public Boolean containsTask(Task task) {
        for (CrawlerThread crawlerThread : crawlerThreads)
            if (crawlerThread.hasTask(task))
                return true;
        return false;
    }
}
