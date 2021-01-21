package pl.tscript3r.notify.monitor.threads.drivers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.containers.AdContainer;
import pl.tscript3r.notify.monitor.crawlers.Crawler;
import pl.tscript3r.notify.monitor.crawlers.CrawlerFactory;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.CrawlerException;
import pl.tscript3r.notify.monitor.exceptions.IncompatibleHostnameException;
import pl.tscript3r.notify.monitor.exceptions.MonitorThreadException;
import pl.tscript3r.notify.monitor.services.AdFilterService;
import pl.tscript3r.notify.monitor.utils.HostnameExtractor;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
@Scope("prototype")
public class CrawlerMonitorThreadDriver implements MonitorThreadDriver {

    private final AdContainer adContainer;
    private final CrawlerFactory crawlerFactory;
    private final AdFilterService adFilterService;
    private final List<Crawler> crawlers;
    private final HashSet<Task> tasks;
    private final Integer cooldownTime;
    private final Integer maxAcceptableExecutionTime;

    private Boolean isWorkTimeExceeded = false;

    public CrawlerMonitorThreadDriver(AdContainer adContainer, CrawlerFactory crawlerFactory,
                                      AdFilterService adFilterService,
                                      @Value("#{new Integer('${notify.monitor.threads.crawler.cooldownTime}')}") Integer cooldownTime,
                                      @Value("#{new Integer('${notify.monitor.threads.crawler.maxExecutionTime}')}") Integer maxAcceptableExecutionTime) {
        this.adContainer = adContainer;
        this.crawlerFactory = crawlerFactory;
        this.adFilterService = adFilterService;
        this.cooldownTime = cooldownTime * 1000;
        this.maxAcceptableExecutionTime = maxAcceptableExecutionTime * 1000;
        tasks = new HashSet<>();
        crawlers = new ArrayList<>(4);
    }

    @Override
    public Boolean isFull() {
        return isWorkTimeExceeded;
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
            log.debug("Received task id={}", task.getId());
            if (isFull())
                throw new MonitorThreadException("Tried to save a task to a full CrawlerMonitorThread");
            return tasks.add(task);
        }
    }

    @Override
    public void execute(Integer betweenDelay) throws InterruptedException {
        long iterationTime = System.currentTimeMillis();
        for (Task task : getShallowCopiedTasks())
            try {
                if (task.isRefreshable()) {
                    adContainer.addAds(task,
                            adFilterService.filter(getCrawler(task).getAds(task)));
                    task.setRefreshTime();
                }
                Thread.sleep(betweenDelay);
            } catch (Exception e) {
                handleException(e);
            }
        testExecutionTime(System.currentTimeMillis() - iterationTime);
    }

    private HashSet<Task> getShallowCopiedTasks() {
        synchronized (tasks) {
            return (HashSet<Task>) tasks.clone();
        }
    }

    private Crawler getCrawler(Task task) {
        for (Crawler crawler : crawlers)
            if (crawlerCompatible(crawler, task))
                return crawler;
        Crawler result = crawlerFactory.getParser(HostnameExtractor.getDomain(task.getUrl()));
        crawlers.add(result);
        return result;
    }


    private Boolean crawlerCompatible(Crawler crawler, Task task) {
        return crawler.getHandledHostname()
                .equals(HostnameExtractor.getDomain(task.getUrl()));
    }

    private void handleException(Exception e) throws InterruptedException {
        if (isCooldownException(e)) {
            logSuppressedException(e);
            Thread.sleep(cooldownTime);
        } else if (isIgnoredException(e))
            log.warn("Exception[{}: {}] ignored", e.getClass().getSimpleName(), e.getMessage(), e);
        else
            throw new CrawlerException("CrawlerMonitorThreadDriver: " + e.getMessage());
    }

    private Boolean isCooldownException(Exception e) {
        return (e instanceof ConnectException || e instanceof UnknownHostException
                || e instanceof SocketTimeoutException);
    }

    private Boolean isIgnoredException(Exception e) {
        return (e instanceof CrawlerException || e instanceof IncompatibleHostnameException);
    }

    private void logSuppressedException(Exception e) {
        log.error("Following exception appeared: [{}: {}] - after cooldown time ({} sec) thread will resume",
                e.getClass().getSimpleName(), e.getMessage(), cooldownTime);
    }

    private void testExecutionTime(long executionTime) {
        isWorkTimeExceeded = (executionTime > maxAcceptableExecutionTime);
    }

}
