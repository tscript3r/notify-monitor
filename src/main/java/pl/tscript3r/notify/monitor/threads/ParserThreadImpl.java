package pl.tscript3r.notify.monitor.threads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.exceptions.ParserException;
import pl.tscript3r.notify.monitor.parsers.Parser;
import pl.tscript3r.notify.monitor.parsers.ParserFactory;
import pl.tscript3r.notify.monitor.services.DocumentDownloadService;
import pl.tscript3r.notify.monitor.utils.HostnameExtractor;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Component
@Scope("prototype")
public class ParserThreadImpl implements ParserThread {

    private static Integer parserCounter = 0;

    private final int INITIAL_ADS_CAPACITY = 120;

    private final Thread thread;
    private final Integer parserThreadId;
    private final Integer parserCapacity;
    private final DocumentDownloadService documentDownloadService;
    private final ParserFactory parserFactory;
    private final LinkedHashMap<Task, List<Ad>> taskAndDiscoveredAds;
    private final List<Parser> parsers;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final List<Future<Parser>> parsersFuture;

    private Integer parserThreadCapacity;

    public ParserThreadImpl(DocumentDownloadService documentDownloadService, ParserFactory parserFactory,
                            @Value("#{new Integer('${notify.monitor.threads.parserCapacity}')}") Integer parserThreadCapacity) {
        parserThreadId = parserCounter++;
        this.documentDownloadService = documentDownloadService;
        this.parserFactory = parserFactory;
        this.parserThreadCapacity = parserThreadCapacity;
        this.parserCapacity = parserThreadCapacity;
        thread = new Thread(this);
        thread.setName("pThread id=" + parserThreadId);
        taskAndDiscoveredAds = new LinkedHashMap<>(parserThreadCapacity + 1);
        parsers = new ArrayList<>(parserThreadCapacity + 1);
        parsersFuture = new ArrayList<>(parserThreadCapacity + 1);
    }

    @Override
    public Boolean hasFreeSlot() {
        Boolean result = taskAndDiscoveredAds.size() < parserThreadCapacity;
        if (result)
            log.debug("ParserThread id=" + this.parserThreadId + " has available slot");
        else
            log.debug("ParserThread id=" + this.parserThreadId + " has no free slots");

        return result;
    }

    @Override
    public Integer getParserThreadId() {
        return parserThreadId;
    }

    @Override
    public Boolean isTask(Task task) {
        Boolean result = taskAndDiscoveredAds.containsKey(task);
        if (result)
            log.debug("ParserThread id=" + this.parserThreadId
                    + " contains task id=" + task.getId());
        else
            log.debug("ParserThread id=" + this.parserThreadId
                    + " does not contain task id=" + task.getId());

        return result;
    }

    @Override
    public Boolean removeTask(Task task) {
        Boolean result = taskAndDiscoveredAds.remove(task) != null;
        if (result)
            log.debug("Task id=" + task.getId()
                    + " has been removed from ParserThread id=" + this.parserThreadId);
        else
            log.debug("Task id=" + task.getId()
                    + " could not be removed from ParserThread id=" + this.parserThreadId);
        return result;
    }

    @Override
    public Boolean addTask(Task task) {
        if (task != null && hasFreeSlot() && !isTask(task)) {
            taskAndDiscoveredAds.put(task,
                    Collections.synchronizedList(new ArrayList<>(INITIAL_ADS_CAPACITY)));
            log.debug("Task id=" + task.getId() +
                    " has been added to ParserThread id=" + this.parserThreadId);
            return true;
        }
        log.debug("Task id=" + task.getId()
                + "could not be added to ParserThread id=" + this.parserThreadId);
        return false;
    }

    @Override
    public void start() {
        thread.start();
    }

    @Override
    public void stop() {
        thread.interrupt();
    }

    // TODO: move?
    private Boolean parserCompatible(Parser parser, Task task) {
        return parser.getHandledHostname().equals(HostnameExtractor.getDomain(task.getUrl()));
    }

    // TODO: refactor for sure }: )
    private Parser getParser(Task task) {
        if (!parsers.isEmpty())
            for (int i = 0; i < parsers.size(); i++) {
                Parser parser = parsers.get(i);
                if (parserCompatible(parser, task)) {
                    parsers.remove(i);
                    return parser;
                }
            }

        return parserFactory.getParser(HostnameExtractor.getDomain(task.getUrl()));
    }

    @Override
    public void run() {
        while (!thread.isInterrupted()) {
            try {
                parsersFuture.removeIf(parserFuture -> {
                    if (parserFuture.isDone()) {
                        try {
                            parsers.add(parserFuture.get());
                        } catch (InterruptedException | ExecutionException e) {
                            throw new ParserException(e.getMessage());
                        } finally {
                            return true;
                        }
                    }
                    return false;
                });

                taskAndDiscoveredAds.forEach((task, list) -> {
                    if (list.isEmpty()) {
                        parsersFuture.add(executor.submit(FutureCreator.getInitialExecutor(task, getParser(task),
                                taskAndDiscoveredAds, documentDownloadService)));
                    } else if (task.refreshable())
                        parsersFuture.add(executor.submit(FutureCreator.getRefreshExecutor(task, getParser(task),
                                taskAndDiscoveredAds, documentDownloadService)));
                });

                Thread.sleep(15000);
            } catch (InterruptedException e) {
                throw new ParserException(e.getMessage());
            }
        }
        log.warn("Thread stopped");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParserThread that = (ParserThread) o;
        return Objects.equals(parserThreadId, that.getParserThreadId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(parserThreadId);
    }

}
